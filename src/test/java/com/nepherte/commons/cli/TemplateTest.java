/*
 * Copyright 2012-2020 Bart Verhoeven
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nepherte.commons.cli;

import com.nepherte.commons.cli.Option.Template;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.number.OrderingComparison.*;

import static com.nepherte.commons.test.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * A test that covers {@link Template Template} and {@link Template.Builder
 * Template.Builder}.
 */
class TemplateTest {

  @Test
  void shortName() {
    Template.Builder builder = Option.newTemplate().shortName("-b");
    assertThat(new Template(builder).getShortName(), optionalWithValue("b"));
  }

  @Test
  void nullShortName() {
    assertThrows(IllegalArgumentException.class,
      () -> Option.newTemplate().shortName(null));
  }

  @Test
  void emptyShortName() {
    assertThrows(IllegalArgumentException.class,
      () -> Option.newTemplate().shortName(""));
  }

  @Test
  void shortNameWithSpace() {
    assertThrows(IllegalArgumentException.class,
      () -> Option.newTemplate().shortName("short\tname"));
  }

  @Test
  void dashOnlyShortName() {
    assertThrows(IllegalArgumentException.class,
      () -> Option.newTemplate().shortName("--"));
  }

  @Test
  void longName() {
    Template.Builder builder = Option.newTemplate().longName("--block");
    assertThat(new Template(builder).getLongName(), optionalWithValue("block"));
  }

  @Test
  void nullLongName() {
    assertThrows(IllegalArgumentException.class,
      () -> Option.newTemplate().longName(null));
  }

  @Test
  void emptyLongName() {
    assertThrows(IllegalArgumentException.class,
      () -> Option.newTemplate().longName(""));
  }

  @Test
  void longNameWithSpace() {
    assertThrows(IllegalArgumentException.class,
      () -> Option.newTemplate().longName("long\tname"));
  }

  @Test
  void dashOnlyLongName() {
    assertThrows(IllegalArgumentException.class,
      () -> Option.newTemplate().longName("--"));
  }

  @Test
  void name() {
    // Only short name available.
    Template.Builder builder = Option.newTemplate().shortName("-b");
    assertThat(new Template(builder).getName(), is("b"));

    // Only long name available.
    builder = Option.newTemplate().longName("--block-size");
    assertThat(new Template(builder).getName(), is("block-size"));

    // Short name takes precedence over long name.
    builder = Option.newTemplate().shortName("-b").longName("--block-size");
    assertThat(new Template(builder).getName(), is("b"));
  }

  @Test
  void sortByName() {
    Template t1 = Option.newTemplate().shortName("a").build();
    Template t2 = Option.newTemplate().longName("b").build();
    Template t3 = Option.newTemplate().shortName("c").build();

    // Reflexive
    assertThat(Template.byName(t1, t1), equalTo(0));

    // Symmetric
    assertThat(Template.byName(t2, t1), greaterThan(0));
    assertThat(Template.byName(t1, t2), lessThan(0));

    // Transitive
    assertThat(Template.byName(t2, t1), greaterThan(0));
    assertThat(Template.byName(t3, t2), greaterThan(0));
    assertThat(Template.byName(t3, t1), greaterThan(0));
  }

  @Test
  void description() {
    Template.Builder builder = Option.newTemplate().description("ab");
    assertThat(new Template(builder).getDescription(), optionalWithValue("ab"));
  }

  @Test
  void nullDescription() {
    assertThrows(IllegalArgumentException.class,
      () -> Option.newTemplate().description(null));
  }

  @Test
  void blankDescription() {
    assertThrows(IllegalArgumentException.class,
      () -> Option.newTemplate().description("  "));
  }

  @Test
  void optional() {
    Template.Builder builder = Option.newTemplate();
    assertThat(new Template(builder).isRequired(), is(false));
  }

  @Test
  void required() {
    Template.Builder builder = Option.newTemplate().required();
    assertThat(new Template(builder).isRequired(), is(true));
  }

  @Test
  void minValues() {
    Template.Builder builder = Option.newTemplate().minValues(8);
    assertThat(new Template(builder).getMinValues(), is(8));
  }

  @Test
  void negativeMinValues() {
    assertThrows(IllegalArgumentException.class,
      () -> Option.newTemplate().minValues(-1));
  }

  @Test
  void maxValues() {
    Template.Builder builder = Option.newTemplate().maxValues(1);
    assertThat(new Template(builder).getMaxValues(), is(1));
  }

  @Test
  void negativeMaxValues() {
    assertThrows(IllegalArgumentException.class,
      () -> Option.newTemplate().maxValues(-1));
  }

  @Test
  void requiresValues() {
    Template.Builder builder = Option.newTemplate().minValues(1);
    assertThat(new Template(builder).requiresValues(), is(true));

    builder = Option.newTemplate().minValues(0);
    assertThat(new Template(builder).requiresValues(), is(false));
  }

  @Test
  void canHaveValues() {
    Template.Builder builder = Option.newTemplate().maxValues(1);

    assertThat(new Template(builder).canHaveValues(), is(true));
    assertThat(new Template(builder).canHaveValues(0), is(true));
    assertThat(new Template(builder).canHaveValues(1), is(true));
    assertThat(new Template(builder).canHaveValues(2), is(false));
  }

  @Test
  void valueName() {
    Template.Builder builder = Option.newTemplate().valueName("SIZE");
    assertThat(new Template(builder).getValueName(), optionalWithValue("SIZE"));
  }

  @Test
  void nullValueName() {
    assertThrows(IllegalArgumentException.class,
      () -> Option.newTemplate().valueName(null));
  }

  @Test
  void blankValueName() {
    assertThrows(IllegalArgumentException.class,
      () -> Option.newTemplate().valueName("  "));
  }

  @Test
  void nameMissing() {
    assertThrows(IllegalStateException.class,
      () -> Option.newTemplate().build());
  }

  @Test
  void tooFewValues() {
    assertThrows(IllegalStateException.class, () ->
      Option.newTemplate().shortName("-b").minValues(2).maxValues(1).build());
  }

  @Test
  void builderReUsage() {
    Template.Builder builder = Option.newTemplate();

    Template templateA = builder.shortName("a").build();
    Template templateB = builder.shortName("b").build();

    assertThat(templateA.getShortName(), optionalWithValue("a"));
    assertThat(templateB.getShortName(), optionalWithValue("b"));
  }

  @Test
  void stringValue() {
    // Builder with no name.
    Template.Builder builder = Option.newTemplate();
    assertThat(builder.toString(), is("-<undefined>"));

    // Template with short name.
    builder = Option.newTemplate().shortName("a");
    assertThat(builder.toString(), is("-a"));

    // Template with long name.
    builder = Option.newTemplate().longName("b");
    assertThat(builder.toString(), is("--b"));

    // Template with optional values.
    builder = Option.newTemplate().valueName("value");
    builder.shortName("-a").maxValues(1);
    assertThat(builder.toString(), is("-a=[<value>]"));

    // Template with required values.
    builder = Option.newTemplate().valueName("value");
    builder.shortName("-a").minValues(1).maxValues(1);
    assertThat(builder.toString(), is("-a=<value>"));
  }
}

/*
 * Copyright 2012-2018 Bart Verhoeven
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
package be.nepherte.commons.cli;

import be.nepherte.commons.cli.Option.Template;

import org.junit.Test;

import static be.nepherte.commons.test.Matchers.optionalWithValue;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.hamcrest.number.OrderingComparison.lessThan;

import static org.junit.Assert.assertThat;

/**
 * A test that covers {@link Template Template} and {@link Template.Builder
 * Template.Builder}.
 */
public class TemplateTest {

  @Test
  public void shortName() {
    Template.Builder builder = Option.newTemplate().shortName("-b");
    assertThat(new Template(builder).getShortName(), optionalWithValue("b"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void nullShortName() {
    Option.newTemplate().shortName(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void emptyShortName() {
    Option.newTemplate().shortName("");
  }

  @Test(expected = IllegalArgumentException.class)
  public void shortNameWithSpace() {
    Option.newTemplate().shortName("short\tname");
  }

  @Test(expected = IllegalArgumentException.class)
  public void dashOnlyShortName() {
    Option.newTemplate().shortName("--");
  }

  @Test
  public void longName() {
    Template.Builder builder = Option.newTemplate().longName("--block");
    assertThat(new Template(builder).getLongName(), optionalWithValue("block"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void nullLongName() {
    Option.newTemplate().longName(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void emptyLongName() {
    Option.newTemplate().longName("");
  }

  @Test(expected = IllegalArgumentException.class)
  public void longNameWithSpace() {
    Option.newTemplate().longName("long\tname");
  }

  @Test(expected = IllegalArgumentException.class)
  public void dashOnlyLongName() {
    Option.newTemplate().longName("--");
  }

  @Test
  public void name() {
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
  public void sortByName() {
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
  public void description() {
    Template.Builder builder = Option.newTemplate().description("ab");
    assertThat(new Template(builder).getDescription(), optionalWithValue("ab"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void nullDescription() {
    Option.newTemplate().description(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void blankDescription() {
    Option.newTemplate().description("  ");
  }

  @Test
  public void optional() {
    Template.Builder builder = Option.newTemplate();
    assertThat(new Template(builder).isRequired(), is(false));
  }

  @Test
  public void required() {
    Template.Builder builder = Option.newTemplate().required();
    assertThat(new Template(builder).isRequired(), is(true));
  }

  @Test
  public void minValues() {
    Template.Builder builder = Option.newTemplate().minValues(8);
    assertThat(new Template(builder).getMinValues(), is(8));
  }

  @Test(expected = IllegalArgumentException.class)
  public void negativeMinValues() {
    Option.newTemplate().minValues(-1);
  }

  @Test
  public void maxValues() {
    Template.Builder builder = Option.newTemplate().maxValues(1);
    assertThat(new Template(builder).getMaxValues(), is(1));
  }

  @Test(expected = IllegalArgumentException.class)
  public void negativeMaxValues() {
    Option.newTemplate().maxValues(-1);
  }

  @Test
  public void requiresValues() {
    Template.Builder builder = Option.newTemplate().minValues(1);
    assertThat(new Template(builder).requiresValues(), is(true));

    builder = Option.newTemplate().minValues(0);
    assertThat(new Template(builder).requiresValues(), is(false));
  }

  @Test
  public void canHaveValues() {
    Template.Builder builder = Option.newTemplate().maxValues(1);

    assertThat(new Template(builder).canHaveValues(), is(true));
    assertThat(new Template(builder).canHaveValues(0), is(true));
    assertThat(new Template(builder).canHaveValues(1), is(true));
    assertThat(new Template(builder).canHaveValues(2), is(false));
  }

  @Test
  public void valueName() {
    Template.Builder builder = Option.newTemplate().valueName("SIZE");
    assertThat(new Template(builder).getValueName(), optionalWithValue("SIZE"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void nullValueName() {
    Option.newTemplate().valueName(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void blankValueName() {
    Option.newTemplate().valueName("  ");
  }

  @Test(expected = IllegalStateException.class)
  public void nameMissing() {
    Option.newTemplate().build();
  }

  @Test(expected = IllegalStateException.class)
  public void tooFewValues() {
    Option.newTemplate().shortName("-b").minValues(2).maxValues(1).build();
  }

  @Test
  public void builderReUsage() {
    Template.Builder builder = Option.newTemplate();

    Template templateA = builder.shortName("a").build();
    Template templateB = builder.shortName("b").build();

    assertThat(templateA.getShortName(), optionalWithValue("a"));
    assertThat(templateB.getShortName(), optionalWithValue("b"));
  }

  @Test
  public void stringValue() {
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

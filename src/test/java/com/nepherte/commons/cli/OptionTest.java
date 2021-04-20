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

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.collection.IsIterableContainingInOrder.*;

import static com.nepherte.commons.test.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test that covers options and their builders.
 */
class OptionTest {

  @Test
  void shortName() {
    Option.Builder builder = Option.newInstance().shortName("-b");
    assertThat(new Option(builder).getShortName(), optionalWithValue("b"));
  }

  @Test
  void nullShortName() {
    Option.Builder builder = Option.newInstance()
      .shortName("-b").shortName(null);

    assertThat(new Option(builder).getShortName(), optionalWithNoValue());
  }

  @Test
  void emptyShortName() {
    assertThrows(IllegalArgumentException.class,
      () -> Option.newInstance().shortName(""));
  }

  @Test
  void shortNameWithSpace() {
    assertThrows(IllegalArgumentException.class,
      () -> Option.newInstance().shortName("short\tname"));
  }

  @Test
  void dashOnlyShortName() {
    assertThrows(IllegalArgumentException.class,
      () -> Option.newInstance().shortName("--"));
  }

  @Test
  void longName() {
    Option.Builder builder = Option.newInstance().longName("--block");
    assertThat(new Option(builder).getLongName(), optionalWithValue("block"));
  }

  @Test
  void nullLongName() {
    Option.Builder builder = Option.newInstance()
      .longName("--block").longName(null);

    assertThat(new Option(builder).getLongName(), optionalWithNoValue());
  }

  @Test
  void emptyLongName() {
    assertThrows(IllegalArgumentException.class,
      () -> Option.newInstance().longName(""));
  }

  @Test
  void longNameWithSpace() {
    assertThrows(IllegalArgumentException.class,
      () -> Option.newInstance().longName("long\tname"));
  }

  @Test
  void dashOnlyLongName() {
    assertThrows(IllegalArgumentException.class,
      () -> Option.newInstance().longName("--"));
  }

  @Test
  void name() {
    // Only short name available.
    Option.Builder builder = Option.newInstance().shortName("-b");
    assertThat(new Option(builder).getName(), is("b"));

    // Only long name available.
    builder = Option.newInstance().longName("--block");
    assertThat(new Option(builder).getName(), is("block"));

    // Short name takes precedence.
    builder = Option.newInstance().shortName("-b").longName("--block");
    assertThat(new Option(builder).getName(), is("b"));
  }

  @Test
  void templateCopy() {
    Option.Template template = Option.newTemplate()
      .shortName("b").longName("block").build();

    Option option = new Option(Option.newInstance(template));
    assertThat(option.getShortName(), optionalWithValue("b"));
    assertThat("block", option.getLongName(), optionalWithValue("block"));
  }

  @Test
  void value() {
    Option.Builder builder = Option.newInstance().value("8");
    assertThat(new Option(builder).getValue(), optionalWithValue("8"));
    assertThat(new Option(builder).getValues(), contains("8"));
  }

  @Test
  void nullValue() {
    assertThrows(IllegalArgumentException.class,
      () -> Option.newInstance().value(null));
  }

  @Test
  void valuesArray() {
    Option.Builder builder = Option.newInstance().values("8", "9");
    assertThat(new Option(builder).getValues(), contains("8", "9"));
    assertThat(new Option(builder).getValue(), optionalWithValue("8"));
  }

  @Test
  void nullValuesArray() {
    assertThrows(IllegalArgumentException.class,
      () -> Option.newInstance().values((String[]) null));
  }

  @Test
  void valuesIterable() {
    List<String> values = Arrays.asList("8", "9");
    Option.Builder builder = Option.newInstance().values(values);
    assertThat(new Option(builder).getValues(), contains("8", "9"));
    assertThat(new Option(builder).getValue(), optionalWithValue("8"));
  }

  @Test
  void nullValuesIterable() {
    assertThrows(IllegalArgumentException.class,
      () -> Option.newInstance().values((Iterable<String>) null));
  }

  @Test
  void immutableValues() {
    Option.Builder builder = Option.newInstance();
    assertThrows(UnsupportedOperationException.class,
      () -> new Option(builder).getValues().add("10"));
  }

  @Test
  void nameMissing() {
    assertThrows(IllegalStateException.class,
      () -> Option.newInstance().build());
  }

  @Test
  void builderReUsage() {
    Option.Builder builder = Option.newInstance();

    Option optionA = builder.shortName("a").build();
    Option optionB = builder.shortName("b").build();

    assertThat(optionA.getShortName(), optionalWithValue("a"));
    assertThat(optionB.getShortName(), optionalWithValue("b"));
  }

  @Test
  void stringValue() {
    // Builder with no name.
    Option.Builder builder = Option.newInstance();
    assertThat(builder.toString(), is("-<undefined>"));

    // Option with short name.
    builder = Option.newInstance().shortName("a");
    assertThat(builder.toString(), is("-a"));

    // Option with long name.
    builder = Option.newInstance().longName("a");
    assertThat(builder.toString(), is("--a"));

    // Option with values.
    builder = Option.newInstance().shortName("a").values("1", "2");
    assertThat(builder.toString(), is("-a=1,2"));
  }
}

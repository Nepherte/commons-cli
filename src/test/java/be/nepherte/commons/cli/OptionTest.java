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

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static be.nepherte.commons.test.Matchers.optionalWithNoValue;
import static be.nepherte.commons.test.Matchers.optionalWithValue;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyIterable.emptyIterable;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;

/**
 * Test that covers options and their builders.
 */
public class OptionTest {

  @Test
  public void shortName() {
    Option.Builder builder = Option.newInstance().shortName("-b");
    assertThat(new Option(builder).getShortName(), optionalWithValue("b"));
  }

  @Test
  public void nullShortName() {
    Option.Builder builder = Option.newInstance().shortName(null);
    assertThat(new Option(builder).getShortName(), optionalWithNoValue());
  }

  @Test
  public void blankShortName() {
    Option.Builder builder = Option.newInstance().shortName("");
    assertThat(new Option(builder).getShortName(), optionalWithNoValue());
  }

  @Test(expected = IllegalArgumentException.class)
  public void shortNameWithWhitespace() {
    Option.newInstance().shortName("short\tname");
  }

  @Test
  public void longName() {
    Option.Builder builder = Option.newInstance().longName("--block");
    assertThat(new Option(builder).getLongName(), optionalWithValue("block"));
  }

  @Test
  public void nullLongName() {
    Option.Builder builder = Option.newInstance().longName(null);
    assertThat(new Option(builder).getLongName(), optionalWithNoValue());
  }

  @Test
  public void blankLongName() {
    Option.Builder builder = Option.newInstance().longName("");
    assertThat(new Option(builder).getLongName(), optionalWithNoValue());
  }

  @Test(expected = IllegalArgumentException.class)
  public void longNameWithSpaces() {
    Option.newInstance().longName("long\tname");
  }

  @Test
  public void name() {
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
  public void templateCopy() {
    Option.Template template = Option.newTemplate()
      .shortName("b").longName("block").build();

    Option option = new Option(Option.newInstance(template));
    assertThat(option.getShortName(), optionalWithValue("b"));
    assertThat("block", option.getLongName(), optionalWithValue("block"));
  }

  @Test
  public void value() {
    Option.Builder builder = Option.newInstance().value("8");
    assertThat(new Option(builder).getValue(), optionalWithValue("8"));
    assertThat(new Option(builder).getValues(), contains("8"));
  }

  @Test
  public void nullValue() {
    Option.Builder builder = Option.newInstance().value(null);
    assertThat(new Option(builder).getValue(), optionalWithNoValue());
    assertThat(new Option(builder).getValues(), emptyIterable());
  }

  @Test
  public void absentValue() {
    Option.Builder builder = Option.newInstance();
    assertThat(new Option(builder).getValue(), optionalWithNoValue());
    assertThat(new Option(builder).getValues(), emptyIterable());
  }

  @Test
  public void values() {
    Option.Builder builder = Option.newInstance().values("8", "9");
    assertThat(new Option(builder).getValues(), contains("8", "9"));
    assertThat(new Option(builder).getValue(), optionalWithValue("8"));
  }

  @Test
  public void nullValues() {
    List<String> values = Arrays.asList(null, null);
    Option.Builder builder = Option.newInstance().values(values);
    assertThat(new Option(builder).getValues(), emptyIterable());
  }

  @Test( expected = UnsupportedOperationException.class)
  public void immutableValues() {
    Option.Builder builder = Option.newInstance();
    new Option(builder).getValues().add("10");
  }

  @Test(expected = IllegalStateException.class)
  public void nameMissing() {
    Option.newInstance().build();
  }

  @Test
  public void builderReUsage() {
    Option.Builder builder = Option.newInstance();

    Option optionA = builder.shortName("a").build();
    Option optionB = builder.shortName("b").build();

    assertThat(optionA.getShortName(), optionalWithValue("a"));
    assertThat(optionB.getShortName(), optionalWithValue("b"));
  }

  @Test
  public void stringValue() {
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

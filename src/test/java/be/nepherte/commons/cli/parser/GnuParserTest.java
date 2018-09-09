/*
 * Copyright 2012-2018 Bart Verhoeven
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.nepherte.commons.cli.parser;

import be.nepherte.commons.cli.Command;
import be.nepherte.commons.cli.Command.Descriptor;
import be.nepherte.commons.cli.Option;
import be.nepherte.commons.cli.Option.Group;
import be.nepherte.commons.cli.Option.Template;
import be.nepherte.commons.cli.Parser.ParseException;

import be.nepherte.commons.cli.internal.Exceptions.ExclusiveOptionsException;
import be.nepherte.commons.cli.internal.Exceptions.MissingArgumentException;
import be.nepherte.commons.cli.internal.Exceptions.MissingGroupException;
import be.nepherte.commons.cli.internal.Exceptions.MissingOptionException;
import be.nepherte.commons.cli.internal.Exceptions.MissingValueException;
import be.nepherte.commons.cli.internal.Exceptions.TooManyArgumentsException;
import be.nepherte.commons.cli.internal.Exceptions.TooManyValuesException;
import be.nepherte.commons.cli.internal.Exceptions.UnrecognizedTokenException;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;

/**
 * Test that covers the option format of a Gnu parser.
 */
public class GnuParserTest {

  @Test
  public void shortOption() throws ParseException {
    Template a = Option.newTemplate().shortName("a").build();

    Descriptor.Builder builder = Command.newDescriptor();
    Descriptor descriptor = builder.templates(a).build();

    GnuParser parser = new GnuParser(descriptor);
    Command cmd = parser.parse(new String[]{"-a"});

    assertThat(cmd.hasOption("a"), is(true));
  }

  @Test
  public void longOption() throws ParseException {
    Template a = Option.newTemplate().longName("enable-a").build();

    Descriptor.Builder builder = Command.newDescriptor();
    Descriptor descriptor = builder.templates(a).build();

    GnuParser parser = new GnuParser(descriptor);
    Command cmd = parser.parse(new String[]{"--enable-a"});

    assertThat(cmd.hasOption("enable-a"), is(true));
  }

  @Test
  public void requiredOption() throws ParseException {
    Template a = Option.newTemplate().shortName("a").required().build();

    Descriptor.Builder builder = Command.newDescriptor();
    Descriptor descriptor = builder.templates(a).build();

    GnuParser parser = new GnuParser(descriptor);
    Command cmd = parser.parse(new String[]{"-a"});

    assertThat(cmd.hasOption("a"), is(true));
  }

  @Test(expected = MissingOptionException.class)
  public void missingOption() throws ParseException {
    Template a = Option.newTemplate().shortName("a").required().build();

    Descriptor.Builder builder = Command.newDescriptor();
    Descriptor descriptor = builder.template(a).build();

    GnuParser parser = new GnuParser(descriptor);
    parser.parse(new String[0]);
  }

  @Test(expected = UnrecognizedTokenException.class)
  public void unrecognizedOption() throws ParseException {
    Template a = Option.newTemplate().shortName("a").build();

    Descriptor.Builder builder = Command.newDescriptor();
    Descriptor descriptor = builder.templates(a).build();
    GnuParser parser = new GnuParser(descriptor);

    parser.parse(new String[]{"-b"});
  }

  @Test(expected = UnrecognizedTokenException.class)
  public void unrecognizedOptionWithValues() throws ParseException {
    Template a = Option.newTemplate().shortName("a").build();

    Descriptor.Builder builder = Command.newDescriptor();
    Descriptor descriptor = builder.templates(a).build();

    GnuParser parser = new GnuParser(descriptor);
    parser.parse(new String[]{"-unknown=value"});
  }

  @Test(expected = MissingValueException.class)
  public void optionWithMissingValue() throws ParseException {
    Template a = Option.newTemplate().shortName("a")
      .minValues(1).maxValues(1).build();

    Descriptor.Builder builder = Command.newDescriptor();
    Descriptor descriptor = builder.templates(a).build();

    GnuParser parser = new GnuParser(descriptor);
    parser.parse(new String[]{"-a"});
  }

  @Test(expected = UnrecognizedTokenException.class)
  public void optionWithTooManyDashes() throws ParseException {
    Template a = Option.newTemplate().shortName("a").build();

    Descriptor.Builder builder = Command.newDescriptor();
    Descriptor descriptor = builder.templates(a).build();

    GnuParser parser = new GnuParser(descriptor);
    parser.parse(new String[]{"--a"});
  }

  @Test(expected = TooManyValuesException.class)
  public void optionWithUnexpectedValues() throws ParseException {
    Template a = Option.newTemplate().shortName("a").maxValues(0).build();

    Descriptor.Builder builder = Command.newDescriptor();
    Descriptor descriptor = builder.templates(a).build();

    GnuParser parser = new GnuParser(descriptor);
    parser.parse(new String[]{"-a=1"});
  }

  @Test(expected = TooManyValuesException.class)
  public void optionWithTooManyValues() throws ParseException {
    Template a = Option.newTemplate().shortName("a").maxValues(1).build();

    Descriptor.Builder builder = Command.newDescriptor();
    Descriptor descriptor = builder.templates(a).build();

    GnuParser parser = new GnuParser(descriptor);
    parser.parse(new String[]{"-a=1,2"});
  }

  @Test(expected = UnrecognizedTokenException.class)
  public void optionWithTooManyValueSeparators() throws ParseException {
    Template a = Option.newTemplate().shortName("a").maxValues(1).build();

    Descriptor.Builder builder = Command.newDescriptor();
    Descriptor descriptor = builder.templates(a).build();

    GnuParser parser = new GnuParser(descriptor);
    parser.parse(new String[]{"-a=foo=bar"});
  }

  @Test
  public void optionWithValue() throws ParseException {
    Template a = Option.newTemplate().shortName("a").maxValues(1).build();

    Descriptor.Builder builder = Command.newDescriptor();
    Descriptor descriptor = builder.templates(a).build();

    GnuParser parser = new GnuParser(descriptor);
    Command cmd = parser.parse(new String[]{"-a=1"});


    assertThat(cmd.getOptionValue("a"), is("1"));
    assertThat(cmd.getOptionValues("a"), contains("1"));
  }

  @Test
  public void optionWithValues() throws ParseException {
    Template a = Option.newTemplate().shortName("a").maxValues(2).build();

    Descriptor.Builder builder = Command.newDescriptor();
    Descriptor descriptor = builder.templates(a).build();

    GnuParser parser = new GnuParser(descriptor);
    Command cmd = parser.parse(new String[]{"-a=1,2"});

    assertThat(cmd.getOptionValues("a"), contains("1", "2"));
  }

  @Test
  public void optionValueWithHyphen() throws ParseException {
    Template a = Option.newTemplate().shortName("a").maxValues(1).build();

    Descriptor.Builder builder = Command.newDescriptor();
    Descriptor descriptor = builder.templates(a).build();

    GnuParser parser = new GnuParser(descriptor);
    Command cmd = parser.parse(new String[]{"-a=-foo"});

    assertThat(cmd.hasOption("foo"), is(false));
    assertThat(cmd.getOptionValue("a"), is("-foo"));
  }

  @Test
  public void optionValueIsAlsoAnOption() throws ParseException {
    Template a = Option.newTemplate().shortName("a").maxValues(1).build();
    Template b = Option.newTemplate().shortName("b").build();

    Descriptor.Builder builder = Command.newDescriptor();
    Descriptor descriptor = builder.templates(a, b).build();

    GnuParser parser = new GnuParser(descriptor);
    Command cmd = parser.parse(new String[]{"-a=-b"});

    assertThat(cmd.hasOption("b"), is(false));
    assertThat(cmd.getOptionValue("a"), is("-b"));
  }

  @Test
  public void optionValueIsDoubleDash() throws ParseException {
    Template a = Option.newTemplate().shortName("a").maxValues(1).build();

    Descriptor.Builder builder = Command.newDescriptor();
    Descriptor descriptor = builder.templates(a).build();

    GnuParser parser = new GnuParser(descriptor);
    Command cmd = parser.parse(new String[]{"-a=--"});

    assertThat(cmd.getOptionValue("a"), is("--"));
  }

  @Test
  public void options() throws ParseException {
    Template a = Option.newTemplate().shortName("a").build();
    Template b = Option.newTemplate().longName("b").build();

    Descriptor.Builder builder = Command.newDescriptor();
    Descriptor descriptor = builder.templates(a, b).build();

    GnuParser parser = new GnuParser(descriptor);
    Command cmd = parser.parse(new String[]{"-a", "--b"});

    assertThat(cmd.hasOption("a"), is(true));
    assertThat(cmd.hasOption("b"), is(true));
  }

  @Test(expected = ExclusiveOptionsException.class)
  public void exclusiveOptions() throws ParseException {
    Template a = Option.newTemplate().shortName("a").build();
    Template b = Option.newTemplate().shortName("b").build();

    Group.Builder gb = Option.newGroup().required();
    Group g = gb.templates(a, b).build();

    Descriptor.Builder builder = Command.newDescriptor();
    Descriptor descriptor = builder.groups(g).build();

    GnuParser parser = new GnuParser(descriptor);
    parser.parse(new String[]{"-a", "-b"});
  }

  @Test
  public void group() throws ParseException {
    Template a = Option.newTemplate().shortName("a").build();
    Group group = Option.newGroup().template(a).build();

    Descriptor.Builder builder = Command.newDescriptor();
    Descriptor descriptor = builder.groups(group).build();

    GnuParser parser = new GnuParser(descriptor);
    Command cmd = parser.parse(new String[]{"-a"});

    assertThat(cmd.hasOption("a"), is(true));
  }

  @Test
  public void requiredGroup() throws ParseException {
    Template a = Option.newTemplate().shortName("a").build();
    Group g = Option.newGroup().required().template(a).build();

    Descriptor.Builder builder = Command.newDescriptor();
    Descriptor descriptor = builder.groups(g).build();

    GnuParser parser = new GnuParser(descriptor);
    Command cmd = parser.parse(new String[]{"-a"});

    assertThat(cmd.hasOption("a"), is(true));
  }

  @Test(expected = MissingGroupException.class)
  public void missingGroup() throws ParseException {
    Template a = Option.newTemplate().shortName("a").build();
    Group g = Option.newGroup().required().template(a).build();

    Descriptor.Builder builder = Command.newDescriptor();
    Descriptor descriptor = builder.groups(g).build();

    GnuParser parser = new GnuParser(descriptor);
    parser.parse(new String[0]);
  }

  @Test
  public void argument() throws ParseException {
    Descriptor.Builder builder = Command.newDescriptor();
    Descriptor descriptor = builder.maxArgs(1).build();

    GnuParser parser = new GnuParser(descriptor);
    Command cmd = parser.parse(new String[]{"1"});

    assertThat(cmd.getArgument(0), is("1"));
    assertThat(cmd.argumentCount(), is(1));
  }

  @Test(expected = MissingArgumentException.class)
  public void missingArgument() throws ParseException {
    Descriptor.Builder builder = Command.newDescriptor();
    Descriptor descriptor = builder.minArgs(3).maxArgs(3).build();

    GnuParser parser = new GnuParser(descriptor);
    parser.parse(new String[]{"1", "2"});
  }

  @Test
  public void firstArgumentHasDash() throws ParseException {
    Descriptor.Builder builder = Command.newDescriptor();
    Descriptor descriptor = builder.maxArgs(1).build();

    GnuParser parser = new GnuParser(descriptor);
    Command cmd = parser.parse(new String[]{"-a"});

    // This only works because there are no templates.
    assertThat(cmd.getArgument(0), is("-a"));
  }

  @Test
  public void arguments() throws ParseException {
    Descriptor.Builder builder = Command.newDescriptor();
    Descriptor descriptor = builder.maxArgs(2).build();

    GnuParser parser = new GnuParser(descriptor);
    Command cmd = parser.parse(new String[]{"1", "2"});

    assertThat(cmd.getArgument(0), is("1"));
    assertThat(cmd.getArgument(1), is("2"));
    assertThat(cmd.argumentCount(), is(2));
  }

  @Test(expected = TooManyArgumentsException.class)
  public void tooManyArguments() throws ParseException {
    Descriptor.Builder builder = Command.newDescriptor();
    Descriptor descriptor = builder.maxArgs(1).build();

    GnuParser parser = new GnuParser(descriptor);
    parser.parse(new String[]{"foo", "bar"});
  }

  @Test(expected = UnrecognizedTokenException.class)
  public void singleDash() throws ParseException {
    Template a = Option.newTemplate().shortName("a").build();

    Descriptor.Builder builder = Command.newDescriptor();
    Descriptor descriptor = builder.templates(a).build();

    GnuParser parser = new GnuParser(descriptor);
    parser.parse(new String[]{"-"});
  }

  @Test
  public void doubleDash() throws ParseException {
    Descriptor.Builder builder = Command.newDescriptor();
    Descriptor descriptor = builder.maxArgs(1).build();

    GnuParser parser = new GnuParser(descriptor);
    Command cmd = parser.parse(new String[]{"--"});

    assertThat(cmd.hasOption(""), is(false));
    assertThat(cmd.argumentCount(), is(0));
  }

  @Test
  public void doubleDashSeparator() throws ParseException {
    Template a = Option.newTemplate().shortName("a").build();
    Template b = Option.newTemplate().shortName("b").build();

    Descriptor.Builder builder = Command.newDescriptor();
    Descriptor descriptor = builder.templates(a, b).maxArgs(2).build();

    GnuParser parser = new GnuParser(descriptor);
    Command cmd = parser.parse(new String[]{"-a", "--", "-b", "foo"});

    assertThat(cmd.hasOption("a"), is(true));
    assertThat(cmd.hasOption("b"), is(false));

    assertThat(cmd.getArgument(0), is("-b"));
    assertThat(cmd.getArgument(1), is("foo"));
  }

  @Test(expected = UnrecognizedTokenException.class)
  public void tripleDash() throws ParseException {
    Template a = Option.newTemplate().shortName("a").build();

    Descriptor.Builder builder = Command.newDescriptor();
    Descriptor descriptor = builder.templates(a).build();

    GnuParser parser = new GnuParser(descriptor);
    parser.parse(new String[]{"---"});
  }
}
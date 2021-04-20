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
package com.nepherte.commons.cli.parser;

import com.nepherte.commons.cli.Command;
import com.nepherte.commons.cli.Command.Descriptor;
import com.nepherte.commons.cli.Option;
import com.nepherte.commons.cli.Option.Group;
import com.nepherte.commons.cli.Option.Template;
import com.nepherte.commons.cli.exception.ParseException;

import com.nepherte.commons.cli.exception.ExclusiveOptionsException;
import com.nepherte.commons.cli.exception.MissingArgumentException;
import com.nepherte.commons.cli.exception.MissingGroupException;
import com.nepherte.commons.cli.exception.MissingOptionException;
import com.nepherte.commons.cli.exception.MissingValueException;
import com.nepherte.commons.cli.exception.TooManyArgumentsException;
import com.nepherte.commons.cli.exception.TooManyValuesException;
import com.nepherte.commons.cli.exception.UnrecognizedTokenException;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.collection.IsIterableContainingInOrder.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test that covers the option format of a Gnu parser.
 */
class GnuParserTest {

  @Test
  void shortOption() throws ParseException {
    Template a = Option.newTemplate().shortName("a").build();

    Descriptor.Builder builder = Command.newDescriptor();
    Descriptor descriptor = builder.templates(a).build();

    GnuParser parser = new GnuParser(descriptor);
    Command cmd = parser.parse(new String[]{"-a"});

    assertThat(cmd.hasOption("a"), is(true));
  }

  @Test
  void longOption() throws ParseException {
    Template a = Option.newTemplate().longName("enable-a").build();

    Descriptor.Builder builder = Command.newDescriptor();
    Descriptor descriptor = builder.templates(a).build();

    GnuParser parser = new GnuParser(descriptor);
    Command cmd = parser.parse(new String[]{"--enable-a"});

    assertThat(cmd.hasOption("enable-a"), is(true));
  }

  @Test
  void requiredOption() throws ParseException {
    Template a = Option.newTemplate().shortName("a").required().build();

    Descriptor.Builder builder = Command.newDescriptor();
    Descriptor descriptor = builder.templates(a).build();

    GnuParser parser = new GnuParser(descriptor);
    Command cmd = parser.parse(new String[]{"-a"});

    assertThat(cmd.hasOption("a"), is(true));
  }

  @Test
  void missingOption() throws ParseException {
    Template a = Option.newTemplate().shortName("a").required().build();

    Descriptor.Builder builder = Command.newDescriptor();
    Descriptor descriptor = builder.template(a).build();

    GnuParser parser = new GnuParser(descriptor);
    assertThrows(MissingOptionException.class,
      () -> parser.parse(new String[0]));
  }

  @Test
  void unrecognizedOption() throws ParseException {
    Template a = Option.newTemplate().shortName("a").build();

    Descriptor.Builder builder = Command.newDescriptor();
    Descriptor descriptor = builder.templates(a).build();

    GnuParser parser = new GnuParser(descriptor);
    assertThrows(UnrecognizedTokenException.class,
      () -> parser.parse(new String[]{"-b"}));
  }

  @Test
  void unrecognizedOptionWithValues() throws ParseException {
    Template a = Option.newTemplate().shortName("a").build();

    Descriptor.Builder builder = Command.newDescriptor();
    Descriptor descriptor = builder.templates(a).build();

    GnuParser parser = new GnuParser(descriptor);
    assertThrows(UnrecognizedTokenException.class,
      () -> parser.parse(new String[]{"-unknown=value"}));
  }

  @Test
  void optionWithMissingValue() throws ParseException {
    Template a = Option.newTemplate().shortName("a")
      .minValues(1).maxValues(1).build();

    Descriptor.Builder builder = Command.newDescriptor();
    Descriptor descriptor = builder.templates(a).build();

    GnuParser parser = new GnuParser(descriptor);
    assertThrows(MissingValueException.class,
      () -> parser.parse(new String[]{"-a"}));
  }

  @Test
  void optionWithTooManyDashes() throws ParseException {
    Template a = Option.newTemplate().shortName("a").build();

    Descriptor.Builder builder = Command.newDescriptor();
    Descriptor descriptor = builder.templates(a).build();

    GnuParser parser = new GnuParser(descriptor);
    assertThrows(UnrecognizedTokenException.class,
      () -> parser.parse(new String[]{"--a"}));
  }

  @Test
  void optionWithUnexpectedValues() throws ParseException {
    Template a = Option.newTemplate().shortName("a").maxValues(0).build();

    Descriptor.Builder builder = Command.newDescriptor();
    Descriptor descriptor = builder.templates(a).build();

    GnuParser parser = new GnuParser(descriptor);
    assertThrows(TooManyValuesException.class,
      () -> parser.parse(new String[]{"-a=1"}));
  }

  @Test
  void optionWithTooManyValues() throws ParseException {
    Template a = Option.newTemplate().shortName("a").maxValues(1).build();

    Descriptor.Builder builder = Command.newDescriptor();
    Descriptor descriptor = builder.templates(a).build();

    GnuParser parser = new GnuParser(descriptor);
    assertThrows(TooManyValuesException.class,
      () -> parser.parse(new String[]{"-a=1,2"}));
  }

  @Test
  void optionWithTooManyValueSeparators() throws ParseException {
    Template a = Option.newTemplate().shortName("a").maxValues(1).build();

    Descriptor.Builder builder = Command.newDescriptor();
    Descriptor descriptor = builder.templates(a).build();

    GnuParser parser = new GnuParser(descriptor);
    assertThrows(UnrecognizedTokenException.class,
      () -> parser.parse(new String[]{"-a=foo=bar"}));
  }

  @Test
  void optionWithValue() throws ParseException {
    Template a = Option.newTemplate().shortName("a").maxValues(1).build();

    Descriptor.Builder builder = Command.newDescriptor();
    Descriptor descriptor = builder.templates(a).build();

    GnuParser parser = new GnuParser(descriptor);
    Command cmd = parser.parse(new String[]{"-a=1"});

    assertThat(cmd.getOptionValue("a"), is("1"));
    assertThat(cmd.getOptionValues("a"), contains("1"));
  }

  @Test
  void optionWithValues() throws ParseException {
    Template a = Option.newTemplate().shortName("a").maxValues(2).build();

    Descriptor.Builder builder = Command.newDescriptor();
    Descriptor descriptor = builder.templates(a).build();

    GnuParser parser = new GnuParser(descriptor);
    Command cmd = parser.parse(new String[]{"-a=1,2"});

    assertThat(cmd.getOptionValues("a"), contains("1", "2"));
  }

  @Test
  void optionValueWithHyphen() throws ParseException {
    Template a = Option.newTemplate().shortName("a").maxValues(1).build();

    Descriptor.Builder builder = Command.newDescriptor();
    Descriptor descriptor = builder.templates(a).build();

    GnuParser parser = new GnuParser(descriptor);
    Command cmd = parser.parse(new String[]{"-a=-foo"});

    assertThat(cmd.hasOption("foo"), is(false));
    assertThat(cmd.getOptionValue("a"), is("-foo"));
  }

  @Test
  void optionValueIsAlsoAnOption() throws ParseException {
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
  void optionValueIsDoubleDash() throws ParseException {
    Template a = Option.newTemplate().shortName("a").maxValues(1).build();

    Descriptor.Builder builder = Command.newDescriptor();
    Descriptor descriptor = builder.templates(a).build();

    GnuParser parser = new GnuParser(descriptor);
    Command cmd = parser.parse(new String[]{"-a=--"});

    assertThat(cmd.getOptionValue("a"), is("--"));
  }

  @Test
  void options() throws ParseException {
    Template a = Option.newTemplate().shortName("a").build();
    Template b = Option.newTemplate().longName("b").build();

    Descriptor.Builder builder = Command.newDescriptor();
    Descriptor descriptor = builder.templates(a, b).build();

    GnuParser parser = new GnuParser(descriptor);
    Command cmd = parser.parse(new String[]{"-a", "--b"});

    assertThat(cmd.hasOption("a"), is(true));
    assertThat(cmd.hasOption("b"), is(true));
  }

  @Test
  void exclusiveOptions() throws ParseException {
    Template a = Option.newTemplate().shortName("a").build();
    Template b = Option.newTemplate().shortName("b").build();

    Group.Builder gb = Option.newGroup().required();
    Group g = gb.templates(a, b).build();

    Descriptor.Builder builder = Command.newDescriptor();
    Descriptor descriptor = builder.groups(g).build();

    GnuParser parser = new GnuParser(descriptor);
    assertThrows(ExclusiveOptionsException.class,
      () -> parser.parse(new String[]{"-a", "-b"}));
  }

  @Test
  void group() throws ParseException {
    Template a = Option.newTemplate().shortName("a").build();
    Group group = Option.newGroup().template(a).build();

    Descriptor.Builder builder = Command.newDescriptor();
    Descriptor descriptor = builder.groups(group).build();

    GnuParser parser = new GnuParser(descriptor);
    Command cmd = parser.parse(new String[]{"-a"});

    assertThat(cmd.hasOption("a"), is(true));
  }

  @Test
  void requiredGroup() throws ParseException {
    Template a = Option.newTemplate().shortName("a").build();
    Group g = Option.newGroup().required().template(a).build();

    Descriptor.Builder builder = Command.newDescriptor();
    Descriptor descriptor = builder.groups(g).build();

    GnuParser parser = new GnuParser(descriptor);
    Command cmd = parser.parse(new String[]{"-a"});

    assertThat(cmd.hasOption("a"), is(true));
  }

  @Test
  void missingGroup() throws ParseException {
    Template a = Option.newTemplate().shortName("a").build();
    Group g = Option.newGroup().required().template(a).build();

    Descriptor.Builder builder = Command.newDescriptor();
    Descriptor descriptor = builder.groups(g).build();

    GnuParser parser = new GnuParser(descriptor);
    assertThrows(MissingGroupException.class,
      () -> parser.parse(new String[0]));
  }

  @Test
  void argument() throws ParseException {
    Descriptor.Builder builder = Command.newDescriptor();
    Descriptor descriptor = builder.maxArgs(1).build();

    GnuParser parser = new GnuParser(descriptor);
    Command cmd = parser.parse(new String[]{"1"});

    assertThat(cmd.getArgument(0), is("1"));
    assertThat(cmd.argumentCount(), is(1));
  }

  @Test
  void missingArgument() throws ParseException {
    Descriptor.Builder builder = Command.newDescriptor();
    Descriptor descriptor = builder.minArgs(3).maxArgs(3).build();

    GnuParser parser = new GnuParser(descriptor);
    assertThrows(MissingArgumentException.class,
      () -> parser.parse(new String[]{"1", "2"}));
  }

  @Test
  void firstArgumentHasDash() throws ParseException {
    Descriptor.Builder builder = Command.newDescriptor();
    Descriptor descriptor = builder.maxArgs(1).build();

    GnuParser parser = new GnuParser(descriptor);
    Command cmd = parser.parse(new String[]{"-a"});

    // This only works because there are no templates.
    assertThat(cmd.getArgument(0), is("-a"));
  }

  @Test
  void arguments() throws ParseException {
    Descriptor.Builder builder = Command.newDescriptor();
    Descriptor descriptor = builder.maxArgs(2).build();

    GnuParser parser = new GnuParser(descriptor);
    Command cmd = parser.parse(new String[]{"1", "2"});

    assertThat(cmd.getArgument(0), is("1"));
    assertThat(cmd.getArgument(1), is("2"));
    assertThat(cmd.argumentCount(), is(2));
  }

  @Test
  void tooManyArguments() throws ParseException {
    Descriptor.Builder builder = Command.newDescriptor();
    Descriptor descriptor = builder.maxArgs(1).build();

    GnuParser parser = new GnuParser(descriptor);
    assertThrows(TooManyArgumentsException.class,
      () -> parser.parse(new String[]{"foo", "bar"}));
  }

  @Test
  void singleDash() throws ParseException {
    Descriptor.Builder builder = Command.newDescriptor();
    Descriptor descriptor = builder.maxArgs(1).build();

    GnuParser parser = new GnuParser(descriptor);
    Command cmd = parser.parse(new String[]{"-"});

    assertThat(cmd.hasOption(""), is(false));
    assertThat(cmd.argumentCount(), is(1));
    assertThat(cmd.getArgument(0), is("-"));
  }

  @Test
  void doubleDash() throws ParseException {
    Descriptor.Builder builder = Command.newDescriptor();
    Descriptor descriptor = builder.maxArgs(1).build();

    GnuParser parser = new GnuParser(descriptor);
    Command cmd = parser.parse(new String[]{"--"});

    assertThat(cmd.hasOption(""), is(false));
    assertThat(cmd.argumentCount(), is(0));
  }

  @Test
  void doubleDashSeparator() throws ParseException {
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

  @Test
  void tripleDash() throws ParseException {
    Template a = Option.newTemplate().shortName("a").build();

    Descriptor.Builder builder = Command.newDescriptor();
    Descriptor descriptor = builder.templates(a).build();

    GnuParser parser = new GnuParser(descriptor);
    assertThrows(UnrecognizedTokenException.class,
      () -> parser.parse(new String[]{"---"}));
  }
}
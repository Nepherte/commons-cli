/*
 * Copyright 2012-2021 Bart Verhoeven
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
import com.nepherte.commons.cli.Option;
import com.nepherte.commons.cli.Parser;
import com.nepherte.commons.cli.exception.*;

import com.nepherte.commons.cli.internal.OptionFormat;
import com.nepherte.commons.cli.internal.OptionFormats;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static com.nepherte.commons.cli.internal.OptionFormat.*;
import static com.nepherte.commons.test.Matchers.*;

import static org.hamcrest.MatcherAssert.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test that covers generic features of an option style.
 */
class GenericParserTest {

  /** Name of method that provides all formats */
  private static final String ALL_OPTION_FORMATS = "allOptionFormats";
  /** Name of method that provides all formats that support short options. */
  private static final String SHORT_OPTION_FORMATS = "shortOptionFormats";
  /** Name of method that provides all formats that support long options. */
  private static final String LONG_OPTION_FORMATS = "longOptionFormats";
  /** Name of method that provides all formats that support group options. */
  private static final String GROUP_OPTION_FORMATS = "groupOptionFormats";
  /** Name of method that provides all formats that supports arguments. */
  private static final String OPTION_FORMATS_WITH_ARGS = "optionFormatsWithArgs";

  // short options - no values

  @ParameterizedTest @MethodSource(SHORT_OPTION_FORMATS)
  void shortOption(OptionFormat optionFormat) throws ParseException {
    Option.Template a = Option.newTemplate().shortName("a").build();

    Command.Descriptor.Builder builder = Command.newDescriptor();
    Command.Descriptor descriptor = builder.templates(a).build();

    for (String tokens : optionFormat.shortOptionFor("a")) {
      Parser parser = optionFormat.parserFor(descriptor);
      Command cmd = parser.parse(tokens.split(" "));

      assertThat(cmd, hasOptionCount(1));
      assertThat(cmd, hasArgumentCount(0));
      assertThat(cmd, hasOption("a"));
    }
  }

  @ParameterizedTest @MethodSource(SHORT_OPTION_FORMATS)
  void shortOptions(OptionFormat optionFormat) throws ParseException {
    Option.Template a = Option.newTemplate().shortName("a").build();
    Option.Template b = Option.newTemplate().shortName("b").build();

    Command.Descriptor.Builder builder = Command.newDescriptor();
    Command.Descriptor descriptor = builder.templates(a, b).build();

    for (String tokens : optionFormat.shortOptionsFor("a", "b")) {
      Parser parser = optionFormat.parserFor(descriptor);
      Command cmd = parser.parse(tokens.split(" "));

      assertThat(cmd, hasOptionCount(2));
      assertThat(cmd, hasArgumentCount(0));

      assertThat(cmd, hasOption("a"));
      assertThat(cmd, hasOption("b"));
    }
  }

  @ParameterizedTest @MethodSource(SHORT_OPTION_FORMATS)
  void requiredShortOption(OptionFormat optionFormat) throws ParseException {
    Option.Template a = Option.newTemplate().shortName("a").required().build();

    Command.Descriptor.Builder builder = Command.newDescriptor();
    Command.Descriptor descriptor = builder.templates(a).build();

    for (String tokens : optionFormat.shortOptionFor("a")) {
      Parser parser = optionFormat.parserFor(descriptor);
      Command cmd = parser.parse(tokens.split(" "));

      assertThat(cmd, hasOptionCount(1));
      assertThat(cmd, hasArgumentCount(0));
      assertThat(cmd, hasOption("a"));
    }
  }

  @ParameterizedTest @MethodSource(SHORT_OPTION_FORMATS)
  void missingShortOption(OptionFormat optionFormat) {
    Option.Template a = Option.newTemplate().shortName("a").required().build();

    Command.Descriptor.Builder builder = Command.newDescriptor();
    Command.Descriptor descriptor = builder.template(a).build();

    assertThrows(MissingOptionException.class, () -> {
      Parser parser = optionFormat.parserFor(descriptor);
      parser.parse(new String[0]);
    });
  }

  @ParameterizedTest @MethodSource(SHORT_OPTION_FORMATS)
  void unrecognizedShortOption(OptionFormat optionFormat) {
    Option.Template a = Option.newTemplate().shortName("a").build();

    Command.Descriptor.Builder builder = Command.newDescriptor();
    Command.Descriptor descriptor = builder.templates(a).build();

    for (String tokens : optionFormat.shortOptionFor("b")) {
      assertThrows(UnrecognizedTokenException.class, () -> {
        Parser parser = optionFormat.parserFor(descriptor);
        parser.parse(tokens.split(" "));
      });
    }
  }

  // short options - single value

  @ParameterizedTest @MethodSource(SHORT_OPTION_FORMATS)
  void shortOptionWithValue(OptionFormat optionFormat) throws ParseException {
    Option.Template a = Option.newTemplate()
      .shortName("a").maxValues(1).build();

    Command.Descriptor.Builder builder = Command.newDescriptor();
    Command.Descriptor descriptor = builder.templates(a).build();

    for (String tokens : optionFormat.shortOptionFor("a", "1")) {
      Parser parser = optionFormat.parserFor(descriptor);
      Command cmd = parser.parse(tokens.split(" "));

      assertThat(cmd, hasOptionCount(1));
      assertThat(cmd, hasArgumentCount(0));
      assertThat(cmd, hasOption("a").withValue("1"));
    }
  }

  @ParameterizedTest @MethodSource(SHORT_OPTION_FORMATS)
  void shortOptionWithMissingValue(OptionFormat optionFormat) {
    Option.Template a = Option.newTemplate().shortName("a")
      .minValues(1).maxValues(1).build();

    Command.Descriptor.Builder builder = Command.newDescriptor();
    Command.Descriptor descriptor = builder.templates(a).build();

    for (String tokens : optionFormat.shortOptionFor("a")) {
      assertThrows(MissingValueException.class, () -> {
        Parser parser = optionFormat.parserFor(descriptor);
        parser.parse(tokens.split(" "));
      });
    }
  }

  // long options - no values

  @ParameterizedTest @MethodSource(LONG_OPTION_FORMATS)
  void longOption(OptionFormat optionFormat) throws ParseException {
    Option.Template a = Option.newTemplate().longName("enable-a").build();

    Command.Descriptor.Builder builder = Command.newDescriptor();
    Command.Descriptor descriptor = builder.templates(a).build();

    for (String tokens : optionFormat.longOptionFor("enable-a")) {
      Parser parser = optionFormat.parserFor(descriptor);
      Command cmd = parser.parse(tokens.split(" "));

      assertThat(cmd, hasOptionCount(1));
      assertThat(cmd, hasArgumentCount(0));
      assertThat(cmd, hasOption("enable-a"));
    }
  }

  @ParameterizedTest @MethodSource(LONG_OPTION_FORMATS)
  void longOptions(OptionFormat optionFormat) throws ParseException {
    Option.Template a = Option.newTemplate().longName("enable-a").build();
    Option.Template b = Option.newTemplate().longName("enable-b").build();

    Command.Descriptor.Builder builder = Command.newDescriptor();
    Command.Descriptor descriptor = builder.templates(a, b).build();

    for (String tokens : optionFormat.longOptionsFor("enable-a", "enable-b")) {
      Parser parser = optionFormat.parserFor(descriptor);
      Command cmd = parser.parse(tokens.split(" "));

      assertThat(cmd, hasOptionCount(2));
      assertThat(cmd, hasArgumentCount(0));

      assertThat(cmd, hasOption("enable-a"));
      assertThat(cmd, hasOption("enable-b"));
    }
  }

  // groups

  @ParameterizedTest @MethodSource(GROUP_OPTION_FORMATS)
  void group(OptionFormat optionFormat) throws ParseException {
    Option.Template a = Option.newTemplate().shortName("a").build();
    Option.Template b = Option.newTemplate().shortName("b").build();
    Option.Group group = Option.newGroup().templates(a, b).build();

    Command.Descriptor.Builder builder = Command.newDescriptor();
    Command.Descriptor descriptor = builder.groups(group).build();

    for (String tokens : optionFormat.shortOptionFor("a")) {
      Parser parser = optionFormat.parserFor(descriptor);
      Command cmd = parser.parse(tokens.split(" "));

      assertThat(cmd, hasOptionCount(1));
      assertThat(cmd, hasArgumentCount(0));
      assertThat(cmd, hasOption("a"));
    }
  }

  @ParameterizedTest @MethodSource(GROUP_OPTION_FORMATS)
  void requiredGroup(OptionFormat optionFormat) throws ParseException {
    Option.Template a = Option.newTemplate().shortName("a").build();
    Option.Template b = Option.newTemplate().shortName("b").build();
    Option.Group g = Option.newGroup().required().templates(a, b).build();

    Command.Descriptor.Builder builder = Command.newDescriptor();
    Command.Descriptor descriptor = builder.groups(g).build();

    for (String tokens : optionFormat.shortOptionFor("a")) {
      Parser parser = optionFormat.parserFor(descriptor);
      Command cmd = parser.parse(tokens.split(" "));

      assertThat(cmd, hasOptionCount(1));
      assertThat(cmd, hasOption("a"));
    }
  }

  @ParameterizedTest @MethodSource(GROUP_OPTION_FORMATS)
  void missingGroup(OptionFormat optionFormat) {
    Option.Template a = Option.newTemplate().shortName("a").build();
    Option.Group g = Option.newGroup().required().template(a).build();

    Command.Descriptor.Builder builder = Command.newDescriptor();
    Command.Descriptor descriptor = builder.groups(g).build();

    assertThrows(MissingGroupException.class, () -> {
      Parser parser = optionFormat.parserFor(descriptor);
      parser.parse(new String[0]);
    });
  }

  @ParameterizedTest @MethodSource(GROUP_OPTION_FORMATS)
  void exclusiveGroup(OptionFormat optionFormat) {
    Option.Template a = Option.newTemplate().shortName("a").build();
    Option.Template b = Option.newTemplate().shortName("b").build();

    Option.Group.Builder gb = Option.newGroup().required();
    Option.Group g = gb.templates(a, b).build();

    Command.Descriptor.Builder builder = Command.newDescriptor();
    Command.Descriptor descriptor = builder.groups(g).build();

    for (String tokens : optionFormat.shortOptionsFor("a", "b")) {
      assertThrows(ExclusiveOptionsException.class, () -> {
        Parser parser = optionFormat.parserFor(descriptor);
        parser.parse(tokens.split(" "));
      });
    }
  }

  // argument(s)

  @ParameterizedTest @MethodSource(OPTION_FORMATS_WITH_ARGS)
  void argument(OptionFormat optionFormat) throws ParseException {
    Command.Descriptor.Builder builder = Command.newDescriptor();
    Command.Descriptor descriptor = builder.maxArgs(1).build();

    for (String tokens : optionFormat.argumentsFor("1")) {
      Parser parser = optionFormat.parserFor(descriptor);
      Command cmd = parser.parse(tokens.split(" "));

      assertThat(cmd, hasOptionCount(0));
      assertThat(cmd, hasArgumentCount(1));
      assertThat(cmd, hasArgument("1"));
    }
  }

  @ParameterizedTest @MethodSource(OPTION_FORMATS_WITH_ARGS)
  void arguments(OptionFormat optionFormat) throws ParseException {
    Command.Descriptor.Builder builder = Command.newDescriptor();
    Command.Descriptor descriptor = builder.maxArgs(2).build();

    for (String tokens : optionFormat.argumentsFor("1", "2")) {
      Parser parser = optionFormat.parserFor(descriptor);
      Command cmd = parser.parse(tokens.split(" "));

      assertThat(cmd, hasOptionCount(0));
      assertThat(cmd, hasArgumentCount(2));

      assertThat(cmd, hasArgument("1").atIndex(0));
      assertThat(cmd, hasArgument("2").atIndex(1));
    }
  }

  @ParameterizedTest @MethodSource(OPTION_FORMATS_WITH_ARGS)
  void missingArgument(OptionFormat optionFormat) {
    Command.Descriptor.Builder builder = Command.newDescriptor();
    Command.Descriptor descriptor = builder.minArgs(3).maxArgs(3).build();

    for (String tokens : optionFormat.argumentsFor("1", "2")) {
      assertThrows(MissingArgumentException.class, () -> {
        Parser parser = optionFormat.parserFor(descriptor);
        parser.parse(tokens.split(" "));
      });
    }
  }

  @ParameterizedTest @MethodSource(OPTION_FORMATS_WITH_ARGS)
  void tooManyArguments(OptionFormat optionFormat) {
    Command.Descriptor.Builder builder = Command.newDescriptor();
    Command.Descriptor descriptor = builder.maxArgs(1).build();

    for (String tokens : optionFormat.argumentsFor("foo", "bar")) {
      assertThrows(TooManyArgumentsException.class, () -> {
        Parser parser = optionFormat.parserFor(descriptor);
        parser.parse(tokens.split(" "));
      });
    }
  }

  // options and arguments

  @ParameterizedTest @MethodSource(OPTION_FORMATS_WITH_ARGS)
  void optionsAndArguments(OptionFormat optionFormat) throws ParseException {
    Option.Template a = Option.newTemplate().shortName("a").build();
    Option.Template b = Option.newTemplate().shortName("b").build();

    Command.Descriptor.Builder builder = Command.newDescriptor();
    Command.Descriptor descriptor = builder.templates(a, b).maxArgs(2).build();

    List<String> optionsAndArgs = optionFormat.optionsAndArgsFor(
      new String[]{"a", "b"},     // short options
      null,                      // long options
      new String[]{"foo", "bar"} // arguments
    );

    for (String tokens : optionsAndArgs) {
      Parser parser = optionFormat.parserFor(descriptor);
      Command cmd = parser.parse(tokens.split(" "));

      assertThat(cmd, hasOptionCount(2));
      assertThat(cmd, hasOption("a"));
      assertThat(cmd, hasOption("b"));

      assertThat(cmd, hasArgumentCount(2));
      assertThat(cmd, hasArgument("foo").atIndex(0));
      assertThat(cmd, hasArgument("bar").atIndex(1));
    }
  }

  // dashes

  @ParameterizedTest @MethodSource(ALL_OPTION_FORMATS)
  void singleDash(OptionFormat optionFormat) throws ParseException {
    Option.Template a = Option.newTemplate().shortName("a").build();

    Command.Descriptor.Builder builder = Command.newDescriptor();
    Command.Descriptor descriptor = builder.template(a).maxArgs(1).build();

    Parser parser = optionFormat.parserFor(descriptor);
    Command cmd = parser.parse(new String[]{"-"});

    assertThat(cmd, hasOptionCount(0));
    assertThat(cmd, hasArgumentCount(1));
    assertThat(cmd, hasArgument("-"));
  }

  @ParameterizedTest @MethodSource(ALL_OPTION_FORMATS)
  void doubleDash(OptionFormat optionFormat) throws ParseException {
    Command.Descriptor.Builder builder = Command.newDescriptor();
    Command.Descriptor descriptor = builder.maxArgs(1).build();

    Parser parser = optionFormat.parserFor(descriptor);
    Command cmd = parser.parse(new String[]{"--"});

    assertThat(cmd, hasNoOptions());
    assertThat(cmd, hasNoArguments());
  }

  // parameterized tests - input data

  @SuppressWarnings("unused") // used by parameterized tests for input
  private static Stream<OptionFormat> allOptionFormats() {
    return OptionFormats.all();
  }

  @SuppressWarnings("unused") // used by parameterized tests for input
  private static Stream<OptionFormat> shortOptionFormats() {
    return OptionFormats.withFeatures(SHORT_OPTION_FEATURE);
  }

  @SuppressWarnings("unused") // used by parameterized tests for input
  private static Stream<OptionFormat> longOptionFormats() {
    return OptionFormats.withFeatures(LONG_OPTION_FEATURE);
  }

  @SuppressWarnings("unused") // used by parameterized tests for input
  private static Stream<OptionFormat> groupOptionFormats() {
    return OptionFormats.withFeatures(GROUP_OPTION_FEATURE);
  }

  @SuppressWarnings("unused") // used by parameterized tests for input
  private static Stream<OptionFormat> optionFormatsWithArgs() {
    return OptionFormats.withFeatures(ARGUMENT_FEATURE);
  }
}

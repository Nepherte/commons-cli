/*
 * Copyright 2012-2019 Bart Verhoeven
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

//import java.util.Comparator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test that covers {@link Formatter}.
 */
class FormatterTest {
//
  private static final Charset CHARSET = StandardCharsets.UTF_8;
  private static final String NEW_LINE = System.lineSeparator();

  private ByteArrayOutputStream byteStream;
  private PrintWriter writer;

  @BeforeEach
  void createStreams() throws IOException {
    byteStream = new ByteArrayOutputStream();
    writer = new PrintWriter(new OutputStreamWriter(byteStream, CHARSET), true);
  }

  @AfterEach
  void closeStreams() throws IOException {
    writer.close();
  }

  @Test
  void printUsageWithCommandName() {
    Command.Descriptor descriptor = Command
      .newDescriptor().name("app").build();

    Formatter formatter = new Formatter();
    formatter.printUsage(writer, descriptor);

    String expected = "Usage: app" + NEW_LINE;
    assertThat(getPrinted(), is(expected));
  }

  @Test
  void printUsageWithShortOption() {
    Option.Template a = Option
      .newTemplate().shortName("a").build();

    Command.Descriptor descriptor = Command
      .newDescriptor().name("app").templates(a).build();

    Formatter formatter = new Formatter();
    formatter.printUsage(writer, descriptor);

    String expected = "Usage: app [-a]" + NEW_LINE;
    assertThat(expected, is(getPrinted()));
  }

  @Test
  void printUsageWithLongOption() {
    Option.Template a = Option
      .newTemplate().longName("a").build();

    Command.Descriptor descriptor = Command
      .newDescriptor().name("app").templates(a).build();

    Formatter formatter = new Formatter();
    formatter.printUsage(writer, descriptor);

    String expected = "Usage: app [--a]" + NEW_LINE;
    assertThat(getPrinted(), is(expected));
  }

  @Test
  void printUsageWithRequiredOption() {
    Option.Template a = Option.newTemplate()
      .required().shortName("a").build();

    Command.Descriptor descriptor = Command
      .newDescriptor().name("app").templates(a).build();

    Formatter formatter = new Formatter();
    formatter.printUsage(writer, descriptor);

    String expected = "Usage: app -a" + NEW_LINE;
    assertThat(getPrinted(), is(expected));
  }

  @Test
  void printUsageWithValueName() {
    Option.Template a = Option.newTemplate()
      .maxValues(1).valueName("<arg>").shortName("a").build();

    Command.Descriptor descriptor = Command
      .newDescriptor().name("app").templates(a).build();

    Formatter formatter = new Formatter();
    formatter.printUsage(writer, descriptor);

    String expected = "Usage: app [-a <arg>]" + NEW_LINE;
    assertThat(getPrinted(), is(expected));
  }

  @Test
  void printUsageWithDefaultValueName() {
    Option.Template a = Option.newTemplate()
      .maxValues(1).shortName("a").build();

    Command.Descriptor descriptor = Command
      .newDescriptor().name("app").templates(a).build();

    Formatter formatter = new Formatter();
    formatter.printUsage(writer, descriptor);

    String expected = "Usage: app [-a <ARG>]" + NEW_LINE;
    assertThat(getPrinted(), is(expected));
  }

  @Test
  void printUsageWithEmptyDefaultValueName() {
    Option.Template a = Option.newTemplate()
      .maxValues(1).shortName("a").build();

    Command.Descriptor descriptor = Command
      .newDescriptor().name("app").templates(a).build();

    Formatter formatter = new Formatter();
    formatter.setOptionValueName("");
    formatter.printUsage(writer, descriptor);

    String expected = "Usage: app [-a]" + NEW_LINE;
    assertThat(getPrinted(), is(expected));
  }

  @Test
  void printUsageWithCustomDefaultValueName() {
    Option.Template a = Option.newTemplate()
      .maxValues(1).shortName("a").build();

    Command.Descriptor descriptor = Command
      .newDescriptor().name("app").templates(a).build();

    Formatter formatter = new Formatter();
    formatter.setOptionValueName("<arg>");
    formatter.printUsage(writer, descriptor);

    String expected = "Usage: app [-a <arg>]" + NEW_LINE;
    assertThat(getPrinted(), is(expected));
  }

  @Test
  void printUsageWithRequiredArgs() {
    Command.Descriptor descriptor = Command.newDescriptor()
      .name("app").minArgs(1).maxArgs(1).build();

    Formatter formatter = new Formatter();
    formatter.printUsage(writer, descriptor);

    String expected = "Usage: app <ARG>" + NEW_LINE;
    assertThat(getPrinted(), is(expected));
  }

  @Test
  void printUsageWithOptionalArgs() {
    Command.Descriptor descriptor = Command
      .newDescriptor().name("app").maxArgs(1).build();

    Formatter formatter = new Formatter();
    formatter.printUsage(writer, descriptor);

    String expected = "Usage: app [<ARG>]" + NEW_LINE;
    assertThat(getPrinted(), is(expected));
  }

  @Test
  void printUsageWithCustomArgName() {
    Command.Descriptor descriptor = Command
      .newDescriptor().name("app").minArgs(1).maxArgs(1).build();

    Formatter formatter = new Formatter();
    formatter.setArgumentName("<arg>");
    formatter.printUsage(writer, descriptor);

    String expected = "Usage: app <arg>" + NEW_LINE;
    assertThat(getPrinted(), is(expected));
  }

  @Test
  void printUsageWithEmptyDefaultArgName() {
    Command.Descriptor descriptor = Command
      .newDescriptor().name("app").maxArgs(1).build();

    Formatter formatter = new Formatter();
    formatter.setArgumentName("");
    formatter.printUsage(writer, descriptor);

    String expected = "Usage: app" + NEW_LINE;
    assertThat(getPrinted(), is(expected));
  }

  @Test
  void printUsageWithCustomShortOptionPrefix() {
    Option.Template a = Option
      .newTemplate().shortName("a").build();

    Command.Descriptor descriptor = Command
      .newDescriptor().name("app").templates(a).build();

    Formatter formatter = new Formatter();
    formatter.setShortOptionPrefix("*");
    formatter.printUsage(writer, descriptor);

    String expected = "Usage: app [*a]" + NEW_LINE;
    assertThat(getPrinted(), is(expected));
  }

  @Test
  void printUsageWithCustomLongOptionPrefix() {
    Option.Template a = Option
      .newTemplate().longName("a").build();

    Command.Descriptor descriptor = Command
      .newDescriptor().name("app").templates(a).build();

    Formatter formatter = new Formatter();
    formatter.setLongOptionPrefix("**");
    formatter.printUsage(writer, descriptor);

    String expected = "Usage: app [**a]" + NEW_LINE;
    assertThat(getPrinted(), is(expected));
  }

  @Test
  void printUsageWithCustomValueSeparator() {
    Option.Template a = Option.newTemplate()
      .maxValues(1).shortName("a").build();

    Command.Descriptor descriptor = Command
      .newDescriptor().name("app").templates(a).build();

    Formatter formatter = new Formatter();
    formatter.setOptionValueSeparator("=");
    formatter.printUsage(writer, descriptor);

    String expected = "Usage: app [-a=<ARG>]" + NEW_LINE;
    assertThat(getPrinted(), is(expected));
  }

  @Test
  void printUsageWithCustomSyntax() {
    new Formatter().printUsage(writer, "app [OPT...]");
    String expected = "Usage: app [OPT...]" + NEW_LINE;
    assertThat(getPrinted(), is(expected));
  }

  @Test
  void printUsageWithCustomSyntaxPrefix() {
    Command.Descriptor descriptor = Command
      .newDescriptor().name("app").build();

    Formatter formatter = new Formatter();
    formatter.setUsagePrefix("Custom:");
    formatter.printUsage(writer, descriptor);

    String expected = "Custom: app" + NEW_LINE;
    assertThat(getPrinted(), is(expected));
  }

  @Test
  void printUsageWithCustomComparator() {
    Option.Template a = Option
      .newTemplate().shortName("a").build();

    Option.Template b = Option
      .newTemplate().shortName("b").build();

    Command.Descriptor descriptor = Command
      .newDescriptor().name("app").templates(a, b).build();

    Formatter formatter = new Formatter();
    formatter.setOptionComparator(new InverseComparator());
    formatter.printUsage(writer, descriptor);

    String expected = "Usage: app [-b] [-a]" + NEW_LINE;
    assertThat(getPrinted(), is(expected));
  }

  @Test
  void printShortOption() {
    Option.Template a = Option
      .newTemplate().shortName("a").build();

    Command.Descriptor descriptor = Command
      .newDescriptor().templates(a).build();

    Formatter formatter = new Formatter();
    formatter.printOptions(writer, descriptor);

    String expected = " -a" + NEW_LINE;
    assertThat(getPrinted(), is(expected));
  }

  @Test
  void printLongOption() {
    Option.Template a = Option
      .newTemplate().longName("a").build();

    Command.Descriptor descriptor = Command
      .newDescriptor().templates(a).build();

    Formatter formatter = new Formatter();
    formatter.printOptions(writer, descriptor);

    String expected = " --a" + NEW_LINE;
    assertThat(getPrinted(), is(expected));
  }

  @Test
  void printRequiredOption() {
    Option.Template a = Option.newTemplate()
      .required().shortName("a").build();

    Command.Descriptor descriptor = Command
      .newDescriptor().templates(a).build();

    Formatter formatter = new Formatter();
    formatter.printOptions(writer, descriptor);

    String expected = " -a" + NEW_LINE;
    assertThat(getPrinted(), is(expected));
  }

  @Test
  void printOptionWithValue() {
    Option.Template a = Option
      .newTemplate().shortName("a").maxValues(1).build();

    Command.Descriptor descriptor = Command
      .newDescriptor().templates(a).build();

    Formatter formatter = new Formatter();
    formatter.printOptions(writer, descriptor);

    String expected = " -a <ARG>" + NEW_LINE;
    assertThat(getPrinted(), is(expected));
  }

  @Test
  void printOptionWithDescription() {
    Option.Template a = Option.newTemplate()
      .shortName("a").description("first").build();

    Command.Descriptor descriptor = Command
      .newDescriptor().templates(a).build();

    Formatter formatter = new Formatter();
    formatter.printOptions(writer, descriptor);

    String expected = " -a   first" + NEW_LINE;
    assertThat(getPrinted(), is(expected));
  }

  @Test
  void printOptionWithBothNames() {
    Option.Template a = Option.newTemplate()
      .shortName("a").longName("a").build();

    Command.Descriptor descriptor = Command
      .newDescriptor().templates(a).build();

    Formatter formatter = new Formatter();
    formatter.printOptions(writer, descriptor);

    String expected = " -a, --a" + NEW_LINE;
    assertThat(getPrinted(), is(expected));
  }

  @Test
  void printOptionsAligned() {
    Option.Template a = Option.newTemplate()
      .shortName("a").description("first").build();

    Option.Template b = Option.newTemplate()
      .longName("b").description("second").build();

    Command.Descriptor descriptor = Command
      .newDescriptor().templates(a, b).build();

    Formatter formatter = new Formatter();
    formatter.printOptions(writer, descriptor);

    String expected =
      " -a       first"  + NEW_LINE +
      "    --b   second" + NEW_LINE;

    assertThat(getPrinted(), is(expected));
  }

  @Test
  void printShortOptionWithCustomPrefix() {
    Option.Template a = Option
      .newTemplate().shortName("a").build();

    Command.Descriptor descriptor = Command
      .newDescriptor().templates(a).build();

    Formatter formatter = new Formatter();
    formatter.setShortOptionPrefix("*");
    formatter.printOptions(writer, descriptor);

    String expected = " *a" + NEW_LINE;
    assertThat(getPrinted(), is(expected));
  }

  @Test
  void printLongOptionWithCustomPrefix() {
    Option.Template a = Option
      .newTemplate().longName("a").build();

    Command.Descriptor descriptor = Command
      .newDescriptor().templates(a).build();

    Formatter formatter = new Formatter();
    formatter.setLongOptionPrefix("**");
    formatter.printOptions(writer, descriptor);

    String expected = " **a" + NEW_LINE;
    assertThat(getPrinted(), is(expected));
  }

  @Test
  void printOptionWithCustomValueName() {
    Option.Template a = Option.newTemplate()
      .shortName("a").maxValues(1).build();

    Command.Descriptor descriptor = Command
      .newDescriptor().templates(a).build();

    Formatter formatter = new Formatter();
    formatter.setOptionValueName("<arg>");
    formatter.printOptions(writer, descriptor);

    String expected = " -a <arg>" + NEW_LINE;
    assertThat(getPrinted(), is(expected));
  }

  @Test
  void printOptionWithCustomValueSeparator() {
    Option.Template a = Option.newTemplate()
      .shortName("a").maxValues(1).build();

    Command.Descriptor descriptor = Command
      .newDescriptor().templates(a).build();

    Formatter formatter = new Formatter();
    formatter.setOptionValueSeparator("=");
    formatter.printOptions(writer, descriptor);

    String expected = " -a=<ARG>" + NEW_LINE;
    assertThat(getPrinted(), is(expected));
  }

  @Test
  void printOptionWithCustomComparator() {
    Option.Template a = Option
      .newTemplate().shortName("a").build();

    Option.Template b = Option
      .newTemplate().shortName("b").build();

    Command.Descriptor descriptor = Command
      .newDescriptor().templates(a, b).build();

    Formatter formatter = new Formatter();
    formatter.setOptionComparator(new InverseComparator());
    formatter.printOptions(writer, descriptor);

    String expected =
      " -b" + NEW_LINE +
      " -a" + NEW_LINE;

    assertThat(getPrinted(), is(expected));
  }

  @Test
  void printOptionWithCustomPadding() {
    Option.Template a = Option
      .newTemplate().shortName("a").build();

    Command.Descriptor descriptor = Command
      .newDescriptor().templates(a).build();

    Formatter formatter = new Formatter();
    formatter.setOptionPadding(8);
    formatter.printOptions(writer, descriptor);

    String expected = "        -a" + NEW_LINE;
    assertThat(getPrinted(), is(expected));
  }

  @Test
  void printOptionWithCustomDescriptionPadding() {
    Option.Template a = Option.newTemplate()
      .shortName("a").description("first").build();

    Command.Descriptor descriptor = Command
      .newDescriptor().templates(a).build();

    Formatter formatter = new Formatter();
    formatter.setDescriptionPadding(4);
    formatter.printOptions(writer, descriptor);

    String expected = " -a    first" + NEW_LINE;
    assertThat(getPrinted(), is(expected));
  }

  @Test
  void printHelpWithAutoSyntax() {
    Option.Template a = Option.newTemplate()
      .shortName("a").description("first").build();

    Option.Template b = Option.newTemplate()
      .shortName("b").longName("b")
      .description("second").build();

    Option.Template c = Option.newTemplate()
      .shortName("c").maxValues(1).required()
      .description("third").build();

    Command.Descriptor descriptor = Command
      .newDescriptor().name("app").templates(a, b, c).build();

    Formatter formatter = new Formatter();
    formatter.printHelp(writer, descriptor, null, null, null);

    String expected =
      "Usage: app [-a] [-b] -c <ARG>"                  + NEW_LINE
                                                       + NEW_LINE +
      " -a             first"                          + NEW_LINE +
      " -b,      --b   second"                         + NEW_LINE +
      " -c <ARG>       third"                          + NEW_LINE;

    assertThat(getPrinted(), is(expected));
  }

  @Test
  void printHelpWithCustomSyntax() {
    Option.Template a = Option.newTemplate()
      .shortName("a").description("first").build();

    Option.Template b = Option.newTemplate()
      .shortName("b").longName("b")
      .description("second").build();

    Option.Template c = Option.newTemplate()
      .shortName("c").maxValues(1).required()
      .description("third").build();

    Command.Descriptor descriptor = Command
      .newDescriptor().templates(a, b, c).build();

    String syntax = "app [OPTION...]";
    Formatter formatter = new Formatter();
    formatter.printHelp(writer, descriptor, syntax, null, null);

    String expected =
      "Usage: app [OPTION...]"                         + NEW_LINE
                                                       + NEW_LINE +
      " -a             first"                          + NEW_LINE +
      " -b,      --b   second"                         + NEW_LINE +
      " -c <ARG>       third"                          + NEW_LINE;

    assertThat(getPrinted(), is(expected));
  }

  @Test
  void printHelpWithHeader() {
    Option.Template a = Option.newTemplate()
      .shortName("a").description("first").build();

    Command.Descriptor descriptor = Command
      .newDescriptor().name("app").templates(a).build();

    String header = "A very neat and useful utility.";
    new Formatter().printHelp(writer, descriptor, null, header, null);

    String expected =
      "Usage: app [-a]"                                + NEW_LINE +
      "A very neat and useful utility."                + NEW_LINE +
                                                         NEW_LINE +
      " -a   first"                                    + NEW_LINE;

    assertThat(getPrinted(), is(expected));
  }

  @Test
  void printHelpWithFooter() {
    Option.Template a = Option.newTemplate()
      .shortName("a").description("first").build();

    Command.Descriptor descriptor = Command
      .newDescriptor().name("app").templates(a).build();

    String footer = "Mail to info@example.com for bug reports.";
    new Formatter().printHelp(writer, descriptor, null, null, footer);

    String expected =
      "Usage: app [-a]"                                  + NEW_LINE +
                                                           NEW_LINE +
      " -a   first"                                      + NEW_LINE +
                                                           NEW_LINE +
      "Mail to info@example.com for bug reports."        + NEW_LINE;

    assertThat(getPrinted(), is(expected));
  }

  @Test
  void setCustomCharacterSet() {
    Formatter formatter = new Formatter();
    formatter.setCharacterSet(Charset.forName("ISO-8859-8"));

    formatter.printUsage(writer, "ק");
    assertThat(getPrinted(), is("Usage: ק" + NEW_LINE));
  }

  @Test
  void negativeOptionPadding() {
    assertThrows(IllegalArgumentException.class,
      () -> new Formatter().setOptionPadding(-1));
  }

  @Test
  void zeroDescriptionPadding() {
    assertThrows(IllegalArgumentException.class,
      () -> new Formatter().setDescriptionPadding(0));
  }

  @Test
  void negativeDescriptionPadding() {
    assertThrows(IllegalArgumentException.class,
      () -> new Formatter().setDescriptionPadding(-1));
  }

  @Test
  void nullUsagePrefix() {
    assertThrows(IllegalArgumentException.class,
      () -> new Formatter().setUsagePrefix(null));
  }

  @Test
  void spaceUsagePrefix() {
    assertThrows(IllegalArgumentException.class,
      () -> new Formatter().setUsagePrefix("s p a c e"));
  }

  @Test
  void nullShortOptionPrefix() {
    assertThrows(IllegalArgumentException.class,
      () -> new Formatter().setShortOptionPrefix(null));
  }

  @Test
  void emptyShortOptionPrefix() {
    assertThrows(IllegalArgumentException.class,
      () -> new Formatter().setShortOptionPrefix(""));
  }

  @Test
  void spaceShortOptionPrefix() {
    assertThrows(IllegalArgumentException.class,
      () -> new Formatter().setShortOptionPrefix("s p a c e"));
  }

  @Test
  void nullLongOptionPrefix() {
    assertThrows(IllegalArgumentException.class,
      () -> new Formatter().setLongOptionPrefix(null));
  }

  @Test
  void emptyLongOptionPrefix() {
    assertThrows(IllegalArgumentException.class,
      () -> new Formatter().setLongOptionPrefix(""));
  }

  @Test
  void spaceLongOptionPrefix() {
    assertThrows(IllegalArgumentException.class,
      () -> new Formatter().setLongOptionPrefix("s p a c e"));
  }

  @Test
  void nullOptionValueSeparator() {
    assertThrows(IllegalArgumentException.class,
      () -> new Formatter().setOptionValueSeparator(null));
  }

  @Test
  void spaceOptionValueSeparator() {
    assertThrows(IllegalArgumentException.class,
      () -> new Formatter().setOptionValueSeparator("s p a c e"));
  }

  @Test
  void nullOptionValueName() {
    assertThrows(IllegalArgumentException.class,
      () -> new Formatter().setOptionValueName(null));
  }

  @Test
  void spaceOptionValueName() {
    assertThrows(IllegalArgumentException.class,
      () -> new Formatter().setOptionValueName("s p a c e"));
  }

  @Test
  void nullCharacterSet() {
    assertThrows(IllegalArgumentException.class,
      () -> new Formatter().setCharacterSet(null));
  }

  @Test
  void nullOptionComparator() {
    assertThrows(IllegalArgumentException.class,
      () -> new Formatter().setOptionComparator(null));
  }

  /**
   * Returns the content printed to the stream.
   *
   * @return the content printed to the stream
   */
  private String getPrinted() {
    return byteStream.toString(CHARSET);
  }

  /**
   * Comparator that reverses the order of another comparator.
   */
  static final class InverseComparator implements Comparator<Option.Template> {

    @Override
    public int compare(Option.Template option1, Option.Template option2) {
      return -Formatter.defaultComparator().compare(option1, option2);
    }
  }
}

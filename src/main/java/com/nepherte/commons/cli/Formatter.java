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

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.nepherte.commons.cli.internal.Preconditions.*;
import static com.nepherte.commons.cli.internal.Predicates.*;
import static com.nepherte.commons.cli.internal.Strings.*;

/**
 * A command-line formatter for help messages and usage statements.
 */
public final class Formatter {

  /** Default amount of padding to precede each option description. */
  private static final int DEFAULT_OPTION_PADDING;
  /** Default amount of padding between each option and its description. */
  private static final int DEFAULT_DESCRIPTION_PADDING;
  /** Default string to precede each usage statement. */
  private static final String DEFAULT_USAGE_PREFIX;
  /** Default string to precede each short option. */
  private static final String DEFAULT_SHORT_OPTION_PREFIX;
  /** Default string to precede each long option. */
  private static final String DEFAULT_LONG_OPTION_PREFIX;
  /** Default token that separates an option from its value(s). */
  private static final String DEFAULT_OPTION_VALUE_SEPARATOR;
  /** Default name of an option value. */
  private static final String DEFAULT_OPTION_VALUE_NAME;
  /** Default character set for printing out text. */
  private static final Charset DEFAULT_CHARACTER_SET;
  /** Comparator that alphabetically sorts options by name. */
  private static final Comparator<Option.Template> DEFAULT_OPTION_COMPARATOR;
  /** Default name of arguments. */
  private static final String DEFAULT_ARGUMENT_NAME;

  static {
    DEFAULT_OPTION_PADDING = 1;
    DEFAULT_DESCRIPTION_PADDING = 3;
    DEFAULT_USAGE_PREFIX = "Usage:";
    DEFAULT_SHORT_OPTION_PREFIX = "-";
    DEFAULT_LONG_OPTION_PREFIX = "--";
    DEFAULT_OPTION_VALUE_SEPARATOR = " ";
    DEFAULT_OPTION_VALUE_NAME = "<ARG>";
    DEFAULT_ARGUMENT_NAME = "<ARG>";
    DEFAULT_OPTION_COMPARATOR = defaultComparator();
    DEFAULT_CHARACTER_SET = StandardCharsets.UTF_8;
  }

  /** The amount of padding to precede each option description. */
  private int optionPadding;
  /** The amount of padding between an option and its description. */
  private int descriptionPadding;
  /** The string to precede the usage statement of a command. */
  private String usagePrefix;
  /** The string to precede short options. */
  private String shortOptionPrefix;
  /** The string to precede long options. */
  private String longOptionPrefix;
  /** The token that separates an option from its value(s). */
  private String optionValueSeparator;
  /** The default name of an option value. */
  private String optionValueName;
  /** The character set for printing out text. */
  private Charset characterSet;
  /** Comparator that sorts the options. */
  private Comparator<Option.Template> optionComparator;
  /** The default name for arguments. */
  private String argumentName;

  /**
   * Creates a new {@code Formatter}.
   */
  public Formatter() {
    usagePrefix = DEFAULT_USAGE_PREFIX;
    optionValueName = DEFAULT_OPTION_VALUE_NAME;

    optionPadding = DEFAULT_OPTION_PADDING;
    descriptionPadding = DEFAULT_DESCRIPTION_PADDING;

    shortOptionPrefix = DEFAULT_SHORT_OPTION_PREFIX;
    longOptionPrefix = DEFAULT_LONG_OPTION_PREFIX;

    optionComparator = DEFAULT_OPTION_COMPARATOR;
    optionValueSeparator = DEFAULT_OPTION_VALUE_SEPARATOR;

    argumentName = DEFAULT_ARGUMENT_NAME;
    characterSet = DEFAULT_CHARACTER_SET;
  }

  /**
   * Sets the amount of padding to precede an option description.
   *
   * @param padding the amount of padding to apply
   * @throws IllegalArgumentException padding is less than zero
   */
  public void setOptionPadding(int padding) {
    requireArg(padding, greaterThan(-1), "Option padding [%d] is negative");
    optionPadding = padding;
  }

  /**
   * Sets the amount of padding between an option and its description.
   *
   * @param padding the amount of padding to apply
   * @throws IllegalArgumentException padding is zero or less
   */
  public void setDescriptionPadding(int padding) {
    requireArg(padding, greaterThan(0), "Description padding [%d] is negative");
    descriptionPadding = padding;
  }

  /**
   * Sets the string to precede the usage statement of a command.
   *
   * @param prefix the string to precede the usage statement of a command
   * @throws IllegalArgumentException prefix is null or has a space
   */
  public void setUsagePrefix(String prefix) {
    requireArg(prefix, notNull(), "Usage prefix is [%s]");
    requireArg(prefix, noSpace(), "Usage prefix [%s] has a space");
    usagePrefix = prefix.strip();
  }

  /**
   * Sets the string to precede a short option.
   *
   * @param prefix the string to precede a short option
   * @throws IllegalArgumentException prefix is null or empty
   * @throws IllegalArgumentException prefix has a space
   */
  public void setShortOptionPrefix(String prefix) {
    requireArg(prefix, notNull(), "Short prefix is [%s]");
    requireArg(prefix, notEmpty(), "Short prefix [%s] is empty");
    requireArg(prefix, noSpace(), "Short prefix [%s] has a space");
    shortOptionPrefix = prefix;
  }

  /**
   * Sets the string to precede a long option.
   *
   * @param prefix the string to precede a long option
   * @throws IllegalArgumentException prefix is null or empty
   * @throws IllegalArgumentException prefix has a space
   */
  public void setLongOptionPrefix(String prefix) {
    requireArg(prefix, notNull(), "Long prefix is [%s]");
    requireArg(prefix, notEmpty(), "Long prefix [%s[ is empty");
    requireArg(prefix, noSpace(), "Long prefix [%s] has a space");
    longOptionPrefix = prefix;
  }

  /**
   * Sets the token that separates an option from its value(s).
   *
   * @param separator the option value separator
   * @throws IllegalArgumentException separator is null
   * @throws IllegalArgumentException separator has (but not equals) a space
   */
  public void setOptionValueSeparator(String separator) {
    requireArg(separator, notNull(), "Separator is [%s]");
    requireArg(separator, noSpace().or(eq(" ")), "Separator [%s] has a space");
    optionValueSeparator = separator;
  }

  /**
   * Sets the default option value name.
   *
   * @param name the default option value name
   * @throws IllegalArgumentException name is null or has a space
   */
  public void setOptionValueName(String name) {
    requireArg(name, notNull(), "Value name is [%s]");
    requireArg(name, noSpace(), "Value name [%s] has a space");
    optionValueName = name;
  }

  /**
   * Sets the comparator to sort options.
   *
   * @param comparator the new option comparator
   * @throws IllegalArgumentException comparator is null
   */
  public void setOptionComparator(Comparator<Option.Template> comparator) {
    requireArg(comparator, notNull(), "Comparator is [%s]");
    optionComparator = comparator;
  }

  /**
   * Sets the default argument name.
   *
   * @param name the default argument name
   * @throws IllegalArgumentException name is null or has a space
   */
  public void setArgumentName(String name) {
    requireArg(name, notNull(), "Argument name is [%s]");
    requireArg(name, noSpace(), "Argument name [%s] has a space");
    argumentName = nullToEmpty(name);
  }

  /**
   * Sets the character set to use when printing out text.
   *
   * @param charSet the new character set
   */
  public void setCharacterSet(Charset charSet) {
    requireArg(charSet, notNull(), "Charset is [%s]");
    characterSet = charSet;
  }

  /**
   * Prints the help message of a command to {@link System#out}.
   *
   * @param descriptor the command descriptor
   */
  public void printHelp(Command.Descriptor descriptor) {
    printHelp(descriptor, null, null, null);
  }

  /**
   * Prints the help message of a command to {@link System#out}.
   *
   * @param descriptor the command descriptor to print
   * @param syntax the syntax for the command, {@code null} to auto-generate
   */
  public void printHelp(Command.Descriptor descriptor, String syntax) {
    printHelp(descriptor, syntax, null, null);
  }

  /**
   * Prints the help message of a command to {@link System#out}.
   *
   * @param descriptor the command descriptor to print
   * @param syntax the syntax for the command, {@code null} to auto-generate
   * @param header the message to display at the start, {@code null} to disable
   * @param footer the message to display at the end, {@code null} to disable
   */
  public void printHelp(Command.Descriptor descriptor, String syntax,
  String header, String footer) {

    try (PrintWriter pw = createDefaultPrintWriter()) {
      printHelp(pw, descriptor, syntax, header, footer);
    }
  }

  /**
   * Prints the help message of a command to the given print writer.
   *
   * @param pw the writer to print the help message
   * @param descriptor the command descriptor to print
   * @param syntax the syntax for the command, {@code null} to auto-generate
   * @param header the message to display at the start, {@code null} to disable
   * @param footer the message to display at the end, {@code null} to disable
   */
  public void printHelp(PrintWriter pw, Command.Descriptor descriptor,
  String syntax, String header, String footer) {

    if (isNullOrBlank(syntax)) {
      // Auto-generated command syntax.
      printUsage(pw, descriptor);
    }
    else {
      // User-provided command syntax.
      printUsage(pw, syntax);
    }

    // Print header, if present.
    if (!isNullOrBlank(header)) {
      pw.println(header.strip());
    }

    // Print options.
    if (!descriptor.getTemplates().isEmpty()) {
      pw.append(System.lineSeparator());
      printOptions(pw, descriptor);
    }

    // Print footer, if present.
    if (!isNullOrBlank(footer)) {
      // Additional options - footer separator.
      if (!descriptor.getTemplates().isEmpty()) {
        pw.append(System.lineSeparator());
      }
      pw.println(footer.strip());
    }
  }

  /**
   * Prints the usage statement of a command.
   *
   * @param pw the writer to print the usage statement
   * @param descriptor the command descriptor to print
   */
  public void printUsage(PrintWriter pw, Command.Descriptor descriptor) {
    StringBuilder builder = new StringBuilder();

    // Print usage prefix (never null).
    if (!isNullOrEmpty(usagePrefix)) {
      builder.append(usagePrefix).append(' ');
    }

    // Print command name.
    String name = descriptor.getName().orElse("cmd");

    if (!isNullOrEmpty(name)) {
      builder.append(name);
    }

    // Append each option to the usage statement.
    Set<Option.Template> templates = descriptor.getTemplates();

    if (!templates.isEmpty()) {
      builder.append(' ').append(templates
        .stream().sorted(optionComparator)
        .map(this::renderOptionUsage)
        .collect(Collectors.joining(" ")));
    }

    // Append argument name (never null) to the usage statement.
    if (descriptor.canHaveArgs() && !argumentName.isEmpty()) {
      StringBuilder argBuilder = new StringBuilder(argumentName);

      if (!descriptor.requiresArgs()) {
        argBuilder.insert(0, '[');
        argBuilder.append(']');
      }

      builder.append(' ').append(argBuilder);
    }

    // Print the usage statement.
    pw.println(builder);
  }

  /**
   * Prints the usage statement of a command.
   *
   * @param pw the writer to print the usage statement
   * @param syntax the usage statement to print
   */
  public void printUsage(PrintWriter pw, String syntax) {
    pw.println(usagePrefix + ' ' + syntax.strip());
  }

  /**
   * Prints the option names, values and descriptions.
   *
   * @param pw the print writer to print the options
   * @param descriptor the command descriptor
   */
  void printOptions(PrintWriter pw, Command.Descriptor descriptor) {
    StringBuilder builder = new StringBuilder();
    renderOptions(builder, descriptor.getTemplates());
    pw.println(builder);
  }

  /**
   * Converts an option to a usage statement. The clause is wrapped in square
   * brackets if the option is not required. If the option has a value, it will
   * be included as well and surrounded by comparison brackets.
   *
   * @param template the option to convert
   * @return the option usage statement
   */
  private String renderOptionUsage(Option.Template template) {
    StringBuilder optionBuilder = new StringBuilder();

    // Print the option.
    Optional<String> shortName = template.getShortName();
    Optional<String> longName = template.getLongName();

    if (shortName.isPresent()) {
      optionBuilder
        .append(shortOptionPrefix)
        .append(shortName.get());
    }
    else if (longName.isPresent()){
      optionBuilder
        .append(longOptionPrefix)
        .append(longName.get());
    }

    // Print the values.
    if (template.canHaveValues()) {
      String name = template.getValueName().orElse(optionValueName);

      if (!name.isEmpty()) {
        optionBuilder
          .append(optionValueSeparator)
          .append(name);
      }
    }

    // Surround with brackets if not required.
    if (!template.isRequired()) {
      optionBuilder.insert(0, '[');
      optionBuilder.append(']');
    }

    return optionBuilder.toString();
  }

  /**
   * Renders the option names, values and descriptions.
   *
   * @param sb the builder to render the options in
   * @param options the command options
   */
  private void renderOptions(StringBuilder sb, Set<Option.Template> options) {
    // Sort options with comparator.
    List<Option.Template> sortedOptions = new ArrayList<>(options);
    sortedOptions.sort(optionComparator);

    // FIRST PASS: Determine the maximum length of options.
    int maxShortOptSize = 0;
    int maxLongOptSize = 0;

    for (Option.Template template: sortedOptions) {
      int shortOptSize = 0;
      int longOptSize = 0;

      Optional<String> shortName = template.getShortName();
      Optional<String> longName = template.getLongName();

      // Take short name into account.
      if (shortName.isPresent()) {
        shortOptSize += shortOptionPrefix.length();
        shortOptSize += shortName.get().length();

        if (longName.isPresent()) {
          // + 1 for comma separating names.
          shortOptSize += 1;
        }
      }

      // Take long name into account.
      if (longName.isPresent()) {
        longOptSize += longOptionPrefix.length();
        longOptSize += longName.get().length();
      }

      // Take values into account.
      if (template.canHaveValues()) {
        String name = template.getValueName().orElse(optionValueName);

        if (!name.isEmpty()) {
          int valueSize = name.length() + optionValueSeparator.length();
          shortOptSize += shortName.isPresent() ? valueSize : 0;
          longOptSize += longName.isPresent() ? valueSize : 0;
        }
      }

      // Align short and long names of options.
      maxShortOptSize = Math.max(maxShortOptSize, shortOptSize);
      maxLongOptSize = Math.max(maxLongOptSize, longOptSize);
    }

    // SECOND PASS: Render the options.
    int longOptIndex = optionPadding + maxShortOptSize;
    if (maxShortOptSize != 0 && maxLongOptSize != 0) {longOptIndex++;}
    int descIndex = longOptIndex + maxLongOptSize + descriptionPadding;
    renderOptions(sb, sortedOptions, optionPadding, longOptIndex, descIndex);
  }

  /**
   * Renders the option names, values and descriptions.
   *
   * @param sb the builder to render the options in
   * @param options the options to render
   * @param shortOptIndex the index at which to put the short option name
   * @param longOptIndex the index at which to put the long option name
   * @param descIndex the index at which to put the option description
   */
  private void renderOptions(StringBuilder sb, List<Option.Template> options,
  int shortOptIndex, int longOptIndex, int descIndex) {

    for (int i = 0; i < options.size(); i++) {
      Option.Template option = options.get(i);
      StringBuilder optionBuilder = new StringBuilder();
      StringBuilder optionValueBuilder = new StringBuilder();

      // Print option value.
      if (option.canHaveValues()) {
        String name = option.getValueName().orElse(optionValueName);

        if (!name.isEmpty()) {
          optionValueBuilder
            .append(optionValueSeparator)
            .append(name);
        }
      }

      // Print option name.
      Optional<String> shortName = option.getShortName();
      Optional<String> longName = option.getLongName();

      if (shortName.isPresent()) {
        optionBuilder
          // Align short options.
          .append(" ".repeat(shortOptIndex))
          .append(shortOptionPrefix)
          .append(shortName.get())
          .append(optionValueBuilder);

        if (longName.isPresent()) {
          optionBuilder.append(',');
        }
      }

      // Print long option name.
      if (longName.isPresent()) {
        optionBuilder
          // Align long options.
          .append(" ".repeat(longOptIndex - optionBuilder.length()))
          .append(longOptionPrefix)
          .append(longName.get())
          .append(optionValueBuilder);
      }

      // Print description.
      Optional<String> description = option.getDescription();

      if (description.isPresent()) {
        int padSize = descIndex - optionBuilder.length();
        optionBuilder.append(" ".repeat( padSize));
        optionBuilder.append(description.get());
      }

      // Wrap the option line.
      sb.append(optionBuilder);

      if (i != options.size() - 1) {
        sb.append(System.lineSeparator());
      }
    }
  }

  /**
   * Creates a default writer printing to {@link System#out}.
   *
   * @return the default print writer
   */
  private PrintWriter createDefaultPrintWriter() {
    //noinspection UseOfSystemOutOrSystemErr acceptable default
    Writer out = new OutputStreamWriter(System.out, characterSet);
    return new PrintWriter(out, true);
  }

  /**
   * Lexicographically compares the names of two template options.
   *
   * @return the default option template comparator
   */
  static Comparator<Option.Template> defaultComparator() {
    return Option.Template::byName;
  }

}

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
import com.nepherte.commons.cli.Option;
import com.nepherte.commons.cli.Parser;

import com.nepherte.commons.cli.exception.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;

/**
 * <p>Parser that supports the {@code GNU}-style option format:
 *
 * <blockquote>{@code -shortOption[=<values>] --longOption[=<values>] [--]
 * [<args>]}</blockquote>
 *
 * <p>Short options start with a single dash, while long options start with a
 * double dash. A command can take both short and long options. Options can
 * take zero or more values (separated by <em>commas</em>).
 *
 * <p>The first encountered argument implicitly marks the end of the options,
 * whereas a double dash marks the beginning of the arguments (separated by
 * spaces). Unambiguous arguments can start with a dash.
 */
public final class GnuParser implements Parser {

  /** The command builder. */
  private Command.Builder builder;
  /** The command descriptor. */
  private final Command.Descriptor descriptor;

  /** Required groups that are still missing. */
  private final List<Option.Group> missingGroups;
  /** Required options that are still missing. */
  private final List<Option.Template> missingTemplates;
  /** The selected templates of groups. */
  private final Map<Option.Group, Option.Template> selectedTemplates;

  /**
   * Creates a new Gnu parser.
   *
   * @param descriptor the command descriptor
   */
  public GnuParser(Command.Descriptor descriptor) {
    this.descriptor = descriptor;

    missingGroups = new ArrayList<>();
    missingTemplates = new ArrayList<>();
    selectedTemplates = new HashMap<>();
  }

  @Override
  public Command parse(String[] tokens) throws ParseException {
    initParser();

    try {
      return parseTokens(tokens);
    }
    finally {
      cleanupParser();
    }
  }

  /**
   * <p>Parses options and arguments from the command line tokens:
   * <ol>
   *   <li>Parses options until it encounters a non-option
   *   ({@link #parseOptions(ListIterator) parseOptions}),</li>
   *   <li>Considers any remaining tokens to be arguments
   *   ({@link #parseArguments(ListIterator) parseArguments}).</li>
   * </ol>
   *
   * <p>The token array is assumed to start with either an option or argument.
   * This method is guaranteed to consume all tokens (not considering
   * exceptions). Temporary input and output is stored in the instance fields.
   *
   * @param tokens the command line tokens
   * @return the parsed command line
   * @throws ParseException the parser encounters an issue
   */
  private Command parseTokens(String[] tokens) throws ParseException {
    // Convert tokens to more convenient iterator.
    List<String> tokenList = Arrays.asList(tokens);
    ListIterator<String> tokenIt = tokenList.listIterator();

    // Do the actual parsing.
    parseOptions(tokenIt);
    parseArguments(tokenIt);

    // Return the parsed command.
    return builder.build();
  }

  /**
   * <p>Parses options from the command line tokens. Consumes the tokens
   * it needs to parse all options it encounters. Delegates to {@link
   * #parseOption parseOption} each time it comes across a token that looks
   * like an option.
   *
   * <p>The provided iterator is assumed to start with an option. That same
   * iterator is guaranteed to end in front of the first argument it encounters,
   * or at the end in case of no arguments (not considering exceptions).
   *
   * @param tokens the command line token cursor
   * @throws ParseException the parser encounters an issue
   */
  private void parseOptions(ListIterator<String> tokens) throws ParseException {
    // Don't parse options if there are none.
    if (descriptor.getTemplates().isEmpty()) {
      return;
    }

    // Iterate until no more options.
    while (tokens.hasNext()) {
      String token = tokens.next();

      // A double dash marks the end.
      if (token.equals("--")) {
        return;
      }
      // The token looks like an option.
      else if (token.startsWith("-")) {
        parseOption(token);
      }
      // The token is an argument.
      else {
        tokens.previous();
        return;
      }
    }

    // Check for missing options.
    if (!missingTemplates.isEmpty()) {
      Option.Template template = missingTemplates.get(0);
      throw new MissingOptionException(template);
    }

    // Check for missing groups.
    if (!missingGroups.isEmpty()) {
      Option.Group group = missingGroups.get(0);
      throw new MissingGroupException(group);
    }
  }

  /**
   * Parses an option from the command line token. Delegates to {@link
   * #parseValues} when the option is expected to have one or more values.
   *
   * @param token the command line token
   * @throws ParseException the parser encounters an issue
   */
  private void parseOption(String token) throws ParseException {
    // Split on option-value separator.
    String[] split = token.split("=");

    // Determine the option and values.
    String option;
    String[] values;

    // It's an option with no value.
    if (split.length == 1) {
      option = split[0];
      values = new String[0];
    }
    // It's an option with values.
    else if (split.length == 2) {
      option = split[0];
      values = split[1].split(",");
    }
    // It's not an option at all.
    else {
      throw new UnrecognizedTokenException(token);
    }

    parseOption(option, values);
  }

  /**
   * Parses an option from the command line token. Delegates to {@link
   * #parseValues} when the option is expected to have one or more values.
   *
   * @param option the command line option token
   * @param values the command line value tokens
   * @throws ParseException the parser encounters an issue
   */
  private void parseOption(String option, String[] values)
  throws ParseException {

    // Lookup template.
    Option.Template template = searchTemplate(option);

    if (template == null) {
      // Token does not match a template in the descriptor.
      throw new UnrecognizedTokenException(option);
    }

    // Take a copy of the template.
    Option.Builder optionBuilder = Option.newInstance(template);
    Option.Group group = descriptor.getGroup(template).orElse(null);

    // The template is no longer missing.
    if (template.isRequired()) {
      missingTemplates.remove(template);
    }

    if (group != null) {
      // The group is no longer missing.
      if (group.isRequired()) {
        missingGroups.remove(group);
      }

      // A different option already provided.
      Option.Template selected = selectedTemplates.get(group);
      if (selected != null && selected != template) {
        throw new ExclusiveOptionsException(selected, template);
      }

      // Select the option.
      selectedTemplates.put(group, template);
    }

    // Parse option values.
    parseValues(optionBuilder, template, values);

    // Build option and add to command.
    Option parsedOption = optionBuilder.build();
    builder.option(parsedOption);
  }

  /**
   * <p>Parses option values from the current command line tokens. Consumes all
   * the tokens it needs to parse the values. Stops in front of double dashes or
   * in front of the next option token.
   *
   * <p>The provided iterator is assumed to start with an option value. That
   * same iterator is guaranteed to end in front of the next option/argument,
   * or at the end in case of no arguments (not considering exceptions).
   *
   * @param builder the option currently being built
   * @param template the template currently being parsed
   * @param values the command line token cursor
   * @throws ParseException the parser encounters an issue
   */
  private static void parseValues(Option.Builder builder, Option.Template
  template, String[] values) throws ParseException {

    int valueCount = values.length;
    int minValues = template.getMinValues();
    int maxValues = template.getMaxValues();

    // Check too few values.
    if (valueCount < minValues) {
      throw new MissingValueException(template);
    }

    // Check for too many values.
    if (valueCount > maxValues) {
      throw new TooManyValuesException(template);
    }

    builder.values(values);
  }

  /**
   * <p>Parses the arguments from the command line tokens. Consumes all tokens
   * provided by the iterator. Ignores double dashes and stops when all tokens
   * have been consumed.
   *
   * <p>The provided iterator is assumed to start with the first argument. That
   * same iterator is guaranteed to have consumed every token, i.e. the iterator
   * has no more tokens left (not considering exceptions).
   *
   * @param tokens the command line token cursor
   * @throws ParseException the command has too few, or too many arguments
   */
  private void parseArguments(ListIterator<String> tokens)
  throws ParseException {

    // Keep track of the number of arguments.
    int argumentCount = 0;

    // Loop until the end of the list.
    while (tokens.hasNext()) {
      String token = tokens.next();

      // Ignore first double dash.
      if (!token.equals("--") || argumentCount != 0) {
        argumentCount++;
        builder.argument(token);
      }
    }

    // Check for missing arguments.
    String commandName = descriptor.getName().orElse("<undefined>");

    if (argumentCount < descriptor.getMinArgs()) {
      throw new MissingArgumentException(commandName);
    }

    // Check for too many arguments.
    if (argumentCount > descriptor.getMaxArgs()) {
      throw new TooManyArgumentsException(commandName);
    }
  }

  /**
   * Searches the descriptor for a template that matches the given token, or
   * {@code null} if no such template exists.
   *
   * @param token the token for which to look for options
   * @return the template that matches the given token or {@code null}
   */
  private Option.Template searchTemplate(String token) {
    // Check for the long name.
    if (token.startsWith("--")) {
      String optionName = token.substring(2);
      return descriptor.getLongTemplate(optionName).orElse(null);
    }

    // Check for the short name.
    if (token.startsWith("-")) {
      String optionName = token.substring(1);
      return descriptor.getShortTemplate(optionName).orElse(null);
    }

    // No template found.
    return null;
  }

  /**
   * Initializes this parser with the descriptor.
   */
  private void initParser() {
    // Initialize command builder.
    builder = Command.newInstance();

    Optional<String> name = descriptor.getName();
    name.ifPresent(builder::name);

    // Initialize missing templates and groups.
    missingGroups.addAll(descriptor.getRequiredGroups());
    missingTemplates.addAll(descriptor.getRequiredTemplates());
  }

  /**
   * Cleans up the state of the parser.
   */
  private void cleanupParser() {
    // Destroy command builder.
    builder = null;

    // Clear missing templates and groups.
    missingGroups.clear();
    missingTemplates.clear();
    selectedTemplates.clear();
  }
}
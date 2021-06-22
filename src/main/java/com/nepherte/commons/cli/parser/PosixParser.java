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
import com.nepherte.commons.cli.Option.Template;
import com.nepherte.commons.cli.Parser;
import com.nepherte.commons.cli.exception.ParseException;

import java.util.*;

import static com.nepherte.commons.cli.internal.Preconditions.requireArg;
import static com.nepherte.commons.cli.internal.Predicates.notNull;

/**
 * <p>Parser that supports the {@code Posix}-style option format:
 *
 * <blockquote>{@code -i[[ ]<value>] [--] [<args>]}</blockquote>
 *
 * <p>Short options start with a single dash. They can occur as separate tokens
 * or glued together into a single token. Options can take a single value
 * (either separated by a space or glued to the option).
 *
 * <p>The first encountered argument implicitly marks the end of the options,
 * whereas a double dash marks the beginning of the arguments (separated by
 * spaces). Unambiguous arguments can start with a dash.
 */
public class PosixParser implements Parser {

  private final GnuParser delegateParser;

  /**
   * Creates a new Posix parser.
   *
   * @param descriptor the command descriptor
   */
  public PosixParser(Command.Descriptor descriptor) {
    requireArg(descriptor, notNull(), "Descriptor is [%s]" );
    Command.Descriptor sanitized = sanitize(descriptor);
    delegateParser = new GnuParser(sanitized);
  }

  @Override
  public Command parse(String[] tokens) throws ParseException {
    String[] convertedTokens = convertTokens(tokens);
    return delegateParser.parse(convertedTokens);
  }

  /**
   * <p>Converts {@code Posix}-style tokens to {@code Gnu}-style tokens:
   * <ol>
   *   <li>Converts options until it encounters a non-option
   *   ({@link #convertOptions(ListIterator) convertOptions}),</li>
   *   <li>Considers any remaining tokens to be arguments
   *   ({@link #convertArguments(ListIterator) convertArguments}).</li>
   * </ol>
   *
   * <p>The token array is assumed to start with either an option or argument.
   * This method is guaranteed to consume all tokens (not considering
   * exceptions). Temporary input and output is stored in the instance fields.
   *
   * @param tokens the command line tokens
   * @return the converted command line tokens
   */
  private String[] convertTokens(String[] tokens)  {
    // Convert tokens to more convenient iterator.
    List<String> tokenList = new ArrayList<>(List.of(tokens));
    ListIterator<String> tokenIt = tokenList.listIterator();

    // Do the actual conversions.
    convertOptions(tokenIt);
    convertArguments(tokenIt);

    // Return the converted tokens.
    return tokenList.toArray(String[]::new);
  }

  /**
   * <p>Consumes the {@code Posix}-style options and replaces them with their
   * {@code Gnu}-style equivalent. Delegates to {@link #convertOption
   * convertOption} each time it comes across a token that looks like an option.
   *
   * <p>The provided iterator is assumed to start with an option. That same
   * iterator is guaranteed to end in front of the first argument it encounters,
   * or at the end in case of no arguments (not considering exceptions).
   *
   * @param tokens the command line token cursor
   */
  private void convertOptions(ListIterator<String> tokens) {
    Command.Descriptor descriptor = delegateParser.getDescriptor();

    // Don't convert options if there aren't any.
    if (descriptor.getTemplates().isEmpty()) {
      return;
    }

    // Iterate until no more options.
    while (tokens.hasNext()) {
      String token = tokens.next();

      // A single dash is an argument.
      if (token.equals("-")) {
        return;
      }
      // A double dash marks the end.
      if (token.equals("--")) {
        return;
      }

      // The token looks like an option.
      if (token.startsWith("-")) {
        tokens.previous();
        convertOption(tokens);
      }
      // The token is an argument.
      else {
        tokens.previous();
        return;
      }
    }
  }

  /**
   * <p>Consumes the {@code Posix}-style option and replaces it with their
   * {@code Gnu}-style equivalent. If the option can have one or more values,
   * then {@link #extractValue} will be called as well.
   *
   * <p>The provided iterator is assumed to start with an option. That same
   * iterator is guaranteed to end after the option and its associated values
   * (not considering exceptions).
   *
   * @param tokens the command line token cursor
   */
  private void convertOption(ListIterator<String> tokens) {
    // Take and remove the token.
    String token = tokens.next();
    tokens.remove();

    // Replace each char with a Gnu-style option.
    StringBuilder remaining = new StringBuilder();
    remaining.append(token).deleteCharAt(0);

    while (remaining.length() != 0) {
      char option = remaining.charAt(0);
      remaining.deleteCharAt(0);

      Template template = searchTemplate(option);
      StringBuilder tokenBuilder = new StringBuilder();
      tokenBuilder.append('-').append(option);

      // Look for a value (if the option can have one).
      if (template != null && template.canHaveValues()) {
        Optional<String> valueOpt = extractValue(remaining, tokens);
        valueOpt.ifPresent(value -> tokenBuilder.append('=').append(value));
      }

      tokens.add(tokenBuilder.toString());
    }
  }

  private Optional<String> extractValue(StringBuilder remaining,
  ListIterator<String> tokens) {

    // Look for a value glued to the option.
    if (remaining.length() != 0) {
      char token = remaining.charAt(0);
      Template template = searchTemplate(token);

      // Next char is an option, so not a value.
      if (template != null) {
        return Optional.empty();
      }

      // Next char is not an option. Assume it's a value.
      String value = remaining.toString();
      remaining.delete(0, remaining.length());
      return Optional.of(value);
    }

    // No remaining tokens, so no values.
    if (!tokens.hasNext()) {
      return Optional.empty();
    }

    // Look for a value as a separate token.
    String token = tokens.next();

    if (token.startsWith("-")) {
      return Optional.empty();
    }

    tokens.remove();
    return Optional.of(token);
  }

  private void convertArguments(ListIterator<String> tokens) {
    while (tokens.hasNext()) {
      tokens.next();
    }
  }

  /**
   * Searches the descriptor for a template that matches the given token, or
   * {@code null} if no such template exists.
   *
   * @param token the token for which to look for options
   * @return the template that matches the given token or {@code null}
   */
  private Option.Template searchTemplate(char token) {
    String shortName = Character.toString(token);
    Command.Descriptor descriptor = delegateParser.getDescriptor();
    return descriptor.getShortTemplate(shortName).orElse(null);
  }

  private static Command.Descriptor sanitize(Command.Descriptor descriptor) {
    Command.Descriptor.Builder result = Command.newDescriptor();
    descriptor.getName().ifPresent(result::name);

    result.minArgs(descriptor.getMinArgs());
    result.maxArgs(descriptor.getMaxArgs());

    Map<String, Template> newTemplates = new HashMap<>();

    for (Template template : descriptor.getTemplates()) {
      Template newTemplate = template.asBuilder().longName(null).build();
      result.template(newTemplate);

      String shortName = newTemplate.getName();
      newTemplates.put(shortName, newTemplate);
    }

    for (Option.Group group : descriptor.getGroups()) {
      Option.Group.Builder newGroupBuilder = Option.newGroup();

      if (group.isRequired()) {
        newGroupBuilder.required();
      }

      for (Template template : group.getTemplates()) {
        String name = template.getName();
        newGroupBuilder.template(newTemplates.get(name));
      }

      result.group(newGroupBuilder.build());
    }

    return result.build();
  }
}

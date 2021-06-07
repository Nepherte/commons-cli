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
package com.nepherte.commons.cli.internal;

import com.nepherte.commons.cli.Command;
import com.nepherte.commons.cli.Parser;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * An option format.
 */
public interface OptionFormat {

  /** Suggests that an option format supports arguments. */
  int ARGUMENT_FEATURE = 1;
  /** Suggests that an option format supports short options. */
  int SHORT_OPTION_FEATURE = 2;
  /** Suggests that an option format supports long options. */
  int LONG_OPTION_FEATURE = 4;
  /** Suggests that an option format supports exclusive options. */
  int GROUP_OPTION_FEATURE = 8;

  /**
   * Indicates whether a certain feature is supported by this option format. See
   * the constants declared in this interface for available features. Multiple
   * features can be combined via a bitwise OR.
   *
   * @param features the features to check
   * @return true if the given features are supported
   */
  boolean supportsFeatures(int features);

  /**
   * Returns a parser for a given command descriptor.
   *
   * @param descriptor the command descriptor
   * @return the parser for the command descriptor
   */
  Parser parserFor(Command.Descriptor descriptor);

  /**
   * Returns a short option and its values, in all possible ways it can be
   * formatted according to this option format.
   *
   * @param name the name of the short option
   * @param values the option values
   * @return all possible formattings for a short option and its values
   * @throws UnsupportedOperationException if short options are not supported
   */
  List<String> shortOptionFor(String name, String... values);

  /**
   * Combines short options and formats them according to this option format.
   * By default, only the first formatting variant of each option is considered.
   * Implementations are encouraged to override this method to include alternate
   * formatting variants too.
   *
   * @param names the names of the short options
   * @return possible formattings for all short options combined
   * @throws UnsupportedOperationException if short options are not supported
   * @see #shortOptionFor(String, String...)
   */
  default List<String> shortOptionsFor(String... names) {
    if (names == null || names.length == 0) {
      return Collections.emptyList();
    }

    return List.of(
      Arrays.stream(names).map(this::shortOptionFor)
        // always pick the first short option variant
        .map(variants -> variants.stream().findFirst())
        .filter(Optional::isPresent).map(Optional::get)
        // join the short options together in a string
        .collect(Collectors.joining(" "))
    );
  }

  /**
   * Returns a long option and its values, in all possible ways it can be
   * formatted according to this option format.
   *
   * @param name the name of the long option
   * @param values the option values
   * @return all possible formattings for a long option and its values
   * @throws UnsupportedOperationException if long options are not supported
   */
  List<String> longOptionFor(String name, String... values);

  /**
   * Combines long options and formats them according to this option format.
   * By default, only the first formatting variant of each option is considered.
   * Implementations are encouraged to override this method to include alternate
   * formatting variants too.
   *
   * @param names the names of the long options
   * @return possible formattings for all long options combined
   * @throws UnsupportedOperationException if long options are not supported
   * @see #longOptionFor(String, String...)
   */
  default List<String> longOptionsFor(String... names) {
    if (names == null || names.length == 0) {
      return Collections.emptyList();
    }

    return List.of(
      Arrays.stream(names).map(this::longOptionFor)
        // always pick the first long option variant
        .map(variants -> variants.stream().findFirst())
        .filter(Optional::isPresent).map(Optional::get)
        // join the long options together in a string
        .collect(Collectors.joining(" "))
    );
  }

  /**
   * Returns the arguments, in all possible ways it can be formatted according
   * to this option format.
   *
   * @param arguments the arguments
   * @return all possible formattings for the arguments
   * @throws UnsupportedOperationException if arguments are not supported
   */
  List<String> argumentsFor(String... arguments);

  /**
   * Combines the options with the arguments, and formats them according to this
   * option format. By default, only the first formatting variant of each option
   * and argument is considered. Implementations are encouraged to override this
   * method to include alternate formatting variants too.
   *
   * @param shortNames the names of the short options
   * @param longNames the names of the long options
   * @param argumentTokens the argument tokens
   * @return possible formattings for the options and arguments combined
   * @see #shortOptionsFor(String...)
   * @see #longOptionsFor(String...)
   * @see #argumentsFor(String...)
   */
  default List<String> optionsAndArgsFor(String[] shortNames,
  String[] longNames, String[] argumentTokens) {

    List<String> shortOptions = shortOptionsFor(shortNames);
    List<String> longOptions = longOptionsFor(longNames);
    List<String> arguments = argumentsFor(argumentTokens);

    Optional<String> firstShortOptions = shortOptions.stream().findFirst();
    Optional<String> firstLongOptions = longOptions.stream().findFirst();
    Optional<String> firstArguments = arguments.stream().findFirst();

    boolean noShortOptions = firstShortOptions.isEmpty();
    boolean noLongOptions = firstLongOptions.isEmpty();
    boolean noArguments = firstArguments.isEmpty();

    if (noShortOptions && noLongOptions && noArguments) {
      return Collections.emptyList();
    }

    if (noShortOptions && noLongOptions) {
      return List.of(firstArguments.get());
    }

    StringJoiner optionJoiner = new StringJoiner(" ");
    firstShortOptions.ifPresent(optionJoiner::add);
    firstLongOptions.ifPresent(optionJoiner::add);
    String formattedOptions = optionJoiner.toString();

    if (noArguments) {
      return List.of(formattedOptions);
    }

    return List.of(
      String.join(" ", formattedOptions, firstArguments.get()),
      String.join(" ", formattedOptions, "--", firstArguments.get())
    );
  }
}

package com.nepherte.commons.test.style;

import com.nepherte.commons.cli.Command;
import com.nepherte.commons.cli.Parser;

import java.util.Set;

/**
 * An option style.
 */
public interface OptionStyle {

  /** Suggests that an option style supports arguments. */
  int ARGUMENT_FEATURE = 1;
  /** Suggests that an option style supports short options. */
  int SHORT_OPTION_FEATURE = 2;
  /** Suggests that an option style supports long options. */
  int LONG_OPTION_FEATURE = 4;
  /** Suggests that an option style supports exclusive options. */
  int GROUP_OPTION_FEATURE = 8;

  /**
   * Indicates whether a certain feature is supported by this option style. See
   * the constants declared in this interface for available features. Multiple
   * features can be combined via a bitwise OR.
   *
   * @param features the features to check
   * @return true if the given features are supported
   */
  boolean supportsFeature(int features);

  /**
   * Returns a parser for a given command descriptor.
   *
   * @param descriptor the command descriptor
   * @return the parser for the command descriptor
   */
  Parser parserFor(Command.Descriptor descriptor);

  /**
   * Returns a short option and its values, in all possible ways it can be
   * formatted according to this option style.
   *
   * @param name the short option name
   * @param values the option values
   * @return all possible formattings for a short option and its values
   * @throws UnsupportedOperationException if short options are not supported
   */
  Set<String> shortOptionFor(String name, String... values);

  /**
   * Returns a long option and its values, in all possible ways it can be
   * formatted according to this option style.
   *
   * @param name the long option name
   * @param values the option values
   * @return all possible formattings for a long option and its values
   * @throws UnsupportedOperationException if long options are not supported
   */
  Set<String> longOptionFor(String name, String... values);

  /**
   * Returns the arguments, in all possible ways it can be formatted according
   * to this option style.
   *
   * @param argument the first argument
   * @param others the remaining arguments
   * @return app possible formattings for the arguments
   * @throws UnsupportedOperationException if arguments are not supported
   */
  Set<String> arguments(String argument, String... others);

}

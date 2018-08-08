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

import be.nepherte.commons.cli.Option;
import be.nepherte.commons.cli.Parser.ParseException;

import java.util.Arrays;

/**
 * Factory class for common parsing exceptions.
 */
final class Exceptions {

  /* This class is added to group exceptions which command line parsers may
   * encounter. The actual implementations are hidden as they are merely to test
   * whether parsers can semantically identify these cases. The user of the api
   * should not care about this other than the occurrence of "an" error. At a
   * later stage, one might consider to open this up to api users. */

  /**
   * Creates a new {@code Exceptions}.
   */
  private Exceptions() {
    // Hide constructor of factory method class.
  }

  /**
   * Exception thrown when a command encounters an unrecognized token.
   */
  static final class UnrecognizedTokenException extends ParseException {
    private static final long serialVersionUID = 2418916161039553401L;

    /**
     * Creates a new {@code UnrecognizedTokenException}.
     *
     * @param token the unrecognized token
     */
    UnrecognizedTokenException(String token) {
      super("Unrecognized token [" + token + "]");
    }
  }

  /**
   * Exception thrown when a command is missing a required option.
   */
  static final class MissingOptionException extends ParseException {
    private static final long serialVersionUID = -750674298160485010L;

    /**
     * Creates a new {@code MissingOptionException}.
     *
     * @param template the template of the missing option
     */
    MissingOptionException(Option.Template template) {
      super("Missing required option [" + template + "]");
    }
  }

  /**
   * Exception thrown when a command is missing an option from a required group.
   */
  static final class MissingGroupException extends ParseException {
    private static final long serialVersionUID = 4611455865956293357L;

    /**
     * Creates a new {@code MissingGroupException}.
     *
     * @param group the missing group
     */
    MissingGroupException(Option.Group group) {
      super("Missing required group [" + group + "]");
    }
  }

  /**
   * Exception thrown when a command is missing a required argument.
   */
  static final class MissingArgumentException extends ParseException {
    private static final long serialVersionUID = 6038171462219781253L;

    /**
     * Creates a new {@code MissingArgumentException}.
     *
     * @param command the command missing an argument
     */
    MissingArgumentException(String command) {
      super("Missing argument(s) for command [" + command + "]");
    }
  }

  /**
   * Exception thrown when a command has too many arguments.
   */
  static final class TooManyArgumentsException extends ParseException {
    private static final long serialVersionUID = -7644793293717786265L;

    /**
     * Creates a new {@code TooManyArgumentsException}.
     *
     * @param command the command with too many arguments
     */
    TooManyArgumentsException(String command) {
      super("Too many arguments for command [" + command + "]");
    }
  }

  /**
   * Exception thrown when an option is missing a value.
   */
  static final class MissingValueException extends ParseException {
    private static final long serialVersionUID = -718092007041448691L;

    /**
     * Creates a new {@code MissingValueException}.
     *
     * @param template the template of the option missing a value
     */
    MissingValueException(Option.Template template) {
      super("Missing value for option [" + template + "]");
    }
  }

  /**
   * Exception thrown when an option has too many values.
   */
  static final class TooManyValuesException extends ParseException {
    private static final long serialVersionUID = -1451812468054340266L;

    /**
     * Creates a new {@code TooManyValuesException}.
     *
     * @param template the template of the option with too many values
     */
    TooManyValuesException(Option.Template template) {
      super("Too many values for option [" + template + "]");
    }
  }

  /**
   * Exception thrown when a command has mutually exclusive options.
   */
  static final class ExclusiveOptionsException extends ParseException {
    private static final long serialVersionUID = 356042360910950845L;

    /**
     * Creates a new {@code ExclusiveOptionsException}.
     *
     * @param templates the templates of the mutually exclusive options
     */
    ExclusiveOptionsException(Option.Template... templates) {
      super("Mutually exclusive options " + Arrays.toString(templates));
    }
  }
}

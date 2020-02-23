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

/**
 * An object that parses tokens according to a {@link Command.Descriptor
 * Descriptor}. The result is an <em>immutable</em> representation of a {@link
 * Command}, accompanied by the options and arguments that were used to launch
 * the application.
 */
public interface Parser {

  /**
   * Parses tokens according to a command descriptor. Implementations must
   * specify the expected format.
   *
   * @param tokens the command line tokens
   * @return the parsed command line
   * @throws ParseException unable to parse tokens
   */
  Command parse(String[] tokens) throws ParseException;

  /**
   * Exception thrown when a parser encounters an issue.
   */
  class ParseException extends Exception {
    private static final long serialVersionUID = 2624991404609831294L;

    /**
     * Creates a new {@code Exception}.
     *
     * @param message the error message
     */
    protected ParseException(String message) {
      super(message);
    }
  }
}

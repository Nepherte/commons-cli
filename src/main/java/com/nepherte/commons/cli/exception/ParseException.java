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
package com.nepherte.commons.cli.exception;

/**
 * Exception thrown when a parser encounters an issue.
 */
public class ParseException extends Exception {

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

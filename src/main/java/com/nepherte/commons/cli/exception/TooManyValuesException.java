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

import com.nepherte.commons.cli.Option;

/**
 * Exception thrown when an option has too many values.
 */
public final class TooManyValuesException extends ParseException {

  private static final long serialVersionUID = -1451812468054340266L;

  /**
   * Creates a new {@code TooManyValuesException}.
   *
   * @param template the template of the option with too many values
   */
  public TooManyValuesException(Option.Template template) {
    super("Too many values for option [" + template + "]");
  }
}

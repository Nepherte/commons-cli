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
package be.nepherte.commons.cli.internal;

import java.util.regex.Pattern;

/**
 * Utility class for {@code String}-related functionality.
 */
public final class Strings {

  /** Pattern that indicates a whitespace character. */
  private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s");

  /**
   * Creates a new instance.
   */
  private Strings() {
    // Hide constructor.
  }

  /**
   * Converts blank strings to {@code null}.
   *
   * @param string the string to convert
   * @return {@code null} if blank
   */
  public static String blankToNull(String string) {
    return isNullOrBlank(string) ? null : string;
  }

  /**
   * Converts empty strings to {@code null}.
   *
   * @param string the string to convert
   * @return {@code null} if empty
   */
  public static String emptyToNull(String string) {
    return isNullOrEmpty(string) ? null : string;
  }

  /**
   * Converts {@code null} to empty strings.
   *
   * @param string the string to convert
   * @return empty if {@code null}
   */
  public static String nullToEmpty(String string) {
    return isNullOrEmpty(string) ? "" : string;
  }

  /**
   * Returns true if the string is {@code null} or empty.
   *
   * @param string the string to test
   * @return true if {@code null} or empty
   */
  public static boolean isNullOrEmpty(String string) {
    return string == null || string.isEmpty();
  }

  /**
   * Indicates if the string is {@code null} or blank.
   *
   * @param string the string to test
   * @return true if {@code null} or blank
   */
  public static boolean isNullOrBlank(String string) {
    return string == null || string.isBlank();
  }

  /**
   * Indicates if the string contains whitespace.
   *
   * @param string the string to test
   * @return true if contains whitespace
   */
  public static boolean containsWhitespace(String string) {
    return WHITESPACE_PATTERN.matcher(string).find();
  }
}

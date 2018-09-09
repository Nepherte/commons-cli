/*
 * Copyright 2012-2018 Bart Verhoeven
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

import java.util.Arrays;
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
   * Converts whitespace strings to {@code null}.
   *
   * @param string the string to convert
   * @return {@code null} if whitespace-only
   */
  public static String whitespaceToNull(String string) {
    return isNullOrWhitespace(string) ? null : string;
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
   * Indicates if the string is {@code null} or whitespace.
   *
   * @param string the string to test
   * @return true if {@code null} or whitespace
   */
  public static boolean isNullOrWhitespace(String string) {
    return string == null || string.trim().isEmpty();
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

  /**
   * Repeats a character by a given amount.
   *
   * @param c the character to repeat
   * @param n the amount to repeat it
   * @return a character repeated by the given amount
   * @throws IllegalArgumentException {@code n} is negative
   */
  public static String repeat(char c, int n) {
    if (n < 0) {
      throw new IllegalArgumentException(
        "Cannot repeat character [" + c + "] by negative amount [" + n + "]"
      );
    }

    char[] data = new char[n];
    Arrays.fill(data, c);
    return String.valueOf(data);
  }
}

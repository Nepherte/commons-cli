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
package com.nepherte.commons.test;

import com.nepherte.commons.cli.internal.OptionFormat;

import org.hamcrest.Matcher;
import java.util.Optional;

/**
 * This class contains static factory methods for {@code Matchers}.
 */
public final class Matchers {

  private Matchers() {
    // Hide constructor of static factory method class.
  }

  /**
   * Matcher that verifies an item is an {@code Optional} with a given value.
   *
   * @param expected the expected value of the {@code Optional}
   * @param <T> the type of the {@code Optional} value
   */
  public static <T> Matcher<Optional<T>> optionalWithValue(T expected) {
    return new OptionalWithValue<>(expected);
  }

  /**
   * Matcher that verifies an item is an {@code Optional} with no value.
   *
   * @param <T> the type of the {@code Optional} value
   */
  public static <T> Matcher<Optional<T>> optionalWithNoValue() {
    return new OptionalWithNoValue<>();
  }

  /**
   * Matcher that verifies a {@code Command} has an option with a given name.
   *
   * @param name the name of the option
   */
  public static HasOption hasOption(String name) {
    return new HasOption(name);
  }

  /**
   * Matcher that verifies a {@code Command} has a given argument.
   *
   * @param argument the argument
   */
  public static HasArgument hasArgument(String argument) {
    return new HasArgument(argument);
  }

  /**
   * Matcher that verifies an {@code OptionFormat} has a given set of features.
   *
   * @param features the expected feature set
   */
  public static Matcher<OptionFormat> supportsFeature(int features) {
    return new SupportsFeature(features);
  }
}

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
package com.nepherte.commons.cli.internal;

import com.nepherte.commons.cli.Option;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Utility class for {@code Predicate}-related functionality.
 */
public final class Predicates {

  /**
   * Creates a new {@code Predicates}.
   */
  private  Predicates() {
    // Hide constructor.
  }

  /**
   * Predicate that performs a logical 'not' of another predicate.
   *
   * @param p the predicate to negate
   * @param <T> the type of input for the predicate
   * @return a new predicate that negates the predicate
   */
  public static <T> Predicate<T> not(Predicate<T> p) {
    return p.negate();
  }

  /**
   * Predicate that performs a logical 'and' between two other predicates.
   *
   * @param p1 the first predicate
   * @param p2 the second predicate
   * @param <T> the type of input for the predicate
   * @return a new predicate that combines both predicates
   */
  public static <T> Predicate<T> and(Predicate<T> p1, Predicate <T> p2) {
    return p1.and(p2);
  }

  /**
   * Predicate that performs a logical 'or' between two other predicates.
   *
   * @param p1 the first predicate
   * @param p2 the second predicate
   * @param <T> the type of input for the predicate
   * @return a new predicate that combines both predicates
   */
  public static <T> Predicate<T> or(Predicate<T> p1, Predicate<T> p2) {
    return p1.or(p2);
  }

  /**
   * Predicate that tests for equality.
   *
   * @param expected the expected value
   * @param <T> the type of input for the predicate
   * @return a predicate that tests for equality
   */
  public static <T> Predicate<T> eq(T expected) {
    return actual -> Objects.equals(actual, expected);
  }

  /**
   * Predicate that tests for non-null values.
   *
   * @param <T> the type of input for the predicate
   * @return a new predicate that tests for non-null values
   */
  public static <T> Predicate<T> notNull() {
    return Objects::nonNull;
  }

  /**
   * Predicate that tests for non-null values.
   *
   * @param function the function that provides test values
   * @param <T> the type of input for the predicate
   * @return a new predicate that tests for non-null values
   */
  public static <T> Predicate<T> notNull(Function<T,?> function) {
    return object -> Objects.nonNull(function.apply(object));
  }

  /**
   * Predicate that tests whether values are greater than another value.
   *
   * @param value the value to compare with
   * @param <T> the type of input for the predicate
   * @return a new predicate that compares values
   */
  public static <T> Predicate<Comparable<T>> greaterThan(T value) {
    return comparable -> comparable.compareTo(value) > 0;
  }

  /**
   * Predicate that tests whether values are smaller than another value.
   *
   * @param value the value to compare with
   * @param <T> the type of input for the predicate
   * @return a new predicate that compares values
   */
  public static <T> Predicate<Comparable<T>> smallerThan(T value) {
    return comparable -> comparable.compareTo(value) < 0;
  }

  /**
   * Predicate that tests whether values are in a specified range.
   *
   * @param minValue the minimum value (exclusive)
   * @param maxValue the maximum value (exclusive)
   * @param <T> the type of input for the predicate
   * @return a new predicate that compares values
   */
  public static <T> Predicate<Comparable<T>> between(T minValue, T maxValue) {
    return and(greaterThan(minValue), smallerThan(maxValue));
  }

  /**
   * Predicate that tests for strings with no spaces.
   *
   * @return a new predicate that tests for strings with no spaces
   */
  public static Predicate<String> noSpace() {
    return not(Strings::containsWhitespace);
  }

  /**
   * Predicate that tests for non-empty strings.
   *
   * @return a new predicate that tests for non-empty strings
   */
  public static Predicate<String> notEmpty() {
    return not(Strings::isNullOrEmpty);
  }

  /**
   * Predicate that tests for non-blank strings.
   *
   * @return a new predicate that tests for non-blank strings
   */
  public static Predicate<String> notBlank() {
    return not(Strings::isNullOrBlank);
  }

  /**
   * Predicate that tests for non-required templates.
   *
   * @return a new predicate that tests for non-required templates
   */
  public static Predicate<Option.Template> notRequired() {
    return not(Option.Template::isRequired);
  }
}

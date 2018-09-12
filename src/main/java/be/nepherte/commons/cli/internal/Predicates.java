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
package be.nepherte.commons.cli.internal;

import java.util.Objects;
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
   * Predicate that tests for null values.
   *
   * @param <T> the type of input for the predicate.
   * @return a new predicate that tests for null values
   */
  public static <T> Predicate<T> isNull() {
    return Objects::isNull;
  }

  /**
   * Predicate that tests for non-null values.
   *
   * @param <T> the type of input for the predicate
   * @return a new predicate that tests for non-null values
   */
  public static <T> Predicate<T> nonNull() {
    return Objects::nonNull;
  }
}

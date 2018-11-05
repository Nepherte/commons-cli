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

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Utility class to enforce preconditions.
 */
public final class Preconditions {

  /**
   * Creates a new instance.
   */
  private Preconditions() {
    // Hide constructor.
  }

  /**
   * Requires that an argument matches a given predicate.
   *
   * @param argument the argument to evaluate
   * @param predicate the predicate to evaluate
   * @param error the error message template
   * @param <T> the type of the argument
   * @return the argument that was tested
   * @throws IllegalArgumentException the argument does not match
   */
  public static <T> T requireArg(T argument, Predicate<T> predicate,
  String error) throws IllegalArgumentException {

    return require(argument, predicate, error, IllegalArgumentException::new);
  }

  /**
   * Requires that an object matches a given predicate.
   *
   * @param object the object to evaluate
   * @param predicate the predicate to evaluate
   * @param error the error message template
   * @param <T> the type of the object
   * @return the object that was tested
   * @throws IllegalStateException the object does not match
   */
  public static <T> T requireState(T object, Predicate<T> predicate,
  String error) throws IllegalStateException {

    return require(object, predicate, error, IllegalStateException::new);
  }

  /**
   * Requires that an input matches a given predicate.
   *
   * @param input the input to evaluate
   * @param predicate the predicate to evaluate
   * @param error the error message template
   * @param exception the exception to throw
   * @param <T> the type of the object
   * @return the input that was tested
   * @throws RuntimeException the input doesn't match the predicate
   */
  private static <T> T require(T input, Predicate<T> predicate,
  String error, Function<String, RuntimeException> exception) {

    if (!predicate.test(input)) {
      String errorMessage = String.format(error, input);
      throw exception.apply(errorMessage);
    }

    return input;
  }
}

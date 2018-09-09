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
   * @param argument the argument to inspect
   * @param predicate the predicate evaluating the argument
   * @param errorMessage the error message template
   * @param <T> the type of the argument
   * @return the argument that was tested
   * @throws IllegalArgumentException the argument does not match
   */
  public static <T> T requireArg(T argument, Predicate<T> predicate,
  String errorMessage) throws IllegalArgumentException {

    if (!predicate.test(argument)) {
      String formattedMessage = String.format(errorMessage, argument);
      throw new IllegalArgumentException(formattedMessage);
    }

    return argument;
  }

  /**
   * Requires that an object matches a given state.
   *
   * @param object the object whose state to inspect
   * @param function the function evaluating the state
   * @param errorMessage the error message template
   * @param <T> the type of the object
   * @throws IllegalStateException the object is in an inappropriate state
   */
  public static <T> void requireState(T object, Function<T, Boolean> function,
  String errorMessage) throws IllegalStateException {

    if (Boolean.FALSE.equals(function.apply(object))) {
      String formattedMessage = String.format(errorMessage, object);
      throw new IllegalStateException(formattedMessage);
    }
  }
}

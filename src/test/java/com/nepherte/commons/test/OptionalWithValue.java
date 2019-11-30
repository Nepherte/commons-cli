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
package com.nepherte.commons.test;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.util.Objects;
import java.util.Optional;

/**
 * Matcher that verifies an item is an {@code Optional} with a given value.
 *
 * @param <T> the type of the expected value
 */
final class OptionalWithValue<T> extends BaseMatcher<Optional<T>> {

  private final T expected;

  /**
   * Creates a new {@code OptionalWithValue}.
   *
   * @param expected the expected value of the {@code Optional}
   */
  OptionalWithValue(T expected) {
    this.expected = expected;
  }

  @Override
  public boolean matches(Object item) {
    Optional<?> optional = (Optional<?>) item;

    if (!optional.isPresent()) {
      return false;
    }

    Object optionalValue = optional.get();
    return Objects.equals(expected, optionalValue);
  }

  @Override
  public void describeTo(Description description) {
    description.appendValue(Optional.of(expected));
  }
}

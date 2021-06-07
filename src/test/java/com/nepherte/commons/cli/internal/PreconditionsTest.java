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
package com.nepherte.commons.cli.internal;

import org.junit.jupiter.api.Test;
import java.util.function.Predicate;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import static com.nepherte.commons.cli.internal.Preconditions.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test that covers {@link Preconditions}.
 */
class PreconditionsTest {

  @Test
  void requireArgSuccess() {
    Object input = new Object();

    Predicate<Object> predicate = object -> true;
    Object result = requireArg(input, predicate, "ignored");

    assertThat(result, is(input));
  }

  @Test
  void requireArgException() {
    Integer integer = Integer.valueOf(3);
    Predicate<Object> predicate = object -> false;
    assertThrows(IllegalArgumentException.class, () ->
      requireArg(integer, predicate, "Value is %d"), "Value is 3");
  }

  @Test
  void requireStateSuccess() {
    Object input = new Object();

    Predicate<Object> predicate = object -> true;
    Object result = requireState(input, predicate, "ignored");

    assertThat(result, is(input));
  }

  @Test
  void requireStateException() {
    Integer integer = Integer.valueOf(3);
    Predicate<Integer> predicate = aObject -> false;
    assertThrows(IllegalStateException.class, () ->
      requireState(integer, predicate, "Value is %d"), "Value is 3");

  }
}
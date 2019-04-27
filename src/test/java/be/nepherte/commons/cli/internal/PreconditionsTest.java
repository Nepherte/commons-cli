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

import org.junit.jupiter.api.Test;
import java.util.function.Predicate;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import static be.nepherte.commons.cli.internal.Preconditions.*;
import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test that covers {@link Preconditions}.
 */
class PreconditionsTest {

  @Test
  void requireArgSuccess() {
    Object object = new Object();

    //noinspection unchecked all good.
    Predicate<Object> predicate = mock(Predicate.class);
    when(predicate.test(any(Object.class))).thenReturn(true);

    Object result = requireArg(object, predicate, "ignored");
    assertThat(result, is(object));
    verify(predicate).test(object);
  }

  @Test
  void requireArgException() {
    Integer integer = Integer.valueOf(3);

    //noinspection unchecked all good.
    Predicate<Integer> predicate = mock(Predicate.class);
    when(predicate.test(any(Integer.class))).thenReturn(false);

    assertThrows(IllegalArgumentException.class, () ->
      requireArg(integer, predicate, "Value is %d"), "Value is 3");

    verify(predicate).test(integer);
  }

  @Test
  void requireStateSuccess() {
    Object object = new Object();

    //noinspection unchecked all good.
    Predicate<Object> predicate = mock(Predicate.class);
    when(predicate.test(object)).thenReturn(true);

    Object result = requireState(object, predicate, "ignored");
    assertThat(result, is(object));
    verify(predicate).test(object);
  }

  @Test
  void requireStateException() {
    Integer integer = Integer.valueOf(3);

    //noinspection unchecked all good.
    Predicate<Integer> predicate = mock(Predicate.class);
    when(predicate.test(integer)).thenReturn(false);

    assertThrows(IllegalStateException.class, () ->
      requireState(integer, predicate, "Value is %d"), "Value is 3");

    verify(predicate).test(integer);
  }
}
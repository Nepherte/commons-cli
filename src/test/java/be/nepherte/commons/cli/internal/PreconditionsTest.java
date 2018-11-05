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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.function.Predicate;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test that covers {@link Preconditions}.
 */
public class PreconditionsTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void requireArg() {
    Object object = new Object();

    //noinspection unchecked all good.
    Predicate<Object> predicate = mock(Predicate.class);
    when(predicate.test(any(Object.class))).thenReturn(true);

    Object result = Preconditions.requireArg(object, predicate, "ignored");
    assertThat(result, is(object));
    verify(predicate).test(object);
  }

  @Test
  public void requireArgException() {
    Integer integer = Integer.valueOf(3);

    //noinspection unchecked all good.
    Predicate<Integer> predicate = mock(Predicate.class);
    when(predicate.test(any(Integer.class))).thenReturn(false);

    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("Value is 3");

    Preconditions.requireArg(integer, predicate, "Value is %d");
  }

  @Test
  public void requireState() {
    Object object = new Object();

    //noinspection unchecked all good.
    Predicate<Object> predicate = mock(Predicate.class);
    when(predicate.test(object)).thenReturn(true);

    Object result = Preconditions.requireState(object, predicate, "ignored");
    assertThat(result, is(object));
    verify(predicate).test(object);
  }

  @Test
  public void requireStateException() {
    Integer integer = Integer.valueOf(3);

    //noinspection unchecked all good.
    Predicate<Integer> predicate = mock(Predicate.class);
    when(predicate.test(integer)).thenReturn(false);

    thrown.expect(IllegalStateException.class);
    thrown.expectMessage("Value is 3");

    Preconditions.requireState(integer, predicate, "Value is %d");
    verify(predicate).test(integer);
  }
}
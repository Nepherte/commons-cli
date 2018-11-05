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

import be.nepherte.commons.cli.Option;

import org.junit.Test;

import java.util.function.Function;
import java.util.function.Predicate;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test that covers {@link Predicates}.
 */
public class PredicatesTest {

  @Test
  public void not() {
    Predicate<Object> p1 = input -> true;
    Predicate<Object> p2 = Predicates.not(p1);
    assertThat(p2.test(new Object()), is(false));
  }

  @Test
  public void and() {
    Object object = new Object();

    Predicate<Object> p1 = ignored -> false;
    Predicate<Object> p2 = ignored -> true;

    Predicate<Object> p11 = Predicates.and(p1, p1);
    Predicate<Object> p12 = Predicates.and(p1, p2);
    Predicate<Object> p21 = Predicates.and(p2, p1);
    Predicate<Object> p22 = Predicates.and(p2, p2);

    assertThat(p11.test(object), is(false));
    assertThat(p12.test(object), is(false));
    assertThat(p21.test(object), is(false));
    assertThat(p22.test(object), is(true));
  }

  @Test
  public void or() {
    Object object = new Object();

    Predicate<Object> p1 = ignored -> false;
    Predicate<Object> p2 = ignored -> true;

    Predicate<Object> p11 = Predicates.or(p1, p1);
    Predicate<Object> p12 = Predicates.or(p1, p2);
    Predicate<Object> p21 = Predicates.or(p2, p1);
    Predicate<Object> p22 = Predicates.or(p2, p2);

    assertThat(p11.test(object), is(false));
    assertThat(p12.test(object), is(true));
    assertThat(p21.test(object), is(true));
    assertThat(p22.test(object), is(true));
  }

  @Test
  public void eq() {
    Object object1 = new Object();
    Object object2 = new Object();

    Predicate<Object> predicate = Predicates.eq(object1);
    assertThat(predicate.test(object1), is(true));
    assertThat(predicate.test(object2), is(false));
  }

  @Test
  public void isNull() {
    Predicate<Object> predicate = Predicates.isNull();
    assertThat(predicate.test(new Object()), is(false));
    assertThat(predicate.test(null), is(true));
  }

  @Test
  public void notNull() {
    Predicate<Object> predicate = Predicates.notNull();
    assertThat(predicate.test(new Object()), is(true));
    assertThat(predicate.test(null), is(false));
  }

  @Test
  public void notNullFunction() {
    Object nullObject = null;
    Object object = mock(Object.class);

    //noinspection unchecked these are not the droids you're looking for.
    Function<Object,Object> function = mock(Function.class);
    when(function.apply(anyString())).thenReturn(object, nullObject);

    Predicate<Object> predicate = Predicates.notNull(function);
    assertThat(predicate.test("ignored"), is(true));
    assertThat(predicate.test("ignored"), is(false));
  }

  @Test
  public void greaterThan() {
    Predicate<Comparable<Integer>> predicate = Predicates.greaterThan(0);
    assertThat(predicate.test(1), is(true));
    assertThat(predicate.test(0), is(false));
    assertThat(predicate.test(-1), is(false));
  }

  @Test
  public void smallerThan() {
    Predicate<Comparable<Integer>> predicate = Predicates.smallerThan(0);
    assertThat(predicate.test(1), is(false));
    assertThat(predicate.test(0), is(false));
    assertThat(predicate.test(-1), is(true));
  }

  @Test
  public void between() {
    Predicate<Comparable<Integer>> predicate = Predicates.between(0, 9);
    assertThat(predicate.test(-1), is(false));
    assertThat(predicate.test(0), is(false));
    assertThat(predicate.test(1), is(true));
    assertThat(predicate.test(8), is(true));
    assertThat(predicate.test(9), is(false));
    assertThat(predicate.test(10), is(false));
  }

  @Test
  public void noSpace() {
    Predicate<String> predicate = Predicates.noSpace();
    assertThat(predicate.test("a space"), is(false));
    assertThat(predicate.test("nospace"), is(true));
  }

  @Test
  public void notEmpty() {
    Predicate<String> predicate = Predicates.notEmpty();
    assertThat(predicate.test(""), is(false));
    assertThat(predicate.test("a"), is(true));
  }

  @Test
  public void notBlank() {
    Predicate<String> predicate = Predicates.notBlank();
    assertThat(predicate.test("   "), is(false));
    assertThat(predicate.test("not blank"), is(true));
  }

  @Test
  public void notRequired() {
    Option.Template template1 = mock(Option.Template.class);
    when(template1.isRequired()).thenReturn(false);

    Option.Template template2 = mock(Option.Template.class);
    when(template2.isRequired()).thenReturn(true);

    Predicate<Option.Template> predicate = Predicates.notRequired();
    assertThat(predicate.test(template1), is(true));
    assertThat(predicate.test(template2 ), is(false));
  }
}

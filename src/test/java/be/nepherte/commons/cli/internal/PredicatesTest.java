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

import org.junit.Test;

import java.util.function.Predicate;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

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
  public void nonNull() {
    Predicate<Object> predicate = Predicates.nonNull();
    assertThat(predicate.test(new Object()), is(true));
    assertThat(predicate.test(null), is(false));
  }
}

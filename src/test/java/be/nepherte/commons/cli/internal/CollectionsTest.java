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
package be.nepherte.commons.cli.internal;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.collection.IsIterableContainingInOrder.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test that covers {@link Collections}.
 */
class CollectionsTest {

  @Test
  void identicalList() {
    Collection<String> mutable = Arrays.asList("a", "b", "c");
    List<String> immutable = Collections.immutableList(mutable);
    assertThat(immutable, contains("a", "b", "c"));
  }

  @Test
  void immutableList() {
    List<String> mutable = Arrays.asList("a", "b", "c");
    assertThrows(UnsupportedOperationException.class,
      () -> Collections.immutableList(mutable).set(0, "d"));
  }

  @Test
  void immutableListNull() {
    assertThrows(NullPointerException.class,
      () -> Collections.immutableList(null));
  }

  @Test
  void identicalListOf() {
    String[] elements = {"a", "b", "c", "d", "e"};
    List<String> immutable = Collections.immutableListOf(elements);
    assertThat(immutable, contains("a", "b", "c", "d", "e"));
  }

  @Test
  void immutableListOf() {
    String[] elements = {"a", "b", "c", "d", "e"};
    assertThrows(UnsupportedOperationException.class,
      () -> Collections.immutableListOf(elements).set(0, "d"));
  }

  @Test
  void immutableListOfNull() {
    assertThrows(NullPointerException.class,
      () -> Collections.immutableListOf((Object[]) null));
  }

  @Test
  void identicalSet() {
    Collection<String> mutable = new HashSet<>(Arrays.asList("a", "b", "c"));
    Set<String> immutable = Collections.immutableSet(mutable);
    assertThat(immutable, contains("a", "b", "c"));
  }

  @Test
  void immutableSet() {
    Collection<String> mutable = new HashSet<>(Arrays.asList("a", "b", "c"));
    assertThrows(UnsupportedOperationException.class,
      () -> Collections.immutableSet(mutable).add("d"));
  }

  @Test
  void immutableSetNull() {
    assertThrows(NullPointerException.class,
      () -> Collections.immutableSet(null));
  }

  @Test
  void identicalSetOf() {
    String[] elements = {"a", "b", "c"};
    Set<String> immutable = Collections.immutableSetOf(elements);
    assertThat(immutable, contains("a", "b", "c"));
  }

  @Test
  void immutableSetOf() {
    String[] elements = {"a", "b", "c"};
    assertThrows(UnsupportedOperationException.class,
      () -> Collections.immutableSetOf(elements).add("d"));
  }

  @Test
  void immutableSetOfNull() {
    assertThrows(NullPointerException.class,
      () -> Collections.immutableSetOf((Object[]) null));
  }
}

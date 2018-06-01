/*
 * Copyright 2012-2018 Bart Verhoeven
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
package be.nepherte.commons.cli;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertThat;

/**
 * Test that covers {@link Collections}.
 */
public class CollectionsTest {

  @Test
  public void identicalList() {
    Collection<String> mutable = Arrays.asList("a", "b", "c");
    List<String> immutable = Collections.immutableList(mutable);
    assertThat(immutable, contains("a", "b", "c"));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void immutableList() {
    List<String> mutable = Arrays.asList("a", "b", "c");
    Collections.immutableList(mutable).set(0, "d");
  }

  @Test(expected = NullPointerException.class)
  public void immutableListNull() {
    Collections.immutableList(null);
  }

  @Test
  public void identicalListOf() {
    String[] elements = {"a", "b", "c"};
    List<String> immutable = Collections.immutableListOf(elements);
    assertThat(immutable, contains("a", "b", "c"));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void immutableListOf() {
    String[] elements = {"a", "b", "c"};
    Collections.immutableListOf(elements).set(0, "d");
  }

  @Test(expected = NullPointerException.class)
  public void immutableListOfNull() {
    Collections.immutableListOf((Object[]) null);
  }

  @Test
  public void identicalSet() {
    Collection<String> mutable = new HashSet<>(Arrays.asList("a", "b", "c"));
    Set<String> immutable = Collections.immutableSet(mutable);
    assertThat(immutable, contains("a", "b", "c"));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void immutableSet() {
    Collection<String> mutable = new HashSet<>(Arrays.asList("a", "b", "c"));
    Collections.immutableSet(mutable).add("d");
  }

  @Test(expected = NullPointerException.class)
  public void immutableSetNull() {
    Collections.immutableSet(null);
  }

  @Test
  public void identicalSetOf() {
    String[] elements = {"a", "b", "c"};
    Set<String> immutable = Collections.immutableSetOf(elements);
    assertThat(immutable, contains("a", "b", "c"));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void immutableSetOf() {
    String[] elements = {"a", "b", "c"};
    Collections.immutableSetOf(elements).add("d");
  }

  @Test(expected = NullPointerException.class)
  public void immutableSetOfNull() {
    Collections.immutableSetOf((Object[]) null);
  }
}

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * Utility class for {@code Collection}-related functionality.
 */
public final class Collections {

  /**
   * Creates a new {@code Collections}.
   */
  private Collections() {
    // Hide constructor.
  }

  /**
   * Creates an immutable list from a collection. Changes made afterwards, do
   * not affect the returned instance.
   *
   * @param collection the elements for the new list
   * @param <T> the type of elements in the new list
   * @return an immutable list containing the elements
   * @throws NullPointerException the collection is {@code null}
   */
  public static <T> List<T> immutableList(Collection<? extends T> collection) {
    requireNonNull(collection, "Cannot create immutable list of [null]");

    if (collection.isEmpty()) {
      return java.util.Collections.emptyList();
    }

    if (collection.size() == 1) {
      T element = collection.iterator().next();
      return java.util.Collections.singletonList(element);
    }

    List<? extends T> mutableList = new ArrayList<>(collection);
    return java.util.Collections.unmodifiableList(mutableList);
  }

  /**
   * Creates an immutable list from the elements. Changes made afterwards, do
   * not affect the returned instance.
   *
   * @param elements the elements for the new list
   * @param <T> the type of elements in the new list
   * @return an immutable list containing the elements
   * @throws NullPointerException the elements are {@code null}
   */
  @SafeVarargs
  public static <T> List<T> immutableListOf(T... elements) {
    requireNonNull(elements, "Cannot create immutable list of [null]");

    if (elements.length == 0) {
      return java.util.Collections.emptyList();
    }

    if (elements.length == 1) {
      return java.util.Collections.singletonList(elements[0]);
    }

    List<T> mutableList = Arrays.asList(elements);
    return java.util.Collections.unmodifiableList(mutableList);
  }

  /**
   * Creates an immutable set from a collection. Changes made afterwards, do not
   * affect the returned instance.
   *
   * @param collection the elements for the new set
   * @param <T> the type of elements in the new set
   * @return an immutable set containing the elements
   * @throws NullPointerException the collection is {@code null}
   */
  public static <T> Set<T> immutableSet(Collection<? extends T> collection) {
    requireNonNull(collection, "Cannot create immutable set of [null]");

    if (collection.isEmpty()) {
      return java.util.Collections.emptySet();
    }

    if (collection.size() == 1) {
      T element = collection.iterator().next();
      return java.util.Collections.singleton(element);
    }

    return java.util.Collections.unmodifiableSet(new HashSet<>(collection));
  }

  /**
   * Creates an immutable set from the elements. Changes made afterwards, do not
   * affect the returned instance.
   *
   * @param elements the elements for the new set
   * @param <T> the type of elements in the new set
   * @return an immutable set containing the elements
   * @throws NullPointerException the elements are {@code null}
   */
  @SafeVarargs
  public static <T> Set<T> immutableSetOf(T... elements) {
    requireNonNull(elements, "Cannot create immutable set of [null]");

    if (elements.length == 0) {
      return java.util.Collections.emptySet();
    }

    if (elements.length == 1) {
      return java.util.Collections.singleton(elements[0]);
    }

    Set<T> mutableSet = new HashSet<>(elements.length);
    java.util.Collections.addAll(mutableSet, elements);
    return java.util.Collections.unmodifiableSet(mutableSet);
  }
}

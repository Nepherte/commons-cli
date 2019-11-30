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
package com.nepherte.commons.cli.internal;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test that covers {@link Strings}.
 */
final class StringsTest {

  @Test
  void isNullOrEmpty() {
    assertThat(Strings.isNullOrEmpty(null), is(true));
    assertThat(Strings.isNullOrEmpty(""), is(true));
    assertThat(Strings.isNullOrEmpty(" "), is(false));
    assertThat(Strings.isNullOrEmpty("\t"), is(false));
    assertThat(Strings.isNullOrEmpty("a"), is(false));
  }

  @Test
  void nullToEmpty() {
    assertThat(Strings.nullToEmpty(null), is(""));
    assertThat(Strings.nullToEmpty(""), is(""));
    assertThat(Strings.nullToEmpty(" "), is(" "));
    assertThat(Strings.nullToEmpty("\t"), is("\t"));
    assertThat(Strings.nullToEmpty("a"), is("a"));
  }

  @Test
  void emptyToNull() {
    assertThat(Strings.emptyToNull(null), nullValue());
    assertThat(Strings.emptyToNull(""), nullValue());
    assertThat(Strings.emptyToNull(" "), is(" "));
    assertThat(Strings.emptyToNull("\t"), is("\t"));
    assertThat(Strings.emptyToNull("a"), is("a"));
  }

  @Test
  void isNullOrBlank() {
    assertThat(Strings.isNullOrBlank(null), is(true));
    assertThat(Strings.isNullOrBlank(""), is(true));
    assertThat(Strings.isNullOrBlank("  "), is(true));
    assertThat(Strings.isNullOrBlank("\t"), is(true));
    assertThat(Strings.isNullOrBlank("a"), is(false));
  }

  @Test
  void blankToNull() {
    assertThat(Strings.blankToNull(null), nullValue());
    assertThat(Strings.blankToNull(""), nullValue());
    assertThat(Strings.blankToNull("  "), nullValue());
    assertThat(Strings.blankToNull("\t"), nullValue());
    assertThat(Strings.blankToNull("a"), is("a"));
  }

  @Test
  void containsWhitespace() {
    assertThat(Strings.containsWhitespace("  "), is(true));
    assertThat(Strings.containsWhitespace("\t"), is(true));
    assertThat(Strings.containsWhitespace("a\ta"), is(true));
    assertThat(Strings.containsWhitespace("aa"), is(false));
  }

  @Test
  void containsWhitespaceNull() {
    assertThrows(NullPointerException.class,
      () -> Strings.containsWhitespace(null));
  }
}

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
package be.nepherte.commons.cli.internal;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Test that covers {@link Strings}.
 */
public final class StringsTest {

  @Test
  public void isNullOrEmpty() {
    assertThat(Strings.isNullOrEmpty(null), is(true));
    assertThat(Strings.isNullOrEmpty(""), is(true));
    assertThat(Strings.isNullOrEmpty(" "), is(false));
    assertThat(Strings.isNullOrEmpty("\t"), is(false));
    assertThat(Strings.isNullOrEmpty("a"), is(false));
  }

  @Test
  public void nullToEmpty() {
    assertThat(Strings.nullToEmpty(null), is(""));
    assertThat(Strings.nullToEmpty(""), is(""));
    assertThat(Strings.nullToEmpty(" "), is(" "));
    assertThat(Strings.nullToEmpty("\t"), is("\t"));
    assertThat(Strings.nullToEmpty("a"), is("a"));
  }

  @Test
  public void emptyToNull() {
    assertThat(Strings.emptyToNull(null), nullValue());
    assertThat(Strings.emptyToNull(""), nullValue());
    assertThat(Strings.emptyToNull(" "), is(" "));
    assertThat(Strings.emptyToNull("\t"), is("\t"));
    assertThat(Strings.emptyToNull("a"), is("a"));
  }

  @Test
  public void isNullOrWhitespace() {
    assertThat(Strings.isNullOrWhitespace(null), is(true));
    assertThat(Strings.isNullOrWhitespace(""), is(true));
    assertThat(Strings.isNullOrWhitespace("  "), is(true));
    assertThat(Strings.isNullOrWhitespace("\t"), is(true));
    assertThat(Strings.isNullOrWhitespace("a"), is(false));
  }

  @Test
  public void whitespaceToNull() {
    assertThat(Strings.whitespaceToNull(null), nullValue());
    assertThat(Strings.whitespaceToNull(""), nullValue());
    assertThat(Strings.whitespaceToNull("  "), nullValue());
    assertThat(Strings.whitespaceToNull("\t"), nullValue());
    assertThat(Strings.whitespaceToNull("a"), is("a"));
  }

  @Test
  public void containsWhitespace() {
    assertThat(Strings.containsWhitespace("  "), is(true));
    assertThat(Strings.containsWhitespace("\t"), is(true));
    assertThat(Strings.containsWhitespace("a\ta"), is(true));
    assertThat(Strings.containsWhitespace("aa"), is(false));
  }

  @Test(expected = NullPointerException.class)
  public void containsWhitespaceNull() {
    Strings.containsWhitespace(null);
  }

  @Test
  public void repeat() {
    String result = Strings.repeat('a', 6);
    assertThat(result, is("aaaaaa"));
  }

  @Test
  public void repeatZeroTimes() {
    String result = Strings.repeat('a', 0);
    assertThat(result.isEmpty(), is(true));
  }

  @Test(expected = IllegalArgumentException.class)
  public void repeatNegativeAmount() {
    Strings.repeat('a', -1);
  }
}

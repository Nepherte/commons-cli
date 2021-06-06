/*
 * Copyright 2012-2020 Bart Verhoeven
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

import com.nepherte.commons.cli.Command;
import com.nepherte.commons.cli.Parser;
import com.nepherte.commons.cli.parser.GnuParser;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.nepherte.commons.cli.internal.OptionFormat.*;
import static com.nepherte.commons.test.Matchers.*;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

/**
 * Test that covers the GNU-style option format.
 */
public class GnuOptionFormatTest {

  @Test
  public void supportedFeatures() {
    OptionFormat optionFormat = OptionFormats.gnu();
    assertThat(optionFormat, supportsFeature(SHORT_OPTION_FEATURE));
    assertThat(optionFormat, supportsFeature(LONG_OPTION_FEATURE));
    assertThat(optionFormat, supportsFeature(GROUP_OPTION_FEATURE));
    assertThat(optionFormat, supportsFeature(ARGUMENT_FEATURE));
  }

  @Test
  public void parser() {
    Command.Descriptor descriptor = mock(Command.Descriptor.class);
    Parser parser = OptionFormats.gnu().parserFor(descriptor);
    assertThat(parser, instanceOf(GnuParser.class));
  }

  @Test
  public void shortOption() {
    List<String> formattings = OptionFormats.gnu().shortOptionFor("a");
    assertThat(formattings, containsInAnyOrder("-a"));
  }

  @Test
  public void shortOptionWithValue() {
    List<String> formattings = OptionFormats.gnu().shortOptionFor("a", "1");
    assertThat(formattings, containsInAnyOrder("-a=1"));
  }

  @Test
  public void shortOptionWithValues() {
    List<String> formattings = OptionFormats.gnu().shortOptionFor("a", "1", "2");
    assertThat(formattings, containsInAnyOrder("-a=1,2"));
  }

  @Test
  public void shortOptions() {
    List<String> formattings = OptionFormats.gnu().shortOptionsFor("a", "b");
    assertThat(formattings, containsInAnyOrder("-a -b"));
  }

  @Test
  public void longOption() {
    List<String> formattings = OptionFormats.gnu().longOptionFor("a");
    assertThat(formattings, containsInAnyOrder("--a"));
  }

  @Test
  public void longOptionWithValue() {
    List<String> formattings = OptionFormats.gnu().longOptionFor("a", "1");
    assertThat(formattings, containsInAnyOrder("--a=1"));
  }

  @Test
  public void longOptionWithValues() {
    List<String> formattings = OptionFormats.gnu().longOptionFor("a", "1", "2");
    assertThat(formattings, containsInAnyOrder("--a=1,2"));
  }

  @Test
  public void longOptions() {
    List<String> formattings = OptionFormats.gnu().longOptionsFor("a", "b");
    assertThat(formattings, containsInAnyOrder("--a --b"));
  }

  @Test
  public void arguments() {
    List<String> formattings = OptionFormats.gnu().argumentsFor("foo", "bar");
    assertThat(formattings, containsInAnyOrder("foo bar"));
  }

  @Test
  public void optionsAndArgs() {
    List<String> formattings = OptionFormats.gnu().optionsAndArgsFor(
      new String[]{"a"}, new String[]{"b"}, new String[]{"foo"});
    assertThat(formattings, containsInAnyOrder("-a --b foo", "-a --b -- foo"));
  }
}

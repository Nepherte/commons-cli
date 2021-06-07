/*
 * Copyright 2012-2021 Bart Verhoeven
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

import java.util.List;
import java.util.stream.Stream;

/**
 * Class with factory methods for various option formats.
 */
public final class OptionFormats {

  private OptionFormats() {
  }

  /**
   * Returns all supported option format.
   *
   * @return all supported option format
   */
  public static Stream<OptionFormat> all() {
    return Stream.of(gnu());
  }

  /**
   * Returns the GNU-style option format.
   *
   * @return the GNU-style option format
   */
  public static OptionFormat gnu() {
    return new GnuOptionFormat();
  }

  /**
   * Returns all option formats that support a set of features.
   *
   * @param features the features an option format needs to support
   * @return a stream of all option formats with the specified feature set
   * @see OptionFormat#supportsFeatures
   */
  public static Stream<OptionFormat> withFeatures(int features) {
    return all().filter(format -> format.supportsFeatures(features));
  }

  /**
   * The GNU-style option format.
   */
  private static final class GnuOptionFormat implements OptionFormat {

    private static final int SUPPORTED_FEATURES =
      ARGUMENT_FEATURE | SHORT_OPTION_FEATURE |
      LONG_OPTION_FEATURE | GROUP_OPTION_FEATURE;

    @Override
    public boolean supportsFeatures(int features) {
      return (SUPPORTED_FEATURES & features) == features;
    }

    @Override
    public Parser parserFor(Command.Descriptor descriptor) {
      return new GnuParser(descriptor);
    }

    @Override
    public List<String> shortOptionFor(String name, String... values) {
      if (values.length == 0) {
        return List.of("-" + name);
      }
      return List.of("-" + name + "=" + String.join(",", values));
    }

    @Override
    public List<String> longOptionFor(String name, String... values) {
      if (values.length == 0) {
        return List.of("--" + name);
      }
      return List.of("--" + name + "=" + String.join(",", values));
    }

    @Override
    public List<String> argumentsFor(String... arguments) {
      return List.of(String.join(" ", arguments));
    }

    @Override
    public String toString() {
      return "GNU-style option format [features=" + SUPPORTED_FEATURES + "]";
    }
  }
}

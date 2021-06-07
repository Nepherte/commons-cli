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
package com.nepherte.commons.test;

import com.nepherte.commons.cli.Command;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Matcher that verifies a {@code Command} has a given option.
 */
public class HasOption extends BaseMatcher<Command> {

  private static final String DESCRIPTION =
    "Command with option [name=%s, values=%s]";

  private final String name;
  private final List<String> values;

  /**
   * Creates a new {@code HasOption}.
   *
   * @param name the expected name of the option
   */
  HasOption(String name) {
    this.name = name;
    this.values = new ArrayList<>();
  }

  /**
   * Adjusts the expected value.
   *
   * @param value the new value
   * @return this instance
   */
  public HasOption withValue(String value) {
    values.clear();
    values.add(value);
    return this;
  }

  /**
   * Adjusts the expected values.
   *
   * @param value1 the first value
   * @param value2 the second value
   * @param others the remaining values
   * @return this instance
   */
  public HasOption withValues(String value1, String value2, String... others) {
    values.clear();
    values.add(value1);
    values.add(value2);
    values.addAll(List.of(others));
    return this;
  }

  @Override
  public boolean matches(Object actual) {
    Command command = (Command) actual;

    if (!command.hasOption(name)) {
      return false;
    }

    List<String> actualValues = command.getOptionValues(name);
    return Objects.equals(values, actualValues);
  }

  @Override
  public void describeTo(Description description) {
    description.appendText(String.format(DESCRIPTION, name, values));
  }
}

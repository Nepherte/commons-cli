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

import java.util.Objects;

/**
 * Matcher that verifies a {@code Command} has a given argument.
 */
public class HasArgument extends BaseMatcher<Command> {

  private static final String DESCRIPTION =
    "Command with argument [value=%s, index=%s]";

  private final String argument;
  private int index = 0;

  /**
   * Creates a new {@code HasArgument}.
   *
   * @param argument the expected argument
   */
  HasArgument(String argument) {
    this.argument = argument;
  }

  /**
   * Adjusts the argument index.
   *
   * @param index the new index
   * @return this instance
   */
  public HasArgument atIndex(int index) {
    this.index = index;
    return this;
  }

  @Override
  public boolean matches(Object actual) {
    Command command = (Command) actual;

    if (command.argumentCount() == 0) {
      return false;
    }

    String actualArgument = command.getArgument(index);
    return Objects.equals(argument, actualArgument);
  }

  @Override
  public void describeTo(Description description) {
    description.appendText(String.format(DESCRIPTION, argument, index));
  }
}

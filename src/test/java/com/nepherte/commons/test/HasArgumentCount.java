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

/**
 * Matcher that verifies a {@link Command} has a given number of arguments.
 */
class HasArgumentCount extends BaseMatcher<Command> {

  private static final String DESCRIPTION =
    "Command with argument count %d";

  private final int argumentCount;

  /**
   * Creates a new {@code HasArgumentCount}
   *
   * @param argumentCount the expected number of arguments
   */
  HasArgumentCount(int argumentCount) {
    this.argumentCount = argumentCount;
  }

  @Override
  public boolean matches(Object actual) {
    Command command = (Command) actual;
    return command.argumentCount() == argumentCount;
  }

  @Override
  public void describeTo(Description description) {
    description.appendText(String.format(DESCRIPTION, argumentCount));
  }
}

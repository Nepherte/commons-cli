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
package be.nepherte.commons.cli;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static be.nepherte.commons.test.Matchers.optionalWithValue;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test that covers {@link Command}.
 */
class CommandTest {

  @Test
  void name() {
    Command.Builder builder = Command.newInstance().name("ls");
    assertThat(new Command(builder).getName(), optionalWithValue("ls"));
  }

  @Test
  void nullName() {
    assertThrows(IllegalArgumentException.class,
      () -> Command.newInstance().name(null));
  }

  @Test
  void nameWithSpace() {
    assertThrows(IllegalArgumentException.class,
      () -> Command.newInstance().name("command\tname"));
  }

  @Test
  void shortOption() {
    Option option = mock(Option.class);
    when(option.getShortName()).thenReturn(Optional.of("b"));
    when(option.getLongName()).thenReturn(Optional.empty());

    Command.Builder builder = Command.newInstance().option(option);
    Command command = new Command(builder);

    assertThat(command.hasOption("b"), is(true));
    assertThat(command.hasOption("-b"), is(true));
  }

  @Test
  void longOption() {
    Option option = mock(Option.class);
    when(option.getShortName()).thenReturn(Optional.empty());
    when(option.getLongName()).thenReturn(Optional.of("block"));

    Command.Builder builder = Command.newInstance().option(option);
    Command command = new Command(builder);

    assertThat(command.hasOption("block"), is(true));
    assertThat(command.hasOption("--block"), is(true));
  }

  @Test
  void optionIterable() {
    Option o1 = mock(Option.class);
    when(o1.getShortName()).thenReturn(Optional.of("a"));
    when(o1.getLongName()).thenReturn(Optional.empty());

    Option o2 = mock(Option.class);
    when(o2.getShortName()).thenReturn(Optional.of("b"));
    when(o2.getLongName()).thenReturn(Optional.empty());

    List<Option> options = Arrays.asList(o1, o2);

    Command.Builder builder = Command.newInstance().options(options);
    Command command = new Command(builder);

    assertThat(command.hasOption("a"), is(true));
    assertThat(command.hasOption("b"), is(true));
  }

  @Test
  void nullOptionIterable() {
    assertThrows(IllegalArgumentException.class, () ->
      Command.newInstance().options((Iterable<Option>) null));
  }

  @Test
  void optionArray() {
    Option o1 = mock(Option.class);
    when(o1.getShortName()).thenReturn(Optional.of("a"));
    when(o1.getLongName()).thenReturn(Optional.empty());

    Option o2 = mock(Option.class);
    when(o2.getShortName()).thenReturn(Optional.of("b"));
    when(o2.getLongName()).thenReturn(Optional.empty());

    Command.Builder builder = Command.newInstance().options(o1, o2);
    Command command = new Command(builder);

    assertThat(command.hasOption("a"), is(true));
    assertThat(command.hasOption("b"), is(true));
  }

  @Test
  void nullOptionArray() {
    assertThrows(IllegalArgumentException.class, () ->
      Command.newInstance().options((Option[]) null));
  }

  @Test
  void duplicateOptions() {
    Option o1 = mock(Option.class);
    when(o1.getName()).thenReturn("a");
    when(o1.getShortName()).thenReturn(Optional.of("a"));
    when(o1.getLongName()).thenReturn(Optional.empty());
    when(o1.getValues()).thenReturn(List.of("1"));

    Option o2 = mock(Option.class);
    when(o2.getName()).thenReturn("a");
    when(o2.getShortName()).thenReturn(Optional.of("a"));
    when(o2.getLongName()).thenReturn(Optional.empty());
    when(o2.getValues()).thenReturn(List.of("2"));

    Command.Builder builder = Command.newInstance().options(o1, o2);
    Command command = new Command(builder);

    assertThat(command.hasOption("a"), is(true));
    assertThat(command.getOptionValue("a"), is("2"));
  }

  @Test
  void optionValue() {
    Option option = mock(Option.class);
    when(option.getShortName()).thenReturn(Optional.of("b"));
    when(option.getLongName()).thenReturn(Optional.empty());
    when(option.getValues()).thenReturn(List.of("1"));

    Command.Builder builder = Command.newInstance().option(option);
    assertThat(new Command(builder).getOptionValue("b"), is("1"));
  }

  @Test
  void firstOptionValue() {
    Option option = mock(Option.class);
    when(option.getShortName()).thenReturn(Optional.of("b"));
    when(option.getLongName()).thenReturn(Optional.empty());
    when(option.getValues()).thenReturn(List.of("1", "2"));

    Command.Builder builder = Command.newInstance().option(option);
    assertThat(new Command(builder).getOptionValue("b"), is("1"));
  }

  @Test
  void optionUnknown() {
    Command.Builder builder = Command.newInstance();
    assertThrows(IllegalArgumentException.class, () ->
      new Command(builder).getOptionValue("unknown"));
  }

  @Test
  void argument() {
    Command.Builder builder = Command.newInstance().argument("arg");
    assertThat(new Command(builder).argumentCount(), is(1));
    assertThat(new Command(builder).getArgument(0), is("arg"));
  }

  @Test
  void whitespaceArgument() {
    Command.Builder builder = Command.newInstance().argument("  ");
    assertThat(new Command(builder).argumentCount(), is(1));
    assertThat(new Command(builder).getArgument(0), is("  "));
  }

  @Test
  void absentArgument() {
    Command.Builder builder = Command.newInstance();
    assertThat(new Command(builder).argumentCount(), is(0));
    assertThrows(IllegalArgumentException.class,
      () -> new Command(builder).getArgument(0));
  }

  @Test
  void argumentIterable() {
    List<String> args = Arrays.asList("arg1", "arg2", "arg3");

    Command.Builder builder = Command.newInstance().arguments(args);
    Command command = new Command(builder);

    assertThat(command.argumentCount(), is(3));
    assertThat(command.getArgument(0), is("arg1"));
    assertThat(command.getArgument(1), is("arg2"));
    assertThat(command.getArgument(2), is("arg3"));
  }

  @Test
  void nullArgumentIterable() {
    assertThrows(IllegalArgumentException.class, () ->
      Command.newInstance().arguments((Iterable<String>) null));
  }

  @Test
  void argumentArray() {
    String[] args = {"arg1", "arg2", "arg3"};

    Command.Builder builder = Command.newInstance().arguments(args);
    Command command = new Command(builder);

    assertThat(command.argumentCount(), is(3));
    assertThat(command.getArgument(0), is("arg1"));
    assertThat(command.getArgument(1), is("arg2"));
    assertThat(command.getArgument(2), is("arg3"));
  }

  @Test
  void nullArgumentArray() {
    assertThrows(IllegalArgumentException.class, () ->
      Command.newInstance().arguments((String[]) null));
  }

  @Test
  void stringValue() {
    Option optionA = mock(Option.class);
    when(optionA.toString()).thenReturn("-a");

    Option optionB = mock(Option.class);
    when(optionB.toString()).thenReturn("--b=1");

    // Command with options and arguments.
    Command.Builder builder = Command.newInstance().name("cmd");
    builder = builder.options(optionA, optionB).arguments("2", "3");
    assertThat(builder.toString(), is("cmd -a --b=1 2 3"));

    // Command with no options or arguments.
    builder = Command.newInstance().name("cmd");
    assertThat(builder.toString(), is("cmd"));

    // Builder with no name, options or arguments.
    builder = Command.newInstance();
    assertThat(builder.toString(), is("<undefined>"));
  }
}

/*
 * Copyright 2012-2018 Bart Verhoeven
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
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
import java.util.List;
import java.util.Optional;

import static be.nepherte.commons.cli.Collections.immutableListOf;
import static be.nepherte.commons.test.Matchers.optionalWithNoValue;
import static be.nepherte.commons.test.Matchers.optionalWithValue;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test that covers {@link Command}.
 */
public class CommandTest {

  @Test
  public void name() {
    Command.Builder builder = Command.newInstance().name("ls");
    assertThat(new Command(builder).getName(), optionalWithValue("ls"));
  }

  @Test
  public void nullName() {
    Command command = Command.newInstance().name(null).build();
    assertThat(command.getName(), optionalWithNoValue());
  }

  @Test(expected = IllegalArgumentException.class)
  public void whitespaceName() {
    Command.newInstance().name("command\tname");
  }

  @Test
  public void shortOption() {
    Option option = mock(Option.class);
    when(option.getShortName()).thenReturn(Optional.of("b"));
    when(option.getLongName()).thenReturn(Optional.empty());

    Command.Builder builder = Command.newInstance().option(option);
    Command command = new Command(builder);

    assertThat(command.hasOption("b"), is(true));
    assertThat(command.hasOption("-b"), is(true));
  }

  @Test
  public void longOption() {
    Option option = mock(Option.class);
    when(option.getShortName()).thenReturn(Optional.empty());
    when(option.getLongName()).thenReturn(Optional.of("block"));

    Command.Builder builder = Command.newInstance().option(option);
    Command command = new Command(builder);

    assertThat(command.hasOption("block"), is(true));
    assertThat(command.hasOption("--block"), is(true));
  }

  @Test
  public void optionList() {
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
  public void optionArray() {
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
  public void duplicateOptions() {
    Option o1 = mock(Option.class);
    when(o1.getName()).thenReturn("a");
    when(o1.getShortName()).thenReturn(Optional.of("a"));
    when(o1.getLongName()).thenReturn(Optional.empty());
    when(o1.getValues()).thenReturn(immutableListOf("1"));

    Option o2 = mock(Option.class);
    when(o2.getName()).thenReturn("a");
    when(o2.getShortName()).thenReturn(Optional.of("a"));
    when(o2.getLongName()).thenReturn(Optional.empty());
    when(o2.getValues()).thenReturn(immutableListOf("2"));

    Command.Builder builder = Command.newInstance().options(o1, o2);
    Command command = new Command(builder);

    assertThat(command.hasOption("a"), is(true));
    assertThat(command.getOptionValue("a"), is("2"));
  }

  @Test
  public void optionValue() {
    Option option = mock(Option.class);
    when(option.getShortName()).thenReturn(Optional.of("b"));
    when(option.getLongName()).thenReturn(Optional.empty());
    when(option.getValues()).thenReturn(immutableListOf("1"));

    Command.Builder builder = Command.newInstance().option(option);
    assertThat(new Command(builder).getOptionValue("b"), is("1"));
  }

  @Test
  public void firstOptionValue() {
    Option option = mock(Option.class);
    when(option.getShortName()).thenReturn(Optional.of("b"));
    when(option.getLongName()).thenReturn(Optional.empty());
    when(option.getValues()).thenReturn(immutableListOf("1", "2"));

    Command.Builder builder = Command.newInstance().option(option);
    assertThat(new Command(builder).getOptionValue("b"), is("1"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void optionUnknown() {
    Command.Builder builder = Command.newInstance();
    new Command(builder).getOptionValue("unknown");
  }

  @Test
  public void argument() {
    Command.Builder builder = Command.newInstance().argument("arg");
    assertThat(new Command(builder).argumentCount(), is(1));
    assertThat(new Command(builder).getArgument(0), is("arg"));
  }

  @Test
  public void whitespaceArgument() {
    Command.Builder builder = Command.newInstance().argument("  ");
    assertThat(new Command(builder).argumentCount(), is(0));
  }

  @Test(expected = IllegalArgumentException.class)
  public void absentArgument() {
    Command.Builder builder = Command.newInstance();
    assertThat(new Command(builder).argumentCount(), is(0));
    new Command(builder).getArgument(0);
  }

  @Test
  public void argumentList() {
    List<String> args = Arrays.asList("arg1", null, "arg3");

    Command.Builder builder = Command.newInstance().arguments(args);
    Command command = new Command(builder);

    assertThat(command.argumentCount(), is(2));
    assertThat(command.getArgument(0), is("arg1"));
    assertThat(command.getArgument(1), is("arg3"));
  }

  @Test
  public void argumentArray() {
    String[] args = {"arg1", null, "arg3"};

    Command.Builder builder = Command.newInstance().arguments(args);
    Command command = new Command(builder);

    assertThat(command.argumentCount(), is(2));
    assertThat(command.getArgument(0), is("arg1"));
    assertThat(command.getArgument(1), is("arg3"));
  }

  @Test
  public void stringValue() {
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

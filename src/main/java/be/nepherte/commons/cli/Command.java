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

import be.nepherte.commons.cli.internal.Collections;
import be.nepherte.commons.cli.internal.Strings;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toSet;

/**
 * <p>An <em>immutable</em> representation of a command, its {@link Option
 * options} and its arguments. It is often the result of a {@link Parser}
 * that processes command-line tokes against an application's {@link
 * Command.Descriptor descriptor}.
 *
 * <p>A command contains the options and arguments used to launch an
 * application. The values of an option can be retrieved with {@link
 * #getOptionValues}, whereas arguments can be accessed via {@link
 * #getArgument}.
 *
 * <p>A command can only be configured by means of a {@link Builder Builder},
 * acquired using one of the <em >static factory methods</em>. A convenience
 * implementation is available to create a command from scratch.
 */
public final class Command {

  /** The name of this command. */
  private final String name;
  /** The options of this command. */
  private final List<Option> options;
  /** The arguments of this command. */
  private final List<String> arguments;

  /**
   * Returns a builder to create new commands.
   *
   * @return a builder to create new commands
   */
  public static Builder newInstance() {
    return new Builder();
  }

  /**
   * Returns a builder to create new descriptors.
   *
   * @return a builder to create new descriptors
   */
  public static Descriptor.Builder newDescriptor() {
    return new Descriptor.Builder();
  }

  /**
   * Creates a new command, initialized with a builder's values. Changes made
   * to the builder afterwards, do not affect this command. As such, a builder
   * can be re-used several times.
   *
   * @param builder the settings of the new command
   */
  Command(Builder builder) {
    name = builder.name;
    options = new ArrayList<>(builder.options);
    arguments = new ArrayList<>(builder.arguments);
  }

  /**
   * Returns the name of this command.
   *
   * @return the name of this command
   */
  public Optional<String> getName() {
    return Optional.ofNullable(name);
  }

  /**
   * Returns the number of arguments of this command.
   *
   * @return the number of arguments of this command
   */
  public int argumentCount() {
    return arguments.size();
  }

  /**
   * Returns the {@code ith} argument of this command. An argument's index is
   * determined by the order in which arguments are provided by the user. It is
   * a positive number, smaller than {@link #argumentCount()}.
   *
   * @param i the index of the argument
   * @return the argument at position {@code i}
   * @throws IllegalArgumentException index is out of range
   */
  public String getArgument(int i) {
    if (i < 0 || i >= argumentCount()) {
      throw new IllegalArgumentException(
        "Command has no argument at index [" + i + "]"
      );
    }
    return arguments.get(i);
  }

  /**
   * Indicates whether this command contains an option with a given name.
   * Both short and long names, optionally preceded by hyphens, are accepted.
   * Returns true if an option with such name is present.
   *
   * @param name the option name, preceded by zero or more hyphens
   * @return true if an option with such name is present
   */
  public boolean hasOption(String name) {
    return resolveOption(name, options) != null;
  }

  /**
   * Returns the (first) {@link #getOptionValues value} of an option, if such a
   * value exists.
   *
   * @param name the option name, preceded by zero or more hyphens
   * @return the option value if it exists, or {@code null}
   * @throws IllegalArgumentException no option with such name
   */
  public String getOptionValue(String name) {
    List<String> values = getOptionValues(name);
    return values.isEmpty() ? null : values.get(0);
  }

  /**
   * Returns the values of an option as an immutable list.
   *
   * @param name the option name, preceded by zero or more hyphens
   * @return the values of an option as an immutable list
   * @throws IllegalArgumentException no option with such name
   */
  public List<String> getOptionValues(String name) {
    Option option = resolveOption(name, options);

    if (option == null) {
      throw new IllegalArgumentException(
        "Command has no option with name [" + name + "]"
      );
    }

    // Option values are already immutable.
    return option.getValues();
  }

  /**
   * Returns a human-readable representation of this command. The format is:
   * {@link #getName name} {@link #hasOption options} {@link #getArgument
   * arguments}.
   *
   * @return a human-readable representation of this command
   */
  @Override
  public String toString() {
    return new Builder(this).toString();
  }

  /**
   * Resolves a name to an option. A name is resolved to an option if the
   * short or long name of the option, notwithstanding leading hyphens, matches
   * the string. Returns {@code null} if there is no such option.
   *
   * @param name the string to resolve
   * @param options the available options
   * @return the option with such name, or {@code null}
   */
  private static Option resolveOption(String name, Iterable<Option> options) {
    if (Strings.isNullOrBlank(name)) {
      return null;
    }

    // Support querying an option name with leading hyphens.
    String stripped = Option.NAME_PREFIX_PATTERN.matcher(name).replaceFirst("");

    // The last provided option prevails.
    for (Option option: options) {
      String shortName = option.getShortName().orElse(null);
      String longName = option.getLongName().orElse(null);

      // Check the short name of the option first, then the long name.
      if (stripped.equals(shortName) || stripped.equals(longName)) {
        return option;
      }
    }

    return null;
  }

  /**
   * Extracts a command name from a string. In particular, this method:
   * <ol>
   *   <li>Checks that the string does not contain spaces,</li>
   *   <Li>Makes sure that the parsed name is not empty.</Li>
   * </ol>
   *
   * @param name the string to parse
   * @return the parsed command name, or {@code null} if blank
   * @throws IllegalArgumentException name contains spaces
   */
  private static String parseCommandName(String name) {
    // Bounce back null names.
    if (name == null) {
      return null;
    }

    // Command names never contain spaces.
    if (Strings.containsWhitespace(name)) {
      throw new IllegalArgumentException(
        "Command name [" + name + "] contains whitespace"
      );
    }

    // Convert empty names to null.
    return Strings.emptyToNull(name);
  }

  /**
   * <p>A builder to create new {@link Command Commands} in a fluent, chained
   * fashion. Typically used by a {@link Parser} to construct a command from
   * one or more command-line tokens against an application's {@link
   * Command.Descriptor Descriptor}.
   *
   * <p><strong>NOTE:</strong> A builder can be re-used several times without
   * affecting previously created commands. However, values previously applied
   * to the builder, stick after creating a command, unless overridden again.
   */
  public static final class Builder {

    /** The name of the new command. */
    private String name;
    /** The options of new command. */
    private final List<Option> options;
    /** The arguments of the new command. */
    private final List<String> arguments;

    /**
     * Creates a new, uninitialized builder.
     */
    Builder() {
      options = new ArrayList<>(10);
      arguments = new ArrayList<>(10);
    }

    /**
     * Creates a new builder, initialized with a command's values.
     *
     * @param command the settings of the new builder
     */
    Builder(Command command) {
      name = command.name;
      options = new ArrayList<>(command.options);
      arguments = new ArrayList<>(command.arguments);
    }

    /**
     * Sets the name of the new command.
     * reset.
     *
     * @param name the name of the new command
     * @return this builder
     * @throws IllegalArgumentException the name contains a space
     */
    public Builder name(String name) {
      this.name = parseCommandName(name);
      return this;
    }

    /**
     * Adds an option to the new command. Replaces options that share the same
     * name. {@code Null} options are ignored.
     *
     * @param option the option to add
     * @return this builder
     */
    public Builder option(Option option) {
      if (option != null) {
        String optionName = option.getName();
        Option resolved = resolveOption(optionName, options);

        if (resolved != null) {
          options.remove(resolved);
        }
        options.add(option);
      }
      return this;
    }

    /**
     * Adds options to the new command. Replaces options that share the same
     * name. {@code Null} options are ignored.
     *
     * @param options the options to add
     * @return this builder
     */
    public Builder options(Iterable<Option> options) {
      if (options != null) {
        options.forEach(this::option);
      }
      return this;
    }

    /**
     * Adds options to the new command. Replaces options that share the same
     * name. {@code Null} options are ignored.
     *
     * @param options the options to add
     * @return this builder
     */
    public Builder options(Option... options) {
      if (options != null) {
        stream(options).forEach(this::option);
      }
      return this;
    }

    /**
     * Adds an argument to the new command. {@code Null} arguments are ignored.
     *
     * @param argument the argument to add
     * @return this builder
     */
    public Builder argument(String argument) {
      if (!Strings.isNullOrBlank(argument)) {
        arguments.add(argument);
      }
      return this;
    }

    /**
     * Adds arguments to the new command. {@code Null} arguments are ignored.
     *
     * @param arguments the arguments to add
     * @return this builder
     */
    public Builder arguments(Iterable<String> arguments) {
      if (arguments != null) {
        arguments.forEach(this::argument);
      }
      return this;
    }

    /**
     * Adds arguments to the new command. {@code Null} arguments are ignored.
     *
     * @param arguments the arguments to add
     * @return this builder
     */
    public Builder arguments(String... arguments) {
      if (arguments != null) {
        stream(arguments).forEach(this::argument);
      }
      return this;
    }

    /**
     * @see Command#toString() Command.toString()
     */
    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append(name != null ? name : "<undefined>");

      for (Option option: options) {
        builder.append(' ').append(option);
      }

      for (String argument: arguments) {
        builder.append(' ').append(argument);
      }

      return builder.toString();
    }

    /**
     * Constructs a new command, using the values that were applied to this
     * builder. The result can be used to query the options and arguments
     * provided to the command. The effect of the command remains unspecified.
     *
     * @return a new command
     */
    public Command build() {
      return new Command(this);
    }
  }

  /**
   * <p>An <em>immutable</em> description of the options and arguments that are
   * available to a command. It is used by a {@link Parser} to determine the
   * semantics of the tokens provided to a {@link Command}.
   *
   * <p>A descriptor can only be configured by means of a {@link Builder
   * Builder}, acquired using one of the <em>static factory methods</em>. A
   * convenience implementation is available to create a descriptor from
   * scratch.
   */
  public static final class Descriptor {

    /** The name of this descriptor. */
    private final String name;
    /** The templates of this descriptor. */
    private final Set<Option.Template> templates;
    /** The groups of this descriptor. */
    private final Set<Option.Group> groups;
    /** The minimum number of arguments. */
    private final int minArgs;
    /** The maximum number of arguments. */
    private final int maxArgs;

    /**
     * Creates a new descriptor, initialized with a builder's values. Changes
     * made to the builder afterwards, do not affect this descriptor. As such, a
     * builder can be re-used several times.
     *
     * @param builder the settings of the new descriptor
     */
    Descriptor(Builder builder) {
      name = builder.name;
      minArgs = builder.minArgs;
      maxArgs = builder.maxArgs;
      groups = new HashSet<>(builder.groups);
      templates = new HashSet<>(builder.templates);
    }

    /**
     * Returns the name of this descriptor.
     *
     * @return the name of this descriptor
     */
    public Optional<String> getName() {
      return Optional.ofNullable(name);
    }

    /**
     * Returns the templates of this descriptor as an immutable set.
     *
     * @return the templates of this descriptor as an immutable set
     */
    public Set<Option.Template> getTemplates() {
      return Collections.immutableSet(templates);
    }

    /**
     * Returns the required templates of this descriptor as an immutable set.
     *
     * @return the required templates of this descriptor as an immutable set
     */
    public Set<Option.Template> getRequiredTemplates() {
      return templates
        .stream().filter(Option.Template::isRequired)
        .collect(collectingAndThen(toSet(), Collections::immutableSet));
    }

    /**
     * Returns the template matching the given short name.
     *
     * @param name the short name to match the template to
     * @return the template matching the given short name
     */
    public Optional<Option.Template> getShortTemplate(String name) {
      Predicate<Option.Template> predicate = new HasShortName(name);
      return templates.stream().filter(predicate).findFirst();
    }

    /**
     * Returns the template matching the given long name.
     *
     * @param name the long name to match the template to
     * @return the template matching the given long name
     */
    public Optional<Option.Template> getLongTemplate(String name) {
      Predicate<Option.Template> predicate = new HasLongName(name);
      return templates.stream().filter(predicate).findFirst();
    }

    /**
     * Returns the groups of this descriptor as an immutable set.
     *
     * @return the groups of this descriptor as an immutable set
     */
    public Set<Option.Group> getGroups() {
      return Collections.immutableSet(groups);
    }

    /**
     * Returns the required groups of this descriptor as an immutable set.
     *
     * @return the required groups of this descriptor as an immutable set
     */
    public Set<Option.Group> getRequiredGroups() {
      return groups
        .stream().filter(Option.Group::isRequired)
        .collect(collectingAndThen(toSet(), Collections::immutableSet));
    }

    /**
     * Returns the group a template belongs to.
     *
     * @param template the template whose group to retrieve
     * @return the group the template belongs to
     */
    public Optional<Option.Group> getGroup(Option.Template template) {
      Predicate<Option.Group> predicate = new HasTemplate(template);
      return groups.stream().filter(predicate).findFirst();
    }

    /**
     * Indicates whether a command requires at least one argument.
     *
     * @return true if a command requires at least one argument
     */
    public boolean requiresArgs() {
      // builder guarantees >= 0
      return minArgs != 0;
    }

    /**
     * Indicates whether a command can have arguments.
     *
     * @return true if a command can have arguments
     */
    public boolean canHaveArgs() {
      // builder guarantees >= 0
      return maxArgs != 0;
    }

    /**
     * Returns the minimum number of arguments a command must have.
     *
     * @return the minimum number of arguments a command must have
     */
    public int getMinArgs() {
      return minArgs;
    }

    /**
     * Returns the maximum number of arguments a command can have.
     *
     * @return the maximum number of arguments a command can have
     */
    public int getMaxArgs() {
      return maxArgs;
    }

    /**
     * Returns a human-readable representation of this descriptor. The expected
     * format is:
     * <blockquote>{@link #getName command} [{@link #getTemplates templates}]
     * [{@code <args>}]
     * </blockquote>
     *
     * @return a human-readable representation of this command
     */
    @Override
    public String toString() {
      return new Builder(this).toString();
    }

    /**
     * <p>A builder to create new descriptors in a fluent, chained fashion.
     * Typically used by a developer to specify the available options and
     * expected number of arguments of a {@link Command}.
     *
     * <p><strong>NOTE:</strong> A builder can be re-used several times without
     * affecting previously created descriptors. However, values previously
     * applied to the builder, stick after creating a descriptor, unless
     * overridden again.
     */
    public static final class Builder {

      /** The name of the new descriptor. */
      private String name;
      /** The templates of the new descriptor. */
      private final Set<Option.Template> templates;
      /** The groups of the new descriptor. */
      private final Set<Option.Group> groups;
      /** The minimum number of arguments. */
      private int minArgs;
      /** The maximum number of arguments. */
      private int maxArgs;

      /**
       * Creates a new, uninitialized builder.
       */
      Builder() {
        templates = new HashSet<>(10);
        groups = new HashSet<>(10);
      }

      /**
       * Creates a new builder, initialized with the values set on a descriptor.
       *
       * @param descriptor the settings of this builder
       */
      Builder(Descriptor descriptor) {
        name = descriptor.name;
        minArgs = descriptor.minArgs;
        maxArgs = descriptor.maxArgs;
        groups = new HashSet<>(descriptor.groups);
        templates = new HashSet<>(descriptor.templates);
      }

      /**
       * Sets the name of the new descriptor.
       *
       * @param name the name of the new descriptor
       * @return this builder
       */
      public Builder name(String name) {
        this.name = parseCommandName(name);
        return this;
      }

      /**
       * Adds a template to the new descriptor.
       *
       * @param template the template to add
       * @return this builder
       */
      public Builder template(Option.Template template) {
        if (template != null) {
          templates.add(template);
        }
        return this;
      }

      /**
       * Adds templates to the new descriptor.
       *
       * @param templates the templates to add
       * @return this builder
       */
      public Builder templates(Iterable<Option.Template> templates) {
        if (templates != null) {
          templates.forEach(this::template);
        }
        return this;
      }

      /**
       * Adds templates to the new descriptor.
       *
       * @param templates the templates to add
       * @return this builder
       */
      public Builder templates(Option.Template... templates) {
        if (templates != null) {
          stream(templates).forEach(this::template);
        }
        return this;
      }

      /**
       * Adds a group to the new descriptor.
       *
       * @param group the group to add
       * @return this builder
       */
      public Builder group(Option.Group group) {
        if (group != null) {
          groups.add(group);
          templates(group.getTemplates());
        }
        return this;
      }

      /**
       * Adds groups to the new descriptor.
       *
       * @param groups the groups to add
       * @return this builder
       */
      public Builder groups(Iterable<Option.Group> groups) {
        if (groups != null) {
          groups.forEach(this::group);
        }
        return this;
      }

      /**
       * Adds groups to the new descriptor.
       *
       * @param groups the groups to add
       * @return this builder
       */
      public Builder groups(Option.Group... groups) {
        if (groups != null) {
          stream(groups).forEach(this::group);
        }
        return this;
      }

      /**
       * Sets the minimum number of arguments the new descriptor takes.
       *
       * @param minArgs the minimum number of arguments the new descriptor takes
       * @return this builder
       * @throws IllegalArgumentException the number of arguments is negative
       */
      public Builder minArgs(int minArgs) {
        if (minArgs < 0) {
          throw new IllegalArgumentException(
            "Minimum number of arguments is negative [" + minArgs + "]"
          );
        }
        this.minArgs = minArgs;
        return this;
      }

      /**
       * Sets the maximum number of arguments the new descriptor takes.
       *
       * @param maxArgs the maximum number of arguments the new descriptor takes
       * @return this builder
       * @throws IllegalArgumentException the number of arguments is negative
       */
      public Builder maxArgs(int maxArgs) {
        if (maxArgs < 0) {
          throw new IllegalArgumentException(
            "Maximum number of arguments is negative [" + maxArgs + "]"
          );
        }
        this.maxArgs = maxArgs;
        return this;
      }

      /**
       * Creates a new descriptor, using the values that were applied to this
       * builder. changes made to this builder afterwards do not affect the
       * created descriptor. As such, a builder can be re-used several times.
       *
       * @return a new descriptor
       * @throws IllegalStateException the descriptor is invalid
       */
      public Descriptor build() {
        if (maxArgs < minArgs) {
          throw new IllegalStateException(
            "Descriptor requires more arguments than allowed" +
              "[" + maxArgs + " < " + minArgs + "]"
          );
        }

        return new Descriptor(this);
      }

      /**
       * @see Descriptor#toString() Descriptor.toString()
       */
      @Override
      public String toString() {
        // Include the name.
        StringBuilder builder = new StringBuilder();
        builder.append(name != null ? name : "<undefined>");

        // Include the options.
          templates.stream()
            .sorted(Option.Template::byName)
            .forEach(t -> builder.append(" ").append(t));

        // Include arguments if applicable.
        if (maxArgs != 0) {
          builder.append(" ");

          // Surround optional arguments with [].
          if (minArgs == 0) {
            builder.append("[");
          }

          builder.append("<args>");

          // Surround optional arguments with [].
          if (minArgs == 0) {
            builder.append("]");
          }
        }

        return builder.toString();
      }
    }
  }

  /**
   * Predicate that asserts a template has a given short name.
   */
  private static final class HasShortName implements Predicate<Option.Template>{
    private final String name;

    /**
     * Creates a new {@code HasShortName}.
     *
     * @param name the name to assert
     */
    HasShortName(String name) {
      this.name = name;
    }

    @Override
    public boolean test(Option.Template template) {
      Optional<String> shortName = template.getShortName();
      return shortName.isPresent() && shortName.get().equals(name);
    }
  }

  /**
   * Predicate that asserts a template has a given long name.
   */
  private static final class HasLongName implements Predicate<Option.Template> {
    private final String name;

    /**
     * Creates a new {@code HasLongName}.
     *
     * @param name the name to assert
     */
    HasLongName(String name) {
      this.name = name;
    }

    @Override
    public boolean test(Option.Template option) {
      Optional<String> longName = option.getLongName();
      return longName.isPresent() && longName.get().equals(name);
    }
  }

  /**
   * Predicate that asserts a group has a given template.
   */
  private static final class HasTemplate implements Predicate<Option.Group> {
    private final Option.Template template;

    /**
     * Creates a new {@code HasTemplate}.
     *
     * @param template the template to assert
     */
    HasTemplate(Option.Template template) {
      this.template = template;
    }

    @Override
    public boolean test(Option.Group group) {
      return group.getTemplates().contains(template);
    }
  }
}

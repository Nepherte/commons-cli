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

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toSet;

public final class Command {

  /** Pattern that indicates a whitespace character. */
  private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s");

  /**
   * Returns a builder to create new descriptors.
   *
   * @return a builder to create new descriptors
   */
  public static Descriptor.Builder newDescriptor() {
    return new Descriptor.Builder();
  }

  /**
   * Creates a new {@code Command}.
   */
  private Command() {
    // Hide constructor.
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
    if (WHITESPACE_PATTERN.matcher(name).find()) {
      throw new IllegalArgumentException(
        "Command name [" + name + "] contains whitespace"
      );
    }

    // Convert empty names to null.
    return Strings.emptyToNull(name);
  }

  /**
   * <p>An <em>immutable</em> description of the options and arguments that are
   * available to a command. It is used by a {@code Parser} to determine the
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

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
package com.nepherte.commons.cli;

import java.util.Collections;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.nepherte.commons.cli.internal.Preconditions.*;
import static com.nepherte.commons.cli.internal.Predicates.*;

import static java.lang.String.format;
import static java.util.Arrays.stream;

/**
 * <p>An <em>immutable</em> option that modifies the behavior of a {@code
 * Command}. Options are preceded by the command name and are usually separated
 * from each other by a single space. The effect of an option is left
 * <em>unspecified</em>.
 *
 * <p>An option can only be configured by means of a {@link Builder Builder},
 * acquired using one of the available <em >static factory methods</em>. A
 * convenience implementation is already provided to initialize a builder with a
 * {@link Template Template}.
 */
public final class Option {

  /** Pattern that indicates the start of an option name. */
  static final Pattern NAME_PREFIX_PATTERN = Pattern.compile("^-+");

  /** The short name of this option. */
  private final String shortName;
  /** The long name of this option. */
  private final String longName;
  /** The immutable values of this option. */
  private final List<String> values;

  /**
   * Returns a builder to create new options.
   *
   * @return a builder to create new options
   */
  public static Builder newInstance() {
    return new Builder();
  }

  /**
   * Returns a builder initialized with the values of an existing template.
   *
   * @param template the settings of the new builder
   * @return a pre-initialized builder to create new options
   * @throws IllegalArgumentException the template is null
   */
  public static Builder newInstance(Template template) {
    requireArg(template, notNull(), "The template is [%s]");
    return new Builder(template);
  }

  /**
   * Returns a builder to create new templates.
   *
   * @return a builder to create new templates
   */
  public static Template.Builder newTemplate() {
    return new Template.Builder();
  }

  /**
   * Returns a builder to create new groups.
   *
   * @return a builder to create new groups
   */
  public static Group.Builder newGroup() {
    return new Group.Builder();
  }

  /**
   * Creates a new option, initialized with a builder's values. Changes made
   * to the builder afterwards, do not affect this option. As such, a builder
   * can be re-used several times.
   *
   * @param builder the settings of the new option
   * @throws NullPointerException the builder is null
   */
  Option(Builder builder) {
    shortName = builder.shortName;
    longName = builder.longName;
    values = new ArrayList<>(builder.values);
  }

  /**
   * Returns the name of this option. It is either the {@link #getShortName
   * short} or {@link #getLongName long} name of this option. If both names
   * exist, the short name takes precedence over the long one. The name is
   * never {@code null} or blank.
   *
   * @return the name of this option
   */
  public String getName() {
    // The builder guarantees that at least one exists.
    return shortName == null ? longName : shortName;
  }

  /**
   * Returns the short name of this option.
   *
   * @return the short name of this option
   */
  public Optional<String> getShortName() {
    return Optional.ofNullable(shortName);
  }

  /**
   * Returns the long name of this option.
   *
   * @return the long name of this option
   */
  public Optional<String> getLongName() {
    return Optional.ofNullable(longName);
  }

  /**
   * Returns the (first) {@link #getValues() value} of this option.
   *
   * @return the (first) value of this option
   */
  public Optional<String> getValue() {
    if (values.isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(values.get(0));
  }

  /**
   * Returns the values of this option as an immutable list.
   *
   * @return the values of this option as an immutable list
   */
  public List<String> getValues() {
    return Collections.unmodifiableList(values);
  }

  /**
   * <p>Returns a human-readable representation of this option. The format is:
   * -{@link #getName name} [{@link #getValues values}].</p>
   *
   * @return a human-readable representation of this option
   */
  @Override
  public String toString() {
    return new Option.Builder(this).toString();
  }

  /**
   * <p>A builder to create new {@link Option Options} in a fluent, chained
   * fashion. Typically used by a parser to build options from command line
   * tokens, based on one of the {@link Template Templates} available in a
   * command descriptor.
   *
   * <p><strong>NOTE:</strong> A builder can be re-used several times without
   * affecting previously built options. However, values previously applied to
   * the builder, stick after creating an option, unless overridden again.
   */
  public static final class Builder {

    /** The short name of the new option. */
    private String shortName;
    /** The long name of the new option. */
    private String longName;
    /** The values of the new option. */
    private final List<String> values;

    /**
     * Creates a new, uninitialized builder.
     */
    Builder() {
      values = new ArrayList<>(10);
    }

    /**
     * Creates a new builder, initialized with a template's values.
     *
     * @param template the settings of the new builder
     * @throws NullPointerException the template is null
     */
    Builder(Template template) {
      shortName = template.shortName;
      longName = template.longName;
      values = new ArrayList<>(10);
    }

    /**
     * Creates a new builder, initialized with an option's values.
     *
     * @param option the settings of the new builder
     * @throws NullPointerException the option is null
     */
    Builder(Option option) {
      shortName = option.shortName;
      longName = option.longName;
      values = new ArrayList<>(option.values);
    }

    /**
     * Sets the short name of the new option. Leading dashes are stripped.
     *
     * @param shortName the short name of the new option
     * @return this builder
     * @throws IllegalArgumentException the name is empty
     * @throws IllegalArgumentException the name has a space
     */
    public Builder shortName(String shortName) {
      this.shortName = parseOptionName(shortName);
      return this;
    }

    /**
     * Sets the long name of the new option. Leading dashes are stripped.
     *
     * @param longName the long name of the new option
     * @return this builder
     * @throws IllegalArgumentException the name is empty
     * @throws IllegalArgumentException the name has a space
     */
    public Builder longName(String longName) {
      this.longName = parseOptionName(longName);
      return this;
    }

    /**
     * Adds a value to the new option.
     *
     * @param value the value to add
     * @return this builder
     * @throws IllegalArgumentException the value is null
     */
    public Builder value(String value) {
      requireArg(value, notNull(), "The value is [%s]");
      values.add(value);
      return this;
    }

    /**
     * Adds values to the new option.
     *
     * @param values the values to add
     * @return this builder
     * @throws IllegalArgumentException the array or a value is null
     */
    public Builder values(Iterable<String> values) {
      requireArg(values, notNull(), "The value iterable is [%s]");
      values.forEach(this::value);
      return this;
    }

    /**
     * Adds values to the new option.
     *
     * @param values the values to add
     * @return this builder
     * @throws IllegalArgumentException the iterable or a value is null
     */
    public Builder values(String... values) {
      requireArg(values, notNull(), "The value array is [%s]");
      stream(values).forEach(this::value);
      return this;
    }

    /**
     * Constructs a new option, using the values that were applied to this
     * builder. The result can be used to describe a command line switch used to
     * start an application. The effect of the option is left unspecified.
     *
     * @return a new option
     * @throws IllegalStateException the built option is invalid
     */
    public Option build() {
      requireState(this, hasShortName().or(hasLongName()),
        "The option has neither a short name nor a long name"
      );
      return new Option(this);
    }

    /**
     * @see Option#toString() Option.toString()
     */
    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder();

      // Include the name.
      if (shortName != null) {
        builder.append("-").append(shortName);
      }
      else if (longName != null) {
        builder.append("--").append(longName);
      }
      else {
        builder.append("-").append("<undefined>");
      }

      // Include the values.
      if (!values.isEmpty()) {
        StringJoiner joiner = new StringJoiner(",");
        values.forEach(joiner::add);
        builder.append('=').append(joiner);
      }

      return builder.toString();
    }

    /**
     * Predicate that tests for the presence of a short name.
     *
     * @return a predicate that tests for the presence of a short name
     */
    private static Predicate<Builder> hasShortName() {
      return notNull(builder -> builder.shortName);
    }

    /**
     * Predicate that tests for the presence of a long name.
     *
     * @return a predicate that tests for the presence of a long name
     */
    private static Predicate<Builder> hasLongName() {
      return notNull(builder -> builder.longName);
    }
  }

  /**
   * Extracts an option name from a string. In particular, this method:
   * <ol>
   *   <li>Strips any leading hyphens from the string,</li>
   *   <li>Checks that the string does not contain spaces,</li>
   *   <Li>Makes sure that the parsed name is not empty.</Li>
   * </ol>
   *
   * @param name the option name to parse
   * @return the parsed option name
   * @throws IllegalArgumentException the name is empty
   * @throws IllegalArgumentException the name has a space
   */
  private static String parseOptionName(String name) {
    // Use null values to rest the option name.
    if (name == null) {
      return null;
    }

    // Names cannot contain space(s).
    requireArg(name, noSpace(), "The name [%s] has a space");

   // Remove leading hyphens from the name.
    Matcher matcher = NAME_PREFIX_PATTERN.matcher(name);
    String stripped = matcher.replaceFirst("");

    // The name without hyphens cannot be empty.
    requireArg(stripped, notEmpty(), "The name [%s] is empty");

    return stripped;
  }

  /**
   * <p>An <em>immutable</em> template that describes the options of a
   * {@link Command}. It acts as a <em>blueprint</em> that spells out exactly
   * what an {@link Option} should look like. Failing to comply to this
   * blueprint is a direct violation of the contract.
   *
   * <p>A template can only be configured by means of a {@link Builder Builder},
   * acquired using one of the available <em>static factory methods</em>. A
   * convenience implementation is provided to copy an already existing
   * template.
   */
  public static final class Template {

    /** The short name of this template. */
    private final String shortName;
    /** The long name of this template. */
    private final String longName;
    /** The description of this template. */
    private final String description;
    /** An indication that this template is required. */
    private final boolean required;
    /** The minimum number of values. */
    private final int minValues;
    /** The maximum number of values. */
    private final int maxValues;
    /** The name for the values of this template. */
    private final String valueName;

    /**
     * Creates a new template, initialized with a builder's values. Changes made
     * to the builder afterwards, do not affect this template. As such, a
     * builder can be re-used several times.
     *
     * @param builder the settings of the new template
     * @throws NullPointerException builder is {@code null}
     */
    Template(Builder builder) {
      shortName = builder.shortName;
      longName = builder.longName;
      description = builder.description;
      required = builder.required;
      valueName = builder.valueName;
      minValues = builder.minValues;
      maxValues = builder.maxValues;
    }

    /**
     * Returns the name of this template. It is either the {@link #getShortName
     * short} or {@link #getLongName long} name of this template. If both names
     * exist, the short name takes precedence over the long one. The name is
     * never {@code null} or blank.
     *
     * @return the name of this template
     */
    public String getName() {
      // The builder guarantees that at least one exists.
      return shortName == null ? longName : shortName;
    }

    /**
     * Returns the short name of this template.
     *
     * @return the short name of this template
     */
    public Optional<String> getShortName() {
      return Optional.ofNullable(shortName);
    }

    /**
     * Returns the long name of this template.
     *
     * @return the long name of this template
     */
    public Optional<String> getLongName() {
      return Optional.ofNullable(longName);
    }

    /**
     * Returns the description of this template.
     *
     * @return the description of this template
     */
    public Optional<String> getDescription() {
      return Optional.ofNullable(description);
    }

    /**
     * Indicates if this template is required.
     *
     * @return true if this template is required
     */
    public boolean isRequired() {
      return required;
    }

    /**
     * Returns the minimum number of values this template takes.
     *
     * @return the minimum number of values this template takes
     */
    public int getMinValues() {
      return minValues;
    }

    /**
     * Returns the maximum number of values this template takes.
     *
     * @return the maximum number of values this template takes
     */
    public int getMaxValues() {
      return maxValues;
    }

    /**
     * Indicates whether this template requires at least one value.
     *
     * @return true if this template requires at least one value
     */
    public boolean requiresValues() {
      // builder guarantees >= 0
      return minValues != 0;
    }

    /**
     * Indicates this template can have values.
     *
     * @return true if this template can have values
     */
    public boolean canHaveValues() {
      // builder guarantees >= 0
      return maxValues != 0;
    }

    /**
     * Indicates this template can have {@code n} values.
     *
     * @param n the requested number of values
     * @return true if this template can have {@code n} values
     */
    public boolean canHaveValues(int n) {
      return minValues <= n && n <= maxValues;
    }

    /**
     * Returns a human-readable name for the values.
     *
     * @return a human-readable name for the values
     */
    public Optional<String> getValueName() {
      return Optional.ofNullable(valueName);
    }

    /**
     * Returns a human-readable representation of this template. The expected
     * format is: -{@link #getName name} [{@link #getValueName valueName}].
     *
     * @return a human-readable representation of this template
     */
    @Override
    public String toString() {
      return new Template.Builder(this).toString();
    }

    /**
     * Returns a new builder, initialized with a copy of this template.
     *
     * @return a new builder, initialized with a copy of this template
     */
    public Template.Builder asBuilder() {
      return new Template.Builder(this);
    }

    /**
     * Compares templates by their name.
     *
     * @param aTemplate1 the first template
     * @param aTemplate2 the second template
     * @return see {@link Comparable#compareTo}
     * @throws IllegalArgumentException a template is {@code null}
     */
    static int byName(Template aTemplate1, Template aTemplate2) {
      requireArg(aTemplate1, notNull(), "The first template is [%s]");
      requireArg(aTemplate2, notNull(), "The second template is [%s]");
      return aTemplate1.getName().compareTo(aTemplate2.getName());
    }

    /**
     * <p>A builder to create new {@link Template Templates} in a fluent,
     * chained fashion. Typically used by a developer to specify the available
     * {@link Option Options} of a command that are able to modify the behavior
     * of the application.
     *
     * <p><strong>NOTE:</strong> A builder can be re-used several times without
     * affecting previously built templates. However, values previously applied
     * to the builder, stick after creating a template, unless overridden again.
     */
    public static final class Builder {

      /** The short name of the new template. */
      private String shortName;
      /** The long name of the new template. */
      private String longName;
      /** The description of the new template. */
      private String description;
      /** Indicates if the new template is required. */
      private boolean required;
      /** The minimum number of values. */
      private int minValues;
      /** The maximum number of values. */
      private int maxValues;
      /** The name for the values of this template. */
      private String valueName;

      /**
       * Creates a new, uninitialized builder.
       */
      private Builder() {
        // Hide default constructor.
      }

      /**
       * Creates a new builder, initialized with the values set on a template.
       *
       * @param template the template whose values to copy
       */
      Builder(Template template) {
        shortName = template.shortName;
        longName = template.longName;
        description = template.description;
        required = template.required;
        valueName = template.valueName;
        minValues = template.minValues;
        maxValues = template.maxValues;
      }

      /**
       * Sets the short name of the new template. Leading dashes are stripped.
       *
       * @param shortName the short name of the new template
       * @return this builder
       * @throws IllegalArgumentException the name is empty
       * @throws IllegalArgumentException the name has a space
       */
      public Builder shortName(String shortName) {
        this.shortName = parseOptionName(shortName);
        return this;
      }

      /**
       * Sets the long name of the new template. Leading dashes are stripped.
       *
       * @param longName the long name of the new template
       * @return this builder
       * @throws IllegalArgumentException the name is empty
       * @throws IllegalArgumentException the name has a space
       */
      public Builder longName(String longName) {
        this.longName = parseOptionName(longName);
        return this;
      }

      /**
       * Sets the description of the new template.
       *
       * @param description the description of the new template
       * @return this builder
       * @throws IllegalArgumentException the description is null or blank
       */
      public Builder description(String description) {
        requireArg(description, notNull(), "The description is [%s]");
        requireArg(description, notBlank(), "The description [%s] is blank");
        this.description = description;
        return this;
      }

      /**
       * Makes the new template required.
       *
       * @return this builder
       */
      public Builder required() {
        required = true;
        return this;
      }

      /**
       * Sets the minimum number of values the new template takes.
       *
       * @param minValues the minimum number of values the new template takes
       * @return this builder
       * @throws IllegalArgumentException the number of values is negative
       */
      public Builder minValues(int minValues) {
        requireArg(minValues, greaterThan(-1), "Min values [%d] is negative");
        this.minValues = minValues;
        return this;
      }

      /**
       * Sets the maximum number of values the new template takes.
       *
       * @param maxValues the maximum number of values the new template takes
       * @return this builder
       * @throws IllegalArgumentException the number of values is negative
       */
      public Builder maxValues(int maxValues) {
        requireArg(maxValues, greaterThan(-1), "Max values [%d] is negative");
        this.maxValues = maxValues;
        return this;
      }

      /**
       * Sets the name of the template values.
       *
       * @param valueName the name of the option values
       * @return this builder
       * @throws IllegalArgumentException the value name is null or blank
       */
      public Builder valueName(String valueName) {
        requireArg(valueName, notNull(), "The value name is [%s]");
        requireArg(valueName, notBlank(), "The value name [%s] is blank");
        this.valueName = valueName;
        return this;
      }

      /**
       * Constructs a new template, using the values that were applied to this
       * builder. Changes made to this builder afterwards, do not affect the
       * created template. As such, a builder can be re-used several times.
       *
       * @return a new template
       * @throws IllegalStateException the template is invalid
       */
      public Template build() {
        requireState(this, hasShortName().or(hasLongName()),
          "Template has neither a short name nor a long name"
        );
        requireState(minValues, not(greaterThan(maxValues)), format(
          "Min values [%%d] greater than max values [%d]", maxValues)
        );
        return new Template(this);
      }

      /**
       * @see Template#toString() Template.toString()
       */
      @Override
      public String toString() {
        StringBuilder builder = new StringBuilder();

        // Include the name.
        if (shortName != null) {
          builder.append("-").append(shortName);
        }
        else if (longName != null) {
          builder.append("--").append(longName);
        }
        else {
          builder.append("-").append("<undefined>");
        }

        boolean canHaveValues = maxValues != 0;
        boolean valuesOptional = minValues == 0;

        // Include value name if applicable.
        if (canHaveValues) {
          builder.append("=");

          // Surround optional values with [].
          if (valuesOptional) {
            builder.append("[");
          }

          builder.append("<");
          builder.append(valueName == null ? "value" : valueName);
          builder.append(">");

          // Surround optional values with [].
          if (valuesOptional) {
            builder.append("]");
          }
        }

        return builder.toString();
      }

      /**
       * Predicate that tests for the presence of a short name.
       *
       * @return a predicate that tests for the presence of a short name
       */
      private static Predicate<Option.Template.Builder> hasShortName() {
        return notNull(builder -> builder.shortName);
      }

      /**
       * Predicate that tests for tne presence of a long name.
       *
       * @return a predicate that tests for the presence of a long name
       */
      private  static Predicate<Option.Template.Builder> hasLongName() {
        return notNull(builder -> builder.longName);
      }
    }
  }

  /**
   * <p>An <em>immutable</em> group of mutually <em>exclusive</em> templates.
   * Any two templates in the same group cannot be present simultaneously on a
   * command line. Groups can be either required or optional.
   *
   * <p>A group can only be configured by means of a {@link Builder Builder},
   * acquired using one of the <em>static factory methods</em>. A convenience
   * implementation is available to create a group from scratch.
   */
  public static final class Group {

    /** An indication that this group is required. */
    private final boolean required;
    /** The mutually exclusive templates. */
    private final Set<Template> templates;

    /**
     * Creates a new group, initialized with a builder's values. Changes made to
     * the builder afterwards, do not affect this group. As such, a builder can
     * be re-used several times.
     *
     * @param builder the settings of the new descriptor
     * @throws NullPointerException the builder is null
     */
    Group(Builder builder) {
      this.required = builder.required;
      this.templates = new HashSet<>(builder.templates);
    }

    /**
     * Indicates this group is required or optional.
     *
     * @return true if this group is required or optional
     */
    public boolean isRequired() {
      return required;
    }

    /**
     * Returns the templates in this group as an immutable set.
     *
     * @return the templates in this group as an immutable set
     */
    public Set<Template> getTemplates() {
      return Collections.unmodifiableSet(templates);
    }

    /**
     * Returns a human-readable representation of this group. The format is:
     * [{@link #getTemplates() templates}].
     *
     * @return a human-readable representation of this group
     */
    @Override
    public String toString() {
      return new Builder(this).toString();
    }

    /**
     * <p>A builder to create new {@link Group groups} in a fluent, chained
     * fashion. Typically used by a developer to refrain the user from
     * specifying a certain combination of options on the command line.
     *
     * <p><strong>NOTE:</strong> A builder can be re-used several times without
     * affecting previously built options. However, values previously applied to
     * the builder, stick after creating an option, unless overridden again.
     */
    public static final class Builder {

      private boolean required;
      private final Set<Template> templates;

      /**
       * Creates a new, uninitialized builder.
       */
      Builder() {
        templates = new HashSet<>();
      }

      /**
       * Creates a new builder, initialized with a group's values.
       *
       * @param group the settings of the new builder
       * @throws NullPointerException the group is null
       */
      Builder(Group group) {
        required = group.required;
        templates = new HashSet<>(group.templates);
      }

      /**
       * Makes this group required.
       *
       * @return this builder
       */
      public Builder required() {
        required = true;
        return this;
      }

      /**
       * Adds a template to the new group.
       *
       * @param template the template to add
       * @return this builder
       * @throws IllegalArgumentException the template is null
       * @throws IllegalArgumentException the template is required
       */
      public Builder template(Template template) {
        requireArg(template, notNull(), "The template is [%s]");
        requireArg(template, notRequired(), "The template [%s] is required");
        templates.add(template);
        return this;
      }

      /**
       * Adds templates to the new group.
       *
       * @param templates the templates to add
       * @return this builder
       * @throws IllegalArgumentException the array or a template is null
       * @throws IllegalArgumentException a template is required
       */
      public Builder templates(Template... templates) {
        requireArg(templates, notNull(), "The template array is [%s]");
        stream(templates).forEach(this::template);
        return this;
      }

      /**
       * Adds templates to the new group.
       *
       * @param templates the templates to add
       * @return this builder
       * @throws IllegalArgumentException the iterable or a template is null
       * @throws IllegalArgumentException a template is required
       */
      public Builder templates(Iterable<Template> templates) {
        requireArg(templates, notNull(), "The template iterable is [%s]");
        templates.forEach(this::template);
        return this;
      }

      /**
       * @see Group#toString() Group.toString()
       */
      @Override
      public String toString() {
        StringJoiner joiner = new StringJoiner(",", "[", "]");

        templates.stream()
          .sorted(Template::byName)
          .map(Object::toString)
          .forEach(joiner::add);

        return joiner.toString();
      }

      /**
       * Constructs a new group, using the values that were applied to this
       * builder. Changes made to this builder afterwards, do not affect the
       * created group. As such, a builder can be re-used several times.
       *
       * @return a new group
       */
      public Group build() {
        return new Group(this);
      }
    }
  }
}

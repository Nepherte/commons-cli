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

import com.nepherte.commons.cli.Command.Descriptor;
import com.nepherte.commons.cli.Option.Template;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import static com.nepherte.commons.test.Matchers.optionalWithValue;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import static org.hamcrest.collection.IsEmptyIterable.*;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.*;
import static org.hamcrest.collection.IsIterableContainingInOrder.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test that covers {@link Command.Descriptor}.
 */
class DescriptorTest {

  @Test
  void name() {
    Descriptor.Builder builder = Command.newDescriptor().name("name");
    assertThat(new Descriptor(builder).getName(), optionalWithValue("name"));
  }

  @Test
  void nullName() {
    assertThrows(IllegalArgumentException.class,
      () -> Command.newDescriptor().name(null));
  }

  @Test
  void blankName() {
    assertThrows(IllegalArgumentException.class,
      () -> Command.newDescriptor().name("  "));
  }

  @Test
  void minArgs() {
    Descriptor.Builder builder = Command.newDescriptor().minArgs(2);
    assertThat(new Descriptor(builder).getMinArgs(), is(2));
  }

  @Test
  void requiresArgs() {
    Descriptor.Builder builder = Command.newDescriptor();
    assertThat(new Descriptor(builder.minArgs(0)).requiresArgs(), is(false));
    assertThat(new Descriptor(builder.minArgs(2)).requiresArgs(), is(true));
  }

  @Test
  void negativeMinArgs() {
    assertThrows(IllegalArgumentException.class,
      () -> Command.newDescriptor().minArgs(-1));
  }

  @Test
  void maxArgs() {
    Descriptor.Builder builder = Command.newDescriptor().maxArgs(2);
    assertThat(new Descriptor(builder).getMaxArgs(), is(2));
  }

  @Test
  void canHaveArgs() {
    Descriptor.Builder builder = Command.newDescriptor();
    assertThat(new Descriptor(builder.maxArgs(0)).canHaveArgs(), is(false));
    assertThat(new Descriptor(builder.maxArgs(2)).canHaveArgs(), is(true));
  }

  @Test
  void negativeMaxArgs() {
    assertThrows(IllegalArgumentException.class,
      () -> Command.newDescriptor().maxArgs(-1));
  }

  @Test
  void requiresMoreArgsThanAllowed() {
    assertThrows(IllegalStateException.class, () ->
      Command.newDescriptor().minArgs(2).maxArgs(1).build());
  }

  @Test
  void shortTemplate() {
    Template template = mock(Template.class);
    when(template.getShortName()).thenReturn(Optional.of("b"));

    Descriptor.Builder builder = Command.newDescriptor().template(template);
    Descriptor descriptor = new Descriptor(builder);

    // Query template using short name.
    assertThat(descriptor.getShortTemplate("b"), optionalWithValue(template));

    // Check presence in template sets.
    assertThat(descriptor.getTemplates(), contains(template));
    assertThat(descriptor.getRequiredTemplates(), not(contains(template)));
  }

  @Test
  void longTemplate() {
    Template template = mock(Template.class);
    when(template.getLongName()).thenReturn(Optional.of("size"));

    Descriptor.Builder builder = Command.newDescriptor().template(template);
    Descriptor descriptor = new Descriptor(builder);

    // Query template using long name.
    assertThat(descriptor.getLongTemplate("size"), optionalWithValue(template));

    // Check presence in template sets.
    assertThat(descriptor.getTemplates(), contains(template));
    assertThat(descriptor.getRequiredTemplates(), not(contains(template)));
  }

  @Test
  void requiredTemplate() {
    Template template = mock(Template.class);
    when(template.isRequired()).thenReturn(true);

    Descriptor.Builder builder = Command.newDescriptor().template(template);
    Descriptor descriptor = new Descriptor(builder);

    // Check presence in template sets.
    assertThat(descriptor.getRequiredTemplates(), contains(template));
  }

  @Test
  void nullTemplate() {
    assertThrows(IllegalArgumentException.class,
      () -> Command.newDescriptor().template(null));
  }

  @Test
  void templateArray() {
    Template t1 = mock(Template.class);
    when(t1.getShortName()).thenReturn(Optional.of("a"));

    Template t2 = mock(Template.class);
    when(t2.getLongName()).thenReturn(Optional.of("b"));

    Descriptor.Builder builder = Command.newDescriptor();
    builder = builder.templates(t1, t2);
    Descriptor descriptor = new Descriptor(builder);

    // Query template A and B.
    assertThat(descriptor.getShortTemplate("a"), optionalWithValue(t1));
    assertThat(descriptor.getLongTemplate("b"), optionalWithValue(t2));

    // Check presence in template sets.
    assertThat(descriptor.getTemplates(), containsInAnyOrder(t1, t2));
  }

  @Test
  void nullTemplateArray() {
    assertThrows(IllegalArgumentException.class, () ->
      Command.newDescriptor().templates((Template[]) null));
  }

  @Test
  void templateIterable() {
    Template t1 = mock(Template.class);
    when(t1.getShortName()).thenReturn(Optional.of("a"));

    Template t2 = mock(Template.class);
    when(t2.getLongName()).thenReturn(Optional.of("b"));

    Descriptor.Builder builder = Command.newDescriptor();
    builder = builder.templates(Arrays.asList(t1, t2));
    Descriptor descriptor = new Descriptor(builder);

    // Query template A and B.
    assertThat(descriptor.getShortTemplate("a"), optionalWithValue(t1));
    assertThat(descriptor.getLongTemplate("b"), optionalWithValue(t2));

    // Check presence in template sets.
    assertThat(descriptor.getTemplates(), containsInAnyOrder(t1, t2));
  }

  @Test
  void nullTemplateIterable() {
    assertThrows(IllegalArgumentException.class, () ->
      Command.newDescriptor().templates((Iterable<Template>) null));
  }

  @Test
  void immutableTemplates() {
    Template template = mock(Template.class);
    Descriptor.Builder builder = Command.newDescriptor();

    assertThrows(UnsupportedOperationException.class, () ->
      new Descriptor(builder).getTemplates().add(template));
  }

  @Test
  void immutableRequiredTemplates() {
    Template template = mock(Template.class);
    Descriptor.Builder builder = Command.newDescriptor();

    assertThrows(UnsupportedOperationException.class, () ->
      new Descriptor(builder).getRequiredTemplates().add(template));
  }

  @Test
  void group() {
    Template template = mock(Template.class);
    when(template.isRequired()).thenReturn(false);

    Option.Group group = mock(Option.Group.class);
    when(group.isRequired()).thenReturn(false);
    when(group.getTemplates()).thenReturn(Set.of(template));

    Descriptor.Builder builder = Command.newDescriptor().group(group);
    Descriptor descriptor = new Descriptor(builder);

    // Query group of an template.
    assertThat(descriptor.getGroup(template), optionalWithValue(group));

    // Check presence in group sets.
    assertThat(descriptor.getGroups(), containsInAnyOrder(group));
    assertThat(descriptor.getRequiredGroups(), not(containsInAnyOrder(group)));
  }

  @Test
  void requiredGroup() {
    Template t = mock(Template.class);
    when(t.isRequired()).thenReturn(false);

    Option.Group g = mock(Option.Group.class);
    when(g.isRequired()).thenReturn(true);
    when(g.getTemplates()).thenReturn(Set.of(t));

    Descriptor.Builder builder = Command.newDescriptor().group(g);
    Descriptor descriptor = new Descriptor(builder);

    // Query group of an template.
    assertThat(descriptor.getGroup(t), optionalWithValue(g));

    // Check presence in group sets.
    assertThat(descriptor.getGroups(), containsInAnyOrder(g));
    assertThat(descriptor.getRequiredGroups(), containsInAnyOrder(g));
  }

  @Test
  void nullGroup() {
    assertThrows(IllegalArgumentException.class,
      () -> Command.newDescriptor().group(null));
  }

  @Test
  void groupArray() {
    Template templateA = mock(Template.class);
    Template templateB = mock(Template.class);

    Option.Group groupA = mock(Option.Group.class);
    when(groupA.getTemplates()).thenReturn(Set.of(templateA));

    Option.Group groupB = mock(Option.Group.class);
    when(groupB.getTemplates()).thenReturn(Set.of(templateB));

    Descriptor.Builder builder = Command.newDescriptor();
    builder = builder.groups(groupA, groupB);
    Descriptor descriptor = new Descriptor(builder);

    // Query group of the options.
    assertThat(descriptor.getGroup(templateA), optionalWithValue(groupA));
    assertThat(descriptor.getGroup(templateB), optionalWithValue(groupB));

    // Check presence in group sets.
    assertThat(descriptor.getGroups(), containsInAnyOrder(groupA, groupB));
    assertThat(descriptor.getRequiredGroups(), emptyIterable());
  }

  @Test
  void nullGroupArray() {
    assertThrows(IllegalArgumentException.class, () ->
      Command.newDescriptor().groups((Option.Group[]) null));
  }

  @Test
  void groupIterable() {
    Template templateA = mock(Template.class);
    Template templateB = mock(Template.class);

    Option.Group groupA = mock(Option.Group.class);
    when(groupA.getTemplates()).thenReturn(Set.of(templateA));

    Option.Group groupB = mock(Option.Group.class);
    when(groupB.getTemplates()).thenReturn(Set.of(templateB));

    Descriptor.Builder builder = Command.newDescriptor();
    builder = builder.groups(Arrays.asList(groupA, groupB));
    Descriptor descriptor = new Descriptor(builder);

    // Query group of the options.
    assertThat(descriptor.getGroup(templateA), optionalWithValue(groupA));
    assertThat(descriptor.getGroup(templateB), optionalWithValue(groupB));

    // Check presence in group sets.
    assertThat(descriptor.getGroups(), containsInAnyOrder(groupA, groupB));
    assertThat(descriptor.getRequiredGroups(), emptyIterable());
  }

  @Test
  void nullGroupIterable() {
    assertThrows(IllegalArgumentException.class, () ->
      Command.newDescriptor().groups((Iterable<Option.Group>) null));
  }

  @Test
  void unmodifiableGroups() {
    Option.Group group = mock(Option.Group.class);
    Descriptor.Builder builder = Command.newDescriptor();

    assertThrows(UnsupportedOperationException.class,
      () -> new Descriptor(builder).getGroups().add(group));
  }

  @Test
  void unmodifiableRequiredGroups() {
    Option.Group group = mock(Option.Group.class);
    Descriptor.Builder builder = Command.newDescriptor();

    assertThrows(UnsupportedOperationException.class, () ->
      new Descriptor(builder).getRequiredGroups().add(group));
  }

  @Test
  void stringValue() {
    Template templateA = mock(Template.class);
    when(templateA.getName()).thenReturn("a");
    when(templateA.toString()).thenReturn("-a");

    Template templateB = mock(Template.class);
    when(templateB.getName()).thenReturn("b");
    when(templateB.toString()).thenReturn("--b");

    // Descriptor with no name, templates, or args.
    Descriptor.Builder builder = Command.newDescriptor();
    assertThat(builder.toString(), is("<undefined>"));

    // Descriptor with name, no templates and no args.
    builder = builder.name("cmd");
    assertThat(builder.toString(), is("cmd"));

    // Descriptor with name and templates, no args.
    builder = builder.templates(templateA, templateB);
    assertThat(builder.toString(), is("cmd -a --b"));

    // Descriptor with name, templates and optional args.
    builder = builder.maxArgs(5);
    assertThat(builder.toString(), is("cmd -a --b [<args>]"));

    // Descriptor with name, templates and required args.
    builder = builder.minArgs(5);
    assertThat(builder.toString(), is("cmd -a --b <args>"));
  }
}
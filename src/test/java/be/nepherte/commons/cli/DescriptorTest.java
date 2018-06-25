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

import be.nepherte.commons.cli.Command.Descriptor;
import be.nepherte.commons.cli.Option.Template;

import org.junit.Test;

import java.util.Arrays;
import java.util.Optional;

import static be.nepherte.commons.cli.Collections.immutableSetOf;
import static be.nepherte.commons.test.Matchers.optionalWithNoValue;
import static be.nepherte.commons.test.Matchers.optionalWithValue;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.collection.IsEmptyIterable.emptyIterable;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test that covers {@link Command.Descriptor}.
 */
public class DescriptorTest {

  @Test
  public void name() {
    Descriptor.Builder builder = Command.newDescriptor().name("name");
    assertThat(new Descriptor(builder).getName(), optionalWithValue("name"));
  }

  @Test
  public void nullName() {
    Descriptor descriptor = Command.newDescriptor().name(null).build();
    assertThat(descriptor.getName(), optionalWithNoValue());
  }

  @Test(expected = IllegalArgumentException.class)
  public void whitespaceName() {
    Command.newDescriptor().name("  ").build();
  }

  @Test
  public void minArgs() {
    Descriptor.Builder builder = Command.newDescriptor().minArgs(2);
    assertThat(new Descriptor(builder).getMinArgs(), is(2));
  }

  @Test
  public void requiresArgs() {
    Descriptor.Builder builder = Command.newDescriptor();
    assertThat(new Descriptor(builder.minArgs(0)).requiresArgs(), is(false));
    assertThat(new Descriptor(builder.minArgs(2)).requiresArgs(), is(true));
  }

  @Test(expected = IllegalArgumentException.class)
  public void negativeMinArgs() {
    Command.newDescriptor().minArgs(-1);
  }

  @Test
  public void maxArgs() {
    Descriptor.Builder builder = Command.newDescriptor().maxArgs(2);
    assertThat(new Descriptor(builder).getMaxArgs(), is(2));
  }

  @Test
  public void canHaveArgs() {
    Descriptor.Builder builder = Command.newDescriptor();
    assertThat(new Descriptor(builder.maxArgs(0)).canHaveArgs(), is(false));
    assertThat(new Descriptor(builder.maxArgs(2)).canHaveArgs(), is(true));
  }

  @Test(expected = IllegalArgumentException.class)
  public void negativeMaxArgs() {
    Command.newDescriptor().maxArgs(-1);
  }

  @Test(expected = IllegalStateException.class)
  public void requiresMoreArgsThanAllowed() {
    Command.newDescriptor().minArgs(2).maxArgs(1).build();
  }

  @Test
  public void shortTemplate() {
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
  public void longTemplate() {
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
  public void requiredTemplate() {
    Template template = mock(Template.class);
    when(template.isRequired()).thenReturn(true);

    Descriptor.Builder builder = Command.newDescriptor().template(template);
    Descriptor descriptor = new Descriptor(builder);

    // Check presence in template sets.
    assertThat(descriptor.getRequiredTemplates(), contains(template));
  }

  @Test
  public void nullTemplate() {
    Descriptor.Builder builder = Command.newDescriptor().template(null);
    assertThat(new Descriptor(builder).getTemplates(), emptyIterable());
  }

  @Test
  public void templateArray() {
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
  public void templateIterable() {
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
  public void nullTemplateIterable() {
    Descriptor.Builder builder = Command.newDescriptor();
    builder = builder.templates((Iterable<Template>) null);
    Descriptor descriptor = new Descriptor(builder);

    assertThat(descriptor.getTemplates(), emptyIterable());
  }

  @Test
  public void nullTemplateArray() {
    Descriptor.Builder builder = Command.newDescriptor();
    builder = builder.templates(null, null);
    Descriptor descriptor = new Descriptor(builder);

    assertThat(descriptor.getTemplates(), emptyIterable());
  }

  @Test(expected = UnsupportedOperationException.class)
  public void immutableTemplates() {
    Template template = mock(Template.class);
    Descriptor.Builder builder = Command.newDescriptor();
    new Descriptor(builder).getTemplates().add(template);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void immutableRequiredTemplates() {
    Template template = mock(Template.class);
    Descriptor.Builder builder = Command.newDescriptor();
    new Descriptor(builder).getRequiredTemplates().add(template);
  }

  @Test
  public void group() {
    Template template = mock(Template.class);
    when(template.isRequired()).thenReturn(false);

    Option.Group group = mock(Option.Group.class);
    when(group.isRequired()).thenReturn(false);
    when(group.getTemplates()).thenReturn(immutableSetOf(template));

    Descriptor.Builder builder = Command.newDescriptor().group(group);
    Descriptor descriptor = new Descriptor(builder);

    // Query group of an template.
    assertThat(descriptor.getGroup(template), optionalWithValue(group));

    // Check presence in group sets.
    assertThat(descriptor.getGroups(), containsInAnyOrder(group));
    assertThat(descriptor.getRequiredGroups(), not(containsInAnyOrder(group)));
  }

  @Test
  public void requiredGroup() {
    Template t = mock(Template.class);
    when(t.isRequired()).thenReturn(false);

    Option.Group g = mock(Option.Group.class);
    when(g.isRequired()).thenReturn(true);
    when(g.getTemplates()).thenReturn(immutableSetOf(t));

    Descriptor.Builder builder = Command.newDescriptor().group(g);
    Descriptor descriptor = new Descriptor(builder);

    // Query group of an template.
    assertThat(descriptor.getGroup(t), optionalWithValue(g));

    // Check presence in group sets.
    assertThat(descriptor.getGroups(), containsInAnyOrder(g));
    assertThat(descriptor.getRequiredGroups(), containsInAnyOrder(g));
  }

  @Test
  public void nullGroup() {
    Descriptor.Builder builder = Command.newDescriptor().group(null);
    assertThat(new Descriptor(builder).getGroups(), emptyIterable());
  }

  @Test
  public void groupArray() {
    Template templateA = mock(Template.class);
    Template templateB = mock(Template.class);

    Option.Group groupA = mock(Option.Group.class);
    when(groupA.getTemplates()).thenReturn(immutableSetOf(templateA));

    Option.Group groupB = mock(Option.Group.class);
    when(groupB.getTemplates()).thenReturn(immutableSetOf(templateB));

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
  public void groupIterable() {
    Template templateA = mock(Template.class);
    Template templateB = mock(Template.class);

    Option.Group groupA = mock(Option.Group.class);
    when(groupA.getTemplates()).thenReturn(immutableSetOf(templateA));

    Option.Group groupB = mock(Option.Group.class);
    when(groupB.getTemplates()).thenReturn(immutableSetOf(templateB));

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
  public void nullGroupArray() {
    Descriptor.Builder builder = Command.newDescriptor();
    builder = builder.groups(null, null, null);
    Descriptor descriptor = new Descriptor(builder);
    assertThat(descriptor.getGroups(), emptyIterable());
  }

  @Test
  public void nullGroupIterable() {
    Descriptor.Builder builder = Command.newDescriptor();
    builder = builder.groups((Iterable<Option.Group>) null);
    Descriptor descriptor = new Descriptor(builder);
    assertThat(descriptor.getGroups(), emptyIterable());
  }

  @Test(expected = UnsupportedOperationException.class)
  public void unmodifiableGroups() {
    Option.Group group = mock(Option.Group.class);
    Descriptor.Builder builder = Command.newDescriptor();
    new Descriptor(builder).getGroups().add(group);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void unmodifiableRequiredGroups() {
    Option.Group group = mock(Option.Group.class);
    Descriptor.Builder builder = Command.newDescriptor();
    new Descriptor(builder).getRequiredGroups().add(group);
  }

  @Test
  public void stringValue() {
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
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

import be.nepherte.commons.cli.Option.Group;
import be.nepherte.commons.cli.Option.Template;

import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test that covers {@link Option.Group}.
 */
public class GroupTest {

  @Test
  public void requiredGroup() {
    Group.Builder builder = Option.newGroup().required();
    assertThat(new Group(builder).isRequired(), is(true));
  }

  @Test
  public void optionalGroup() {
    Group.Builder builder = Option.newGroup();
    assertThat(new Group(builder).isRequired(), is(false));
  }

  @Test
  public void template() {
    Template template = mock(Template.class);
    Group.Builder builder = Option.newGroup().template(template);
    assertThat(new Group(builder).getTemplates(), containsInAnyOrder(template));
  }

  @Test(expected = IllegalArgumentException.class)
  public void requiredTemplate() {
    Template template = mock(Template.class);
    when(template.isRequired()).thenReturn(true);
    Option.newGroup().template(template);
  }

  @Test
  public void templatesArray() {
    Template t1 = mock(Template.class);
    Template t2 = mock(Template.class);

    Group.Builder builder = Option.newGroup().templates(t1, t2);
    assertThat(new Group(builder).getTemplates(), containsInAnyOrder(t1, t2));
  }

  @Test(expected = IllegalArgumentException.class)
  public void nullTemplatesArray() {
    Option.newGroup().templates((Template[]) null);
  }

  @Test
  public void templatesIterable() {
    Template t1 = mock(Template.class);
    Template t2 = mock(Template.class);

    Group.Builder builder = Option.newGroup().templates(Arrays.asList(t1, t2));
    assertThat(new Group(builder).getTemplates(), containsInAnyOrder(t1, t2));
  }

  @Test(expected = IllegalArgumentException.class)
  public void nullTemplatesIterable() {
    Option.newGroup().templates((Iterable<Template>) null);
  }

  @Test
  public void stringValue() {
    // Empty optional group.
    Group.Builder builder = Option.newGroup();
    assertThat(builder.toString(), is("[]"));

    // Empty required group.
    builder = builder.required();
    assertThat(builder.toString(), is("[]"));

    // Non-empty, required group.
    Template t1 = mock(Template.class);
    when(t1.getName()).thenReturn("a");
    when(t1.toString()).thenReturn("-a");

    Template t2 = mock(Template.class);
    when(t2.getName()).thenReturn("b");
    when(t2.toString()).thenReturn("--b=[<value>]");

    builder = builder.templates(t1, t2);
    assertThat(builder.toString(), is("[-a,--b=[<value>]]"));
  }
}

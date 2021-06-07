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

import com.nepherte.commons.cli.internal.OptionFormat;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

/**
 * Matcher that verifies an {@link OptionFormat} has a given set of features.
 */
final class SupportsFeature extends BaseMatcher<OptionFormat> {

  private final int features;

  /**
   * Creates a new {@code SupportsFeature}.
   *
   * @param features the expected set of features
   */
  SupportsFeature(int features) {
    this.features = features;
  }

  @Override
  public boolean matches(Object actual) {
    OptionFormat optionFormat = (OptionFormat) actual;
    return optionFormat.supportsFeatures(features);
  }

  @Override
  public void describeTo(Description description) {
    description.appendText("Option format [features=" + features + "]");
  }
}

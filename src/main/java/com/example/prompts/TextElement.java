/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.prompts;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.example.util.Utils;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TextElement implements Element {

  private List<String> textVariants;
  private List<String> arguments;

  /**
   * Creates a {@link TextElement} for a {@link Prompt}, with different possible
   * text variants.
   *
   * @param textVariants a list of possible variants of texts for this element.
   * Cannot be {@code null} or empty.
   */
  TextElement(String... textVariants) {
    this(Arrays.asList(textVariants), Collections.emptyList());
  }

  /**
   * Creates a {@link TextElement} for a {@link Prompt}, with different possible
   * text variants.
   *
   * @param textVariants a list of possible variants of texts for this element
   * in the form of format strings. Cannot be {@code null} or empty.
   * @param arguments A list of arguments to be referenced by the format
   * specifiers in the format strings provided in {@code textList}. Cannot be
   * {@code null}.
   */
  TextElement(List<String> textVariants, List<String> arguments) {
    checkNotNull(textVariants, "textVariants cannot be null.");
    checkArgument(!textVariants.isEmpty(), "textVariants cannot be empty.");
    checkNotNull(arguments, "arguments cannot be null.");
    this.textVariants = textVariants;
    this.arguments = arguments;
  }

  @Override
  public String getSpeechText() {
    return getDisplayText();
  }

  @Override
  public String getDisplayText() {
    return formatText(
        textVariants.get(Utils.getRandomNumber(0, textVariants.size() - 1)));
  }

  private String formatText(String string) {
    String[] args = arguments.toArray(new String[arguments.size()]);
    return String.format(string, (Object[]) args);
  }
}

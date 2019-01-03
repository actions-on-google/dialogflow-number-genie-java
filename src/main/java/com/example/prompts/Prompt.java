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

import com.google.api.services.actions_fulfillment.v2.model.Suggestion;
import java.util.Collections;
import java.util.List;

public class Prompt {

  private final List<PartialPrompt> partialPrompts;
  private final List<String> suggestions;
  private final ImageCard imageCard;

  /**
   * Creates a {@link Prompt}, given a list of {@link PartialPrompt}s.
   *
   * @param partialPrompts the {@code PartialPrompt}s that make up this {@code
   * Prompt}. Cannot be {@code null} or empty.
   */
  Prompt(List<PartialPrompt> partialPrompts) {
    this(partialPrompts, Collections.emptyList(), null);
  }

  /**
   * Creates a {@link Prompt}, given a list of {@link PartialPrompt}s and a list
   * of {@link Suggestion} strings.
   *
   * @param partialPrompts the {@code PartialPrompt}s that make up this {@code
   * Prompt}. Cannot be {@code null} or empty.
   * @param suggestions a list of strings to be used as {@code Suggestion}s.
   * Cannot be {@code null}.
   */
  Prompt(List<PartialPrompt> partialPrompts, List<String> suggestions) {
    this(partialPrompts, suggestions, null);
  }

  /**
   * Creates a {@link Prompt}, given a list of {@link PartialPrompt}s, a list of
   * {@link Suggestion} strings, and an {@link ImageCard}.
   *
   * @param partialPrompts the {@code PartialPrompt}s that make up this {@code
   * Prompt}. Cannot be {@code null} or empty.
   * @param suggestions a list of strings to be used as {@code Suggestion}s.
   * Cannot be {@code null}.
   * @param imageCard an {@link ImageCard} containing an image that will be
   * displayed as a card to the user.
   */
  Prompt(List<PartialPrompt> partialPrompts, List<String> suggestions,
      ImageCard imageCard) {
    checkNotNull(partialPrompts, "partialPrompts cannot be null.");
    checkArgument(!partialPrompts.isEmpty(), "partialPrompts cannot be empty.");
    checkNotNull(suggestions, "suggestions cannot be null.");
    this.partialPrompts = partialPrompts;
    this.suggestions = suggestions;
    this.imageCard = imageCard;
  }

  public List<PartialPrompt> getPartialPrompts() {
    return partialPrompts;
  }

  public List<String> getSuggestions() {
    return suggestions;
  }

  public ImageCard getImageCard() {
    return imageCard;
  }
}
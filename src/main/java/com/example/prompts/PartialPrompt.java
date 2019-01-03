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

import java.util.Arrays;
import java.util.List;

public class PartialPrompt {

  private List<Element> parts;

  /**
   * Creates a {@link PartialPrompt}, given the provided {@link Element}s.
   *
   * @param parts the {@code Element}s that make up this {@code PartialPrompt}.
   * Cannot be {@code null} or empty.
   */
  public PartialPrompt(Element... parts) {
    this(Arrays.asList(parts));
  }

  /**
   * Creates a {@link PartialPrompt}, given a list of {@link Element}s.
   *
   * @param parts the list of {@code Element}s that make up this {@code
   * PartialPrompt}. Cannot be {@code null} or empty.
   */
  public PartialPrompt(List<Element> parts) {
    checkNotNull(parts, "parts cannot be null.");
    checkArgument(!parts.isEmpty(), "parts cannot be empty.");
    this.parts = parts;
  }

  /**
   * Gets the individual {@link Element}s of this {@link PartialPrompt}.
   *
   * @return a list of {@code Element}s for this {@code PartialPrompt}.
   */
  public List<Element> getParts() {
    return parts;
  }
}

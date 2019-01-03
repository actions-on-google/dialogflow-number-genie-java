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
import java.util.List;

public class ImageCard {

  private String url;
  private String altText;
  private List<String> textVariants;

  /**
   * Creates an {@link ImageCard} with the given URL, alternative text, and list
   * of varied text.
   *
   * @param url the url of the image. Cannot be {@code null}.
   * @param altText the alternative text for the image. Cannot be {@code null}.
   * @param textVariants a list of text variants to be displayed on the card.
   * Cannot be {@code null} or empty.
   */
  public ImageCard(String url, String altText, List<String> textVariants) {
    checkNotNull(url, "url cannot be null.");
    checkNotNull(altText, "altText cannot be null.");
    checkNotNull(textVariants, "textVariants cannot be null.");
    checkArgument(!textVariants.isEmpty(), "textVariants cannot be empty.");
    this.url = url;
    this.altText = altText;
    this.textVariants = textVariants;
  }

  /**
   * Gets the URL of the image for this {@link ImageCard}.
   *
   * @return a string representation of the image URL.
   */
  public String getUrl() {
    return url;
  }

  /**
   * Gets the alternative text of the image for this {@link ImageCard}.
   *
   * @return a string of the alternative text for the image.
   */
  public String getAltText() {
    return altText;
  }

  /**
   * Gets a text string to be displayed on the {@link ImageCard}.
   *
   * @return a text string to be displayed on the card.
   */
  public String getVariantText() {
    return textVariants.get(Utils.getRandomNumber(0, textVariants.size() - 1));
  }
}

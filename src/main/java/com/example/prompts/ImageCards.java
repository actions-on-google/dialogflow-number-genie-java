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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

final class ImageCards {

  private static final String URL_FORMAT_STRING = "https://%s.appspot.com/images/%s";

  private ImageCards() {
  }

  static ImageCard getColdImageCard(Locale locale) {
    return getImageCard(locale, "COLD.gif", "cold_alt_text", "cold_text_1",
        "cold_text_2", "cold_text_3");
  }

  static ImageCard getCoolImageCard(Locale locale) {
    return getImageCard(locale, "COOL.gif", "cool_alt_text", "cool_text_1",
        "cool_text_2", "cool_text_3");
  }

  static ImageCard getWarmImageCard(Locale locale) {
    return getImageCard(locale, "WARM.gif", "warm_alt_text", "warm_text_1",
        "warm_text_2", "warm_text_3");
  }

  static ImageCard getHotImageCard(Locale locale) {
    return getImageCard(locale, "HOT.gif", "hot_alt_text", "hot_text_1",
        "hot_text_2", "hot_text_3");
  }

  static ImageCard getIntroImageCard(Locale locale) {
    return getImageCard(locale, "INTRO.gif", "intro_alt_text", "intro_text_1",
        "intro_text_2", "intro_text_3");
  }

  static ImageCard getWinImageCard(Locale locale) {
    return getImageCard(locale, "WIN.gif", "win_alt_text", "win_text_1",
        "win_text_2", "win_text_3");
  }

  private static ImageCard getImageCard(Locale locale, String fileName,
      String altText, String... textVariants) {
    checkNotNull(locale, "locale cannot be null.");
    ResourceBundle config = ResourceBundle.getBundle("config", locale);
    ResourceBundle rb = ResourceBundle.getBundle("prompts", locale);
    List<String> imageTextVariants = new ArrayList<>();
    for (String textVariant : textVariants) {
      imageTextVariants.add(rb.getString(textVariant));
    }
    String projectId = config.getString("project_id");
    String url = String.format(URL_FORMAT_STRING, projectId, fileName);
    String imageAltText = rb.getString(altText);
    return new ImageCard(url, imageAltText, imageTextVariants);
  }
}

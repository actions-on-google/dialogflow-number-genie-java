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

public class AudioElement implements Element {

  private String url;

  /**
   * Create an {@link AudioElement} for a {@link Prompt}, given a URL for the
   * audio source.
   * @param url the URL to the audio source. Cannot be {@code null}.
   */
  AudioElement(String url) {
    checkNotNull(url, "url cannot be null.");
    this.url = url;
  }

  @Override
  public String getSpeechText() {
    return "<audio src=\"" + url + "\"/>";
  }

  @Override
  public String getDisplayText() {
    return "";
  }
}

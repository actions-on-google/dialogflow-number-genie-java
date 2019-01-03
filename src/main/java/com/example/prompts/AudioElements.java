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

import java.util.ResourceBundle;

final class AudioElements {

  private static final String URL_FORMAT_STRING = "https://%s.appspot.com/audio/%s";

  private AudioElements() {
  }

  static Element getSteamAudioElement() {
    return getAudioElement("Earcon_Steam.wav");
  }

  static Element getSteamOnlyAudioElement() {
    return getAudioElement("Earcon_SteamOnly.wav");
  }

  static Element getWinAudioElement() {
    return getAudioElement("Earcon_YouWin.wav");
  }

  private static Element getAudioElement(String fileName) {
    ResourceBundle config = ResourceBundle.getBundle("config");
    String projectId = config.getString("project_id");
    String url = String.format(URL_FORMAT_STRING, projectId, fileName);
    return new AudioElement(url);
  }
}

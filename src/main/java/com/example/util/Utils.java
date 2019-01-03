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

package com.example.util;

public final class Utils {

  /**
   * Gets a random number between {@code min} and {@code max}.
   * @param min the minimum possible number.
   * @param max the maximum possible number.
   * @return a random number between {@code min} and {@code max}.
   */
  public static int getRandomNumber(int min, int max) {
    return (int) Math.floor(Math.random() * (max - min + 1)) + min;
  }

  /**
   * Checks whether the specified value is in the provided bounds.
   *
   * @param i the value to check
   * @param lower the lower bound (inclusive)
   * @param upper the upper bound (inclusive)
   * @return {@code true} if {@code i} is within the bounds of {@code lower} and
   * {@code upper}, otherwise {@code false}.
   */
  public static boolean isInBounds(int i, int lower, int upper) {
    return i >= lower && i <= upper;
  }
}
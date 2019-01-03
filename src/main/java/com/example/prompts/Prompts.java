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

import com.google.actions.api.ActionRequest;
import com.google.api.services.actions_fulfillment.v2.model.Argument;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.stream.IntStream;

public final class Prompts {

  private Prompts() {
  }

  public static Prompt getStartGamePrompt(ActionRequest request) {
    checkNotNull(request, "request cannot be null.");
    Locale locale = request.getLocale();
    ResourceBundle rb = ResourceBundle.getBundle("prompts", locale);
    String[] greetingVariants = new String[]{
        rb.getString("greeting_1"),
        rb.getString("greeting_2"),
        rb.getString("greeting_3")
    };
    List<String> invocation =
        Collections.singletonList(rb.getString("invocation"));
    String invocationGuess = rb.getString("invocation_guess");

    Map<String, Object> conversationData = request.getConversationData();
    int min = ((Double) conversationData.get("min")).intValue();
    int max = ((Double) conversationData.get("max")).intValue();

    List<String> arguments =
        Arrays.asList(String.valueOf(min), String.valueOf(max));

    List<String> suggestions = getNumberSuggestions(request);
    ImageCard imageCard = ImageCards.getIntroImageCard(locale);

    List<PartialPrompt> partialPrompts = Arrays.asList(
        new PartialPrompt(
            new TextElement(greetingVariants),
            new TextElement(invocation, arguments)),
        new PartialPrompt(new TextElement(invocationGuess)));
    return new Prompt(partialPrompts, suggestions, imageCard);
  }

  public static Prompt getSameGuessPrompt(ActionRequest request) {
    checkNotNull(request, "request cannot be null.");
    Locale locale = request.getLocale();
    ResourceBundle rb = ResourceBundle.getBundle("prompts", locale);

    Double guess = (Double) request.getParameter("guess");
    List<String> arguments =
        Collections.singletonList(String.valueOf(guess.intValue()));

    List<String> sameGuess =
        Collections.singletonList(rb.getString("same_guess_3"));

    List<String> suggestions = getNumberSuggestions(request);

    List<PartialPrompt> partialPrompts = Collections.singletonList(
        new PartialPrompt(new TextElement(sameGuess, arguments)));
    return new Prompt(partialPrompts, suggestions);
  }

  public static Prompt getSameGuessEndPrompt(ActionRequest request) {
    checkNotNull(request, "request cannot be null.");
    Locale locale = request.getLocale();
    ResourceBundle rb = ResourceBundle.getBundle("prompts", locale);

    Double guess = (Double) request.getParameter("guess");
    List<String> arguments =
        Collections.singletonList(String.valueOf(guess.intValue()));

    List<String> sameGuess =
        Collections.singletonList(rb.getString("same_guess_2"));

    List<PartialPrompt> partialPrompts = Collections.singletonList(
        new PartialPrompt(new TextElement(sameGuess, arguments)));
    return new Prompt(partialPrompts);
  }

  public static Prompt getSameGuessHintPrompt(ActionRequest request) {
    checkNotNull(request, "request cannot be null.");
    Locale locale = request.getLocale();
    ResourceBundle rb = ResourceBundle.getBundle("prompts", locale);

    List<String> sameGuess =
        Collections.singletonList(rb.getString("same_guess_1"));

    Map<String, Object> conversationData = request.getConversationData();
    String hint = (String) conversationData.get("hint");
    Double guess = (Double) request.getParameter("guess");
    List<String> arguments =
        Arrays.asList(String.valueOf(guess.intValue()), hint);

    List<String> suggestions = getNumberSuggestions(request);

    List<PartialPrompt> partialPrompts = Collections.singletonList(
        new PartialPrompt(new TextElement(sameGuess, arguments)));
    return new Prompt(partialPrompts, suggestions);
  }

  public static Prompt getStillHigherPrompt(ActionRequest request) {
    checkNotNull(request, "request cannot be null.");
    return getStillWrongPrompt(request, "wrong_higher_1", "wrong_higher_2");
  }

  public static Prompt getStillLowerPrompt(ActionRequest request) {
    checkNotNull(request, "request cannot be null.");
    return getStillWrongPrompt(request, "wrong_lower_1", "wrong_lower_2");
  }

  private static Prompt getStillWrongPrompt(ActionRequest request,
      String... textVariants) {
    Locale locale = request.getLocale();
    ResourceBundle rb = ResourceBundle.getBundle("prompts", locale);
    List<String> variantList = new ArrayList<>();
    for (String textVariant : textVariants) {
      variantList.add(rb.getString(textVariant));
    }

    Map<String, Object> conversationData = request.getConversationData();
    Double previousGuess = (Double) conversationData.get("previousGuess");
    List<String> arguments =
        Collections.singletonList(String.valueOf(previousGuess.intValue()));

    List<String> suggestions = getNumberSuggestions(request);

    ImageCard imageCard = ImageCards.getCoolImageCard(locale);
    List<PartialPrompt> partialPrompts = Collections.singletonList(
        new PartialPrompt(new TextElement(variantList, arguments)));
    return new Prompt(partialPrompts, suggestions, imageCard);
  }

  public static Prompt getNoInputPrompt(ActionRequest request) {
    checkNotNull(request, "request cannot be null.");
    Locale locale = request.getLocale();
    ResourceBundle rb = ResourceBundle.getBundle("prompts", locale);
    Argument argument = request.getArgument("REPROMPT_COUNT");
    int repromptCount = argument.getIntValue().intValue();
    String textVariant =
        rb.getString(String.format("no_input_%d", repromptCount + 1));

    List<PartialPrompt> partialPrompts = Collections.singletonList(
        new PartialPrompt(
            new TextElement(textVariant)));
    return new Prompt(partialPrompts);
  }

  public static Prompt getMinPrompt(ActionRequest request) {
    checkNotNull(request, "request cannot be null.");
    return getBoundaryPrompt(request, "min");
  }

  public static Prompt getMaxPrompt(ActionRequest request) {
    checkNotNull(request, "request cannot be null.");
    return getBoundaryPrompt(request, "max");
  }

  private static Prompt getBoundaryPrompt(ActionRequest request,
      String boundary) {
    Locale locale = request.getLocale();
    ResourceBundle rb = ResourceBundle.getBundle("prompts", locale);

    List<String> variantList = Collections.singletonList(
        rb.getString(String.format("%s_follow", boundary)));

    Map<String, Object> conversationData = request.getConversationData();
    Double bounds = (Double) conversationData.get(boundary);
    List<String> arguments =
        Collections.singletonList(String.valueOf(bounds.intValue()));

    List<String> suggestions = getNumberSuggestions(request);

    List<PartialPrompt> partialPrompts = Arrays.asList(
        new PartialPrompt(new TextElement(rb.getString(boundary))),
        new PartialPrompt(new TextElement(variantList, arguments)));
    return new Prompt(partialPrompts, suggestions);
  }

  private static Prompt getColdPrompt(ActionRequest request,
      String... textVariants) {
    Locale locale = request.getLocale();
    ResourceBundle rb = ResourceBundle.getBundle("prompts", locale);

    List<String> variantList = new ArrayList<>();
    for (String textVariant : textVariants) {
      variantList.add(rb.getString(textVariant));
    }

    Double guess = (Double) request.getParameter("guess");
    List<String> arguments =
        Collections.singletonList(String.valueOf(guess.intValue()));

    List<String> suggestions = getNumberSuggestions(request);

    ImageCard imageCard = ImageCards.getColdImageCard(locale);

    List<PartialPrompt> partialPrompts = Collections.singletonList(
        new PartialPrompt(new TextElement(variantList, arguments)));
    return new Prompt(partialPrompts, suggestions, imageCard);
  }

  public static Prompt getColdLowerPrompt(ActionRequest request) {
    checkNotNull(request, "request cannot be null.");
    return getColdPrompt(request, "cold_low_1", "cold_low_2");
  }

  public static Prompt getColdHigherPrompt(ActionRequest request) {
    checkNotNull(request, "request cannot be null.");
    return getColdPrompt(request, "cold_high_1", "cold_high_2");
  }

  public static Prompt getHotPrompt(ActionRequest request) {
    checkNotNull(request, "request cannot be null.");
    Locale locale = request.getLocale();
    ResourceBundle rb = ResourceBundle.getBundle("prompts", locale);

    List<String> suggestions = getNumberSuggestions(request);

    ImageCard imageCard = ImageCards.getHotImageCard(locale);

    List<PartialPrompt> partialPrompts = Collections.singletonList(
        new PartialPrompt(new TextElement(rb.getString("close"))));
    return new Prompt(partialPrompts, suggestions, imageCard);
  }

  private static Prompt getVeryHotPrompt(ActionRequest request,
      boolean playSound, String... textVariants) {
    Locale locale = request.getLocale();
    ResourceBundle rb = ResourceBundle.getBundle("prompts", locale);

    List<String> variantList = new ArrayList<>();
    for (String textVariant : textVariants) {
      variantList.add(rb.getString(textVariant));
    }

    List<String> suggestions = getNumberSuggestions(request);

    ImageCard imageCard = ImageCards.getHotImageCard(locale);

    List<Element> elements = new ArrayList<>();
    if (playSound) {
      elements.add(AudioElements.getSteamOnlyAudioElement());
    }
    elements.add(new TextElement(variantList.toArray(new String[0])));
    List<PartialPrompt> partialPrompts = Collections.singletonList(
        new PartialPrompt(elements));
    return new Prompt(partialPrompts, suggestions, imageCard);
  }

  public static Prompt getVeryHotHigherPrompt(ActionRequest request,
      boolean playSound) {
    checkNotNull(request, "request cannot be null.");
    return getVeryHotPrompt(request, playSound, "highest_1", "highest_2",
        "highest_3");
  }

  public static Prompt getVeryHotLowerPrompt(ActionRequest request,
      boolean playSound) {
    checkNotNull(request, "request cannot be null.");
    return getVeryHotPrompt(request, playSound, "lowest_1", "lowest_2",
        "lowest_3");
  }

  private static Prompt getWarmPrompt(ActionRequest request,
      String... textVariants) {
    Locale locale = request.getLocale();
    ResourceBundle rb = ResourceBundle.getBundle("prompts", locale);

    List<String> variantList = new ArrayList<>();
    for (String textVariant : textVariants) {
      variantList.add(rb.getString(textVariant));
    }

    String[] anotherVariants = new String[]{
        rb.getString("another_1"),
        rb.getString("another_2"),
        rb.getString("another_3")
    };

    Double guess = (Double) request.getParameter("guess");
    List<String> arguments =
        Collections.singletonList(String.valueOf(guess.intValue()));

    List<String> suggestions = getNumberSuggestions(request);

    ImageCard imageCard = ImageCards.getWarmImageCard(locale);

    List<PartialPrompt> partialPrompts = Arrays.asList(
        new PartialPrompt(new TextElement(variantList, arguments)),
        new PartialPrompt(new TextElement(anotherVariants)));

    return new Prompt(partialPrompts, suggestions, imageCard);
  }

  public static Prompt getWarmHigherPrompt(ActionRequest request) {
    checkNotNull(request, "request cannot be null.");
    return getWarmPrompt(request, "higher_1", "higher_2", "higher_3");
  }

  public static Prompt getWarmLowerPrompt(ActionRequest request) {
    checkNotNull(request, "request cannot be null.");
    return getWarmPrompt(request, "lower_1", "lower_2", "lower_3");
  }

  public static Prompt getHotHigherPrompt(ActionRequest request,
      boolean playSound) {
    checkNotNull(request, "request cannot be null.");
    Locale locale = request.getLocale();
    ResourceBundle rb = ResourceBundle.getBundle("prompts", locale);

    String[] textVariants = new String[]{
        rb.getString("hot_high_1"),
        rb.getString("hot_high_2"),
        rb.getString("hot_high_3"),
        rb.getString("hot_high_4")
    };

    List<String> suggestions = getNumberSuggestions(request);

    List<Element> elements = new ArrayList<>();
    if (playSound) {
      elements.add(AudioElements.getSteamAudioElement());
    }
    elements.add(new TextElement(textVariants));

    ImageCard imageCard = ImageCards.getHotImageCard(locale);

    List<PartialPrompt> partialPrompts = Collections.singletonList(
        new PartialPrompt(elements));

    return new Prompt(partialPrompts, suggestions, imageCard);
  }

  public static Prompt getHotLowerPrompt(ActionRequest request,
      boolean playSound) {
    checkNotNull(request, "request cannot be null.");
    Locale locale = request.getLocale();
    ResourceBundle rb = ResourceBundle.getBundle("prompts", locale);

    String[] textVariants = new String[]{
        rb.getString("hot_low_1"),
        rb.getString("hot_low_2"),
        rb.getString("hot_low_3"),
        rb.getString("hot_low_4")
    };

    List<String> suggestions = getNumberSuggestions(request);

    List<Element> elements = new ArrayList<>();
    if (playSound) {
      elements.add(AudioElements.getSteamAudioElement());
    }
    elements.add(new TextElement(textVariants));

    ImageCard imageCard = ImageCards.getHotImageCard(locale);

    List<PartialPrompt> partialPrompts = Collections.singletonList(
        new PartialPrompt(elements));

    return new Prompt(partialPrompts, suggestions, imageCard);
  }

  public static Prompt getHigherPrompt(ActionRequest request) {
    checkNotNull(request, "request cannot be null.");
    Locale locale = request.getLocale();
    ResourceBundle rb = ResourceBundle.getBundle("prompts", locale);

    List<String> highVariants =
        Collections.singletonList(rb.getString("high"));
    String[] anotherVariants = new String[]{
        rb.getString("another_1"),
        rb.getString("another_2"),
        rb.getString("another_3")
    };

    Double guess = (Double) request.getParameter("guess");
    List<String> arguments =
        Collections.singletonList(String.valueOf(guess.intValue()));

    List<String> suggestions = getNumberSuggestions(request);

    List<PartialPrompt> partialPrompts = Arrays.asList(
        new PartialPrompt(new TextElement(highVariants, arguments)),
        new PartialPrompt(new TextElement(anotherVariants)));

    return new Prompt(partialPrompts, suggestions);
  }

  public static Prompt getLowerPrompt(ActionRequest request) {
    checkNotNull(request, "request cannot be null.");
    Locale locale = request.getLocale();
    ResourceBundle rb = ResourceBundle.getBundle("prompts", locale);

    List<String> lowVariants =
        Collections.singletonList(rb.getString("low"));
    String[] anotherVariants = new String[]{
        rb.getString("another_1"),
        rb.getString("another_2"),
        rb.getString("another_3")
    };

    Double guess = (Double) request.getParameter("guess");
    List<String> arguments =
        Collections.singletonList(String.valueOf(guess.intValue()));

    List<String> suggestions = getNumberSuggestions(request);

    List<PartialPrompt> partialPrompts = Arrays.asList(
        new PartialPrompt(new TextElement(lowVariants, arguments)),
        new PartialPrompt(new TextElement(anotherVariants)));

    return new Prompt(partialPrompts, suggestions);
  }

  public static Prompt getWinManyTriesPrompt(ActionRequest request) {
    checkNotNull(request, "request cannot be null.");
    Locale locale = request.getLocale();
    ResourceBundle rb = ResourceBundle.getBundle("prompts", locale);

    List<String> manyTriesVariants = Arrays.asList(
        rb.getString("many_tries_1"),
        rb.getString("many_tries_2")
    );

    Map<String, Object> conversationData = request.getConversationData();
    Double answer = (Double) conversationData.get("answer");
    List<String> arguments =
        Collections.singletonList(String.valueOf(answer.intValue()));

    List<String> suggestions = getConfirmSuggestion(request);

    List<Element> elements = new ArrayList<>();
    elements.add(AudioElements.getWinAudioElement());
    elements.add(new TextElement(manyTriesVariants, arguments));

    ImageCard imageCard = ImageCards.getWinImageCard(locale);

    List<PartialPrompt> partialPrompts = Arrays.asList(
        new PartialPrompt(elements),
        new PartialPrompt(new TextElement(rb.getString("many_tries_again"))));

    return new Prompt(partialPrompts, suggestions, imageCard);
  }

  public static Prompt getWinPrompt(ActionRequest request) {
    checkNotNull(request, "request cannot be null.");
    Locale locale = request.getLocale();
    ResourceBundle rb = ResourceBundle.getBundle("prompts", locale);

    List<String> correctVariants = Arrays.asList(
        rb.getString("correct_1"),
        rb.getString("correct_2"),
        rb.getString("correct_3")
    );

    String[] againVariants = new String[]{
        rb.getString("again_1"),
        rb.getString("again_2"),
        rb.getString("again_3")
    };

    Map<String, Object> conversationData = request.getConversationData();
    Double answer = (Double) conversationData.get("answer");
    List<String> arguments =
        Collections.singletonList(String.valueOf(answer.intValue()));

    List<String> suggestions = getConfirmSuggestion(request);

    List<Element> elements = new ArrayList<>();
    elements.add(AudioElements.getWinAudioElement());
    elements.add(new TextElement(correctVariants, arguments));

    ImageCard imageCard = ImageCards.getWinImageCard(locale);

    List<PartialPrompt> partialPrompts = Arrays.asList(
        new PartialPrompt(elements),
        new PartialPrompt(new TextElement(againVariants)));

    return new Prompt(partialPrompts, suggestions, imageCard);
  }

  public static Prompt getQuitGamePrompt(ActionRequest request) {
    checkNotNull(request, "request cannot be null.");
    Locale locale = request.getLocale();
    ResourceBundle rb = ResourceBundle.getBundle("prompts", locale);

    List<String> revealVariants = Arrays.asList(
        rb.getString("reveal_1"),
        rb.getString("reveal_2")
    );

    String[] byeVariants = new String[]{
        rb.getString("reveal_bye_1"),
        rb.getString("reveal_bye_2")
    };

    Map<String, Object> conversationData = request.getConversationData();
    Double answer = (Double) conversationData.get("answer");
    List<String> arguments =
        Collections.singletonList(String.valueOf(answer.intValue()));

    List<PartialPrompt> partialPrompts = Arrays.asList(
        new PartialPrompt(new TextElement(revealVariants, arguments)),
        new PartialPrompt(new TextElement(byeVariants)));

    return new Prompt(partialPrompts);
  }

  public static Prompt getPlayAgainPrompt(ActionRequest request) {
    checkNotNull(request, "request cannot be null.");
    Locale locale = request.getLocale();
    ResourceBundle rb = ResourceBundle.getBundle("prompts", locale);

    String[] reVariants = new String[]{
        rb.getString("re_1"),
        rb.getString("re_2"),
        rb.getString("re_3"),
        rb.getString("re_4"),
        rb.getString("re_5"),
        rb.getString("re_6")
    };

    List<String> reinvocationVariants =
        Collections.singletonList(rb.getString("reinvocation"));

    Map<String, Object> conversationData = request.getConversationData();
    int min = ((Double) conversationData.get("min")).intValue();
    int max = ((Double) conversationData.get("max")).intValue();
    List<String> arguments = Arrays.asList(
        String.valueOf(min), String.valueOf(max));

    List<String> suggestions = getNumberAndDoneSuggestions(request);

    List<PartialPrompt> partialPrompts = Arrays.asList(
        new PartialPrompt(new TextElement(reVariants),
            new TextElement(reinvocationVariants, arguments)),
        new PartialPrompt(new TextElement(rb.getString("reinvocation_guess"))));

    return new Prompt(partialPrompts, suggestions);
  }

  public static Prompt getExitPrompt(ActionRequest request) {
    checkNotNull(request, "request cannot be null.");
    Locale locale = request.getLocale();
    ResourceBundle rb = ResourceBundle.getBundle("prompts", locale);

    String[] quitVariants = new String[]{
        rb.getString("quit_1"),
        rb.getString("quit_2"),
        rb.getString("quit_3"),
        rb.getString("quit_4")
    };

    List<PartialPrompt> partialPrompts = Collections.singletonList(
        new PartialPrompt(new TextElement(quitVariants)));

    return new Prompt(partialPrompts);
  }

  public static Prompt getConfirmationFallbackPrompt(ActionRequest request) {
    checkNotNull(request, "request cannot be null.");
    Locale locale = request.getLocale();
    ResourceBundle rb = ResourceBundle.getBundle("prompts", locale);

    List<String> suggestions = getConfirmSuggestion(request);

    List<PartialPrompt> partialPrompts = Collections.singletonList(
        new PartialPrompt(new TextElement(rb.getString("fallback_1"))));

    return new Prompt(partialPrompts, suggestions);
  }

  public static Prompt getFallbackPrompt(ActionRequest request) {
    checkNotNull(request, "request cannot be null.");
    Locale locale = request.getLocale();
    ResourceBundle rb = ResourceBundle.getBundle("prompts", locale);

    List<PartialPrompt> partialPrompts = Arrays.asList(
        new PartialPrompt(new TextElement(rb.getString("fallback_2"))));

    return new Prompt(partialPrompts);
  }

  public static Prompt getDeeplinkHigherPrompt(ActionRequest request) {
    checkNotNull(request, "request cannot be null.");
    Locale locale = request.getLocale();
    ResourceBundle rb = ResourceBundle.getBundle("prompts", locale);

    String[] greetingVariants = new String[]{
        rb.getString("greeting_1"),
        rb.getString("greeting_2"),
        rb.getString("greeting_3")
    };

    String[] deeplinkVariants = new String[]{
        rb.getString("deeplink_1"),
        rb.getString("deeplink_2")
    };

    List<String> suggestions = getNumberSuggestions(request);

    List<PartialPrompt> partialPrompts = Arrays.asList(
        new PartialPrompt(new TextElement(greetingVariants)),
        new PartialPrompt(new TextElement(deeplinkVariants)));

    return new Prompt(partialPrompts, suggestions);
  }

  public static Prompt getDeeplinkLowerPrompt(ActionRequest request) {
    checkNotNull(request, "request cannot be null.");
    Locale locale = request.getLocale();
    ResourceBundle rb = ResourceBundle.getBundle("prompts", locale);

    String[] greetingVariants = new String[]{
        rb.getString("greeting_1"),
        rb.getString("greeting_2"),
        rb.getString("greeting_3")
    };

    String[] deeplinkVariants = new String[]{
        rb.getString("deeplink_3"),
        rb.getString("deeplink_4")
    };

    List<String> suggestions = getNumberSuggestions(request);
    List<PartialPrompt> partialPrompts = Arrays.asList(
        new PartialPrompt(new TextElement(greetingVariants)),
        new PartialPrompt(new TextElement(deeplinkVariants)));

    return new Prompt(partialPrompts, suggestions);
  }

  public static Prompt getDeeplinkWinPrompt(ActionRequest request) {
    checkNotNull(request, "request cannot be null.");
    Locale locale = request.getLocale();
    ResourceBundle rb = ResourceBundle.getBundle("prompts", locale);

    String[] deeplinkVariants = new String[]{
        rb.getString("deeplink_5"),
        rb.getString("deeplink_6")
    };

    String[] againVariants = new String[]{
        rb.getString("again_1"),
        rb.getString("again_2"),
        rb.getString("again_3")
    };

    List<String> suggestions = getConfirmSuggestion(request);

    List<Element> elements = new ArrayList<>();
    elements.add(AudioElements.getWinAudioElement());
    elements.add(new TextElement(deeplinkVariants));

    List<PartialPrompt> partialPrompts = Arrays.asList(
        new PartialPrompt(elements),
        new PartialPrompt(new TextElement(againVariants)));

    return new Prompt(partialPrompts, suggestions);
  }

  public static Prompt getDeeplinkOutOfBoundsPrompt(ActionRequest request) {
    checkNotNull(request, "request cannot be null.");
    Locale locale = request.getLocale();
    ResourceBundle rb = ResourceBundle.getBundle("prompts", locale);

    List<String> invocation =
        Collections.singletonList(rb.getString("invocation"));
    String invocationGuess = rb.getString("invocation_guess");

    Map<String, Object> conversationData = request.getConversationData();
    int min = ((Double) conversationData.get("min")).intValue();
    int max = ((Double) conversationData.get("max")).intValue();

    List<String> arguments =
        Arrays.asList(String.valueOf(min), String.valueOf(max));

    List<String> suggestions = getNumberAndDoneSuggestions(request);

    ImageCard imageCard = ImageCards.getIntroImageCard(locale);

    List<PartialPrompt> partialPrompts = Arrays.asList(
        new PartialPrompt(
            new TextElement(rb.getString("out_of_bounds")),
            new TextElement(invocation, arguments)),
        new PartialPrompt(new TextElement(invocationGuess)));

    return new Prompt(partialPrompts, suggestions, imageCard);
  }

  public static Prompt getPlayAnotherPrompt(ActionRequest request) {
    checkNotNull(request, "request cannot be null.");
    Locale locale = request.getLocale();
    ResourceBundle rb = ResourceBundle.getBundle("prompts", locale);

    String[] reVariants = new String[]{
        rb.getString("re_1"),
        rb.getString("re_2"),
        rb.getString("re_3"),
        rb.getString("re_4"),
        rb.getString("re_5"),
        rb.getString("re_6")
    };

    String[] anotherVariants = new String[]{
        rb.getString("another_1"),
        rb.getString("another_2"),
        rb.getString("another_3")
    };

    List<String> suggestions = getNumberAndDoneSuggestions(request);

    List<PartialPrompt> partialPrompts = Arrays.asList(
        new PartialPrompt(new TextElement(reVariants)),
        new PartialPrompt(new TextElement(anotherVariants)));

    return new Prompt(partialPrompts, suggestions);
  }

  public static Prompt getAnotherPrompt(ActionRequest request) {
    checkNotNull(request, "request cannot be null.");
    Locale locale = request.getLocale();
    ResourceBundle rb = ResourceBundle.getBundle("prompts", locale);

    String[] anotherVariants = new String[]{
        rb.getString("another_1"),
        rb.getString("another_2"),
        rb.getString("another_3")
    };

    List<String> suggestions = getNumberAndDoneSuggestions(request);

    List<PartialPrompt> partialPrompts = Arrays.asList(
        new PartialPrompt(new TextElement(anotherVariants)));

    return new Prompt(partialPrompts, suggestions);
  }

  public static Prompt getLastPrompt(ActionRequest request) {
    checkNotNull(request, "request cannot be null.");
    Map<String, Object> conversationData = request.getConversationData();
    return (Prompt) conversationData.get("lastResponse");
  }

  private static List<String> getNumberAndDoneSuggestions(
      ActionRequest request) {
    List<String> suggestions = getNumberSuggestions(request);
    suggestions.add(getDoneSuggestion(request));
    return suggestions;
  }

  private static List<String> getNumberSuggestions(ActionRequest request) {
    Map<String, Object> conversationData = request.getConversationData();

    int min = ((Double) conversationData.get("min")).intValue();
    int max = ((Double) conversationData.get("max")).intValue();

    Double previousGuess = (Double) conversationData.get("previousGuess");
    String hint = (String) conversationData.get("hint");
    if (!(hint == null || hint.equals("lower"))) {
      min = previousGuess.intValue() + 1;
    }
    if (!(hint == null || hint.equals("higher"))) {
      max = previousGuess.intValue() - 1;
    }

    String[] all = IntStream
        .rangeClosed(min, max)
        .boxed()
        .map(String::valueOf)
        .toArray(String[]::new);

    for (int i = all.length - 1; i > 0; i--) {
      int j = new Random().nextInt(i + 1);
      String temp = all[i];
      all[i] = all[j];
      all[j] = temp;
    }
    Locale locale = request.getLocale();
    ResourceBundle config =
        ResourceBundle.getBundle("config", locale);
    int numSuggestions =
        Integer.parseInt((String) config.getObject("suggestions"));
    List<String> suggestions =
        Arrays.asList(Arrays.copyOfRange(all, 0, numSuggestions));
    return new ArrayList<>(suggestions);
  }

  private static List<String> getConfirmSuggestion(ActionRequest request) {
    Locale locale = request.getLocale();
    ResourceBundle rb = ResourceBundle.getBundle("prompts", locale);
    return Arrays
        .asList(rb.getString("confirm_yes"), rb.getString("confirm_no"));
  }

  private static String getDoneSuggestion(ActionRequest request) {
    Locale locale = request.getLocale();
    ResourceBundle rb = ResourceBundle.getBundle("prompts", locale);
    return rb.getString("done");
  }
}

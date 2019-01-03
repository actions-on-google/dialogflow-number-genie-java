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

package com.example;

import static com.google.common.base.Preconditions.checkNotNull;

import com.example.prompts.Element;
import com.example.prompts.ImageCard;
import com.example.prompts.PartialPrompt;
import com.example.prompts.Prompt;
import com.example.prompts.Prompts;
import com.example.util.Utils;
import com.google.actions.api.ActionContext;
import com.google.actions.api.ActionRequest;
import com.google.actions.api.ActionResponse;
import com.google.actions.api.DialogflowApp;
import com.google.actions.api.ForIntent;
import com.google.actions.api.response.ResponseBuilder;
import com.google.api.services.actions_fulfillment.v2.model.BasicCard;
import com.google.api.services.actions_fulfillment.v2.model.Image;
import com.google.api.services.actions_fulfillment.v2.model.SimpleResponse;
import com.google.api.services.actions_fulfillment.v2.model.Suggestion;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class NumberGenieApp extends DialogflowApp {

  private static final String GAME_CONTEXT = "game";
  private static final String YES_NO_CONTEXT = "yes_no";
  private static final String DONE_YES_NO_CONTEXT = "done_yes_no";

  private static final Logger LOGGER =
      Logger.getLogger(NumberGenieApp.class.getName());

  private static final double MIN;
  private static final double MAX;

  static {
    ResourceBundle config = ResourceBundle.getBundle("config");
    MIN = Double.parseDouble(config.getString("min"));
    MAX = Double.parseDouble(config.getString("max"));
  }

  @ForIntent("start_game")
  public ActionResponse startGame(ActionRequest request) {
    LOGGER.info("'start_game' intent handler");
    Map<String, Object> conversationData = request.getConversationData();
    conversationData.put("answer", (double) Utils.getRandomNumber((int) MIN, (int) MAX));
    conversationData.put("guessCount", 0.0);
    conversationData.put("fallbackCount", 0.0);
    conversationData.put("steamSoundCount", 0.0);
    conversationData.put("min", MIN);
    conversationData.put("max", MAX);
    Prompt prompt = Prompts.getStartGamePrompt(request);
    return createPromptResponse(request, prompt, false);
  }

  @ForIntent("quit_game")
  public ActionResponse quitGame(ActionRequest request) {
    LOGGER.info("'quit_game' intent handler");
    Prompt prompt = Prompts.getQuitGamePrompt(request);
    return createPromptResponse(request, prompt, true);
  }

  @ForIntent("provide_guess")
  public ActionResponse provideGuess(ActionRequest request) {
    LOGGER.info("'provide_guess' intent handler");
    Map<String, Object> conversationData = request.getConversationData();
    Double answer = (Double) conversationData.get("answer");
    Double guess = (Double) request.getParameter("guess");
    Double previousGuess = (Double) conversationData.get("previousGuess");
    Double diff = Math.abs(guess - answer);
    String hint = (String) conversationData.get("hint");
    conversationData
        .put("guessCount", (Double) conversationData.get("guessCount") + 1);
    conversationData.put("fallbackCount", 0.0);
    if (guess.equals(previousGuess)) {
      return guessIsSameAsPrevious(request);
    } else {
      conversationData.put("duplicateCount", 0.0);
    }
    // Check if user isn't following hints
    if (hint != null) {
      if (hint.equals("higher") && guess <= previousGuess) {
        return answerIsStillHigher(request);
      }
      if (hint.equals("lower") && guess >= previousGuess) {
        return answerIsStillLower(request);
      }
    }
    conversationData.put("previousGuess", guess);
    // Handle boundaries with special prompts
    if (!answer.equals(guess)) {
      if (guess == MIN) {
        return guessIsMidBoundary(request);
      }
      if (guess == MAX) {
        return guessIsMaxBoundary(request);
      }
    }
    // Give different responses based on distance from number
    if (diff > 75) {
      return guessIsCold(request);
    }
    if (diff == 4) {
      // Guess is getting closer
      return guessIsHot(request);
    }
    if (diff == 3) {
      // Guess is even closer
      return guessIsVeryHot(request);
    }
    if (diff <= 10 && diff > 4) {
      // Guess is nearby number
      return guessIsWarm(request);
    }
    // Give hints on which direction to go
    if (answer > guess) {
      return guessIsLessThanAnswer(request);
    } else if (answer < guess) {
      return guessIsHigherThanAnswer(request);
    }
    // Guess is same as number
    return guessIsSameAsAnswer(request);
  }

  @ForIntent("play_again_yes")
  public ActionResponse playAgainYes(ActionRequest request) {
    LOGGER.info("'play_again_yes' intent handler");
    Map<String, Object> conversationData = request.getConversationData();
    conversationData.put("answer", (double) Utils.getRandomNumber((int) MIN, (int) MAX));
    conversationData.put("guessCount", 0.0);
    conversationData.put("fallbackCount", 0.0);
    conversationData.put("steamSoundCount", 0.0);
    Prompt prompt = Prompts.getPlayAgainPrompt(request);
    return createPromptResponse(request, prompt, false);
  }

  @ForIntent("play_again_no")
  public ActionResponse playAgainNo(ActionRequest request) {
    LOGGER.info("'play_again_no' intent handler");
    ActionContext context = new ActionContext(GAME_CONTEXT, 1);
    Prompt prompt = Prompts.getExitPrompt(request);
    return createPromptResponse(request, prompt, true, context);
  }

  @ForIntent("Default Fallback Intent")
  public ActionResponse defaultFallback(ActionRequest request) {
    LOGGER.info("'Default Fallback Intent' intent handler");
    return fallback(request);
  }

  @ForIntent("unknown_deeplink")
  public ActionResponse unknownDeeplink(ActionRequest request) {
    LOGGER.info("'unknown_deeplink' intent handler");
    Map<String, Object> conversationData = request.getConversationData();
    double answer = (double) Utils.getRandomNumber((int) MIN, (int) MAX);
    conversationData.put("answer", answer);
    conversationData.put("guessCount", 0.0);
    conversationData.put("fallbackCount", 0.0);
    conversationData.put("steamSoundCount", 0.0);
    conversationData.put("min", MIN);
    conversationData.put("max", MAX);
    Prompt prompt;
    String text = request.getRawText();
    ActionContext gameContext = new ActionContext(GAME_CONTEXT, 1);
    if (text == null) {
      return fallback(request);
    }
    if (text.length() < answer) {
      prompt = Prompts.getDeeplinkHigherPrompt(request);
      return createPromptResponse(request, prompt, false, gameContext);
    } else if (text.length() > answer) {
      prompt = Prompts.getDeeplinkLowerPrompt(request);
      return createPromptResponse(request, prompt, false, gameContext);
    } else {
      conversationData.put("hint", null);
      conversationData.put("previousGuess", -1.0);
      ActionContext yesNoContext = new ActionContext(YES_NO_CONTEXT, 5);
      prompt = Prompts.getDeeplinkWinPrompt(request);
      return createPromptResponse(request, prompt, false, gameContext,
          yesNoContext);
    }
  }

  @ForIntent("deeplink_number")
  public ActionResponse deeplinkNumber(ActionRequest request) {
    LOGGER.info("'deeplink_number' intent handler");
    Map<String, Object> conversationData = request.getConversationData();
    conversationData.put("guessCount", 0.0);
    conversationData.put("fallbackCount", 0.0);
    conversationData.put("steamSoundCount", 0.0);
    conversationData.put("min", MIN);
    conversationData.put("max", MAX);
    Double answer = (Double) request.getParameter("number");
    conversationData.put("answer", answer);
    Prompt prompt;
    if (Utils.isInBounds(answer.intValue(), (int) MIN, (int) MAX)) {
      prompt = Prompts.getStartGamePrompt(request);
    } else {
      conversationData.put("answer", (double) Utils.getRandomNumber((int) MIN, (int) MAX));
      prompt = Prompts.getDeeplinkOutOfBoundsPrompt(request);
    }
    ActionContext context = new ActionContext(GAME_CONTEXT, 1);
    return createPromptResponse(request, prompt, false, context);
  }

  @ForIntent("done_yes")
  public ActionResponse doneYes(ActionRequest request) {
    LOGGER.info("'done_yes' intent handler");
    ActionContext context = new ActionContext(GAME_CONTEXT, 1);
    Prompt prompt = Prompts.getExitPrompt(request);
    return createPromptResponse(request, prompt, true, context);
  }

  @ForIntent("done_no")
  public ActionResponse doneNo(ActionRequest request) {
    LOGGER.info("'done_no' intent handler");
    Map<String, Object> conversationData = request.getConversationData();
    conversationData.put("fallbackCount", 0.0);
    Prompt prompt = Prompts.getPlayAnotherPrompt(request);
    return createPromptResponse(request, prompt, false);
  }

  @ForIntent("repeat")
  public ActionResponse repeat(ActionRequest request) {
    LOGGER.info("'repeat' intent handler");
    Map<String, Object> conversationData = request.getConversationData();
    String key = "lastResponse";
    Prompt prompt;
    if (conversationData.get(key) != null) {
      prompt = Prompts.getLastPrompt(request);
    } else {
      prompt = Prompts.getAnotherPrompt(request);
    }
    return createPromptResponse(request, prompt, false);
  }

  @ForIntent("cancel")
  public ActionResponse cancel(ActionRequest request) {
    LOGGER.info("'cancel' intent handler");
    Prompt prompt = Prompts.getExitPrompt(request);
    return createPromptResponse(request, prompt, true);
  }

  @ForIntent("no_input")
  public ActionResponse noInput(ActionRequest request) {
    LOGGER.info("'no_input' intent handler");
    Prompt prompt = Prompts.getNoInputPrompt(request);
    return createPromptResponse(request, prompt, false);
  }

  private ActionResponse fallback(ActionRequest request) {
    Map<String, Object> conversationData = request.getConversationData();
    String key = "fallbackCount";
    conversationData.putIfAbsent(key, 0.0);
    Double fallbackCount = (Double) conversationData.get(key);
    conversationData.put(key, fallbackCount + 1);
    Prompt prompt;
    if (fallbackCount <= 1) {
      ActionContext context = new ActionContext(DONE_YES_NO_CONTEXT, 5);
      prompt = Prompts.getConfirmationFallbackPrompt(request);
      return createPromptResponse(request, prompt, false, context);
    }
    prompt = Prompts.getFallbackPrompt(request);
    return createPromptResponse(request, prompt, true);
  }

  private ActionResponse createPromptResponse(ActionRequest request,
      Prompt prompt,
      boolean endConversation, ActionContext... contexts) {
    ResponseBuilder responseBuilder = getResponseBuilder(request);
    cachePrompt(request, prompt);
    addPromptToResponse(responseBuilder, prompt);
    if (endConversation) {
      responseBuilder.endConversation();
    }
    if (contexts.length > 0) {
      for (ActionContext context : contexts) {
        responseBuilder.add(context);
      }
    }
    return responseBuilder.build();
  }

  private ActionResponse guessIsSameAsPrevious(ActionRequest request) {
    Map<String, Object> conversationData = request.getConversationData();
    Double duplicateCount = (Double) conversationData.get("duplicateCount") + 1;
    conversationData.put("duplicateCount", duplicateCount);
    String hint = (String) conversationData.get("hint");
    Prompt prompt;
    if (duplicateCount == 1) {
      prompt = Prompts.getSameGuessHintPrompt(request);
      if (hint == null) {
        prompt = Prompts.getSameGuessPrompt(request);
      }
      return createPromptResponse(request, prompt, false);
    } else {
      prompt = Prompts.getSameGuessEndPrompt(request);
      return createPromptResponse(request, prompt, true);
    }
  }

  private ActionResponse answerIsStillHigher(ActionRequest request) {
    Prompt prompt = Prompts.getStillHigherPrompt(request);
    return createPromptResponse(request, prompt, false);
  }

  private ActionResponse answerIsStillLower(ActionRequest request) {
    Prompt prompt = Prompts.getStillLowerPrompt(request);
    return createPromptResponse(request, prompt, false);
  }

  private ActionResponse guessIsMidBoundary(ActionRequest request) {
    Map<String, Object> conversationData = request.getConversationData();
    conversationData.put("hint", "higher");
    Prompt prompt = Prompts.getMinPrompt(request);
    return createPromptResponse(request, prompt, false);
  }

  private ActionResponse guessIsMaxBoundary(ActionRequest request) {
    Map<String, Object> conversationData = request.getConversationData();
    conversationData.put("hint", "lower");
    Prompt prompt = Prompts.getMaxPrompt(request);
    return createPromptResponse(request, prompt, false);
  }

  private ActionResponse guessIsCold(ActionRequest request) {
    Map<String, Object> conversationData = request.getConversationData();
    Double answer = (Double) conversationData.get("answer");
    Double guess = (Double) request.getParameter("guess");
    Prompt prompt;
    if (answer > guess) {
      conversationData.put("hint", "higher");
      prompt = Prompts.getColdHigherPrompt(request);
    } else {
      conversationData.put("hint", "lower");
      prompt = Prompts.getColdLowerPrompt(request);
    }
    return createPromptResponse(request, prompt, false);
  }

  private ActionResponse guessIsHot(ActionRequest request) {
    Map<String, Object> conversationData = request.getConversationData();
    conversationData.put("hint", null);
    Prompt prompt = Prompts.getHotPrompt(request);
    return createPromptResponse(request, prompt, false);
  }

  private ActionResponse guessIsVeryHot(ActionRequest request) {
    Map<String, Object> conversationData = request.getConversationData();
    Double answer = (Double) conversationData.get("answer");
    Double guess = (Double) request.getParameter("guess");
    Double soundCount = (Double) conversationData.get("steamSoundCount");
    Prompt prompt;
    if (answer > guess) {
      conversationData.put("hint", "higher");
      if (soundCount-- <= 0) {
        soundCount = 5.0;
        prompt = Prompts.getVeryHotHigherPrompt(request, true);
      } else {
        prompt = Prompts.getVeryHotHigherPrompt(request, false);
      }
    } else {
      conversationData.put("hint", "lower");
      if (soundCount-- <= 0) {
        soundCount = 5.0;
        prompt = Prompts.getVeryHotLowerPrompt(request, true);
      } else {
        prompt = Prompts.getVeryHotLowerPrompt(request, false);
      }
    }
    conversationData.put("steamSoundCount", soundCount);
    return createPromptResponse(request, prompt, false);
  }

  private ActionResponse guessIsWarm(ActionRequest request) {
    Map<String, Object> conversationData = request.getConversationData();
    Double answer = (Double) conversationData.get("answer");
    Double guess = (Double) request.getParameter("guess");
    Prompt prompt;
    if (answer > guess) {
      conversationData.put("hint", "higher");
      prompt = Prompts.getWarmHigherPrompt(request);
    } else {
      conversationData.put("hint", "lower");
      prompt = Prompts.getWarmLowerPrompt(request);
    }
    return createPromptResponse(request, prompt, false);
  }

  private ActionResponse guessIsLessThanAnswer(ActionRequest request) {
    Map<String, Object> conversationData = request.getConversationData();
    Double answer = (Double) conversationData.get("answer");
    Double guess = (Double) request.getParameter("guess");
    Double diff = Math.abs(guess - answer);
    String previousHint = (String) conversationData.get("hint");
    conversationData.put("hint", "higher");
    Double soundCount = (Double) conversationData.get("steamSoundCount");
    Prompt prompt;
    if (previousHint != null && previousHint.equals("higher") && diff <= 2) {
      // Very close to number
      if (soundCount-- <= 0) {
        soundCount = 5.0;
        prompt = Prompts.getHotHigherPrompt(request, true);
      } else {
        prompt = Prompts.getHotHigherPrompt(request, false);
      }
    } else {
      prompt = Prompts.getHigherPrompt(request);
    }
    conversationData.put("steamSoundCount", soundCount);
    return createPromptResponse(request, prompt, false);
  }

  private ActionResponse guessIsHigherThanAnswer(ActionRequest request) {
    Map<String, Object> conversationData = request.getConversationData();
    Double answer = (Double) conversationData.get("answer");
    Double guess = (Double) request.getParameter("guess");
    Double diff = Math.abs(guess - answer);
    String previousHint = (String) conversationData.get("hint");
    conversationData.put("hint", "lower");
    Double soundCount = (Double) conversationData.get("steamSoundCount");
    Prompt prompt;
    if (previousHint != null && previousHint.equals("lower") && diff <= 2) {
      // Very close to number
      if (soundCount-- <= 0) {
        soundCount = 5.0;
        prompt = Prompts.getHotLowerPrompt(request, true);
      } else {
        prompt = Prompts.getHotLowerPrompt(request, false);
      }
    } else {
      prompt = Prompts.getLowerPrompt(request);
    }
    conversationData.put("steamSoundCount", soundCount);
    return createPromptResponse(request, prompt, false);
  }

  private ActionResponse guessIsSameAsAnswer(ActionRequest request) {
    Map<String, Object> conversationData = request.getConversationData();
    Double guessCount = (Double) conversationData.get("guessCount");
    conversationData.put("hint", null);
    conversationData.put("previousGuess", -1);
    conversationData.put("guessCount", 0);
    Prompt prompt;
    if (guessCount >= 10) {
      prompt = Prompts.getWinManyTriesPrompt(request);
    } else {
      prompt = Prompts.getWinPrompt(request);
    }
    ActionContext context = new ActionContext(YES_NO_CONTEXT, 5);
    return createPromptResponse(request, prompt, false, context);
  }

  private static void cachePrompt(ActionRequest request, Prompt prompt) {
    Map<String, Object> conversationData = request.getConversationData();
    conversationData.put("lastResponse", prompt);
  }

  private static void addPromptToResponse(ResponseBuilder responseBuilder,
      Prompt prompt) {
    checkNotNull(responseBuilder, "responseBuilder cannot be null.");
    checkNotNull(prompt, "prompt cannot be null.");
    StringBuilder displayTextBuilder = new StringBuilder();
    StringBuilder speechTextBuilder = new StringBuilder();
    speechTextBuilder.append("<speech>");
    for (PartialPrompt partialPrompt : prompt.getPartialPrompts()) {
      for (Element element : partialPrompt.getParts()) {
        displayTextBuilder.append(element.getDisplayText());
        displayTextBuilder.append(" ");
        speechTextBuilder.append(element.getSpeechText());
        speechTextBuilder.append(" ");
      }
      displayTextBuilder.append(" ");
      speechTextBuilder.append(" ");
    }
    speechTextBuilder.append("</speech>");
    String displayText = displayTextBuilder.toString();
    String speechText = speechTextBuilder.toString();
    responseBuilder.add(new SimpleResponse()
        .setDisplayText(displayText)
        .setTextToSpeech(speechText));
    ImageCard imageCard = prompt.getImageCard();
    if (imageCard != null) {
      responseBuilder.add(new BasicCard()
          .setImage(new Image()
              .setUrl(imageCard.getUrl())
              .setAccessibilityText(imageCard.getAltText()))
          .setFormattedText(imageCard.getVariantText()));
    }
    for (String suggestion : prompt.getSuggestions()) {
      responseBuilder.add(new Suggestion().setTitle(suggestion));
    }
  }
}

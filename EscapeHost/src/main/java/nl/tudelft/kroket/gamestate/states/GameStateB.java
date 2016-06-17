package nl.tudelft.kroket.gamestate.states;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import nl.tudelft.kroket.gamestate.GameState;
import nl.tudelft.kroket.log.Logger;
import nl.tudelft.kroket.net.protocol.Protocol;
import nl.tudelft.kroket.server.Settings;

/**
 * This is the second game state. In this game state minigame B can be started and ended. The game
 * can progress to state C.
 */
public class GameStateB extends GameState {

  /** Singleton reference to logger. */
  static final Logger log = Logger.getInstance();

  /** Class simpleName, used as tag for logging. */
  private final String className = this.getClass().getSimpleName();

  /** Identifier of the state, used for messages. */
  private static final String STATE_NAME = "B";

  /** Counter for how many players have finished the minigame. */
  private int finishedCounter = 0;

  private boolean inputValid = false;

  private static GameState instance = new GameStateB();

  /** The sequence that is send to the VR and mobile clients. */
  public ArrayList<String> sequencesTotal = new ArrayList<String>();

  /** The sequence that is entered by the VR player. */
  public ArrayList<String> buttonSequence = new ArrayList<String>();

  /**
   * Returns the instance of this game state.
   * 
   * @return the instance.
   */
  public static GameState getInstance() {
    return instance;
  }

  /**
   * Creates a new State B.
   */
  public void newState() {
    instance = new GameStateB();
  }

  /**
   * Starts the minigame belonging to this state.
   */
  @Override
  public void start() {
    setActive(true);

    finishedCounter = 0;
    inputValid = false;
    buttonSequence.clear();

    buttonSequence = generateButtonSequence();

    String message = String.format("%s[%s]%s", Protocol.COMMAND_BEGIN, getName(),
        sequenceToString(buttonSequence));
    host.sendAll(message);

    log.info(className, message);
  }

  /**
   * Acts accordingly upon the inputs received from the clients.
   * 
   * @param input
   *          the input string received from the client
   * @param parsedInput
   *          the parsed input
   */
  @Override
  public void handleInput(String input, HashMap<String, String> parsedInput) {
    log.info(className, "handleInput in state " + getName());

    if (parsedInput.get("command").equals(Protocol.COMMAND_BONUSTIME)) {
      session.bonusTime();
    } else if (parsedInput.get("command").equals(Protocol.COMMAND_GAMEOVER)) {
      session.gameOver();
    }

    if (!parsedInput.containsKey("param_0") || !parsedInput.get("param_0").equals(getName())) {
      return;
    }

    if (!isActive()) {

      if (parsedInput.get("command").equals(Protocol.COMMAND_BEGIN)) {
        start();
      }

    } else if (parsedInput.get("command").equals(Protocol.COMMAND_VERIFY)) {
      finishedCounter++;
      log.info(className, "Mobile players completed: " + finishedCounter);

    } else if (parsedInput.get("command").equals(Protocol.COMMAND_RESTART)) {
      log.info(className, "RESTARTING State " + getName());
      start();

    } else if (parsedInput.get("command").equals(Protocol.COMMAND_DONE)) {
      inputValid = true;
      log.info(className, "VR-client has completed this stage.");
    }

    if (gameComplete()) {

      log.info(className, "Game complete!");
      stop();
    }

  }

  /**
   * Checks whether the game is completed by every player involved.
   * 
   * @return
   */
  private boolean gameComplete() {

    if (finishedCounter >= Settings.REQUIRED_MOBILE) {
      if (inputValid) {
        return true;
      } else {
        log.info(className, "RESTARTING State " + getName());
        start();
      }
    } else {
      return false;
    }

    return false;
  }

  /**
   * generate a sequence of four 4 button sequences that together form a 16 button sequence.
   * 
   * @return sequencesTotal
   */
  public ArrayList<String> generateButtonSequence() {
    ArrayList<String> buttonSequence = new ArrayList<String>();

    buttonSequence.add("RED");
    buttonSequence.add("BLUE");
    buttonSequence.add("YELLOW");
    buttonSequence.add("GREEN");

    for (int i = 0; i < 4; i++) {
      Collections.shuffle(buttonSequence);
      sequencesTotal.addAll(buttonSequence);
    }

    return sequencesTotal;
  }

  /**
   * Generates a string in the right format from the arraylist of colors.
   * 
   * @param colors
   *          the arraylist with the colors
   * @return the string with the colors
   */
  public String sequenceToString(ArrayList<String> buttons) {
    StringBuilder res = new StringBuilder();
    for (int i = 0; i < buttons.size(); i++) {
      res.append("[" + buttons.get(i) + "]");
    }
    return res.toString();
  }

  /**
   * Returns the name of the state.
   */
  @Override
  public String getName() {
    return STATE_NAME;
  }

  public int getFinishedCounter() {
    return finishedCounter;
  }

  public void setFinishedCounter(int count) {
    finishedCounter = count;
  }

  public boolean isInputValid() {
    return inputValid;
  }

  public void setInputValid(boolean bool) {
    inputValid = bool;
  }

  public ArrayList<String> getButtonSequence() {
    return buttonSequence;
  }

}

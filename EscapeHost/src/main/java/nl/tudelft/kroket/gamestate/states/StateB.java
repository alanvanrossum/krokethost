package nl.tudelft.kroket.gamestate.states;

import java.util.HashMap;

import nl.tudelft.kroket.gamestate.GameState;
import nl.tudelft.kroket.server.Settings;

/**
 * This is the second game state. In this game state minigame B can be started and ended. The game
 * can progress to state C.
 */
public class StateB extends GameState {

  private static final String STATE_NAME = "B";

  private int finishedCounter = 0;

  private static GameState instance = new StateB();

  public static GameState getInstance() {
    return instance;
  }

  /**
   * Sends the input to the virtual client and changes the game state to C.
   * 
   * @param input
   *          the input string received from the client
   * @param parsedInput
   *          the parsed input
   */
  @Override
  public void handleInput(String input, HashMap<String, String> parsedInput) {

    System.out.println("handleInput in gameState B");

    if (!parsedInput.containsKey("param_0") || !parsedInput.get("param_0").equals(getName())) {
      return;
    }

    if (!active) {
      if (parsedInput.get("command").equals("BEGIN")) {
        host.sendAll(input);
        start();
        finishedCounter = 0;
      }
    } else if (parsedInput.get("command").equals("DONE")) {

      finishedCounter++;

      // Do not finish when not all two mobile players have finished it.
      if (finishedCounter >= Settings.REQUIRED_MOBILE) {

        host.sendAll(input);
        stop();
      }
    }
  }

  @Override
  public String getName() {
    return STATE_NAME;
  }

}

package nl.tudelft.kroket.gamestate.states;

import java.util.HashMap;

import nl.tudelft.kroket.gamestate.GameState;
import nl.tudelft.kroket.server.GameHost;
import nl.tudelft.kroket.server.Settings;

/**
 * This is the second game state. In this game state minigame B can be started and ended. The game
 * can progress to state C.
 */
public class StateB extends GameState {

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
    finishedCounter++;

    // Do not finish when not all two mobile players have finished it.
    if (finishedCounter < Settings.REQUIRED_MOBILE) {
      return;
    }

    // Send the input to the virtual client.
    if (parsedInput.containsKey("param_0")) {
      host.sendAll(input);
    }
  }

}

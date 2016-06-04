package nl.tudelft.kroket.gamestate.states;

import nl.tudelft.kroket.gamestate.GameState;
import nl.tudelft.kroket.server.GameHost;

import java.util.HashMap;

/**
 * This is the first game state. In this game state minigame A can be started and ended. The game
 * can progress to state B.
 */
public class StateA extends GameState {
  
  private static GameState instance = new StateA();
  
  public static GameState getInstance() {
    return instance;
  }


}

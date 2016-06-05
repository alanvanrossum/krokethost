package nl.tudelft.kroket.gamestate.states;

import java.util.HashMap;

import nl.tudelft.kroket.gamestate.GameState;

/**
 * This is the first game state. In this game state minigame A can be started and ended. The game
 * can progress to state B.
 */
public class StateNone extends GameState {

  private static GameState instance = new StateNone();

  public static GameState getInstance() {
    return instance;
  }

  
}

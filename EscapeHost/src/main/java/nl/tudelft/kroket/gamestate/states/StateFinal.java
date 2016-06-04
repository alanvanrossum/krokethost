package nl.tudelft.kroket.gamestate.states;

import nl.tudelft.kroket.gamestate.GameState;

/**
 * This is the final game state. In this state the game is won.
 */
public class StateFinal extends GameState {

  private static GameState instance = new StateFinal();
  
  public static GameState getInstance() {
    return instance;
  }
  
}

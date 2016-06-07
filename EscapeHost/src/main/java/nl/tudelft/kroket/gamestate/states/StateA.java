package nl.tudelft.kroket.gamestate.states;

import java.util.HashMap;

import nl.tudelft.kroket.gamestate.GameState;
import nl.tudelft.kroket.log.Logger;

/**
 * This is the first game state. In this game state minigame A can be started and ended. The game
 * can progress to state B.
 */
public class StateA extends GameState {
  
  /** Singleton reference to logger. */
  static final Logger log = Logger.getInstance();
  
  /** Class simpleName, used as tag for logging. */
  private final String className = this.getClass().getSimpleName();
  
  private static final String STATE_NAME = "A";

  private static GameState instance = new StateA();

  public static GameState getInstance() {
    return instance;
  }

  @Override
  public String getName() {
    return STATE_NAME;
  }

  

}

package nl.tudelft.kroket.gamestate.states;

import nl.tudelft.kroket.gamestate.GameState;
import nl.tudelft.kroket.log.Logger;
import nl.tudelft.kroket.net.protocol.Protocol;

/**
 * This is the final game state. In this state the game is won.
 */
public class StateFinal extends GameState {

  /** Singleton reference to logger. */
  static final Logger log = Logger.getInstance();
  
  /** Class simpleName, used as tag for logging. */
  private final String className = this.getClass().getSimpleName();
  
  private static final String STATE_NAME = "FINAL";

  private static GameState instance = new StateFinal();

  public static GameState getInstance() {
    return instance;
  }
  
   /**
   * Start a state.
   */
  @Override
  public void start() {
    log.info(className, "Starting gameState " + getName());
    setActive(true);
  }

  @Override
  public String getName() {
    return STATE_NAME;
  }

}

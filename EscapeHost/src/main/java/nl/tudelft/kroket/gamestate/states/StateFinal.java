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
  
  /** Identifier of the state, used for messages. */
  private static final String STATE_NAME = "FINAL";

  private static GameState instance = new StateFinal();

  /**
   * Getter for the instance.
   * 
   * @return the instance.
   */
  public static GameState getInstance() {
    return instance;
  }
  
  /**
   * Creates a new State Final.
   */
  public void newState() {
	  instance = new StateFinal();
  }
  
   /**
   * Start a state.
   */
  @Override
  public void start() {
    log.info(className, "Starting gameState " + getName());
    setActive(true);
  }

  /**
   * Return the name of the state. 
   */
  @Override
  public String getName() {
    return STATE_NAME;
  }

}

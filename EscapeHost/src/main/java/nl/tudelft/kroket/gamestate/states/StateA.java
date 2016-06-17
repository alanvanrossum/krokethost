package nl.tudelft.kroket.gamestate.states;

import java.util.HashMap;

import nl.tudelft.kroket.gamestate.GameState;
import nl.tudelft.kroket.log.Logger;
import nl.tudelft.kroket.net.protocol.Protocol;

/**
 * This is the first game state. In this game state minigame A can be started and ended. The game
 * can progress to state B.
 */
public class StateA extends GameState {

  /** Singleton reference to logger. */
  static final Logger log = Logger.getInstance();

  /** Class simpleName, used as tag for logging. */
  private final String className = this.getClass().getSimpleName();

  /** Identifier of the state, used for messages. */
  private static final String STATE_NAME = "A";

  private static GameState instance = new StateA();

  /**
   * Getter for the instance.
   * 
   * @return the instance.
   */
  public static GameState getInstance() {
    return instance;
  }
  
  /**
   * Creates a new State A.
   */
  public void newState() {
	  instance = new StateA();
  }

  /**
   * Get the name of this minigame.
   */
  @Override
  public String getName() {
    return STATE_NAME;
  }

  /**
   * Stop the minigame.
   */
  @Override
  public void stop() {
    log.info(className, "Stopping gameState " + getName());
    host.sendAll(String.format("%s[%s]", Protocol.COMMAND_DONE, getName()));
  }

  /**
   * Advance to the next gamestate.
   */
  private void advance() {
    setActive(false);
    session.advance();
  }

  /**
   * Method that acts upon input received from the server.
   * 
   * @param input
   *          the input received.
   * @param parsedInput
   *          the parsed input.
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
        host.sendAll(input);
        start();
      }
    } else if (parsedInput.get("command").equals(Protocol.COMMAND_DONE)) {
      if (parsedInput.containsKey("param_1")) {
        advance();
        return;
      }

      host.sendAll(input);
      stop();
    
  	} else {
      log.info(className, "Input ignored.");
    }
  }

}

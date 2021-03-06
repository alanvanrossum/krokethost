package nl.tudelft.kroket.gamestate;

import nl.tudelft.kroket.log.Logger;
import nl.tudelft.kroket.net.protocol.Protocol;
import nl.tudelft.kroket.server.GameHost;
import nl.tudelft.kroket.server.GameSession;

import java.util.HashMap;

/**
 * Abstract class for the state of the game.
 * 
 * @author Team Kroket 
 */
public abstract class GameState {

  /** Singleton reference to logger. */
  static final Logger log = Logger.getInstance();

  /** Class simpleName, used as tag for logging. */
  private final String className = this.getClass().getSimpleName();

  protected GameHost host;
  protected GameSession session;

  protected boolean active = false;

  /**
   * Gets the GameHost.
   * @return the GameHost
   */
  public GameHost getHost() {
    return host;
  }

/**
 * Set the host.
 * @param host the host to be set.
 */
  public void setHost(GameHost host) {
    this.host = host;
  }

  /**
   * Set the game session.
   * @param session the gamesession to be set.
   */
  public void setSession(GameSession session) {
    this.session = session;
  }

  /**
   * Start a state.
   */
  public void start() {
    log.info(className, "Starting gameState " + getName());
    setActive(true);
  }

  /**
   * Stop a state.
   */
  public void stop() {
    log.info(className, "Stopping gameState " + getName());
    host.sendAll(String.format("%s[%s]", Protocol.COMMAND_DONE, getName()));
    setActive(false);
    session.advance();
  }

  /**
   * Abstract method that gets the name of the minigame (state).
   * @return the name.
   */
  public abstract String getName();

  /**
   * Checks whether the state is active.
   * @return true iff the state is active.
   */
  public boolean isActive() {
    return active;
  }

  /**
   * Setter for the active boolean.
   * @param active true iff the state is active.
   */
  public void setActive(boolean active) {
    this.active = active;
  }

  /**
   * Method that acts upon input received from the server.
   * @param input the input received.
   * @param parsedInput the parsed input.
   */
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
      host.sendAll(input);
      stop();
    } else {
      log.info(className, "Input ignored.");
    }
  }

  /**
   * Get the session.
   * @return the GameSession
   */
  public GameSession getSession() {
    return session;
  }
  
  public abstract void newState();  

}

package nl.tudelft.kroket.gamestate;

import java.util.HashMap;

import nl.tudelft.kroket.log.Logger;
import nl.tudelft.kroket.net.protocol.Protocol;
import nl.tudelft.kroket.server.GameHost;
import nl.tudelft.kroket.server.GameSession;

public abstract class GameState {

  /** Singleton reference to logger. */
  static final Logger log = Logger.getInstance();

  /** Class simpleName, used as tag for logging. */
  private final String className = this.getClass().getSimpleName();

  protected GameHost host;
  protected GameSession session;

  protected boolean active = false;

  public void setHost(GameHost host) {
    this.host = host;
  }

  public void setSession(GameSession session) {
    this.session = session;
  }

  protected void start() {

    log.info(className, "Starting gameState " + getName());
    setActive(true);
  }

  public void stop() {
    log.info(className, "Stopping gameState " + getName());
    host.sendAll(String.format("%s[%s]", Protocol.COMMAND_DONE, getName()));
    setActive(false);
    session.advance();
  }

  public abstract String getName();

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public void handleInput(String input, HashMap<String, String> parsedInput) {

    log.info(className, "handleInput in state " + getName());

    if (!parsedInput.containsKey("param_0") || !parsedInput.get("param_0").equals(getName())) {
      return;
    }

    if (!isActive()) {
      if (parsedInput.get("command").equals("BEGIN")) {
        host.sendAll(input);
        start();
      }
    } else if (parsedInput.get("command").equals("DONE")) {
      host.sendAll(input);
      stop();
    } else {
      log.info(className, "Input ignored.");
    }
  }

}

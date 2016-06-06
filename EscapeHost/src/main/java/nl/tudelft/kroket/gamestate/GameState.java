package nl.tudelft.kroket.gamestate;

import java.util.HashMap;

import nl.tudelft.kroket.server.GameHost;
import nl.tudelft.kroket.server.GameSession;

public abstract class GameState {

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
    System.out.println("GameState.start()");
    active = true;
  }

  public void stop() {
    active = false;
    session.advance();
  }

  public abstract String getName();;

  public void handleInput(String input, HashMap<String, String> parsedInput) {

    if (!parsedInput.containsKey("param_0") || !parsedInput.get("param_0").equals(getName())) {
      return;
    }

    System.out.println("handleInput in gameState");

    if (!active) {
      if (parsedInput.get("command").equals("BEGIN")) {
        host.sendAll(input);
        start();
      }
    } else if (parsedInput.get("command").equals("DONE")) {
      host.sendAll(input);
      stop();
    } else {
      System.out.println("Input ignored.");
    }
  }

}

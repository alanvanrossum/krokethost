package nl.tudelft.kroket.gamestate;

import java.util.HashMap;

import nl.tudelft.kroket.server.GameHost;

public abstract class GameState {
  
  protected GameHost host;
  
  public void setHost(GameHost host) {
    this.host = host;
  }

  public void start() {

  }

  public void stop() {

  }
  
  public void handleInput(String input, HashMap<String, String> parsedInput) {
    host.sendAll(input);
  }

}

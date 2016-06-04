package nl.tudelft.kroket.gamestate.states;

import java.util.HashMap;

import nl.tudelft.kroket.gamestate.GameState;
import nl.tudelft.kroket.net.protocol.Protocol;
import nl.tudelft.kroket.server.GameHost;

/**
 * This is the fourth game state. In this game state minigame D can be started and ended. The game
 * can progress to state Final.
 */
public class StateD extends GameState {

  private static GameState instance = new StateD();

  public static GameState getInstance() {
    return instance;
  }

  /**
   * Sends the input to the mobile client.
   * 
   * @param input
   *          the input string received from the client
   * @param parsedInput
   *          the parsed input
   */
  @Override
  public void handleInput(String input, HashMap<String, String> parsedInput) {
    // Send the input to the mobile client and virtual client.
    if (parsedInput.containsKey("param_0")) {
      GameHost.sendMobile(input);
      GameHost.sendVirtual(Protocol.COMMAND_INIT_VR + "[startD]");
    }
  }

}

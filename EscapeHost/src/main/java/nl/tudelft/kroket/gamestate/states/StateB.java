package nl.tudelft.kroket.gamestate.states;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import nl.tudelft.kroket.gamestate.GameState;
import nl.tudelft.kroket.net.protocol.Protocol;
import nl.tudelft.kroket.server.Settings;

/**
 * This is the second game state. In this game state minigame B can be started and ended. The game
 * can progress to state C.
 */
public class StateB extends GameState {

  private static final String STATE_NAME = "B";

  private int finishedCounter = 0;

  private static GameState instance = new StateB();

  // The sequence that is send to the vr and mobile clients.
  public ArrayList<String> sequencesTotal = new ArrayList<String>();

  public static GameState getInstance() {
    return instance;
  }

  /**
   * Sends the input to the virtual client and changes the game state to C.
   * 
   * @param input
   *          the input string received from the client
   * @param parsedInput
   *          the parsed input
   */
  @Override
  public void handleInput(String input, HashMap<String, String> parsedInput) {

    System.out.println("handleInput in gameState B");

    if (!parsedInput.containsKey("param_0") || !parsedInput.get("param_0").equals(getName())) {
      return;
    }

    if (!active) {
      if (parsedInput.get("command").equals("BEGIN")) {

        ArrayList<String> buttonSequence = generateButtonSequence();

        String message = String.format("%s[%s]%s", Protocol.COMMAND_BEGIN, getName(),
            sequenceToString(buttonSequence));
        host.sendAll(message);

        start();
        finishedCounter = 0;
      }
    } else if (parsedInput.get("command").equals("VERIFY")) {

      finishedCounter++;

      // Do not finish when not all two mobile players have finished it.
      if (finishedCounter >= Settings.REQUIRED_MOBILE) {

        host.sendAll("DONE[B]");
        stop();
      }
    }
  }

  /**
   * generate a sequence of four 4 button sequences that together form a 16 button sequence.
   * 
   * @return sequencesTotal
   */
  public ArrayList<String> generateButtonSequence() {
    ArrayList<String> buttonSequence = new ArrayList<String>();

    buttonSequence.add("RED");
    buttonSequence.add("BLUE");
    buttonSequence.add("YELLOW");
    buttonSequence.add("GREEN");

    for (int i = 0; i < 4; i++) {
      Collections.shuffle(buttonSequence);
      sequencesTotal.addAll(buttonSequence);
    }

    return sequencesTotal;
  }

  /**
   * Generates a string in the right format from the arraylist of colors.
   * 
   * @param colors
   *          the arraylist with the colors
   * @return the string with the colors
   */
  public String sequenceToString(ArrayList<String> buttons) {
    StringBuilder res = new StringBuilder();
    for (int i = 0; i < buttons.size(); i++) {
      res.append("[" + buttons.get(i) + "]");
    }
    return res.toString();
  }

  @Override
  public String getName() {
    return STATE_NAME;
  }

}

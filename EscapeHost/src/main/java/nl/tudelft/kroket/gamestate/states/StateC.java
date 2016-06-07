package nl.tudelft.kroket.gamestate.states;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import nl.tudelft.kroket.gamestate.GameState;
import nl.tudelft.kroket.log.Logger;
import nl.tudelft.kroket.net.protocol.Protocol;
import nl.tudelft.kroket.server.GameHost;

/**
 * This is the third game state. In this game state minigame C can be started and ended. The game
 * can progress to state D.
 */
public class StateC extends GameState {

  /** Singleton reference to logger. */
  static final Logger log = Logger.getInstance();

  /** Class simpleName, used as tag for logging. */
  private final String className = this.getClass().getSimpleName();

  private Random random = new Random();

  private static final String STATE_NAME = "C";

  private static GameState instance = new StateC();

  public static GameState getInstance() {
    return instance;
  }

  // The length of the color sequence.
  private int sequenceLength = 7;

  @Override
  public void handleInput(String input, HashMap<String, String> parsedInput) {

    log.info(className, "handleInput in state " + getName());

    // make sure we received a message for the correct state
    if (!parsedInput.containsKey("param_0") || !parsedInput.get("param_0").equals(getName())) {
      return;
    }

    if (!isActive()) {
      if (parsedInput.get("command").equals("BEGIN")) {
        // Generate the color sequence that we will send to the players
        ArrayList<String> colorSequence = generateColorSequence();

        String message = String.format("%s[%s]%s", Protocol.COMMAND_BEGIN, getName(),
            sequenceToString(colorSequence));
        host.sendAll(message);
        start();
      }
    } else if (parsedInput.get("command").equals("DONE")) {
      host.sendAll(input);
      stop();
    } else {
     log.info(className, "Input ignored.");
    }
  }

  /**
   * Generates a random color sequence.
   * 
   * @return an arraylist with the colors.
   */
  public ArrayList<String> generateColorSequence() {
    ArrayList<String> colorSequence = new ArrayList<String>();
    int pointer = 0;

    while (colorSequence.size() < sequenceLength) {
      String color = getRandomColor();

      // Make sure we do not have the same color directly after each
      // other.
      while ((pointer != 0) && color.equals(colorSequence.get(pointer - 1))) {
        color = getRandomColor();
      }
      colorSequence.add(color);
      pointer++;
    }
    return colorSequence;
  }

  /**
   * Gets a random color from the colors blue, red, green and yellow.
   * 
   * @return the random color.
   */
  public String getRandomColor() {

    int rand = random.nextInt(4);
    String color = "";

    if (rand == 0) {
      color = "BLUE";
    } else if (rand == 1) {
      color = "RED";
    } else if (rand == 2) {
      color = "GREEN";
    } else if (rand == 3) {
      color = "YELLOW";
    }
    return color;
  }

  /**
   * Generates a string in the right format from the arraylist of colors.
   * 
   * @param colors
   *          the arraylist with the colors
   * @return the string with the colors
   */
  public String sequenceToString(ArrayList<String> colors) {
    StringBuilder res = new StringBuilder();
    for (int i = 0; i < colors.size(); i++) {
      res.append("[" + colors.get(i) + "]");
    }
    return res.toString();
  }

  @Override
  public String getName() {
    return STATE_NAME;
  }

}

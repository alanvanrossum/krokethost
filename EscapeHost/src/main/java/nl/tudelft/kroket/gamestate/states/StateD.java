package nl.tudelft.kroket.gamestate.states;

import nl.tudelft.kroket.gamestate.GameState;

/**
 * This is the fourth game state. In this game state minigame D can be started and ended. The game
 * can progress to state Final.
 */
public class StateD extends GameState {

  private static final String STATE_NAME = "D";

  private static GameState instance = new StateD();

  public static GameState getInstance() {
    return instance;
  }

  @Override
  public String getName() {
    return STATE_NAME;
  }

}

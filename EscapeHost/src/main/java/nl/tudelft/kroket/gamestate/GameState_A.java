package nl.tudelft.kroket.gamestate;

import nl.tudelft.kroket.server.EscapeHost;

import java.util.HashMap;

/**
 * This is the first game state. In this game state minigame A can be started
 * and ended. The game can progress to state B.
 */
public class GameState_A extends GameState {

    /**
     * Sends the input to the mobile client.
     * 
     * @param input
     *            the input string received from the client
     * @param parsedInput
     *            the parsed input
     */
    @Override
    public void startA(String input, HashMap<String, String> parsedInput) {
	// Send the input to the mobile client and VR client.
	if (parsedInput.containsKey("param_0")) {
	    EscapeHost.sendAll(input);
	}
    }

    /**
     * Sends the input to the virtual client and changes the game state to B.
     * 
     * @param input
     *            the input string received from the client
     * @param parsedInput
     *            the parsed input
     */
    @Override
    public void endA(String input, HashMap<String, String> parsedInput) {
	// Send the input to the virtual and mobile client.
	if (parsedInput.containsKey("param_0")) {
	    EscapeHost.sendAll(input);
	}

	// Update gameState.
	GameState.getInstance().setState(new GameState_B());
    }

}

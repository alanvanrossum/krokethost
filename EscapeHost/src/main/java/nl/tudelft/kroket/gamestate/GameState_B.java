package nl.tudelft.kroket.gamestate;

import java.util.HashMap;

import nl.tudelft.kroket.net.protocol.Protocol;
import nl.tudelft.kroket.server.EscapeHost;

/**
 * This is the second game state. In this game state minigame B can be started
 * and ended. The game can progress to state C.
 */
public class GameState_B extends GameState {

    private int finished_counter = 0;

    /**
     * Sends the input to the mobile client.
     * 
     * @param input
     *            the input string received from the client
     * @param parsedInput
     *            the parsed input
     */
    @Override
    public void startB(String input, HashMap<String, String> parsedInput) {
	// Send the input to the mobile client and virtual client
	if (parsedInput.containsKey("param_0")) {
	    EscapeHost.sendAll(input);
	}
    }

    /**
     * Sends the input to the virtual client and changes the game state to C.
     * 
     * @param input
     *            the input string received from the client
     * @param parsedInput
     *            the parsed input
     */
    @Override
    public void endB(String input, HashMap<String, String> parsedInput) {
	finished_counter++;

	// Do not finish when not all two mobile players have finished it.
	if (finished_counter < 2) {
	    return;
	}

	// Send the input to the virtual client.
	if (parsedInput.containsKey("param_0")) {
	    EscapeHost.sendAll(input);
	}

	// Update gameState.
	GameState.getInstance().setState(new GameState_C());
    }

}

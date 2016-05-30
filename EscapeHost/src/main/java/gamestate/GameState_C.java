package gamestate;

import java.util.HashMap;

import nl.tudelft.kroket.server.EscapeHost;

/**
 * This is the third game state. In this game state minigame C can be
 * started and ended. The game can progress to state D.
 */
public class GameState_C extends GameState {

	/**
	 * Sends the input to the mobile client.
	 * 
	 * @param input the input string received from the client
	 * @param parsedInput the parsed input
	 */
	@Override
	public void startC(String input, HashMap<String, String> parsedInput) {
		//Send the input to the mobile client.
		if (parsedInput.containsKey("param_0")) {
	        EscapeHost.sendMobile(input);
	      }
	}
	
	/**
	 * Sends the input to the virtual client and changes the game state to D.
	 * 
	 * @param input the input string received from the client
	 * @param parsedInput the parsed input
	 */
	@Override
	public void endC(String input, HashMap<String, String> parsedInput) {
		//Send the input to the virtual client.
		if (parsedInput.containsKey("param_0")) {
	        EscapeHost.sendVirtual(input);
	      }
		
		//Update gameState.
		GameState.getInstance().setState(new GameState_D());
	}
	
}

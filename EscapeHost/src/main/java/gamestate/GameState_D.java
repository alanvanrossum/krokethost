package gamestate;

import java.util.HashMap;

import nl.tudelft.kroket.server.EscapeHost;

/**
 * This is the fourth game state. In this game state minigame D can be
 * started and ended. The game can progress to state Final.
 */
public class GameState_D extends GameState {

	/**
	 * Sends the input to the mobile client.
	 * 
	 * @param input the input string received from the client
	 * @param parsedInput the parsed input
	 */
	@Override
	public void startD(String input, HashMap<String, String> parsedInput) {
		//Send the input to the mobile client.
		if (parsedInput.containsKey("param_0")) {
	        EscapeHost.sendMobile(input);
	      }
	}
	
	/**
	 * Sends the input to the virtual client and changes the game state to Final.
	 * 
	 * @param input the input string received from the client
	 * @param parsedInput the parsed input
	 */
	@Override
	public void endD(String input, HashMap<String, String> parsedInput) {
		//Send the input to the virtual client.
		if (parsedInput.containsKey("param_0")) {
	        EscapeHost.sendVirtual(input);
	      }
		
		//Update gameState.
		GameState.getInstance().setState(new GameState_Final());
	}
	
}

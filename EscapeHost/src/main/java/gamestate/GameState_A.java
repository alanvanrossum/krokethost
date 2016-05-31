package gamestate;

import java.util.HashMap;

import nl.tudelft.kroket.net.protocol.Protocol;
import nl.tudelft.kroket.server.EscapeHost;

/**
 * This is the first game state. In this game state minigame A can be
 * started and ended. The game can progress to state B.
 */
public class GameState_A extends GameState {

	/**
	 * Sends the input to the mobile client.
	 * 
	 * @param input the input string received from the client
	 * @param parsedInput the parsed input
	 */
	@Override
	public void startA(String input, HashMap<String, String> parsedInput) {
		//Send the input to the mobile client and VR client.
		if (parsedInput.containsKey("param_0")) {
	        EscapeHost.sendMobile(input);
	        EscapeHost.sendVirtual(Protocol.COMMAND_INIT_VR + "[startA]");
	      }
	}
	
	/**
	 * Sends the input to the virtual client and changes the game state to B.
	 * 
	 * @param input the input string received from the client
	 * @param parsedInput the parsed input
	 */
	@Override
	public void endA(String input, HashMap<String, String> parsedInput) {
		//Send the input to the virtual client.
		if (parsedInput.containsKey("param_0")) {
	        EscapeHost.sendVirtual(input);
	      }
		
		//Update gameState.
		GameState.getInstance().setState(new GameState_B());
	}
	
}

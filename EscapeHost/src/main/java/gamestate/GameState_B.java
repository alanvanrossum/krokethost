package gamestate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import nl.tudelft.kroket.net.protocol.Protocol;
import nl.tudelft.kroket.server.EscapeHost;

/**
 * This is the second game state. In this game state minigame B can be
 * started and ended. The game can progress to state C.
 */
public class GameState_B extends GameState {

	/**
	 * Sends the input to the mobile client.
	 * 
	 * @param input the input string received from the client
	 * @param parsedInput the parsed input
	 */
	@Override
	public void startB(String input, HashMap<String, String> parsedInput) {
		ArrayList<String> buttonSequence = generateButtonSequence();
		
		//Send the input to the mobile client and virtual client
		if (parsedInput.containsKey("param_0")) {
	        //EscapeHost.sendMobile(input);
	        //EscapeHost.sendVirtual(Protocol.COMMAND_INIT_VR + "[startB]");
	        
	        
	        EscapeHost.sendMobile(Protocol.COMMAND_INIT_MOBILE + "[startB]" + sequenceToString(buttonSequence));
			EscapeHost.sendVirtual(Protocol.COMMAND_INIT_VR + "[startB]" + sequenceToString(buttonSequence));
	      }
	}
	
	/**
	 * Sends the input to the virtual client and changes the game state to C.
	 * 
	 * @param input the input string received from the client
	 * @param parsedInput the parsed input
	 */
	@Override
	public void endB(String input, HashMap<String, String> parsedInput) {
		//Send the input to the virtual client.
		if (parsedInput.containsKey("param_0")) {
	        EscapeHost.sendVirtual(input);
	      }
		
		//Update gameState.
		GameState.getInstance().setState(new GameState_C());
	}
	
	public ArrayList<String> generateButtonSequence() {
		ArrayList<String> buttonSequence = new ArrayList<String>();
		
		buttonSequence.add("RED");
		buttonSequence.add("BLUE");
		buttonSequence.add("YELLOW");
		buttonSequence.add("GREEN");
		
		Collections.shuffle(buttonSequence);
		
		return buttonSequence;
	}
	
	/**
	 * Generates a string in the right format from the arraylist of colors.
	 * @param colors the arraylist with the colors
	 * @return the string with the colors
	 */
	public String sequenceToString(ArrayList<String> buttons) {
		StringBuilder res = new StringBuilder();
		for (int i = 0; i < buttons.size(); i++) {
			res.append("[" + buttons.get(i) + "]");
		}
		return res.toString();
	}
}

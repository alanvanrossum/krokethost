package gamestate;

import java.util.HashMap;

public class GameState {

	/** Singleton instance. */
	private static GameState GAMESTATE = new GameState_A();

	/**
	 * Singleton instance.
	 * 
	 * @return the gamestate object
	 */
	public static GameState getInstance() {
		return GAMESTATE;
	}  

	/**
	 * Change the class of GAMESTATE.
	 * 
	 * @param stateClass the class it needs to change to
	 */
	public void setState(GameState stateClass) {
		GAMESTATE = stateClass;
	}

	/**
	 * Matches the action from the input to all possible strings.
	 * The appropriate method call is executed.
	 * 
	 * @param input the input string received from the client
	 * @param parsedInput the parsed input
	 */
	public void handleMessage(String input, HashMap<String, String> parsedInput) {
		String action = parsedInput.get("param_0");

		switch (action) {
		//Inputs received from the VR client
		case "startA":
			startA(input, parsedInput);
			break;

		case "startB":
			startB(input, parsedInput);
			break;

		case "startC":
			startC(input, parsedInput);
			break;

		case "startD":
			startD(input, parsedInput);
			break;

			//Inputs from the Mobile client
		case "doneA":
			endA(input, parsedInput);
			break;

		case "doneB":
			endB(input, parsedInput);
			break;

		case "doneC":
			endC(input, parsedInput);
			break;

		case "doneD":
			endD(input, parsedInput);
			break;
		}

	}

	/**
	 * Methods that can be overwritten with functionality in the subclasses of GameState.
	 * A certain method should only have functionality/be executed if the game is in the
	 * right game state. 
	 * 
	 * @param input the input string received from the client
	 * @param parsedInput the parsed input string
	 */
	public void startA(String input, HashMap<String, String> parsedInput) {}
	public void endA(String input, HashMap<String, String> parsedInput) {}

	public void startB(String input, HashMap<String, String> parsedInput) {}
	public void endB(String input, HashMap<String, String> parsedInput) {}

	public void startC(String input, HashMap<String, String> parsedInput) {}
	public void endC(String input, HashMap<String, String> parsedInput) {}

	public void startD(String input, HashMap<String, String> parsedInput) {}
	public void endD(String input, HashMap<String, String> parsedInput) {}

}

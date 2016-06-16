package nl.tudelft.kroket.gamestate.states;

import nl.tudelft.kroket.gamestate.GameState;
import nl.tudelft.kroket.log.Logger;
import nl.tudelft.kroket.net.protocol.Protocol;

/**
 * State for the lock game.
 * @author Team Kroket
 */
public class StateD extends GameState {
	
    /** Singleton reference to logger. */
    static final Logger log = Logger.getInstance();

    /** Identifier of the state, used for messages. */
    private static final String STATE_NAME = "D";
    
    /** Class simpleName, used as tag for logging. */
    private final String className = this.getClass().getSimpleName();

    /** The instance of this state */
    private static GameState instance = new StateD();

    /**
     * Getter for the instance.
     * 
     * @return the instance.
     */
    public static GameState getInstance() {
        return instance;
    }

    /**
     * Return the name of the state. 
     */
    @Override
    public String getName() {
        return STATE_NAME;
    }
    
    /**
     * Stop this state and end the game.
     */
    public void stop() {
      log.info(className, "Stopping gameState " + getName());
      host.sendAll(String.format("%s[%s]", Protocol.COMMAND_DONE, getName()));
      setActive(false);
      session.advance();

      // End the game
      host.sendAll(Protocol.COMMAND_GAMEWON);
      session.newSession();
    }

}

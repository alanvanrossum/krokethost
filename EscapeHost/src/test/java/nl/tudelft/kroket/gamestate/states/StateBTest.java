package nl.tudelft.kroket.gamestate.states;

import static org.junit.Assert.*;

import nl.tudelft.kroket.gamestate.GameState;
import nl.tudelft.kroket.gamestate.states.StateA;
import nl.tudelft.kroket.gamestate.states.StateB;
import nl.tudelft.kroket.gamestate.states.StateC;
import nl.tudelft.kroket.server.GameHost;
import nl.tudelft.kroket.server.GameSession;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashMap;

/**
 * Class for testing StateB class.
 * 
 * @author Team Kroket
 *
 */
public class StateBTest {

  GameState gameState;
  GameState stateSpy;
  GameHost host;
  GameSession gs;
  private HashMap<String, String> parsedInput;

  /**
   * Sets up objects for the test methods.
   * @throws Exception exception
   */
  @Before
  public void setUp() throws Exception {
    parsedInput = new HashMap<>();
    gameState = new StateB();
    stateSpy = Mockito.spy(gameState);
    host = Mockito.mock(GameHost.class);
    gs = Mockito.mock(GameSession.class);
    gameState.setHost(host);
    gameState.setSession(gs);
  }

  /**
   * Cleans up after methods.
   * @throws Exception exception
   */
  @After
  public void tearDown() throws Exception {
    gameState = null;
  }
  
  
  /**
   * Test for getInstance method.
   */
  @Test
  public void testGetInstance() {
    assertNotNull(StateB.getInstance());
  }
   
  /**
   * Test for start method.
   */
  @Test
  public void testStart() {
    gameState.start();
    Mockito.verify(host).sendAll(Mockito.anyString());
   // Mockito.verify(stateSpy).generateButtonSequence();
  }
  
  /**
   * Test for handleInput method.
   */
  @Test
  public void testHandleInput() {
    
  }
  
  /**
   * Test for handleInput method, with BEGIN message.
   */
  @Test
  public void testBeginHandleInput() {
    gameState.setHost(host);
    gameState.setSession(gs);
    String input = "BEGIN[A]";
    parsedInput.put("param_0", "A");
    parsedInput.put("command", "BEGIN");
    gameState.handleInput(input, parsedInput);
    //Mockito.verify(stateSpy).start();
  }
  
  /**
   * Test for handleInput method, with DONE message.
   */
  @Test
  public void testDoneHandleInput() {
    gameState.setHost(host);
    gameState.setSession(gs);
    gameState.setActive(true);
    String input = "DONE[A]";
    parsedInput.put("param_0", "A");
    parsedInput.put("command", "DONE");
    gameState.handleInput(input, parsedInput);
    //Mockito.verify(stateSpy).stop();
  }
  
  /**
   * Test for handleInpu method with invalid command.
   */
  @Test
  public void tesIgnoreHandleInput() {
    gameState.setHost(host);
    gameState.setSession(gs);
    gameState.setActive(true);
    String input = "DONE[A]";
    parsedInput.put("param_0", "A");
    parsedInput.put("command", "test");
    gameState.handleInput(input, parsedInput);
    Mockito.verify(host, Mockito.never()).sendAll(Mockito.anyString());
  }
  
  /**
   * Test for method.
   */
  @Test
  public void test() {
    
  }


}

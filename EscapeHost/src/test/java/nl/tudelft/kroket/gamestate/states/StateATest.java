package nl.tudelft.kroket.gamestate.states;

import static org.junit.Assert.assertNotNull;

import nl.tudelft.kroket.server.GameHost;
import nl.tudelft.kroket.server.GameSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashMap;


/**
 * Class for testing StateA class.
 * 
 * @author Team Kroket
 *
 */
public class StateATest {  
  
  GameStateA gameState;
  GameStateA stateSpy;
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
    gameState = new GameStateA();
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
   * Test getInstance method.
   */
  @Test
  public void testGetInstance() {
    assertNotNull(GameStateA.getInstance());
  }
  
  /**
   * Test for stop method.
   */
  @Test
  public void testStop() {
    gameState.stop();
    Mockito.verify(host).sendAll(Mockito.anyString());
  }
  /**
   * Test for handleInput method, with no param_0 in hashmap.
   */
  @Test
  public void testInvalidHandleInput() {
    String input = "test";
    parsedInput.put("param_5", "A");
    parsedInput.put("command", "test");
    gameState.handleInput(input, parsedInput);
    Mockito.verify(host, Mockito.never()).sendAll(Mockito.anyString());
    parsedInput.remove("param_5");
    parsedInput.put("param_0", "D");
    gameState.handleInput(input, parsedInput);
    Mockito.verify(host, Mockito.never()).sendAll(Mockito.anyString());
  }
  
  /**
   * Test for handleInput method, with BEGIN message.
   */
  @Test
  public void testBeginHandleInput() {
    String input = "BEGIN[A]";
    parsedInput.put("param_0", "A");
    parsedInput.put("command", "BEGIN");
    gameState.handleInput(input, parsedInput);
    Mockito.verify(host).sendAll(Mockito.anyString());
   //Mockito.verify(stateSpy).start();
  }
  
  /**
   * Test for handleInput method, with DONE message with param_0.
   */
  @Test
  public void testDoneHandleInput() {
    gameState.setActive(true);
    String input = "DONE[A]";
    parsedInput.put("param_0", "A");
    parsedInput.put("command", "DONE");
    gameState.handleInput(input, parsedInput);
    Mockito.verify(host, Mockito.atLeastOnce()).sendAll(Mockito.anyString());
    //Mockito.verify(stateSpy).stop();
  }
  
  /**
   * Test for handleInput method, with DONE message with param_1.
   */
  @Test
  public void testDoneHandleInputParam1() {
    gameState.setActive(true);
    parsedInput.put("param_0", "A");
    parsedInput.put("param_1", "A");
    parsedInput.put("command", "DONE");
    String input = "DONE[A]";
    gameState.handleInput(input, parsedInput);
    Mockito.verify(host, Mockito.never()).sendAll(Mockito.anyString());
  }
  
  /**
   * Test for handleInput method with invalid command.
   */
  @Test
  public void tesIgnoreHandleInput() {
    gameState.setActive(true);
    String input = "DONE[A]";
    parsedInput.put("param_0", "A");
    parsedInput.put("command", "test");
    gameState.handleInput(input, parsedInput);
    Mockito.verify(host, Mockito.never()).sendAll(Mockito.anyString());
  }
  
  /**
   * Test for handleInput method with invalid command and active = false.
   */
  @Test
  public void tesIgnoreHandleInputFalse() {
    gameState.setActive(false);
    String input = "DONE[A]";
    parsedInput.put("param_0", "A");
    parsedInput.put("command", "test");
    gameState.handleInput(input, parsedInput);
    Mockito.verify(host, Mockito.never()).sendAll(Mockito.anyString());
  }


}

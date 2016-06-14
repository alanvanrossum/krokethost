package nl.tudelft.kroket.gamestate.states;

import static org.junit.Assert.*;

import nl.tudelft.kroket.gamestate.states.StateB;
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

  StateB gameState;
  StateB stateSpy;
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
    gameState.generateButtonSequence();
    assertTrue(gameState.getButtonSequence().isEmpty());
    gameState.start();
    Mockito.verify(host).sendAll(Mockito.anyString());
    assertFalse(gameState.getButtonSequence().isEmpty());
    //Mockito.verify(stateSpy).generateButtonSequence();
  }
  
  /**
   * Test for handleInput method, with no param_0 in hashmap.
   */
  @Test
  public void testInvalidHandleInput() {
    String input = "test";
    parsedInput.put("param_5", "B");
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
    parsedInput.put("param_0", "B");
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
    parsedInput.put("param_0", "B");
    parsedInput.put("command", "DONE");
    gameState.handleInput(input, parsedInput);
    assertTrue(gameState.isInputValid());
    //Mockito.verify(stateSpy).stop();
  }
  
  /**
   * Test for handleInput method, with VERIFY message.
   */
  @Test
  public void testVerifyHandleInput() {
    gameState.setActive(true);
    parsedInput.put("param_0", "B");
    parsedInput.put("param_1", "TEST");
    parsedInput.put("command", "VERIFY");
    String input = "VERIFY[B]";
    int previousCount = gameState.getFinishedCounter();
    gameState.handleInput(input, parsedInput);
    assertEquals(previousCount + 1, gameState.getFinishedCounter());
    Mockito.verify(host, Mockito.never()).sendAll(Mockito.anyString());
  }
  
  /**
   * Test for handleInput method, with RESTART message.
   */
  @Test
  public void testRestartHandleInput() {
    gameState.setActive(true);
    parsedInput.put("param_0", "B");
    parsedInput.put("param_1", "TEST");
    parsedInput.put("command", "RESTART");
    String input = "RESTART[B]";
    gameState.handleInput(input, parsedInput);
    assertEquals(0, gameState.getFinishedCounter());
    Mockito.verify(host).sendAll(Mockito.anyString());
  }
  
  /**
   * Test for handleInput method with invalid command.
   */
  @Test
  public void tesIgnoreHandleInput() {
    gameState.setActive(true);
    String input = "DONE[A]";
    parsedInput.put("param_0", "B");
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
    parsedInput.put("param_0", "B");
    parsedInput.put("command", "test");
    gameState.handleInput(input, parsedInput);
    Mockito.verify(host, Mockito.never()).sendAll(Mockito.anyString());
  }
  
  /**
   * Test for handleInput method with invalid command and 
   * active = true and gameComplete is true.
   */
  @Test
  public void tesHandleInputGameComplete() {
    gameState.setActive(true);
    gameState.setInputValid(true);
    gameState.setFinishedCounter(2);
    parsedInput.put("param_0", "B");
    String input = "DONE[A]"; 
    parsedInput.put("command", "BEGIN");
    gameState.handleInput(input, parsedInput);
    Mockito.verify(host).sendAll(Mockito.anyString());
    Mockito.verify(gs).advance();
  }
  
  
  /**
   * Test for handleInput method with invalid command and 
   * active = true and gameComplete is false.
   */
  @Test
  public void tesHandleInputGameNotComplete() {
    gameState.setActive(true);
    gameState.setInputValid(false);
    gameState.setFinishedCounter(2);
    parsedInput.put("param_0", "B");
    String input = "DONE[A]"; 
    parsedInput.put("command", "BEGIN");
    gameState.handleInput(input, parsedInput);
    Mockito.verify(host).sendAll(Mockito.anyString());
    Mockito.verify(gs, Mockito.never()).advance();
  }
 
}

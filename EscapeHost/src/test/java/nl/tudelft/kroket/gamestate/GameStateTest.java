package nl.tudelft.kroket.gamestate;

import static org.junit.Assert.*;

import nl.tudelft.kroket.gamestate.states.StateA;
import nl.tudelft.kroket.gamestate.states.StateB;
import nl.tudelft.kroket.gamestate.states.StateD;
import nl.tudelft.kroket.server.GameHost;
import nl.tudelft.kroket.server.GameSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


import java.util.HashMap;


public class GameStateTest {
  
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
    gameState = new StateA();
    stateSpy = Mockito.spy(StateA.getInstance());
    host = Mockito.mock(GameHost.class);
    gs = Mockito.mock(GameSession.class);
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
   * Test for setHost method.
   */
  @Test
  public void testSetHost() {
    gameState.setHost(host);
    assertEquals(gameState.getHost(), host);
  }

  /**
   * Test for setSession method.
   */
  @Test
  public void testSetSession() {
    gameState.setSession(gs);
    assertEquals(gameState.getSession(), gs);
  }

  /**
   * Test for start method.
   */
  @Test
  public void testStart() {
    gameState.start();
    assertTrue(gameState.isActive());
  }

  /**
   * Test for stop method.
   */
  @Test
  public void testStop() {
    gameState = new StateB();
    gameState.setHost(host);
    gameState.setSession(gs);
    gameState.start();
    assertTrue(gameState.isActive());
    gameState.stop();
    assertFalse(gameState.isActive());
    Mockito.verify(host, Mockito.atLeastOnce()).sendAll(Mockito.anyString());
    Mockito.verify(gs).advance();
  }

  /**
   * Test for getName method.
   */
  @Test
  public void testGetName() {
    assertEquals(gameState.getName(), "A");
  }

  /**
   * Test for isActive method.
   */
  @Test
  public void testIsActive() {
    assertFalse(gameState.isActive());
  }

  /**
   * Test for setActive method.
   */
  @Test
  public void testSetActive() {
    assertFalse(gameState.isActive());
    gameState.setActive(true);
    assertTrue(gameState.isActive());
  }

  /**
   * Test for handleInput method, with no param_0 in hashmap.
   */
  @Test
  public void testInvalidHandleInput() {
    gameState = new StateD();
    gameState.setHost(host);
    String input = "test";
    parsedInput.put("param_1", "B");
    parsedInput.put("command", "test");
    gameState.handleInput(input, parsedInput);
    Mockito.verify(host, Mockito.never()).sendAll(Mockito.anyString());
    parsedInput.remove("param_1");
    parsedInput.put("param_0", "B");
    gameState.handleInput(input, parsedInput);
    Mockito.verify(host, Mockito.never()).sendAll(Mockito.anyString());
  }
  
  /**
   * Test for handleInput method, with BEGIN message.
   */
  @Test
  public void testBeginHandleInput() {
    gameState = new StateD();
    gameState.setHost(host);
    gameState.setSession(gs);
    String input = "BEGIN[D]";
    parsedInput.put("param_0", "D");
    parsedInput.put("command", "BEGIN");
    gameState.handleInput(input, parsedInput);
    Mockito.verify(host).sendAll(Mockito.anyString());
    //Mockito.verify(stateSpy).start();
  }
  
  /**
   * Test for handleInput method, with DONE message.
   */
  @Test
  public void testDoneHandleInput() {
    gameState = new StateD();
    gameState.setHost(host);
    gameState.setSession(gs);
    gameState.setActive(true);
    String input = "DONE[D]";
    parsedInput.put("param_0", "D");
    parsedInput.put("command", "DONE");
    gameState.handleInput(input, parsedInput);
    Mockito.verify(host, Mockito.atLeastOnce()).sendAll(Mockito.anyString());
    //Mockito.verify(stateSpy).stop();
  }
  
  /**
   * Test for handleInput method with invalid command.
   */
  @Test
  public void tesIgnoreHandleInput() {
    gameState = new StateD();
    gameState.setHost(host);
    gameState.setSession(gs);
    gameState.setActive(true);
    String input = "DONE[A]";
    parsedInput.put("param_0", "D");
    parsedInput.put("command", "test");
    gameState.handleInput(input, parsedInput);
    Mockito.verify(host, Mockito.never()).sendAll(Mockito.anyString());
  }
  
  /**
   * Test for handleInput method with invalid command and active = false.
   */
  @Test
  public void tesIgnoreHandleInputFalse() {
    gameState = new StateD();
    gameState.setHost(host);
    gameState.setSession(gs);
    gameState.setActive(false);
    String input = "DONE[A]";
    parsedInput.put("param_0", "D");
    parsedInput.put("command", "test");
    gameState.handleInput(input, parsedInput);
    Mockito.verify(host, Mockito.never()).sendAll(Mockito.anyString());
  }

}

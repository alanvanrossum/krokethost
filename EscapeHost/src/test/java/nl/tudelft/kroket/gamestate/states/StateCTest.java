package nl.tudelft.kroket.gamestate.states;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import nl.tudelft.kroket.gamestate.states.StateC;
import nl.tudelft.kroket.server.GameHost;
import nl.tudelft.kroket.server.GameSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class for testing StateC class.
 * 
 * @author Kroket
 *
 */
public class StateCTest {
  
  StateC gameState;
  StateC stateSpy;
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
    gameState = new StateC();
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
    assertNotNull(StateC.getInstance());
  }
  
  /**
   * Test for handleInput method, with no param_0 in hashmap.
   */
  @Test
  public void testInvalidHandleInput() {
    String input = "test";
    parsedInput.put("param_5", "C");
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
    String input = "BEGIN[C]";
    parsedInput.put("param_0", "C");
    parsedInput.put("command", "BEGIN");
    gameState.handleInput(input, parsedInput);
    Mockito.verify(host, Mockito.atLeastOnce()).sendAll(Mockito.anyString());
   //Mockito.verify(stateSpy).start();
  }
  
  /**
   * Test for handleInput method, with DONE message with param_0.
   */
  @Test
  public void testDoneHandleInput() {
    gameState.setActive(true);
    String input = "DONE[C]";
    parsedInput.put("param_0", "C");
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
    gameState.setActive(false);
    String input = "DONE[A]";
    parsedInput.put("param_0", "C");
    parsedInput.put("command", "DONE");
    gameState.handleInput(input, parsedInput);
    Mockito.verify(host, Mockito.never()).sendAll(Mockito.anyString());
  }
  
  /**
   * Test for handleInput method with invalid command and active = false.
   */
  @Test
  public void tesIgnoreHandleInputTrue() {
    gameState.setActive(true);
    String input = "TEST[A]";
    parsedInput.put("param_0", "C");
    parsedInput.put("command", "test");
    gameState.handleInput(input, parsedInput);
    Mockito.verify(host, Mockito.never()).sendAll(Mockito.anyString());
  }

  /**
   * Test for generatecolorsequence method.
   */
  @Test
  public void testGenerateColorSequence() {
    StateC gameState = new StateC();
    ArrayList<String> randomColours = gameState.generateColorSequence();
    assertEquals(7, randomColours.size());
    assertTrue(randomColours.contains("RED") || randomColours.contains("BLUE")
        || randomColours.contains("GREEN") || randomColours.contains("YELLOW"));
  }

  /**
   * Test for getrandomcolor method.
   */
  @Test
  public void testGetRandomColor() {
    StateC gameState = new StateC();
    String colour = gameState.getRandomColor();
    assertTrue(colour.equals("RED") || colour.equals("BLUE") || colour.equals("GREEN")
        || colour.equals("YELLOW"));
  }

  /**
   * Test for sequencetostring method.
   */
  @Test
  public void testSequenceToString() {
    StateC gameState = new StateC();
    ArrayList<String> randomColours = gameState.generateColorSequence();
    String actual = gameState.sequenceToString(randomColours);
    String expected = "";
    for (String s : randomColours) {
      expected += "[" + s + "]";
    }
    assertEquals(actual, expected);
  }

}

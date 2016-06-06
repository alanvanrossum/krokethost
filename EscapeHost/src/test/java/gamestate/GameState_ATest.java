package gamestate;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import nl.tudelft.kroket.log.Logger.LogLevel;

/**
 * Class for testing GameState_A class.
 * @author Kroket
 *
 */
public class GameState_ATest {

  private HashMap<String, String> parsedInput;

  /**
   * Set up method, only for certain test methods.
   */
  @Before
  public void setUp() {
    parsedInput = new HashMap<>();
  }
  
  /**
   * Reset the singleton after each method.
   */
  @After
  public void tearDown(){
    GameState.getInstance().setState(new GameState_A());
  }

  /**
   * Test for startA method.
   */
  @Test
  public void testStartA() {
//    String input = "INITM[startA]";
//    parsedInput.put("param_0", "startA");
//    
//    ByteArrayOutputStream messages = new ByteArrayOutputStream();
//    System.setOut(new PrintStream(messages));
//    
//    GameState.getInstance().startA(input, parsedInput);
//    
//    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
//    String msgFormat = "%s %s: %s";
//    
//    String expected = "[" + timeFormat.format(new Date()) + "]: "
//        + String.format(msgFormat, LogLevel.INFO, "EscapeHost", "Message sent to 0 user(s)");
//    String message = messages.toString().trim();
//    assertEquals(expected, message);  
  }

  /**
   * Test for endA method.
   */
  @Test
  public void testEndA() {
    String input = "INITM[DoneA]";
    parsedInput.put("param_0", "doneA");

    GameState.getInstance().endA(input, parsedInput);
    assertTrue(GameState.getInstance() instanceof GameState_B);
  }

}

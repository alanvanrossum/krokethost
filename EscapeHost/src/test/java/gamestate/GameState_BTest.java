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
 * Class for testing GameState_B class.
 * @author Kroket
 *
 */
public class GameState_BTest {

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
   * Test for startB.
   */
  @Test
  public void testStartB() {
//    String input = "INITM[startB]";
//    parsedInput.put("param_0", "startB");
//    GameState.getInstance().setState(new GameState_B());
//    ByteArrayOutputStream messages = new ByteArrayOutputStream();
//    System.setOut(new PrintStream(messages));
//    
//    GameState.getInstance().startB(input, parsedInput);
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
   * Test for endB.
   */
  @Test
  public void testEndB() {
//    String input = "INITM[doneB]";
//    parsedInput.put("param_0", "doneB");
//    GameState.getInstance().setState(new GameState_B());
//    GameState.getInstance().endB(input, parsedInput);
//    assertTrue(GameState.getInstance() instanceof GameState_B);
//    //When method has been called twice, it should go to state c
//    GameState.getInstance().endB(input, parsedInput);
//    assertTrue(GameState.getInstance() instanceof GameState_C);
  }

}

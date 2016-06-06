package gamestate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import nl.tudelft.kroket.log.Logger.LogLevel;

/**
 * Class for testing GameState_C class.
 * @author Kroket
 *
 */
public class GameState_CTest {

  private HashMap<String, String> parsedInput;

  /**
   * Set up method, only for certain test methods.
   */
  @Before
  public void setUp() {
    parsedInput = new HashMap<>();
    GameState.getInstance().setState(new GameState_B());
    GameState.getInstance().setState(new GameState_C());
  }
  
  /**
   * Reset the singleton after each method.
   */
  @After
  public void tearDown(){
    GameState.getInstance().setState(new GameState_A());
    
  }


  /**
   * Test for startC.
   */
  @Test
  public void testStartC() {
//    String input = "INITM[startC]";
//    parsedInput.put("param_0", "startC");
//    
//    ByteArrayOutputStream messages = new ByteArrayOutputStream();
//    System.setOut(new PrintStream(messages));
//    
//    GameState.getInstance().startC(input, parsedInput);
//    
//    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
//    String msgFormat = "%s %s: %s";
//    
//    String expected = "[" + timeFormat.format(new Date()) + "]: "
//        + String.format(msgFormat, LogLevel.INFO, "EscapeHost", "Message sent to mobile user(s)")
//        + "\r\n" + "[" + timeFormat.format(new Date()) + "]: "
//        + String.format(msgFormat, LogLevel.INFO, "EscapeHost", "Message sent to virtual user");
//    String message = messages.toString().trim();
//    assertEquals(expected.trim(), message);  
  }

  @Test
  public void testEndC() {
    String input = "INITM[doneB]";
    parsedInput.put("param_0", "doneB");
    GameState.getInstance().endC(input, parsedInput);
    assertTrue(GameState.getInstance() instanceof GameState_D);
  }

  @Test
  public void testGenerateColorSequence() {
    GameState_C gameState = new GameState_C();
    ArrayList<String> randomColours = gameState.generateColorSequence();
    assertEquals(7, randomColours.size());
    assertTrue(randomColours.contains("RED") || randomColours.contains("BLUE")
        || randomColours.contains("GREEN") || randomColours.contains("YELLOW"));
  }

  @Test
  public void testGetRandomColor() {
    GameState_C gameState = new GameState_C();
    String colour = gameState.getRandomColor();
    assertTrue(colour.equals("RED") || colour.equals("BLUE") 
        || colour.equals("GREEN") || colour.equals("YELLOW"));
  }

  @Test
  public void testSequenceToString() {
    GameState_C gameState = new GameState_C();
    ArrayList<String> randomColours = gameState.generateColorSequence();
    String actual = gameState.sequenceToString(randomColours);
    String expected = "";
    for(String s : randomColours){
      expected += "[" + s + "]";
    }
    assertEquals(actual, expected);
  }

}

//package nl.tudelft.kroket.gamestate.states;
//
//import nl.tudelft.kroket.gamestate.GameState;
//import nl.tudelft.kroket.gamestate.states.StateA;
//import nl.tudelft.kroket.gamestate.states.StateB;
//import nl.tudelft.kroket.gamestate.states.StateC;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import static org.junit.Assert.*;
//
//import java.util.HashMap;
//
///**
// * Class for testing GameState_B class.
// * 
// * @author Team Kroket
// *
// */
//public class StateBTest {
//
//  private HashMap<String, String> parsedInput;
//
//  /**
//   * Set up method, only for certain test methods.
//   */
//  @Before
//  public void setUp() {
//    parsedInput = new HashMap<>();
//  }
//
//  /**
//   * Reset the singleton after each method.
//   */
//  @After
//  public void tearDown() {
//    GameState.getInstance().setState(new StateA());
//  }
//
//  /**
//   * Test for startB.
//   */
//  @Test
//  public void testStartB() {
//    // String input = "INITM[startB]";
//    // parsedInput.put("param_0", "startB");
//    // GameState.getInstance().setState(new GameState_B());
//    // ByteArrayOutputStream messages = new ByteArrayOutputStream();
//    // System.setOut(new PrintStream(messages));
//    //
//    // GameState.getInstance().startB(input, parsedInput);
//    //
//    // SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
//    // String msgFormat = "%s %s: %s";
//    //
//    // String expected = "[" + timeFormat.format(new Date()) + "]: "
//    // + String.format(msgFormat, LogLevel.INFO, "EscapeHost", "Message sent to 0 user(s)");
//    // String message = messages.toString().trim();
//    // assertEquals(expected, message);
//  }
//
//  /**
//   * Test for endB.
//   */
//  @Test
//  public void testEndB() {
//    String input = "INITM[doneB]";
//    parsedInput.put("param_0", "doneB");
//    GameState.getInstance().setState(new StateB());
//    GameState.getInstance().endB(input, parsedInput);
//    assertTrue(GameState.getInstance() instanceof StateB);
//    // When method has been called twice, it should go to state c
//    GameState.getInstance().endB(input, parsedInput);
//    assertTrue(GameState.getInstance() instanceof StateC);
//  }
//
//}

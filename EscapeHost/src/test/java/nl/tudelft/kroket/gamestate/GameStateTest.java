//package nl.tudelft.kroket.gamestate;
//
//import static org.junit.Assert.*;
//import static org.mockito.Mockito.*;
//
//import java.util.HashMap;
//
//import org.junit.After;
//import org.junit.Test;
//import org.mockito.Mockito;
//
//import nl.tudelft.kroket.gamestate.GameState;
//import nl.tudelft.kroket.gamestate.states.StateA;
//import nl.tudelft.kroket.gamestate.states.StateB;
//
///**
// * Class for testing GameState class.
// * 
// * @author Team Kroket
// *
// */
//public class GameStateTest {
//
//  private GameState gameStateSpy;
//  private HashMap<String, String> parsedInput;
//
//  /**
//   * Set up method, only for certain test methods.
//   */
//  public void setUp() {
//    parsedInput = new HashMap<>();
//    gameStateSpy = Mockito.spy(GameState.getInstance());
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
//   * Test for the getInstance method.
//   */
//  @Test
//  public void testGetInstance() {
//    assertNotNull(GameState.getInstance());
//    assertTrue(GameState.getInstance() instanceof StateA);
//  }
//
//  /**
//   * Test the setter for the gamestate.
//   */
//  @Test
//  public void testSetState() {
//    assertTrue(GameState.getInstance() instanceof StateA);
//    GameState.getInstance().setState(new StateB());
//    assertTrue(GameState.getInstance() instanceof StateB);
//  }
//
//  /**
//   * Tests if startA is called when input is startA.
//   */
//  @Test
//  public void testHandleMessageStartA() {
//    setUp();
//    String input = "INITM[startA]";
//    parsedInput.put("param_0", "startA");
//    gameStateSpy.handleMessage(input, parsedInput);
//    verify(gameStateSpy).startA(input, parsedInput);
//  }
//
//  /**
//   * Tests if startB is called when input is startB.
//   */
//  @Test
//  public void testHandleMessageStartB() {
//    setUp();
//    String input = "INITM[startB]";
//    parsedInput.put("param_0", "startB");
//    gameStateSpy.handleMessage(input, parsedInput);
//    verify(gameStateSpy).startB(input, parsedInput);
//  }
//
//  /**
//   * Tests if startC is called when input is startC.
//   */
//  @Test
//  public void testHandleMessageStartC() {
//    setUp();
//    String input = "INITM[startC]";
//    parsedInput.put("param_0", "startC");
//    gameStateSpy.handleMessage(input, parsedInput);
//    verify(gameStateSpy).startC(input, parsedInput);
//  }
//
//  /**
//   * Tests if startD is called when input is startA.
//   */
//  @Test
//  public void testHandleMessageStartD() {
//    setUp();
//    String input = "INITM[startD]";
//    parsedInput.put("param_0", "startD");
//    gameStateSpy.handleMessage(input, parsedInput);
//    verify(gameStateSpy).startD(input, parsedInput);
//  }
//
//  /**
//   * Tests if endA is called when input is doneA.
//   */
//  @Test
//  public void testHandleMessageDoneA() {
//    setUp();
//    String input = "INITM[doneA]";
//    parsedInput.put("param_0", "doneA");
//    gameStateSpy.handleMessage(input, parsedInput);
//    verify(gameStateSpy).endA(input, parsedInput);
//  }
//
//  /**
//   * Tests if endB is called when input is doneB.
//   */
//  @Test
//  public void testHandleMessageDoneB() {
//    setUp();
//    String input = "INITM[doneB]";
//    parsedInput.put("param_0", "doneB");
//    gameStateSpy.handleMessage(input, parsedInput);
//    verify(gameStateSpy).endB(input, parsedInput);
//  }
//
//  /**
//   * Tests if endC is called when input is doneC.
//   */
//  @Test
//  public void testHandleMessageDoneC() {
//    setUp();
//    String input = "INITM[doneC]";
//    parsedInput.put("param_0", "doneC");
//    gameStateSpy.handleMessage(input, parsedInput);
//    verify(gameStateSpy).endC(input, parsedInput);
//  }
//
//  /**
//   * Tests if endD is called when input is doneD.
//   */
//  @Test
//  public void testHandleMessageDoneD() {
//    setUp();
//    String input = "INITM[doneD]";
//    parsedInput.put("param_0", "doneD");
//    gameStateSpy.handleMessage(input, parsedInput);
//    verify(gameStateSpy).endD(input, parsedInput);
//  }
//
//}

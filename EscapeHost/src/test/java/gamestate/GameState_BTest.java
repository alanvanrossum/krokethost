package gamestate;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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

  @Test
  public void testStartB() {
   // fail("Not yet implemented");
  }

  @Test
  public void testEndB() {
    String input = "INITM[doneB]";
    parsedInput.put("param_0", "doneB");
    GameState.getInstance().setState(new GameState_B());
    GameState.getInstance().endB(input, parsedInput);
    assertTrue(GameState.getInstance() instanceof GameState_B);
    //When method has been called twice, it should go to state c
    GameState.getInstance().endB(input, parsedInput);
    assertTrue(GameState.getInstance() instanceof GameState_C);
  }

}

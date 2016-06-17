package nl.tudelft.kroket.gamestate.states;

import static org.junit.Assert.*;

import org.junit.Test;


/**
 * Class for testing StateFinal.
 * @author Kroket
 *
 */
public class StateFinalTest {

  
  /**
   * Test for getInstance method.
   */
  @Test
  public void testgetInstance() {
    assertNotNull(GameStateFinal.getInstance());
  }
  
  
  /**
   * Test for getName method.
   */
  @Test
  public void testGetName() {
    assertEquals(GameStateFinal.getInstance().getName(), "FINAL");
  }


}

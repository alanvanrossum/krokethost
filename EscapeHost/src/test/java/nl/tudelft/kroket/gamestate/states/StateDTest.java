package nl.tudelft.kroket.gamestate.states;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Class for testing StateD class.
 * @author Team Kroket
 *
 */
public class StateDTest {

  
  /**
   * Test for getInstance method.
   */
  @Test
  public void testgetInstance() {
    assertNotNull(GameStateD.getInstance());
  }
  
  
  /**
   * Test for getName method.
   */
  @Test
  public void testGetName() {
    assertEquals(GameStateD.getInstance().getName(), "D");
  }
}

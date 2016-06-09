package nl.tudelft.kroket.gamestate.states;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;


/**
 * Class for testing StateA class.
 * 
 * @author Team Kroket
 *
 */
public class StateATest {

  /**
   * Test getInstance method.
   */
  @Test
  public void testGetInstance() {
    assertNotNull(StateA.getInstance());
  }
  

}

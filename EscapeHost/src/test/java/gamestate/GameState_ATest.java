package gamestate;

import static org.junit.Assert.*;

import java.net.Socket;
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

import nl.tudelft.kroket.server.EscapeHost;
import nl.tudelft.kroket.user.RegisteredUser;

/**
 * Class for testing GameState_A class.
 * @author Kroket
 *
 */
@RunWith(PowerMockRunner.class)
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

  

  @Test
  public void testStartA() {
    String input = "INITM[startA]";
    parsedInput.put("param_0", "startA");
//    PowerMockito.mockStatic(EscapeHost.class);
//    BDDMockito.given(EscapeHost.sendAll(input)).willReturn(true);
//    GameState.getInstance().startA(input, parsedInput);
//    PowerMockito.verifyStatic();
//    EscapeHost.sendAll(input);
  }

  @Test
  public void testEndA() {
    String input = "INITM[DoneA]";
    parsedInput.put("param_0", "doneA");
    GameState.getInstance().endA(input, parsedInput);
    assertTrue(GameState.getInstance() instanceof GameState_B);
  }

}

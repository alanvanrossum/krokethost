package nl.tudelft.kroket.server;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.Socket;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import nl.tudelft.kroket.gamestate.GameState;
import nl.tudelft.kroket.gamestate.states.StateA;
import nl.tudelft.kroket.gamestate.states.StateB;
import nl.tudelft.kroket.gamestate.states.StateFinal;
import nl.tudelft.kroket.log.Logger;
import nl.tudelft.kroket.user.User.PlayerType;

public class GameSessionTest {
  
  GameHost host;
  GameSession gameSession;
  GameSession sessionSpy;

  /**
   * Sets up objects for tests.
   * @throws Exception exception.
   */
  @Before
  public void setUp() throws Exception {
    host = Mockito.mock(GameHost.class);
    gameSession = new GameSession(host, 1);
    sessionSpy = Mockito.spy(gameSession);
  }

  /**
   * Test for gameSession constructor.
   */
  @Test
  public void testGameSession() {
    GameSession session = new GameSession(host, 1);
    assertNotNull(session);
  }

  /**
   * Test for advance method.
   */
  @Test
  public void testAdvance() {
    gameSession.setState(StateA.getInstance());
   // Mockito.doNothing().when(sessionSpy).setState(Mockito.any());
    sessionSpy.advance();
    Mockito.verify(sessionSpy).setState(Mockito.any());
  }

  /**
   * Test for startSession method.
   */
  @Test
  public void testStartSession() {
    sessionSpy.startSession();
    Mockito.verify(sessionSpy).setState(Mockito.any());
    assertTrue(sessionSpy.isActive());
  }

  /**
   * Test for isReady method.
   */
  @Test
  public void testIsReady() {
    assertFalse(sessionSpy.isReady());
    Mockito.when(sessionSpy.countUsers(PlayerType.MOBILE)).thenReturn(2);
    Mockito.when(sessionSpy.countUsers(PlayerType.VIRTUAL)).thenReturn(1);
    assertTrue(sessionSpy.isReady());
  }

  /**
   * Test for stopSession method.
   */
  @Test
  public void testStopSession() {
    sessionSpy.startSession();
    assertTrue(sessionSpy.isActive());
    sessionSpy.stopSession();
    assertFalse(sessionSpy.isActive());
  }
  
  /**
   * Test for setState method.
   */
  @Test
  public void testSetState() {
    gameSession.setState(StateFinal.getInstance());
    assertTrue(gameSession.getCurrentState() instanceof StateFinal);
  }

  /**
   * Test for isactive method.
   */
  @Test
  public void testIsActive() {
    assertFalse(gameSession.isActive());
    gameSession.startSession();
    assertTrue(gameSession.isActive());
  }
//
//  /**
//   * Test for print methods.
//   */
//  @Test
//  public void testPrintSessionUsers() {
//    ByteArrayOutputStream messages = new ByteArrayOutputStream();
//    System.setOut(new PrintStream(messages));
//    gameSession.printSession();
//    String expected = "-- session 1 --\r\n" + "No players currently registered.\r\n"
//        + "----------------";
//    assertEquals(expected, messages.toString().trim());
//  }

  /**
   * Test for addclient method.
   */
  @Test
  public void testAddClient() {
    Socket socket = Mockito.mock(Socket.class);
    ClientInstance client = Mockito.mock(ClientInstance.class);
    gameSession.addClient(socket, client);
    assertTrue(gameSession.getClientList().containsKey(socket));
    assertTrue(gameSession.getClientList().containsValue(client));
    assertEquals(gameSession.getClientList().size(), 1);
  }

  /**
   * Test for removeclient method.
   */
  @Test
  public void testRemoveClient() {
    Socket socket = Mockito.mock(Socket.class);
    ClientInstance client = Mockito.mock(ClientInstance.class);
    gameSession.addClient(socket, client);
    assertEquals(gameSession.getClientList().size(), 1);
    gameSession.removeClient(socket);
    assertTrue(gameSession.getClientList().isEmpty());
  }

  /**
   * Test for handlemessage method.
   */
  @Test
  public void testHandleMessage() {
    StateA state = Mockito.mock(StateA.class);
    gameSession.setState(state);
    sessionSpy.handleMessage("test", null);
    Mockito.verify(state).handleInput("test", null);
  }

}

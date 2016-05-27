package nl.tudelft.kroket.server;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nl.tudelft.kroket.user.RegisteredUser;
import nl.tudelft.kroket.user.User.PlayerType;

/**
 * Test for EscapeHost class.
 * 
 * @author Team Kroket
 *
 */
public class EscapeHostTest {

  private EscapeHost host;
  private ByteArrayOutputStream outputStream;
  private Socket socket;
  private DataOutputStream dataOutputStream;
  private RegisteredUser user;

  @Before
  public void setUp() throws Exception {
    host = new EscapeHost(1234);
    outputStream = new ByteArrayOutputStream();
    dataOutputStream = new DataOutputStream(outputStream);
    socket = new Socket();
    user = new RegisteredUser(socket, dataOutputStream, "test");
  }

  @After
  public void tearDown() throws Exception {
    // os.close();
    // dos.close();
    // socket.close();
    // eh = null;
  }

  @Test
  public void testEscapeHost() {
    assertNotNull(host);
  }

//  @Test
//  public void testRemovePlayer() {
//    Socket socket2 = new Socket();
//
//    RegisteredUser us2 = new RegisteredUser(socket2, dataOutputStream, "test2");
//    EscapeHost.registerPlayer(socket2, dataOutputStream, "test2");
//    assertTrue(EscapeHost.userList.containsValue(us2));
//    EscapeHost.removePlayer(socket2);
//    assertFalse(EscapeHost.userList.containsValue(user));
//    assertFalse(EscapeHost.userList.containsKey(socket2));
//  }
//
//  @Test
//  public void testRegisterPlayer() {
//    assertEquals(user, EscapeHost.registerPlayer(socket, dataOutputStream, "test"));
//    assertTrue(EscapeHost.userList.containsValue(user));
//    assertTrue(EscapeHost.userList.containsKey(socket));
//  }

  // @Test
  // public void testCountUsers() {
  // EscapeHost.registerPlayer(socket, dos, "test3");
  // assertEquals(2, EscapeHost.countUsers(PlayerType.NONE));
  // }

  // @Test
  // public void testUserCount() {
  // fail("Not yet implemented");
  // }
  //
  // @Test
  // public void testClientCount() {
  // fail("Not yet implemented");
  // }
  //
  // @Test
  // public void testGameReady() {
  // fail("Not yet implemented");
  // }
  //
  // @Test
  // public void testGetUser() {
  // fail("Not yet implemented");
  // }
  //
  // @Test
  // public void testSendAll() {
  // fail("Not yet implemented");
  // }
  //
  // @Test
  // public void testSendMobile() {
  // fail("Not yet implemented");
  // }
  //
  // @Test
  // public void testSendVirtual() {
  // fail("Not yet implemented");
  // }

}

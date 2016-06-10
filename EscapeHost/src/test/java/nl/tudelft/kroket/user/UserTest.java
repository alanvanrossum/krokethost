package nl.tudelft.kroket.user;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import nl.tudelft.kroket.gamestate.states.StateD;
import nl.tudelft.kroket.user.User.PlayerType;

/**
 * Test class for the user class.
 * 
 * @author Kroket
 *
 */
public class UserTest {

  private ByteArrayOutputStream outputStream;
  private Socket socket;
  private DataOutputStream dataOutputStream;

  /**
   * Sets up necessary objects for each test.
   */
  @Before
  public void setUp() {
    outputStream = Mockito.mock(ByteArrayOutputStream.class);
    dataOutputStream = Mockito.mock(DataOutputStream.class);
    socket = Mockito.mock(Socket.class);
  }

  /**
   * Test for the constructor.
   */
  @Test
  public void testUser() {
    User user = new User(socket, dataOutputStream);

    assertNotNull(user);
  }

  /**
   * Test for getter and setter for stream.
   */
  @Test
  public void testGetSetStream() {
    User user = new User(socket, dataOutputStream);
    ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream();
    DataOutputStream dataOutputStream2 = new DataOutputStream(outputStream2);

    assertEquals(user.getStream(), dataOutputStream);
    user.setStream(dataOutputStream2);
    assertEquals(user.getStream(), dataOutputStream2);
  }

  /**
   * Test getter and setter socket.
   * 
   * @throws IOException IOexception
   */
  @Test
  public void testGetSetSocket() throws IOException {
    User user = new User(socket, dataOutputStream);
    Socket testSocket = new Socket();

    assertEquals(user.getSocket(), socket);
    user.setSocket(testSocket);
    assertEquals(user.getSocket(), testSocket);
    testSocket.close();
  }

  /**
   * Test for getter and setter type.
   */
  @Test
  public void testGetSetType() {
    User user = new User(socket, dataOutputStream);

    assertEquals(user.getType(), PlayerType.NONE);
    user.setType(PlayerType.MOBILE);
    assertEquals(user.getType(), PlayerType.MOBILE);
  }

  /**
   * Test for the toString method.
   */
  @Test
  public void testToString() {
    User user = new User(socket, dataOutputStream);
    assertEquals(user.toString(), "User not registered - " + user.getType());
  }

  /**
   * Test isConnected method.
   * 
   * @throws IOException IOException
   * @throws UnknownHostException Unknownhostexception
   */
  @Test
  public void testIsConnected() throws UnknownHostException, IOException {
    User user = new User(socket, dataOutputStream);
    assertFalse(user.isConnected());
  }
  
  
  /**
   * Test for sendMessage method.
   */
  @Test
  public void testSendMessage() {
    User user = new User(socket, dataOutputStream);
    user.sendMessage("");
    try {
      Mockito.verify(dataOutputStream, Mockito.never()).flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  

}

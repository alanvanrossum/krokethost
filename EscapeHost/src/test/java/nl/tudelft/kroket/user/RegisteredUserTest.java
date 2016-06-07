package nl.tudelft.kroket.user;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Class for testing RegisteredUser.
 * 
 * @author Kroket
 *
 */
public class RegisteredUserTest {

  private ByteArrayOutputStream ouputStream;
  private Socket socket;
  private DataOutputStream dataOutputStream;

  /**
   * Sets up necessary objects.
   * 
   * @throws Exception
   */
  @Before
  public void setUp() throws Exception {
    ouputStream = new ByteArrayOutputStream();
    dataOutputStream = new DataOutputStream(ouputStream);
    socket = new Socket();
  }

  /**
   * Tears down the objects.
   * 
   * @throws Exception
   */
  @After
  public void tearDown() throws Exception {
    ouputStream.close();
    dataOutputStream.close();
    socket.close();
  }

  /**
   * Test for the constructor.
   */
  @Test
  public void testRegisteredUser() {
    RegisteredUser user = new RegisteredUser(socket, dataOutputStream, "test");

    assertNotNull(user);
  }

  /**
   * Test for toString.
   */
  @Test
  public void testToString() {
//    RegisteredUser user = new RegisteredUser(socket, dataOutputStream, "test");
//
//    assertEquals(user.toString(),
//        "User " + user.getName() + " - " + socket.getRemoteSocketAddress() + " - " + user.getType());
  }

  /**
   * test for getters and setters for name.
   */
  @Test
  public void testGetSetName() {
    RegisteredUser user = new RegisteredUser(socket, dataOutputStream, "test");

    assertEquals(user.getName(), "test");
    user.setName("changed");
    assertEquals(user.getName(), "changed");
  }

}

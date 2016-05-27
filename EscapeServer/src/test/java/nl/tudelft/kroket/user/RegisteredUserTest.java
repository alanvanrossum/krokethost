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
 * @author Kroket
 *
 */
public class RegisteredUserTest {
  
  private ByteArrayOutputStream os;
  private Socket socket;
  private DataOutputStream dos;

  /**
   * Sets up necessary objects.
   * @throws Exception
   */
  @Before
  public void setUp() throws Exception {
    os = new ByteArrayOutputStream();
    dos = new DataOutputStream(os);
    socket = new Socket();
  }

  /**
   * Tears down the objects.
   * @throws Exception
   */
  @After
  public void tearDown() throws Exception {
    os.close();
    dos.close();
    socket.close();
  }
  
  /**
   * Test for the constructor.
   */
  @Test
  public void testRegisteredUser() {
    RegisteredUser us = new RegisteredUser(socket, dos, "test");
    
    assertNotNull(us);
  }

  /**
   * Test for toString.
   */
  @Test
  public void testToString() {
    RegisteredUser us = new RegisteredUser(socket, dos, "test");

    assertEquals(us.toString(), "User " + us.getName() + " - " + socket.getRemoteSocketAddress() 
      + " - " + us.getType());
  }


  /**
   * test for getters and setters for name.
   */
  @Test
  public void testGetSetName() {
    RegisteredUser us = new RegisteredUser(socket, dos, "test");
    
    assertEquals(us.getName(), "test");
    us.setName("changed");
    assertEquals(us.getName(), "changed");
  }


}

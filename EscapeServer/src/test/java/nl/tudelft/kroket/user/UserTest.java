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

import nl.tudelft.kroket.user.User.PlayerType;

/**
 * Test class for the user class.
 * @author Kroket
 *
 */
public class UserTest {

  private ByteArrayOutputStream os;
  private Socket socket;
  private DataOutputStream dos;
  
  /**
   * Sets up necessary objects for each test.
   */
  @Before
  public void setUp(){
    os = new ByteArrayOutputStream();
    dos = new DataOutputStream(os);
    socket = new Socket();
  }
  
  /**
   * Tears down objects after each test.
   * @throws IOException 
   */
  @After
  public void tearDown() throws IOException{
    os.close();
    dos.close();
    socket.close();
  }
  
  /**
   * Test for the constructor.
   */
  @Test
  public void testUser() {
    User us = new User(socket, dos);
    
    assertNotNull(us);
  }
  
  /**
   * Test for getter and setter for stream.
   */
  @Test
  public void testGetSetStream() {
    User us = new User(socket, dos);
    ByteArrayOutputStream osT = new ByteArrayOutputStream();
    DataOutputStream dosT = new DataOutputStream(osT);
    
    assertEquals(us.getStream(), dos);
    us.setStream(dosT);
    assertEquals(us.getStream(), dosT);
  }
  
  /**
   * Test getter and setter socket.
   * @throws IOException 
   */
  @Test
  public void testGetSetSocket() throws IOException {
    User us = new User(socket, dos);
    Socket test = new Socket();
    
    assertEquals(us.getSocket(), socket);
    us.setSocket(test);
    assertEquals(us.getSocket(), test);
    test.close();
  }
  
  /**
   * Test for getter and setter type.
   */
  @Test
  public void testGetSetType() {
    User us = new User(socket, dos);
    
    assertEquals(us.getType(), PlayerType.NONE);
    us.setType(PlayerType.MOBILE);
    assertEquals(us.getType(), PlayerType.MOBILE);
  }
  
  /**
   * Test for the toString method.
   */
  @Test
  public void testToString() {
    User us = new User(socket, dos);
    assertEquals(us.toString(), "User " + socket.getRemoteSocketAddress() + " - " + us.getType());
  }
  
  /**
   * Test isConnected method.
   * @throws IOException 
   * @throws UnknownHostException 
   */
  @Test
  public void testIsConnected() throws UnknownHostException, IOException {
    User us = new User(socket, dos);
    assertFalse(us.isConnected());
  }

}

package nl.tudelft.kroket.net.protocol;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import nl.tudelft.kroket.server.Settings;

/**
 * Class that test if all settings and protocols are correct.
 * @author Team Kroket
 *
 */
public class ProtocolSettingsTest {

  /**
   * Test if the values for the protocol are set correctly.
   */
  @Test
  public void testProtocol() {
    assertEquals(Protocol.COMMAND_ADMIN, "admin");
    assertEquals(Protocol.COMMAND_BEGIN, "BEGIN");  
    assertEquals(Protocol.COMMAND_DONE, "DONE");
    assertEquals(Protocol.COMMAND_REGISTER, "REGISTER");
    assertEquals(Protocol.COMMAND_RESTART, "RESTART");
    assertEquals(Protocol.COMMAND_START, "START"); 
    assertEquals(Protocol.COMMAND_TYPE, "TYPE"); 
    assertEquals(Protocol.COMMAND_VERIFY, "VERIFY"); 
  }
  
  /**
   * Test if the values for the settings are set correctly.
   */
  @Test
  public void testSettings() {
    assertEquals(Settings.INTERVAL_REPORT_STATUS, 120);
    assertEquals(Settings.INTERVAL_SOCKET_RETRY, 5);  
    assertEquals(Settings.PORTNUM, 1234);
    assertEquals(Settings.REQUIRED_MOBILE, 2);
    assertEquals(Settings.REQUIRED_VIRTUAL, 1);
    assertEquals(Settings.THREAD_POOL_MAX, 10); 
  }

}

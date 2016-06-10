package nl.tudelft.kroket.user;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Player object for EscapeServer.
 * 
 * @author Team Kroket
 *
 */
public class User {

  /** The CRLF String. */
  private static final String CRLF = "\r\n"; // newline

  /** the PlayerTypes. */
  public enum PlayerType {
    NONE, VIRTUAL, MOBILE, ADMIN
  }

  /** The client's socket. */
  private Socket socket;

  /** The player's current type. */
  private PlayerType type;

  /** The stream to use to reach the player. */
  private DataOutputStream stream;

  /**
   * Constructor for player object.
   * 
   * @param socket
   *          the socket the client is using
   * @param stream
   *          the dataoutputstream to use to reach the client
   */
  public User(final Socket socket, final DataOutputStream stream) {

    setStream(stream);
    setSocket(socket);

    setType(PlayerType.NONE);
  }

  /**
   * Get the outputstream of the socket.
   * 
   * @return the stream.
   */
  public DataOutputStream getStream() {
    return stream;
  }

  /**
   * Set the outputstream to the socket.
   * 
   * @param stream
   *          the output stream
   */
  public void setStream(final DataOutputStream stream) {
    this.stream = stream;
  }

  /**
   * Get player socket.
   * 
   * @return the socket the player is connected to
   */
  public Socket getSocket() {
    return socket;
  }

  /**
   * Set player socket.
   * 
   * @param socket
   *          the socket the player is connected to
   */
  public void setSocket(final Socket socket) {
    this.socket = socket;
  }

  /**
   * Get the player's type.
   * 
   * @return the player's type
   */
  public PlayerType getType() {
    return type;
  }

  /**
   * Set the player's type.
   */
  public void setType(final PlayerType type) {
    this.type = type;
  }

  /**
   * Get a human-readable string of the Player instance.
   */
  public String toString() {
    return String.format("User %s - %s", getName(), getType());
  }

  /**
   * Check whether player is (still) connected.
   * 
   * @return true if the player is connected
   */
  public boolean isConnected() {
    return getSocket().isConnected() && !getSocket().isClosed();
  }

  /**
   * Send a message to this player.
   * 
   * @param message
   *          the message to send
   */
  public void sendMessage(String message) {

    if (!isConnected()) {
      return;
    }

    // append CRLF if string doesn't end with one
    if (!message.endsWith(CRLF)) {
      message += CRLF;
    }

    try {
      stream.writeBytes(message);
      stream.flush();
    } catch (IOException error) {
      error.printStackTrace();
    }
  }

  public String getName() {
    return "not registered";
  }

}

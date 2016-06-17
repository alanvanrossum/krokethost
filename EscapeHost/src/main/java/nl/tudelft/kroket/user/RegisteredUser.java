package nl.tudelft.kroket.user;

import java.io.DataOutputStream;
import java.net.Socket;

/**
 * Object for a user that is registered; subclass of User.
 * 
 * @author Team Kroket
 */
public class RegisteredUser extends User {

  /** The player's name. */
  private String name;

  /**
   * Constructor for a registered user.
   * @param socket the socket of the user
   * @param stream the output stream of the user
   * @param name the name of the user
   */
  public RegisteredUser(Socket socket, DataOutputStream stream, String name) {
    super(socket, stream);
    setName(name);
  }

  /**
   * Get the user's name.
   * 
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Set the user's name.
   * 
   * @param name
   *          the name to set
   */
  public void setName(String name) {
    this.name = name;
  }


}

package nl.tudelft.kroket.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;

import nl.tudelft.kroket.gamestate.GameState;
import nl.tudelft.kroket.log.Logger;
import nl.tudelft.kroket.net.protocol.CommandParser;
import nl.tudelft.kroket.net.protocol.Protocol;
import nl.tudelft.kroket.user.RegisteredUser;
import nl.tudelft.kroket.user.User;
import nl.tudelft.kroket.user.User.PlayerType;

/**
 * ConnectionThread object to handle interaction with a client.
 * 
 * @author Team Kroket
 *
 */
public class ClientInstance implements Runnable {

  /** Singleton reference to logger. */
  static final Logger log = Logger.getInstance();

  private static final String CRLF = "\r\n";

  /** Class simpleName, used as tag for logging. */
  private final String className = this.getClass().getSimpleName();

  // private EscapeHost HOST = EscapeHost.getInstance();

  /** Client's DataInputStream. */
  DataInputStream inputStream = null;

  /** Client's DataOutputStream. */
  DataOutputStream outputStream = null;

  /** The type of user. */
  User.PlayerType type = PlayerType.NONE;

  /** Reference to the socket of the connected client. */
  protected Socket clientSocket = null;

  private User user;
  
  private GameHost host;

  private InputStream input;

  private OutputStream output;

  /**
   * Constructor for ConnectionThread.
   * 
   * @param clientSocket
   *          the socket of the client
   */
  public ClientInstance(GameHost host, Socket clientSocket) {
    this.host = host;
    this.clientSocket = clientSocket;

    try {
      input = clientSocket.getInputStream();
      output = clientSocket.getOutputStream();
      inputStream = new DataInputStream(input);
      outputStream = new DataOutputStream(output);
    } catch (IOException e) {
      e.printStackTrace();
    }

    this.user = new User(clientSocket, outputStream);
  }

  public User getUser() {
    return user;
  }

  private void setPlayerType(String typeString) {

    if (user == null) {
      return;
    }

    if (typeString.length() == 0) {
      return;
    }

    if (isRegistered())

      try {
        type = PlayerType.valueOf(typeString);

        // set the type of the user
        user.setType(type);

        // tell user what his new type is
        user.sendMessage("Your type is now set to " + type.toString());

        log.info(className, String.format("user %s is now set to type %s.", user.getName(), user
            .getType().toString()));

        // if the game isn't ready, let the new user know
        if (!GameHost.gameReady()) {
          user.sendMessage(GameHost.userCount() + " user(s) connected.");
        }
      } catch (IllegalArgumentException error) {
        user.sendMessage("Invalid client type.");
        log.error(className, "Invalid client type.");
      }
  }

  public boolean isRegistered() {
    return user instanceof RegisteredUser;
  }

  /**
   * Process remote input.
   * 
   * @param input
   *          the remote input
   */
  private void processInput(final String input) {
    log.info(className, "Input received: " + input);

    HashMap<String, String> parsedInput = CommandParser.parseInput(input);

    String command = parsedInput.get("command");

    switch (command) {
    case Protocol.COMMAND_TYPE:

      if (!isRegistered()) {
        log.error(className, "User tried to set type but is not yet registered.");
        sendMessage("Cannot set type: You are not yet registered.");
      } else {

        String typeString = parsedInput.get("param_0");
        setPlayerType(typeString);
      }
      break;

    case Protocol.COMMAND_REGISTER:
      if (parsedInput.containsKey("param_0")) {

        String userName = parsedInput.get("param_0");

        registerUser(userName);

        if (parsedInput.containsKey("param_1")) {
          String playerType = parsedInput.get("param_1");
          setPlayerType(playerType);
        }
      }
      break;
//
//    case Protocol.COMMAND_INIT_VR:
//    case Protocol.COMMAND_INIT_MOBILE:
//      // GameState.getInstance().handleMessage(input, parsedInput);
//      
//      host.getCurrentSession().handleMessage(input, parsedInput);
//
//      break;

    case Protocol.COMMAND_ADMIN:
      if (input.length() > Protocol.COMMAND_ADMIN.length() + 1) {
        adminCommand(input.substring(Protocol.COMMAND_ADMIN.length() + 1));
      }
      break;
    case Protocol.COMMAND_DONE:
    case Protocol.COMMAND_BEGIN:
      host.getCurrentSession().handleMessage(input, parsedInput);
      break;

      
    default:
      log.error(className, "Invalid command : " + command);
    }

  }

  /**
   * Register a player to the server.
   * 
   * @param clientSocket
   *          - the client's socket
   * @param dOutput
   *          - the dataoutputstream
   * @param playername
   *          - the player's name
   */
  public void registerUser(String playername) {

    if (playername.length() > 0) {

      if (isRegistered()) {
        sendMessage("You are already registered with this server.");
      } else if (host.getUser(playername) != null) {
        log.info(className, "Player " + playername + " already registered");
        sendMessage(String.format("Name %s already in use.", playername));
      } else {

        user = new RegisteredUser(clientSocket, outputStream, playername);

        // tell user registration was succesful
        user.sendMessage("You are now registered as " + user.getName());

        // let everyone know a new player registered with the server
        host.sendAll("Player " + user.getName() + " entered the game.");
      }
    }

  }

  public String toString() {
    String result = String.format("CLIENT [%s]: %s", clientSocket.getRemoteSocketAddress()
        .toString(), user.toString());

    if (!isRegistered()) {
      result += " <-- NOT REGISTERED";
    } else if (user.getType() == PlayerType.NONE) {
      result += " <-- NO TYPE SET";
    }

    return result;
  }

  /**
   * Process admin command.
   * 
   * @param command
   *          - the command string
   */
  public void adminCommand(final String command) {
    if (command.startsWith("sendall")) {
     host.sendAll(command.substring("sendall".length()));
    }
  }

  /**
   * Run the server thread.
   */
  public void run() {
    try {

      String response = null;
      while ((response = inputStream.readLine()) != null) {
        processInput(response);
      }

      output.close();
      input.close();
    } catch (IOException error) {

      // in case an exception occured, remove and disconnect the client
      host.removeClient(clientSocket);

    }
  }

  public boolean isConnected() {
    return clientSocket.isConnected() && !clientSocket.isClosed();
  }

  /**
   * Send a message to this user.
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
      outputStream.writeBytes(message);
    } catch (IOException error) {
      error.printStackTrace();
    }
  }

}

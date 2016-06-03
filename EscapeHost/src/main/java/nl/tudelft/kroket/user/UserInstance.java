package nl.tudelft.kroket.user;

import gamestate.GameState;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;

import nl.tudelft.kroket.log.Logger;
import nl.tudelft.kroket.net.protocol.CommandParser;
import nl.tudelft.kroket.net.protocol.Protocol;
import nl.tudelft.kroket.server.EscapeHost;
import nl.tudelft.kroket.user.User.PlayerType;

/**
 * ConnectionThread object to handle interaction with a client.
 * 
 * @author Team Kroket
 *
 */
public class UserInstance implements Runnable {

  /** Singleton reference to logger. */
  static final Logger LOG = Logger.getInstance();

  /** Class simpleName, used as tag for logging. */
  private final String className = this.getClass().getSimpleName();

  // private EscapeHost HOST = EscapeHost.getInstance();

  /** Client's DataInputStream. */
  DataInputStream inputStream = null;

  /** Client's DataOutputStream. */
  DataOutputStream outputStream = null;

  /** The type of player. */
  User.PlayerType type = PlayerType.NONE;

  /** Reference to the socket of the connected client. */
  protected Socket clientSocket = null;

  /**
   * Constructor for ConnectionThread.
   * 
   * @param clientSocket
   *          the socket of the client
   */
  public UserInstance(final Socket clientSocket) {
    this.clientSocket = clientSocket;
  }

  private void setPlayerType(RegisteredUser player, String typeString) {

    if (player == null) {
      return;
    }

    if (typeString.length() == 0) {
      return;
    }

    try {
      type = PlayerType.valueOf(typeString);

      // set the type of the player
      player.setType(type);

      // tell player what his new type is
      player.sendMessage("Your type is now set to " + type.toString());

      LOG.info(className, String.format("Player %s is now set to type %s.", player.getName(),
          player.getType().toString()));

      // if the game isn't ready, let the new player know
      if (!EscapeHost.gameReady()) {
        player.sendMessage(EscapeHost.userCount() + " user(s) connected.");
      }
    } catch (IllegalArgumentException error) {
      player.sendMessage("Invalid client type.");
      LOG.error(className, "Invalid client type.");
    }
  }

  /**
   * Process remote input.
   * 
   * @param input
   *          the remote input
   */
  private void processInput(final String input) {
    Logger.getInstance().info(className, input);

    HashMap<String, String> parsedInput = CommandParser.parseInput(input);
    
    String command = parsedInput.get("command");

    switch (command) {
    case Protocol.COMMAND_TYPE:
      RegisteredUser player = EscapeHost.getUser(clientSocket);

      if (player == null) {
        System.out.println("Player not registered yet.");
      } else {

        String typeString = parsedInput.get("param_0");
        setPlayerType(player, typeString);
      }
      break;
      
    case Protocol.COMMAND_REGISTER:
      if (parsedInput.containsKey("param_0")) {

        String playerName = parsedInput.get("param_0");
        RegisteredUser newPlayer = EscapeHost
            .registerPlayer(clientSocket, outputStream, playerName);

        if (parsedInput.containsKey("param_1")) {
          String playerType = parsedInput.get("param_1");
          setPlayerType(newPlayer, playerType);
        }
      }
      break;
      
    case Protocol.COMMAND_INIT_VR:
    case Protocol.COMMAND_INIT_MOBILE:
    	GameState.getInstance().handleMessage(input, parsedInput);
      break;
      
    case Protocol.COMMAND_ADMIN:
      if (input.length() > Protocol.COMMAND_ADMIN.length() + 1) {
        adminCommand(input.substring(Protocol.COMMAND_ADMIN.length() + 1));
      }
      break;
      
    default:
      LOG.error(className, "Invalid command : " + command);
    }

  }

  /**
   * Process admin command.
   * 
   * @param command
   *          - the command string
   */
  public void adminCommand(final String command) {
    if (command.startsWith("sendall")) {
      EscapeHost.sendAll(command.substring(8));
    }
  }

  /**
   * Run the server thread.
   */
  public void run() {
    try {
      final InputStream input = clientSocket.getInputStream();
      final OutputStream output = clientSocket.getOutputStream();

      inputStream = new DataInputStream(input);
      outputStream = new DataOutputStream(output);

      String response = null;
      while ((response = inputStream.readLine()) != null) {
        processInput(response);
      }

      output.close();
      input.close();
    } catch (IOException error) {

      // in case an exception occured, remove and disconnect the client
      EscapeHost.removePlayer(clientSocket);

    }
  }
}

package nl.tudelft.kroket.server;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map.Entry;

import nl.tudelft.kroket.gamestate.GameState;
import nl.tudelft.kroket.gamestate.states.StateA;
import nl.tudelft.kroket.gamestate.states.StateB;
import nl.tudelft.kroket.gamestate.states.StateC;
import nl.tudelft.kroket.gamestate.states.StateD;
import nl.tudelft.kroket.gamestate.states.StateFinal;
import nl.tudelft.kroket.log.Logger;
import nl.tudelft.kroket.net.protocol.Protocol;
import nl.tudelft.kroket.user.RegisteredUser;
import nl.tudelft.kroket.user.User;
import nl.tudelft.kroket.user.User.PlayerType;

public class GameSession {

  private static GameState currentState;

  static final Logger log = Logger.getInstance();

  private String className = this.getClass().getSimpleName();

  private HashMap<Socket, ClientInstance> clientList;
  private int sessionid;

  private boolean active = false;

  private GameHost host;

  public GameSession(GameHost host, int id) {

    this.host = host;
    this.sessionid = id;

    clientList = new HashMap<Socket, ClientInstance>();

    this.active = false;
  }

  public void startSession() {
    log.info(className, "Starting game...");
    sendAll(Protocol.COMMAND_START);
    active = true;
  }

  public boolean isReady() {
    return countUsers(PlayerType.VIRTUAL) == Settings.REQUIRED_VIRTUAL
        && countUsers(PlayerType.MOBILE) == Settings.REQUIRED_MOBILE;
  }

  /** The count players method. */
  public int countUsers(PlayerType type) {
    int sum = 0;
    for (Entry<Socket, ClientInstance> entry : clientList.entrySet()) {

      User user = entry.getValue().getUser();

      if (user.getType() == type) {

        sum += 1;
      }

    }

    return sum;
  }

  public void sendType(PlayerType type, String message) {
    for (Entry<Socket, ClientInstance> entry : clientList.entrySet()) {

      User user = entry.getValue().getUser();

      if (user != null && user.getType() == type) {
        user.sendMessage(message);
      }
    }
  }

  public void sendMobile(String message) {
    sendType(PlayerType.MOBILE, message);
    Logger.getInstance().info("EscapeHost", "Message sent to virtual user(s)");
  }

  public void sendVirtual(String message) {
    sendType(PlayerType.VIRTUAL, message);
    Logger.getInstance().info("EscapeHost", "Message sent to virtual user(s)");
  }

  private void sendAll(String message) {

    int sum = 0;

    for (Entry<Socket, ClientInstance> entry : clientList.entrySet()) {

      User user = entry.getValue().getUser();
      user.sendMessage(message);

      sum += 1;

    }
    log.info(className, "Message sent to " + sum + " user(s)");
  }

  public void stopSession() {

    active = false;
  }

  public void setState(GameState newState) {
    switchState(currentState, newState);
  }

  private void switchState(GameState oldState, GameState newState) {

    if (oldState == newState) {
      return;
    }

    oldState.stop();

    currentState = newState;

    currentState.setHost(host);
    currentState.start();
  }

  public boolean isActive() {
    return active;
  }

  public void printSession() {

    System.out.printf("---------- session %d --------\r\n", sessionid);
    printUsers();
    System.out.println("---------------------");

  }

  public void printUsers() {

    if (clientList.isEmpty()) {
      System.out.println("No players currently registered.");
    } else {

      System.out.println("Registered players:");
      for (Entry<Socket, ClientInstance> entry : clientList.entrySet()) {
        System.out.println("Registered: \t" + entry.getValue());
      }
    }

  }

  public void addClient(Socket socket, ClientInstance client) {
    clientList.put(socket, client);
  }

  public void removeClient(Socket socket) {
    clientList.remove(socket);
  }

  public void handleMessage(String input, HashMap<String, String> parsedInput) {
    if (parsedInput.get("param_0").startsWith("start")) {
      char character = parsedInput.get("param_0").charAt(6);

      switch (character) {
      case 'A':
        setState(StateA.getInstance());
        break;
      case 'B':
        setState(StateB.getInstance());
        break;
      case 'C':
        setState(StateC.getInstance());
        break;
      case 'D':
        setState(StateD.getInstance());
        break;
      case 'F':
        setState(StateFinal.getInstance());
        break;
      default:
        break;
      }
    }

    currentState.handleInput(input, parsedInput);

  }

}

package nl.tudelft.kroket.server;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import nl.tudelft.kroket.gamestate.GameState;
import nl.tudelft.kroket.gamestate.states.*;
import nl.tudelft.kroket.log.Logger;
import nl.tudelft.kroket.net.protocol.Protocol;
import nl.tudelft.kroket.user.User;
import nl.tudelft.kroket.user.User.PlayerType;

public class GameSession {

  private static GameState currentState;

  static final Logger log = Logger.getInstance();

  private String className = this.getClass().getSimpleName();

  private List<GameState> stateOrder = new ArrayList<GameState>();

  private HashMap<Socket, ClientInstance> clientList;
  private int sessionid;

  private boolean active = false;

  private GameHost host;

  private long timeLimit;

  public GameSession(GameHost host, int id) {

    this.host = host;
    this.sessionid = id;

    // currentState = StateA.getInstance();

    clientList = new HashMap<Socket, ClientInstance>();

    this.active = false;

    stateOrder.add(0, StateA.getInstance());
    stateOrder.add(1, StateB.getInstance());
    stateOrder.add(2, StateC.getInstance());
    stateOrder.add(3, StateD.getInstance());
    stateOrder.add(4, StateFinal.getInstance());

  }

  public void advance() {

    int index = stateOrder.indexOf(currentState);
    index++;
    if (index < stateOrder.size()) {
      setState(stateOrder.get(index));
      log.info(className, "Advancing to " + stateOrder.get(index).getName());
    } else {
      log.error(className, "Cannot advance anymore");
    }
  }

  public void startSession() {
    log.info(className, "Starting game...");
    sendAll(Protocol.COMMAND_START);
    sendAll(String.format("%s[%d]", Protocol.COMMAND_TIMELIMIT, Settings.TIMELIMIT));
    active = true;


    timeLimit = System.currentTimeMillis() + Settings.TIMELIMIT * 1000;

  //  setState(StateA.getInstance());
    setState(stateOrder.get(0));

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
    log.info("EscapeHost", "Message sent to virtual user(s)");
  }

  public void sendVirtual(String message) {
    sendType(PlayerType.VIRTUAL, message);
    log.info("EscapeHost", "Message sent to virtual user(s)");
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
    log.info(className, "Setting GameState to " + newState.getClass().getSimpleName());

    switchState(currentState, newState);
  }

  private void switchState(GameState oldState, GameState newState) {
    if (oldState == newState) {
      log.debug(className, "Not switching state, already in state "
          + newState.getClass().getSimpleName());
      return;
    }

    currentState = newState;

    currentState.setHost(host);
    currentState.setSession(this);
  }

  public boolean isActive() {
    return active;
  }

  public void printSession() {
    System.out.printf("-- session %d --\r\n", sessionid);
    printUsers();
    System.out.println("----------------");
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
    if (clientList.containsKey(socket))
      clientList.remove(socket);
  }

  public void handleMessage(String input, HashMap<String, String> parsedInput) {
    log.debug(className, "handleMessage: " + input);

    if (currentState == null) {
      log.error(className, "currentState == null");
    } else {
      currentState.handleInput(input, parsedInput);
    }
  }

  public void update() {

    if (isActive()) {

      long timeRemaining = Math.max(timeLimit - System.currentTimeMillis(), 0);

      // TODO: stop the game if timeRemaining reaches value <= 0

      System.out.println("timeRemaining = " + timeRemaining);

      if (timeRemaining <= 0) {
        sendAll(String.format("%s", Protocol.COMMAND_GAMEOVER));
      }

    }

  }

}

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

/**
 * Class that handles the current game session.
 * 
 * @author Team Kroket
 */
public class GameSession {

  /** The current game state. */
  private static GameState currentState;

  /** The logger instance. */
  static final Logger log = Logger.getInstance();

  /** The name of this class. */
  private String className = this.getClass().getSimpleName();

  /** List which defines the state order. */
  private List<GameState> stateOrder = new ArrayList<GameState>();

  /** List with all connected clients. */
  private HashMap<Socket, ClientInstance> clientList;
  
  /** The session identifier. */
  private int sessionid;

  /** Boolean to check if this game session is active. */
  private boolean active = false;

  /** The host of the game. */
  private GameHost host;

  /**
   * Constructor for GameSession.
   * 
   * @param host the host of the session.
   * @param id the id of the session.
   */
  public GameSession(GameHost host, int id) {
    this.host = host;
    this.sessionid = id;

    clientList = new HashMap<Socket, ClientInstance>();

    this.active = false;

    stateOrder.add(0, StateA.getInstance());
    stateOrder.add(1, StateB.getInstance());
    stateOrder.add(2, StateC.getInstance());
    stateOrder.add(3, StateD.getInstance());
    stateOrder.add(4, StateFinal.getInstance());
    
    resetStates();
  }
  
  /**
   * Creates new states for the new session.
   */
  private void resetStates() {
	for (GameState state : stateOrder) {
		state.newState();
	}
  }

  /**
   * Advances the session to the next game state.
   */
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

  /**
   * Starts the current session by sending START to all clients.
   */
  public void startSession() {
    log.info(className, "Starting game...");
    sendAll(Protocol.COMMAND_START);
    active = true;
    setState(stateOrder.get(0));
  }

  /**
   * Checks whether the session is ready to be started.
   * 
   * @return true iff the right amount of players are connected.
   */
  public boolean isReady() {
    return countUsers(PlayerType.VIRTUAL) == Settings.REQUIRED_VIRTUAL
        && countUsers(PlayerType.MOBILE) == Settings.REQUIRED_MOBILE;
  }

  /**
   * Counts the amount of connected clients of a type.
   * @param type the type that is checked.
   * @return the sum of connected clients of that type.
   */
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

  /**
   * Send a message to a specific type of client.
   * 
   * @param type the type of the client.
   * @param message the message to be sent.
   */
  public void sendType(PlayerType type, String message) {
    for (Entry<Socket, ClientInstance> entry : clientList.entrySet()) {

      User user = entry.getValue().getUser();

      if (user != null && user.getType() == type) {
        user.sendMessage(message);
      }
    }
  }

  /**
   * Sends a message to only the mobile clients.
   * 
   * @param message the message to be sent.
   */
  public void sendMobile(String message) {
    sendType(PlayerType.MOBILE, message);
    log.info("EscapeHost", "Message sent to mobile user(s)");
  }

  /**
   * Sends a message to only the virtual client.
   * 
   * @param message the message to be sent.
   */
  public void sendVirtual(String message) {
    sendType(PlayerType.VIRTUAL, message);
    log.info("EscapeHost", "Message sent to virtual user(s)");
  }

  /**
   * Sends a message to all connected clients.
   * 
   * @param message the message to be sent.
   */
  protected void sendAll(String message) {
    int sum = 0;

    for (Entry<Socket, ClientInstance> entry : clientList.entrySet()) {

      User user = entry.getValue().getUser();
      user.sendMessage(message);

      sum += 1;
    }
    log.info(className, "Message sent to " + sum + " user(s)");
  }

  /**
   * Stop the current session.
   */
  public void stopSession() {
    active = false;
  }
  
  /**
   * Create a new session.
   */
  public void newSession() {
	sessionid++;
	host.newSession();
	host.startSession();
  }

  /**
   * Setter for the gamestate.
   * 
   * @param newState the state to be set.
   */
  public void setState(GameState newState) {
    log.info(className, "Setting GameState to " + newState.getClass().getSimpleName());

    switchState(currentState, newState);
  }

  /**
   * Switch between states.
   * 
   * @param oldState state you are currently in.
   * @param newState state you want to switch to.
   */
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

  /**
   * Checks whether the currect session is active.
   * 
   * @return true iff the session is active.
   */
  public boolean isActive() {
    return active;
  }

  /**
   * Prints the current session.
   */
  public void printSession() {
    System.out.printf("-- session %d --\r\n", sessionid);
    printUsers();
    System.out.println("----------------");
  }

  /**
   * Prints the list of connected clients.
   */
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

  /**
   * Add a client to the clientlist.
   * 
   * @param socket the socket that should be added.
   * @param client the clientinstance that belongs to the socket.
   */
  public void addClient(Socket socket, ClientInstance client) {
    clientList.put(socket, client);
  }

  /**
   * Remove a client from the clientlist.
   * 
   * @param socket the socket that should be removed.
   */
  public void removeClient(Socket socket) {
    if (clientList.containsKey(socket))
      clientList.remove(socket);
  }

  /**
   * Handle input from the clients.
   * 
   * @param input the input received.
   * @param parsedInput the parsed input.
   */
  public void handleMessage(String input, HashMap<String, String> parsedInput) {
    log.debug(className, "handleMessage: " + input);

    if (currentState == null) {
      log.error(className, "currentState == null");
    } else {
      currentState.handleInput(input, parsedInput);
    }
  }
  
  /**
   * Forwards to all players that bonus time should be received.
   */
  public void bonusTime() {
	sendAll(String.format("%s", Protocol.COMMAND_BONUSTIME));
  }
  
  /**
   * Sends the gameover message to all clients.
   */
  public void gameOver() {
	log.info(className, "Game over!");
	//stopSession();
	sendAll(String.format("%s", Protocol.COMMAND_GAMEOVER));
	newSession();
  }
  
  /**
   * Gets the current state.
   * @return a GameState object that is the current state
   */
  public GameState getCurrentState() {
    return currentState;
  }

  /**
   * gets the clientList.
   * @return the hashmap with sockets and clientinstances
   */
  public HashMap<Socket, ClientInstance> getClientList() {
    return clientList;
  }
  
}

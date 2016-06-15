package nl.tudelft.kroket.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import nl.tudelft.kroket.log.Logger;
import nl.tudelft.kroket.user.User;
import nl.tudelft.kroket.user.User.PlayerType;

/**
 * Class that hosts the game.
 * 
 * @author Team Kroket
 */
public class GameHost implements Runnable {

  /** List of clients connected to the server. */
  public static HashMap<Socket, ClientInstance> clientList = new HashMap<Socket, ClientInstance>();

  /** List of game sessions. */
  private List<GameSession> sessions = new ArrayList<GameSession>();

  /** Value to keep track whether socket was initialized. */
  private static boolean initialized;
  
  /** Class name, used as tag for logging. */
  private static String className = "GameHost";

  /** The serverport int. */
  protected int serverPort;

  /** The logger. */
  static Logger log = Logger.getInstance();

  /** the Server socket. */
  protected ServerSocket serverSocket = null;

  /** Is Stopped boolean. */
  protected boolean isStopped = false;

  /** The running Thread. */
  protected Thread runningThread = null;

  /** Id of the current session. */
  private int currentSessionId;

  /** The thread pool. */
  protected ExecutorService threadPool = Executors.newFixedThreadPool(Settings.THREAD_POOL_MAX);

  /**
   * Constructor for the GameHost.
   * @param port the portnumber used.
   */
  public GameHost(int port) {
    serverPort = port;

    currentSessionId = 0;

    sessions.add(currentSessionId, new GameSession(this, currentSessionId));
  }
  
  /**
   * Called upon update. Starts a session.
   */
  public void update() {
    startSession();    
  }

  /**
   * Getter for the current game session.
   * 
   * @return the current game session.
   */
  public GameSession getCurrentSession() {
    return sessions.get(currentSessionId);
  }

  /**
   * Remove a player based on its socket.
   * 
   * @param clientSocket
   *          the client's socket
   */
  public void removeClient(Socket clientSocket) {

    ClientInstance client = clientList.get(clientSocket);
    clientList.remove(clientSocket);

    getCurrentSession().removeClient(clientSocket);

    if (!clientSocket.isClosed()) {
      try {
        clientSocket.close();
      } catch (IOException error) {
        error.printStackTrace();
      }
    }

    if (client != null) {
      log.info(className, String.format("Player %s disconnected.", client.getUser().getName()));
      sendAll(String.format("Player %s has left the game.", client.getUser().getName()));
    }
    log.info(className, "Socket " + clientSocket.getRemoteSocketAddress().toString() + " removed.");
  }

  /**
   * The run class.
   */
  public void run() {
    synchronized (this) {
      runningThread = Thread.currentThread();
    }

    initialized = false;

    while (!initialized) {

      log.info(className, "Creating socket...");

      try {
        serverSocket = new ServerSocket(serverPort);
        break;
      } catch (IOException exception) {
        log.error(className, "Error: " + exception);
        initialized = false;
      }
      try {
        Thread.sleep(Settings.INTERVAL_SOCKET_RETRY * 1000);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

    }

    if (serverSocket == null) {
      initialized = false;
    } else {

      initialized = true;

      // keep listening for incoming connections
      // until we manually terminate the server or
      // some error (we cannot recover from) occurs
      while (!isStopped()) {
        Socket clientSocket = null;
        try {
          log.info(className,
              String.format("Listening for incoming connections on port %d...", serverPort));
          clientSocket = serverSocket.accept();

          log.info(className, "Connection accepted. Incoming connection from "
              + clientSocket.getRemoteSocketAddress().toString());

        } catch (IOException error) {
          error.printStackTrace();
        }

        // hand the client's socket to a new thread in the pool and
        // start the thread
        ClientInstance instance = new ClientInstance(this, clientSocket);

        clientList.put(clientSocket, instance);

        getCurrentSession().addClient(clientSocket, instance);

        threadPool.execute(instance);

        printStatus();

      }

      for (GameSession session : sessions) {
        session.stopSession();
      }

      for (Entry<Socket, ClientInstance> entry : clientList.entrySet()) {

        Socket socket = entry.getKey();
        ClientInstance user = entry.getValue();

        user.sendMessage("Server is closing. Goodbye. :)");
        try {
          socket.close();
        } catch (IOException e) {
          e.printStackTrace();
        }

      }

      stop();
      threadPool.shutdown();
    }
  }

  /**
   * Starts the current game session. 
   */
  public void startSession() {
    GameSession session = getCurrentSession();

    if (!session.isReady()) {
      // System.out.println("Cannot start session: not ready");
    } else if (!session.isActive()) {

      System.out.println("Starting current session...");

      session.startSession();

      // currentSessionId += 1;
    }

  }

  /**
   * Stops the current game session.
   */
  public void stopSession() {
    GameSession session = getCurrentSession();

    if (session.isActive()) {
      session.stopSession();

      currentSessionId += 1;

      sessions.add(currentSessionId, new GameSession(this, currentSessionId));
    }
  }

  /**
   * Getter for the list of game sessions.
   * 
   * @return the list of sessions.
   */
  public List<GameSession> getSessions() {
    return sessions;
  }

  /**
   * Getter for the initialized boolean.
   * 
   * @return true iff the session is initialized.
   */
  public boolean isInitialized() {
    return initialized;
  }

  /**
   * Getter for the isStopped boolean.
   * 
   * @return true iff the session is stopped.
   */
  private synchronized boolean isStopped() {
    return isStopped;
  }

  /**
   * Synchronized method for stopping a session.
   */
  private synchronized void stop() {
    isStopped = true;
    try {
      serverSocket.close();
    } catch (IOException error) {
      throw new RuntimeException("Error closing server", error);
    }
  }

  /**
   * Counts the players in a session of a specific type.
   * 
   * @param type the type of the players counted.
   * @return the amount of players of that type.
   */
  public static int countUsers(PlayerType type) {
    int sum = 0;
    for (Entry<Socket, ClientInstance> entry : clientList.entrySet()) {

      if (entry.getValue().getUser().getType() == type) {
        sum += 1;
      }
    }

    return sum;
  }

  /**
   * Count the number of players present in the server.
   * 
   * @return the number of players present
   */
  public static int userCount() {

    return clientList.size();
  }

  public static int clientCount() {

    return clientList.size();
  }

  /**
   * Check whether the server is ready to start a game.
   * 
   * @return true when game is ready
   */
  public static boolean gameReady() {
    return countUsers(PlayerType.VIRTUAL) == Settings.REQUIRED_VIRTUAL
        && countUsers(PlayerType.MOBILE) == Settings.REQUIRED_MOBILE;
  }

  /**
   * Get a player by socket.
   * 
   * @param socket
   *          the socket
   * @return Player object
   */
  public static ClientInstance getUser(Socket socket) {
    return clientList.get(socket);
  }

  /**
   * Get a player by name.
   * 
   * @param playername
   *          the name of the player
   * @return the player object
   */
  public User getUser(String playername) {
    for (Entry<Socket, ClientInstance> entry : clientList.entrySet()) {

      // player was found
      if (playername.equals(entry.getValue().getUser().getName())) {

        return entry.getValue().getUser();
      }
    }

    // no matches, return null
    return null;
  }

  /**
   * Send a message to all connected players. This will omit users without a type.
   * 
   * @param message
   *          the message to be sent
   * @return
   */
  public void sendAll(String message) {
    for (Entry<Socket, ClientInstance> entry : clientList.entrySet()) {

      if (entry.getValue() != null) {
        entry.getValue().sendMessage(message);
      }
    }
    log.info("EscapeHost", "Sending message: " + message);
    log.info("EscapeHost", "Message sent to " + clientList.size() + " user(s)");
  }

  /**
   * Sends a message to all clients of a specific type.
   * 
   * @param type the type of the clients the message should be sent to.
   * @param message the message to be sent.
   */
  public static void sendType(PlayerType type, String message) {
    for (Entry<Socket, ClientInstance> entry : clientList.entrySet()) {

      User user = entry.getValue().getUser();

      if (user.getType() == type) {
        entry.getValue().sendMessage(message);
      }
    }
    log.info("EscapeHost", "Sending message: " + message);
    log.info("EscapeHost", "Message sent to type: " + type.toString());
  }

  /**
   * Send a message to all connected mobile clients.
   * 
   * @param message
   *          the string to be sent
   */
  public void sendMobile(String message) {
    sendType(PlayerType.MOBILE, message);
  }

  /**
   * Send a message to all connected virtual clients.
   * 
   * @param message
   *          the string to be sent
   */
  public void sendVirtual(String message) {
    sendType(PlayerType.VIRTUAL, message);
  }

  /**
   * Print the list of connected clients.
   */
  private void printClients() {
    if (clientList.isEmpty()) {
      System.out.println("No clients connected.");
      return;
    }

    System.out.println("Connected clients:");
    for (Entry<Socket, ClientInstance> entry : clientList.entrySet()) {

      String line = entry.getValue().toString();

      System.out.println(line);
    }
  }

  /**
   * Print information about all the sessions.
   */
  public void printSessions() {
    for (GameSession session : getSessions()) {
      session.printSession();

      if (session.isReady()) {
        System.out.println("Game session is ready.");
      } else {
        System.out.println("Game session is not ready.");
      }
    }
  }

  /**
   * Print the status of all sessions.
   */
  public void printStatus() {
    printClients();
    printSessions();
  }
  
  /**
   * Getter for the list of clients.
   * 
   * @return the list of clients.
   */
  public static HashMap<Socket, ClientInstance> getClientList() {
    return clientList;
  }

  /**
   * Getter for the class name.
   * 
   * @return the name of this class.
   */
  public static String getClassName() {
    return className;
  }

  /**
   * Getter for the number of the server port.
   * 
   * @return the server port number.
   */
  public int getServerPort() {
    return serverPort;
  }

  /**
   * Getter for the current session id.
   * 
   * @return the id of the current session.
   */
  public int getCurrentSessionId() {
    return currentSessionId;
  }
  
  /**
   * Getter for the isStopped boolean.
   * 
   * @return true iff the session is stopped.
   */
  public boolean getStopped() {
    return isStopped;
  }

  /**
   * Checks whether two sessions are equal. 
   * 
   * @return true iff the sessions are equal.
   */
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof GameHost) {
      GameHost that = (GameHost)obj;
      return (this.isStopped == that.getStopped() && this.getServerPort() == that.getServerPort()
          && this.getClientList().keySet().containsAll(that.getClientList().keySet())
              && this.clientList.values().contains(that.getClientList().values()));
    }
    return false;
  }

}
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

public class GameHost implements Runnable {

  /** List of clients connected to the server. */
  public static HashMap<Socket, ClientInstance> clientList = new HashMap<Socket, ClientInstance>();

  private List<GameSession> sessions = new ArrayList<GameSession>();

  /** Value to keep track whether socket was initialized. */
  private static boolean initialized;

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

  private int currentSessionId;

  /** The thread pool. */
  protected ExecutorService threadPool = Executors.newFixedThreadPool(Settings.THREAD_POOL_MAX);

  public GameHost(int port) {
    serverPort = port;

    currentSessionId = 0;

    sessions.add(currentSessionId, new GameSession(this, currentSessionId));
  }

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

  public void stopSession() {
    GameSession session = getCurrentSession();

    if (session.isActive()) {
      session.stopSession();

      currentSessionId += 1;

      sessions.add(currentSessionId, new GameSession(this, currentSessionId));
    }
  }

  public List<GameSession> getSessions() {

    return sessions;
  }

  public boolean isInitialized() {
    return initialized;
  }

  private synchronized boolean isStopped() {
    return isStopped;
  }

  /** The stop method. */
  private synchronized void stop() {
    isStopped = true;
    try {
      serverSocket.close();
    } catch (IOException error) {
      throw new RuntimeException("Error closing server", error);
    }
  }

  /** The count players method. */
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

  public void sendVirtual(String message) {
    sendType(PlayerType.VIRTUAL, message);
  }

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

  public void printStatus() {
    printClients();
    printSessions();
  }
  
  public static HashMap<Socket, ClientInstance> getClientList() {
    return clientList;
  }

  public static String getClassName() {
    return className;
  }

  public int getServerPort() {
    return serverPort;
  }

  public int getCurrentSessionId() {
    return currentSessionId;
  }
  
  public boolean getStopped() {
    return isStopped;
  }

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
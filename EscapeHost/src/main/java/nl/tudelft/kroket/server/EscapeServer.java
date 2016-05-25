package nl.tudelft.kroket.server;

import java.io.DataOutputStream;
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
import nl.tudelft.kroket.player.Player;
import nl.tudelft.kroket.player.Player.PlayerType;

/**
 * EscapeServer object.
 * 
 * @author Team Kroket
 *
 */
public class EscapeServer implements Runnable {

	/** List of players connected to the server. */
	public static HashMap<Socket, Player> playerList = new HashMap<Socket, Player>();

	/** Value to keep track whether socket was initialized. */
	private static boolean initialized;

	/** The class name string. */
	private static final String CLASSNAME = "EscapeServer";

	/** The game activity boolean. */
	private static boolean gameActive = false;

	/** The serverport int. */
	protected int serverPort;

	/** The client list array. */
	List<PlayerInstance> clientList = new ArrayList<PlayerInstance>();

	/** The logger. */
	static final Logger LOG = Logger.getInstance();

	/** the Server socket. */
	protected ServerSocket serverSocket = null;

	/** Is Stopped boolean. */
	protected boolean isStopped = false;

	/** The running Thread. */
	protected Thread runningThread = null;

	/** The thread pool. */
	protected ExecutorService threadPool = Executors.newFixedThreadPool(10);

	/**
	 * The main method of the game.
	 * 
	 * @param args
	 *            - String[]
	 */
	public static void main(String[] args) {

		LOG.info(CLASSNAME, "EscapeServer by Team Kroket");
		LOG.info(CLASSNAME,
				String.format("Listening port is set to %d", Settings.PORTNUM));
		LOG.info(CLASSNAME, "Initializing...");
		initialized = false;

		// long startTime = System.currentTimeMillis();

		final EscapeServer server = new EscapeServer(Settings.PORTNUM);

		while (!initialized) {

			// spawn this server in a new thread
			new Thread(server).start();

			if (initialized) {
				break;
			}

			try {
				Thread.sleep(Settings.INTERVAL_SOCKET_RETRY * 1000);
			} catch (InterruptedException error) {
				error.printStackTrace();
			}
		}

		int tickCounter = 0;

		if (initialized) {

			boolean breakLoop = false;

			while (!breakLoop) {

				LOG.info(CLASSNAME,
						"Game not ready. Waiting for players to register...");

				while (!ready()) {

					// long duration = System.currentTimeMillis() - startTime;

					tickCounter += 1;

					if (tickCounter % Settings.INTERVAL_REPORT_STATUS == 0) {

						LOG.info(CLASSNAME, "Game not ready.");

						int count = countPlayers(PlayerType.MOBILE);

						String message = String.format(
								"%d mobile players connected.", count);

						sendAll(message);

						printPlayers();
					}

					try {
						Thread.sleep(1000);
					} catch (InterruptedException error) {
						// TODO Auto-generated catch block
						error.printStackTrace();
					}
				}

				LOG.info(CLASSNAME, "Server is ready to host game.");

				startGame();

				gameActive = true;

				while (gameActive && ready()) {
					tickCounter += 1;

					if (tickCounter % Settings.INTERVAL_REPORT_STATUS == 0) {

						LOG.info(CLASSNAME, "Game is active.");
						printPlayers();

					}

					try {
						Thread.sleep(1000);
					} catch (InterruptedException error) {
						error.printStackTrace();
					}
				}

				gameActive = false;

				LOG.info(CLASSNAME, "Game session has ended.");

			}

		} else {
			LOG.error(CLASSNAME, "Initialization failed. Is the port in use?");
		}

		LOG.info(CLASSNAME, "Exiting...");
	}

	/**
	 * Start the game.
	 */
	private static void startGame() {

		LOG.info(CLASSNAME, "Starting game...");
		sendAll("Starting game...");
		sendAll("START");

	}

	/**
	 * Setter for the server port.
	 * 
	 * @param port
	 *            - int
	 */
	public EscapeServer(final int port) {
		serverPort = port;
	}

	/**
	 * Remove a player based on its socket.
	 * 
	 * @param clientSocket
	 *            the client's socket
	 */
	public static void removePlayer(final Socket clientSocket) {

		final Player player = playerList.get(clientSocket);

		playerList.remove(clientSocket);

		if (!clientSocket.isClosed()) {
			try {
				clientSocket.close();
			} catch (IOException error) {
				error.printStackTrace();
			}
		}

		if (player != null) {
			LOG.info(CLASSNAME,
					String.format("Player %s disconnected.", player.getName()));
			sendAll(String.format("Player %s has left the game.",
					player.getName()));
		}
		LOG.info(CLASSNAME, "Socket "
				+ clientSocket.getRemoteSocketAddress().toString()
				+ " removed.");
	}

	/**
	 * Register a player to the server.
	 * 
	 * @param clientSocket
	 *            - the client's socket
	 * @param dOutput
	 *            - the dataoutputstream
	 * @param playername
	 *            - the player's name
	 */
	public static Player registerPlayer(final Socket clientSocket,
			final DataOutputStream dOutput, final String playername) {
		if (playername.length() > 0) {

			if (getPlayer(clientSocket) == null) {

				// make sure chosen name is still available
				if (getPlayer(playername) == null) {

					// create a new player object
					final Player player = new Player(clientSocket, dOutput,
							playername);

					// put new player object in playerList
					// key should already exist
					playerList.put(clientSocket, player);

					// tell player that he registered succesfully
					player.sendMessage("You are now registered as "
							+ player.toString());

					if (!ready()) {
						player.sendMessage(countPlayers()
								+ " players waiting...");
					}

					// let everyone know a new player registered with the server
					sendAll("Player " + player.getName() + " entered the game.");

					return player;
				} else {

					// player's name was not available, let player know

					LOG.info(CLASSNAME, "Player " + playername
							+ " already registered");

					try {
						dOutput.writeBytes("Name already in use.\r\n");
					} catch (IOException error) {
						error.printStackTrace();
						try {
							clientSocket.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			} else {
				LOG.info(CLASSNAME, "Socket already registered.");
				try {
					dOutput.writeBytes("You are already registered.\r\n");
				} catch (IOException error) {
					error.printStackTrace();

				}

			}
		}
		return null;
	}

	/**
	 * The run class.
	 */
	public void run() {
		synchronized (this) {
			runningThread = Thread.currentThread();
		}

		LOG.info(CLASSNAME, "Creating socket...");

		try {
			serverSocket = new ServerSocket(serverPort);
		} catch (IOException exception) {
			LOG.error(CLASSNAME, "Error: " + exception);
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
					LOG.info(CLASSNAME, String.format(
							"Listening for incoming connections on port %d...",
							serverPort));
					clientSocket = serverSocket.accept();

					LOG.info(CLASSNAME,
							"Connection accepted. Incoming connection from "
									+ clientSocket.getRemoteSocketAddress()
											.toString());

					// prepare an entry in the playerlist
					// we might set the value later
					// use client's socket as key
					playerList.put(clientSocket, null);

				} catch (IOException error) {
					error.printStackTrace();
				}

				// hand the client's socket to a new thread in the pool and
				// start the thread
				threadPool.execute(new PlayerInstance(clientSocket));

				// as we just accepted a new connection, immediately print
				// status
				printPlayers();
			}

			// ...

			threadPool.shutdown();
		}
	}

	private synchronized boolean isStopped() {
		return isStopped;
	}

	/** The stop method. */
	public synchronized void stop() {
		isStopped = true;
		try {
			serverSocket.close();
		} catch (IOException error) {
			throw new RuntimeException("Error closing server", error);
		}
	}

	/** The count players method. */
	public static int countPlayers(PlayerType type) {
		int sum = 0;
		for (Entry<Socket, Player> entry : playerList.entrySet()) {

			// if no Player object, skip
			if (entry.getValue() == null) {
				continue;
			}

			if (entry.getValue().getType() == type) {
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
	public static int countPlayers() {
		int sum = 0;
		for (Entry<Socket, Player> entry : playerList.entrySet()) {

			if (entry.getValue() == null) {
				continue;
			}

			sum += 1;
		}

		return sum;

	}

	/**
	 * Check whether the server is ready to start a game.
	 * 
	 * @return true when game is ready
	 */
	public static boolean ready() {
		return countPlayers(PlayerType.VIRTUAL) == Settings.REQUIRED_VIRTUAL
				&& countPlayers(PlayerType.MOBILE) == Settings.REQUIRED_MOBILE;
	}

	/**
	 * Get a player by socket.
	 * 
	 * @param socket
	 *            the socket
	 * @return Player object
	 */
	public static Player getPlayer(final Socket socket) {
		return playerList.get(socket);
	}

	/**
	 * Get a player by name.
	 * 
	 * @param playername
	 *            the name of the player
	 * @return the player object
	 */
	public static Player getPlayer(final String playername) {
		for (Entry<Socket, Player> entry : playerList.entrySet()) {

			// if entry contains no Player object, ignore
			if (entry.getValue() == null) {
				continue;
			}

			// player was found
			if (playername.equals(entry.getValue().getName())) {

				return entry.getValue();
			}
		}

		// no matches, return null
		return null;
	}

	/**
	 * Send a message to all connected players. This will omit users without a
	 * type.
	 * 
	 * @param message
	 *            the message to be sent
	 */
	public static void sendAll(final String message) {
		for (Entry<Socket, Player> entry : playerList.entrySet()) {

			if (entry.getValue() != null) {
				entry.getValue().sendMessage(message);
			}
		}

	}

	/**
	 * Send a message to all connected mobile clients.
	 * 
	 * @param message
	 *            the string to be sent
	 */
	public static void sendMobile(final String message) {
		for (Entry<Socket, Player> entry : playerList.entrySet()) {

			if (entry.getValue() == null)
				continue;
			if (entry.getValue().getType() == PlayerType.MOBILE) {
				entry.getValue().sendMessage(message);
			}
		}
	}

	/**
	 * Send a message to all connected virtual clients.
	 * 
	 * @param message
	 *            the string to be sent
	 */
	public static void sendVirtual(final String message) {
		for (Entry<Socket, Player> entry : playerList.entrySet()) {

			if (entry.getValue() == null)
				continue;
			if (entry.getValue().getType() == PlayerType.VIRTUAL) {
				entry.getValue().sendMessage(message);
			}
		}
	}

	/**
	 * Print all players, registered and unregistered.
	 */
	private static void printPlayers() {

		if (playerList.isEmpty()) {
			System.out.println("No players currently registered.");
		} else {

			System.out.println("Registered players:");

			for (Entry<Socket, Player> entry : playerList.entrySet()) {

				if (entry.getValue() == null) {
					System.out.println("Unregistered: \t"
							+ entry.getKey().getRemoteSocketAddress()
									.toString());
				} else {
					System.out.println("Registered: \t" + entry.getValue());
				}
			}
		}
	}

}
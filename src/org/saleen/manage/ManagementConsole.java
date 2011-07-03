package org.saleen.manage;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.saleen.event.impl.PlayerLoginEvent.PlayerLogin;
import org.saleen.event.impl.PlayerLoginEvent.PlayerLogout;
import org.saleen.rs2.model.World;
import org.saleen.rs2.net.ThroughputCounter;
import org.saleen.util.Filter;
import org.saleen.util.configuration.ConfigurationNode;
import org.saleen.util.configuration.ConfigurationParser;

/**
 * Represents a remote management console, which can be used to connect to your
 * vps/dedicated server and easily receive all console output and also send
 * commands back to the server.
 * 
 * This is based off a text-line protocol, which is easily implemented using
 * existing codecs
 * 
 * @author Nikki
 * 
 */
public class ManagementConsole {

	/**
	 * The session map
	 */
	private Map<Channel, ManagementSession> sessions = new HashMap<Channel, ManagementSession>();

	/**
	 * Create a new instance on class load
	 */
	private static ManagementConsole instance = new ManagementConsole();

	/**
	 * The <code>IoAcceptor</code> instance.
	 */
	private ServerBootstrap consoleBootstrap;

	/**
	 * The user map
	 */
	private static HashMap<String, String> users = new HashMap<String, String>();

	/**
	 * Setup the management console
	 */
	@SuppressWarnings("unchecked")
	public static void setup() {
		try {
			ConfigurationParser parser = new ConfigurationParser(
					new FileInputStream("conf/management.conf"));
			ConfigurationNode main = parser.parse();

			ConfigurationNode management = main.nodeFor("management");
			if (management.getBoolean("enabled")) {
				ManagementConsole console = ManagementConsole.getInstance();
				console.init();
				console.bind(management.getInteger("port"));

				// Load users!
				ConfigurationNode userNode = main.nodeFor("users");
				for (Map.Entry<String, Object> entry : userNode.getChildren()
						.entrySet()) {
					String username = entry.getKey();
					ConfigurationNode subUserNode = (ConfigurationNode) entry
							.getValue();

					users.put(username, subUserNode.getString("password"));
				}
				PlayerEventConsumer consumer = new PlayerEventConsumer(console);
				consumer.bind(PlayerLogin.class, PlayerLogout.class);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create a new instance
	 */
	public ManagementConsole() {
		addInfoEvent();
	}

	/**
	 * Add an info task to the timer
	 */
	public void addInfoEvent() {
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					try {
						refreshInfo();
						Thread.sleep(2500);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	/**
	 * Setup the acceptor
	 */
	public void init() {
		consoleBootstrap = new ServerBootstrap(
				new NioServerSocketChannelFactory(
						Executors.newSingleThreadExecutor(),
						Executors.newSingleThreadExecutor()));
		consoleBootstrap.setPipelineFactory(new ManagementPipelineFactory());
	}

	/**
	 * Bind the acceptor to a port
	 * 
	 * @param port
	 *            The port to bind to
	 * @throws IOException
	 *             If the acceptor failed to bind
	 */
	public void bind(int port) throws IOException {
		consoleBootstrap.bind(new InetSocketAddress(port));
	}

	/**
	 * Print a line to all sessions
	 */
	public void printMessage(String string) {
		JSONObject object = new JSONObject();
		try {
			object.put("message", string);
			String message = object.toString();
			for (Channel channel : sessions.keySet()) {
				channel.write(message);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get the management instance
	 * 
	 * @return The instance of this class
	 */
	public static ManagementConsole getInstance() {
		return instance;
	}

	/**
	 * Handle a line from the session
	 * 
	 * @param arg0
	 *            The session
	 * @param line
	 *            The line to handle
	 */
	public void handle(MessageEvent e) {
		String line = (String) e.getMessage();
		ManagementSession session = sessions.get(e.getChannel());
		if (!session.isAuthenticated()) {
			if (line.startsWith("AUTH")) {
				String[] userData = line.split(" ");
				String username = userData[1];
				String password = userData[2];
				if (checkLogin(username, password)) {
				}
				Logger.getAnonymousLogger().info(
						username + " has connected to the management console");
				session.setAuthenticated(true);
			}
		} else {
			String channel = line.substring(0, line.indexOf(" "));
			String data = line.substring(line.indexOf(" ") + 1);
			// Likely JSON
			if (data.charAt(0) == '{') {
				try {
					JSONObject object = new JSONObject(data);
					if (channel.equals("console")) {
						World.getWorld().managementCommand(
								object.getString("command"),
								object.getJSONObject("params"));
					} else if (channel.equals("admin")) {
						String action = object.getString("action");
						if (action.equals("ban")) {

						} else if (action.equals("pardon")) {

						}
					}
				} catch (JSONException ex) {
					ex.printStackTrace();
				}
			} else {
				if (channel.equals("subscribe")) {
					session.subscribe(data);
				}
			}
		}
	}

	/**
	 * Check a login, TODO implement ip checking??
	 * 
	 * @param username
	 *            The username
	 * @param password
	 *            The password
	 * @return True, if valid
	 */
	private boolean checkLogin(String username, String password) {
		if (!users.containsKey(username)) {
			return false;
		} else if (users.containsKey(username)) {
			return users.get(username).equals(password);
		}
		return false;
	}

	/**
	 * Write data to sessions using the specified filter
	 * 
	 * @param data
	 *            The data
	 * @param filter
	 *            The filter
	 */
	public void write(String data, Filter<ManagementSession> filter) {
		char c = data.charAt(data.length() - 1);
		if (c != '\n') {
			data += '\n';
		}
		for (ManagementSession session : sessions.values()) {
			if (filter.accept(session)) {
				session.getChannel().write(data);
			}
		}
	}

	/**
	 * Write an unformatted log record to the channels
	 * 
	 * @param object
	 *            The JSON Object to format
	 * @param filter
	 *            The filter
	 */
	public void logRecord(JSONObject object, Filter<ManagementSession> filter) {
		write(ManagementChannels.construct(ManagementChannels.CONSOLE, object),
				filter);
	}

	/**
	 * Log a record by calling
	 * <code>logRecord(org.json.JSONObject, org.saleen.util.Filter)</code> With
	 * a filter created to check subscribed channels
	 * 
	 * @param object
	 */
	public void logRecord(JSONObject object) {
		logRecord(object, new Filter<ManagementSession>() {
			@Override
			public boolean accept(ManagementSession t) {
				return t.isSubscribedTo(ManagementChannels.CONSOLE);
			}
		});
	}

	/**
	 * Add a session to the list
	 * 
	 * @param session
	 *            The session to add
	 */
	public void addSession(ManagementSession session) {
		sessions.put(session.getChannel(), session);
	}

	/**
	 * Remove a session from the list
	 * 
	 * @param channel
	 *            The session to remove
	 */
	public void removeSession(Channel channel) {
		sessions.remove(channel);
	}

	public void refreshInfo() {
		try {
			write(generateInfo(), new Filter<ManagementSession>() {
				@Override
				public boolean accept(ManagementSession t) {
					return t.isSubscribedTo("server");
				}
			});
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Send server information to the connected sessions
	 */
	public String generateInfo() throws JSONException {
		JSONObject object = new JSONObject();

		// VM Info
		Runtime runtime = Runtime.getRuntime();
		JSONObject memory = new JSONObject();
		memory.put("free", runtime.freeMemory());
		memory.put("total", runtime.totalMemory());
		memory.put("max", runtime.maxMemory());
		// Add the VM Info to the main object
		object.put("memory", memory);

		// Network info
		JSONObject network = new JSONObject();
		network.put("sent", ThroughputCounter.getWrittenBytes());
		network.put("received", ThroughputCounter.getReceivedBytes());
		// Add the network info to the main object
		object.put("network", network);

		// World info
		JSONObject world = new JSONObject();
		world.put("players", World.getWorld().getPlayers().size());
		// Add the world info to the object
		object.put("world", world);

		return ManagementChannels.construct(ManagementChannels.SERVER, object);
	}
}

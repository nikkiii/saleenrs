package org.saleen.ls;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.AdaptiveReceiveBufferSizePredictor;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.saleen.ChannelStorage;
import org.saleen.ls.NodeConfiguration.WorldType;
import org.saleen.rs2.Constants;
import org.saleen.rs2.database.ConnectionPool;
import org.saleen.rs2.database.DatabaseConnection;
import org.saleen.rs2.database.mysql.MySQLDatabaseConfiguration;
import org.saleen.rs2.database.mysql.MySQLDatabaseConnection;
import org.saleen.rs2.model.PlayerDetails;
import org.saleen.util.CommonConstants;
import org.saleen.util.configuration.ConfigurationNode;
import org.saleen.util.configuration.ConfigurationParser;
import org.saleen.util.login.LoginPipelineFactory;

/**
 * The login server.
 * 
 * @author Graham Edgecombe
 * 
 */
public class LoginServer {

	/**
	 * The loginserver instance
	 */
	private static LoginServer server;

	/**
	 * MySQL Connector
	 */
	private ConnectionPool<MySQLDatabaseConnection> pool;

	/**
	 * The list of nodes
	 */
	private ChannelStorage<Node> nodeStorage = new ChannelStorage<Node>();

	/**
	 * Logger instance.
	 */
	private static final Logger logger = Logger.getLogger(LoginServer.class
			.getName());

	/**
	 * The acceptor.
	 */
	private ServerBootstrap loginBootstrap;

	/**
	 * The acceptor.
	 */
	private ServerBootstrap worldlistBootstrap;

	/**
	 * The task queue.
	 */
	private BlockingQueue<Runnable> tasks = new LinkedBlockingQueue<Runnable>();

	/**
	 * Login server loader.
	 */
	private LoginWorldLoader loader = new LoginWorldLoader(this);

	/**
	 * The entry point of the program.
	 * 
	 * @param args
	 *            The command line arguments.
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public static void main(String[] args) throws IOException {
		server = new LoginServer();
		server.bind();
		try {
			logger.info("Loading configuration...");
			server.loadConfiguration();
			logger.info("Loading nodes...");
			server.loadNodes();
		} catch (Exception e) {
			e.printStackTrace();
		}
		server.start();
	}

	/**
	 * Creates the login server.
	 */
	public LoginServer() {
		logger.info("Starting " + Constants.SERVER_NAME + " Login Server...");
		loginBootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(
				Executors.newSingleThreadExecutor(),
				Executors.newSingleThreadExecutor()));
		loginBootstrap.setPipelineFactory(new LoginPipelineFactory(
				new LoginConnectionHandler(this)));
		loginBootstrap.setOption("receiveBufferSizePredictor",
				new AdaptiveReceiveBufferSizePredictor(1, 2048, 10240));
		loginBootstrap.setOption("child.receiveBufferSizePredictor",
				new AdaptiveReceiveBufferSizePredictor(1, 2048, 10240));
		worldlistBootstrap = new ServerBootstrap(
				new NioServerSocketChannelFactory(
						Executors.newSingleThreadExecutor(),
						Executors.newSingleThreadExecutor()));
		worldlistBootstrap.setPipelineFactory(new LoginPipelineFactory(
				new WorldlistConnectionHandler(this)));
	}

	/**
	 * Binds the login server to the default port.
	 * 
	 * @return The login server instance, for chaining.
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public LoginServer bind() throws IOException {
		logger.info("Binding to port : " + CommonConstants.LOGIN_PORT + "...");
		loginBootstrap.bind(new InetSocketAddress(CommonConstants.LOGIN_PORT));
		logger.info("Binding worldlist to port : " + CommonConstants.LOGIN_PORT
				+ "...");
		worldlistBootstrap.bind(new InetSocketAddress(
				CommonConstants.LOGIN_PORT + 1));
		return this;
	}

	/**
	 * Starts the login server.
	 */
	public void start() {
		logger.info("Ready.");
		while (true) {
			try {
				tasks.take().run();
			} catch (InterruptedException e) {
				continue;
			}
		}
	}

	/**
	 * Pushses a task onto the queue.
	 * 
	 * @param runnable
	 *            The runnable.
	 */
	public void pushTask(Runnable runnable) {
		tasks.add(runnable);
	}

	/**
	 * Gets the login server loader.
	 * 
	 * @return The loader.
	 */
	public LoginWorldLoader getLoader() {
		return loader;
	}

	/**
	 * Loads server configuration.
	 * 
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	private void loadConfiguration() throws IOException {
		FileInputStream fis = new FileInputStream("conf/server.conf");
		try {
			ConfigurationParser parser = new ConfigurationParser(fis);
			ConfigurationNode node = parser.parse();
			if (node.has("database")) {
				ConfigurationNode settings = node.nodeFor("database");
				MySQLDatabaseConfiguration config = new MySQLDatabaseConfiguration();
				config.setHost(settings.getString("host"));
				config.setPort(Integer.parseInt(settings.getString("port")));
				config.setDatabase(settings.getString("database"));
				config.setUsername(settings.getString("username"));
				config.setPassword(settings.getString("password"));
				pool = new ConnectionPool<MySQLDatabaseConnection>(config);
			}
		} finally {
			fis.close();
		}
	}

	/**
	 * Load the nodes from the mysql database
	 * 
	 * @throws Exception
	 */
	public void loadNodes() throws Exception {
		DatabaseConnection connection = pool.nextFree();
		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt
					.executeQuery("SELECT nodeid,host,port,password,description,type FROM nodes");
			while (rs.next()) {
				int nodeid = rs.getInt("nodeid");
				NodeManager.getNodeManager().addNodeConfiguration(
						nodeid,
						new NodeConfiguration(nodeid, rs.getString("host"), rs
								.getInt("port"), rs.getString("password"), rs
								.getString("description"),
								WorldType.values()[rs.getInt("type")]));
			}
			stmt.close();
		} finally {
			connection.returnConnection();
		}
	}

	/**
	 * Get a ban if present
	 * 
	 * @param subject
	 *            The subject
	 * @return The ban
	 */
	public boolean checkBan(PlayerDetails pd) {
		try {
			DatabaseConnection connection = pool.nextFree();
			try {
				PreparedStatement statement = connection
						.prepareStatement("SELECT id FROM punishments WHERE subject IN(?, ?, ?) AND type IN ('BAN', 'UIDBAN', 'PROFILEBAN')");
				statement.setString(1, pd.getName());
				statement.setString(2, Integer.toString(pd.getUID()));
				statement.setString(3, pd.getProfile());
				try {
					if (statement.executeQuery().next()) {
						return true;
					}
				} finally {
					statement.close();
				}
			} finally {
				connection.returnConnection();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * A map of channel -> node
	 * 
	 * @return
	 */
	public ChannelStorage<Node> getNodeStorage() {
		return nodeStorage;
	}
}

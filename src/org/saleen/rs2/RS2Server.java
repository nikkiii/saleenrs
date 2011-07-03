package org.saleen.rs2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.saleen.rs2.model.World;
import org.saleen.rs2.net.RS2PipelineFactory;
import org.saleen.rs2.util.ConsoleScanner;

/**
 * Starts everything else including MINA and the <code>GameEngine</code>.
 * 
 * @author Graham Edgecombe
 * 
 */
public class RS2Server {

	/**
	 * The instance
	 */
	private static RS2Server instance = new RS2Server();

	/**
	 * The port to listen on.
	 */
	public static final int PORT = 43594;

	/**
	 * Logger instance.
	 */
	private static final Logger logger = Logger.getLogger(RS2Server.class
			.getName());

	/**
	 * The <code>GameEngine</code> instance.
	 */
	private static final GameEngine engine = new GameEngine();

	/**
	 * The server's Netty bootstrap
	 */
	private ServerBootstrap serverBootstrap;

	/**
	 * Create a new instance
	 */
	public RS2Server() {
		serverBootstrap = new ServerBootstrap(
				new NioServerSocketChannelFactory(
						Executors.newCachedThreadPool(),
						Executors.newCachedThreadPool()));
	}

	/**
	 * Creates the server and the <code>GameEngine</code> and initializes the
	 * <code>World</code>.
	 * 
	 * @throws IOException
	 *             if an I/O error occurs loading the world.
	 * @throws ClassNotFoundException
	 *             if a class the world loads was not found.
	 * @throws IllegalAccessException
	 *             if a class loaded by the world was not accessible.
	 * @throws InstantiationException
	 *             if a class loaded by the world was not created.
	 */
	public RS2Server init() throws IOException, ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		World.getWorld().init(engine);
		serverBootstrap.setPipelineFactory(new RS2PipelineFactory());
		// acceptor.getFilterChain().addFirst("throttleFilter", new
		// ConnectionThrottleFilter());
		return this;
	}

	/**
	 * Binds the server to the specified port.
	 * 
	 * @param port
	 *            The port to bind to.
	 * @return The server instance, for chaining.
	 * @throws IOException
	 */
	public RS2Server bind(int port) throws IOException {
		logger.info("Binding to port : " + port + "...");
		serverBootstrap.bind(new InetSocketAddress(port));
		return this;
	}

	/**
	 * Starts the <code>GameEngine</code>.
	 * 
	 * @throws ExecutionException
	 *             if an error occured during background loading.
	 */
	public void start() throws ExecutionException {
		if (World.getWorld().getBackgroundLoader().getPendingTaskAmount() > 0) {
			logger.info("Waiting for pending background loading tasks...");
			World.getWorld().getBackgroundLoader().waitForPendingTasks();
		}
		World.getWorld().getBackgroundLoader().shutdown();
		engine.start();
		new Thread(new ConsoleScanner()).start();
		logger.info("Ready");
	}

	/**
	 * Gets the <code>GameEngine</code>.
	 * 
	 * @return The game engine.
	 */
	public static GameEngine getEngine() {
		return engine;
	}

	/**
	 * Get the RS2Server instance
	 * 
	 * @return The instance
	 */
	public static RS2Server getInstance() {
		return instance;
	}

}

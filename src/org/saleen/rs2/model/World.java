package org.saleen.rs2.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.json.JSONException;
import org.json.JSONObject;
import org.saleen.ChannelStorage;
import org.saleen.cache.Cache;
import org.saleen.cache.obj.ObjectManager;
import org.saleen.event.EventProducer;
import org.saleen.event.impl.PlayerLoginEvent;
import org.saleen.rs2.Constants;
import org.saleen.rs2.GameEngine;
import org.saleen.rs2.GenericWorldLoader;
import org.saleen.rs2.WorldLoader;
import org.saleen.rs2.WorldLoader.LoginResult;
import org.saleen.rs2.config.ConfigLoader;
import org.saleen.rs2.content.area.Areas;
import org.saleen.rs2.content.dialogue.DialogueManager;
import org.saleen.rs2.database.ConnectionPool;
import org.saleen.rs2.database.DatabaseConnection;
import org.saleen.rs2.database.mysql.MySQLDatabaseConfiguration;
import org.saleen.rs2.database.mysql.MySQLDatabaseConnection;
import org.saleen.rs2.event.Event;
import org.saleen.rs2.event.EventManager;
import org.saleen.rs2.event.impl.CleanupEvent;
import org.saleen.rs2.event.impl.ConsoleEvent;
import org.saleen.rs2.event.impl.UpdateEvent;
import org.saleen.rs2.eventlistener.impl.AttackPlayerListener;
import org.saleen.rs2.eventlistener.impl.FriendLoginListener;
import org.saleen.rs2.eventlistener.impl.SkillObjectListener;
import org.saleen.rs2.login.LoginServerConnector;
import org.saleen.rs2.login.LoginServerWorldLoader;
import org.saleen.rs2.model.definition.EquipmentDefinition;
import org.saleen.rs2.model.definition.ItemDefinition;
import org.saleen.rs2.model.region.RegionManager;
import org.saleen.rs2.net.PacketBuilder;
import org.saleen.rs2.net.PacketManager;
import org.saleen.rs2.packet.PacketHandler;
import org.saleen.rs2.plugin.PluginLoader;
import org.saleen.rs2.script.FileScriptManager;
import org.saleen.rs2.task.Task;
import org.saleen.rs2.task.impl.ChannelLoginTask;
import org.saleen.rs2.util.EntityList;
import org.saleen.rs2.util.NameUtils;
import org.saleen.rs2.util.TextUtils;
import org.saleen.util.BlockingExecutorService;
import org.saleen.util.Filter;
import org.saleen.util.RSSystem;
import org.saleen.util.configuration.ConfigurationNode;
import org.saleen.util.configuration.ConfigurationParser;

/**
 * Holds data global to the game world.
 * 
 * @author Graham Edgecombe
 * @author Nikki
 * 
 */
public class World {

	/**
	 * Logging class.
	 */
	private static final Logger logger = Logger
			.getLogger(World.class.getName());

	/**
	 * World instance.
	 */
	private static final World world = new World();

	/**
	 * Gets the world instance.
	 * 
	 * @return The world instance.
	 */
	public static World getWorld() {
		return world;
	}

	/**
	 * The player storage map
	 */
	private ChannelStorage<Player> playerStorage = new ChannelStorage<Player>();

	/**
	 * An executor service which handles background loading tasks.
	 */
	private BlockingExecutorService backgroundLoader = new BlockingExecutorService(
			Executors.newSingleThreadExecutor());

	/**
	 * The game engine.
	 */
	private GameEngine engine;

	/**
	 * The event manager.
	 */
	private EventManager eventManager;

	/**
	 * The current loader implementation.
	 */
	private WorldLoader loader;

	/**
	 * A list of connected players.
	 */
	private EntityList<Player> players = new EntityList<Player>(
			Constants.MAX_PLAYERS);

	/**
	 * A list of active NPCs.
	 */
	private EntityList<NPC> npcs = new EntityList<NPC>(Constants.MAX_NPCS);

	/**
	 * The game object manager.
	 */
	private ObjectManager objectManager;

	/**
	 * The login server connector.
	 */
	private LoginServerConnector connector;

	/**
	 * The region manager.
	 */
	private RegionManager regionManager = new RegionManager();

	/**
	 * The npc manager
	 */
	private NPCManager npcManager;

	/**
	 * The item manager
	 */
	private ItemManager itemManager;

	/**
	 * The cache instance
	 */
	private Cache cache;

	/**
	 * The MySQL Connection pool
	 */
	private ConnectionPool<? extends DatabaseConnection> connectionPool;

	/**
	 * This world's worldid
	 */
	private int nodeId;

	/**
	 * The login event producer
	 */
	private EventProducer loginProducer = new EventProducer();

	/**
	 * Creates the world and begins background loading tasks.
	 */
	public World() {
		backgroundLoader.submit(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				ConfigLoader.load(new File("data/config.bin"));
				return null;
			}
		});
		backgroundLoader.submit(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				DialogueManager.getManager().load(new File("data/dialogue"));
				return null;
			}
		});
		backgroundLoader.submit(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				ItemDefinition.init();
				EquipmentDefinition.init();
				return null;
			}
		});
	}

	/**
	 * Load the cache
	 * 
	 * @param file
	 *            The directory to load from
	 */
	public void loadCache(File file) {

		try {
			logger.info("Loading cache from " + file.getPath());
			cache = new Cache(file);
			backgroundLoader.submit(new Callable<Object>() {
				@Override
				public Object call() throws Exception {
					objectManager = new ObjectManager();
					objectManager.load(cache);
					return null;
				}
			});
			backgroundLoader.submit(new Callable<Object>() {
				@Override
				public Object call() throws Exception {
					npcManager = new NPCManager();
					npcManager.load(cache);
					return null;
				}
			});
			backgroundLoader.submit(new Callable<Object>() {
				@Override
				public Object call() throws Exception {
					itemManager = new ItemManager();
					itemManager.load(cache);
					return null;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the login server connector.
	 * 
	 * @return The login server connector.
	 */
	public LoginServerConnector getLoginServerConnector() {
		return connector;
	}

	/**
	 * Gets the background loader.
	 * 
	 * @return The background loader.
	 */
	public BlockingExecutorService getBackgroundLoader() {
		return backgroundLoader;
	}

	/**
	 * Gets the region manager.
	 * 
	 * @return The region manager.
	 */
	public RegionManager getRegionManager() {
		return regionManager;
	}

	/**
	 * Initialises the world: loading configuration and registering global
	 * events.
	 * 
	 * @param engine
	 *            The engine processing this world's tasks.
	 * @throws IOException
	 *             if an I/O error occurs loading configuration.
	 * @throws ClassNotFoundException
	 *             if a class loaded through reflection was not found.
	 * @throws IllegalAccessException
	 *             if a class could not be accessed.
	 * @throws InstantiationException
	 *             if a class could not be created.
	 * @throws IllegalStateException
	 *             if the world is already initialised.
	 */
	public void init(GameEngine engine) throws IOException,
			ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		if (this.engine != null) {
			throw new IllegalStateException(
					"The world has already been initialised.");
		} else {
			this.engine = engine;
			this.eventManager = new EventManager(engine);
			this.registerGlobalEvents();
			this.registerEventListeners();
			this.loadConfiguration();
		}
	}

	/**
	 * Loads server configuration.
	 * 
	 * @throws IOException
	 *             if an I/O error occurs.
	 * @throws ClassNotFoundException
	 *             if a class loaded through reflection was not found.
	 * @throws IllegalAccessException
	 *             if a class could not be accessed.
	 * @throws InstantiationException
	 *             if a class could not be created.
	 */
	private void loadConfiguration() throws IOException,
			ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		FileInputStream fis = new FileInputStream("conf/server.conf");
		try {
			ConfigurationParser parser = new ConfigurationParser(fis);
			ConfigurationNode mainNode = parser.parse();

			ConfigurationNode serverNode = mainNode.nodeFor("server");
			if (serverNode.has("cacheDirectory")) {
				loadCache(new File(serverNode.getString("cacheDirectory")));
			}
			if (serverNode.has("scriptDirectory")) {
				FileScriptManager.getScriptManager().loadScripts(
						serverNode.getString("scriptDirectory"));
			}
			if (serverNode.has("contentDirectory")) {
				loadContent(new File(serverNode.getString("contentDirectory")));
			}
			if (serverNode.has("pluginDirectory")) {
				PluginLoader.getInstance().load(
						new File(serverNode.getString("pluginDirectory")));
			}
			if (serverNode.has("worldLoader")) {
				String worldLoaderClass = serverNode.getString("worldLoader");
				Class<?> loader = Class.forName(worldLoaderClass);
				this.loader = (WorldLoader) loader.newInstance();
				logger.fine("WorldLoader set to : " + worldLoaderClass);
			} else {
				this.loader = new GenericWorldLoader();
				logger.fine("WorldLoader set to default");
			}

			ConfigurationNode loginNode = serverNode.nodeFor("loginServer");
			if (loader instanceof LoginServerWorldLoader) {
				connector = new LoginServerConnector(
						loginNode.getString("host"));
				nodeId = loginNode.getInteger("nodeid");
				connector.connect(loginNode.getString("password"), nodeId);
			}

			if (mainNode.has("database")) {
				ConfigurationNode databaseNode = mainNode.nodeFor("database");
				MySQLDatabaseConfiguration config = new MySQLDatabaseConfiguration();
				config.setHost(databaseNode.getString("host"));
				config.setPort(databaseNode.getInteger("port"));
				config.setDatabase(databaseNode.getString("database"));
				config.setUsername(databaseNode.getString("username"));
				config.setPassword(databaseNode.getString("password"));
				connectionPool = new ConnectionPool<MySQLDatabaseConnection>(
						config);
			}

			if (mainNode.has("packets")) {
				Map<String, Object> packets = mainNode.nodeFor("packets")
						.nodeFor("packet").getChildren();
				for (Entry<String, Object> handler : packets.entrySet()) {
					Class<?> handlerClass = Class
							.forName(((ConfigurationNode) handler.getValue())
									.getString("className"));
					PacketHandler handlerInstance = (PacketHandler) handlerClass
							.newInstance();
					Integer[] idArray = TextUtils.parseIntArray(
							(String) handler.getKey(), ",");
					for (int id : idArray) {
						PacketManager.getPacketManager().bind(id,
								handlerInstance);
					}
					logger.fine("Bound " + handlerClass.getName()
							+ " to opcodes : " + Arrays.toString(idArray));
				}
			}
		} finally {
			fis.close();
		}
	}

	/**
	 * Load the content such as areas from the directory
	 * 
	 * @param directory
	 *            The directory
	 */
	private void loadContent(File directory) {
		try {
			Areas.load(new File(directory, "areas.xml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Registers global events such as updating.
	 */
	private void registerGlobalEvents() {
		submit(new UpdateEvent());
		submit(new CleanupEvent());
		if (RSSystem.isWindows() || RSSystem.isUnix() || RSSystem.isMacOS())
			submit(new ConsoleEvent());
	}

	/**
	 * Registers all listeners for tasks such as FriendsLists and Skills, like
	 * mining and woodcutting
	 */
	private void registerEventListeners() {
		new FriendLoginListener();
		new SkillObjectListener();
		new AttackPlayerListener();
	}

	/**
	 * Submits a new event.
	 * 
	 * @param event
	 *            The event to submit.
	 */
	public void submit(Event event) {
		this.eventManager.submit(event);
	}

	/**
	 * Submits a new task.
	 * 
	 * @param task
	 *            The task to submit.
	 */
	public void submit(Task task) {
		this.engine.pushTask(task);
	}

	/**
	 * Gets the object map.
	 * 
	 * @return The object map.
	 */
	public ObjectManager getObjectMap() {
		return objectManager;
	}

	/**
	 * Gets the world loader.
	 * 
	 * @return The world loader.
	 */
	public WorldLoader getWorldLoader() {
		return loader;
	}

	/**
	 * Gets the game engine.
	 * 
	 * @return The game engine.
	 */
	public GameEngine getEngine() {
		return engine;
	}

	/**
	 * Loads a player's game in the work service.
	 * 
	 * @param pd
	 *            The player's details.
	 */
	public void load(final PlayerDetails pd) {
		engine.submitWork(new Runnable() {
			public void run() {
				LoginResult lr = loader.checkLogin(pd);
				int code = lr.getReturnCode();
				if (!NameUtils.isValidName(pd.getName())) {
					code = 11;
				}
				if (code != 2) {
					PacketBuilder bldr = new PacketBuilder();
					bldr.put((byte) code);
					if (code == 21) {
						bldr.put((byte) 30);
					}
					pd.getSession().write(bldr.toPacket())
							.addListener(new ChannelFutureListener() {

								@Override
								public void operationComplete(ChannelFuture arg0)
										throws Exception {
									arg0.getChannel().close();
								}
							});
				} else {
					playerStorage.set(lr.getPlayer().getChannel(),
							lr.getPlayer());

					loader.loadPlayer(lr.getPlayer());

					engine.pushTask(new ChannelLoginTask(lr.getPlayer()));
				}
			}
		});
	}

	/**
	 * Registers a new npc.
	 * 
	 * @param npc
	 *            The npc to register.
	 */
	public void register(NPC npc) {
		npcs.add(npc);
	}

	/**
	 * Unregisters an old npc.
	 * 
	 * @param npc
	 *            The npc to unregister.
	 */
	public void unregister(NPC npc) {
		npcs.remove(npc);
		npc.destroy();
	}

	/**
	 * Registers a new player.
	 * 
	 * @param player
	 *            The player to register.
	 */
	public void register(final Player player) {
		int returnCode = 2;
		if (isPlayerOnline(player.getName())) {
			returnCode = 5;
		} else {
			if (!players.add(player)) {
				returnCode = 7;
				logger.warning("Could not register player : " + player
						+ " [world full]");
			}
		}
		final int fReturnCode = returnCode;
		PacketBuilder bldr = new PacketBuilder();
		bldr.put((byte) returnCode);
		bldr.put((byte) player.getRights().toInteger());
		bldr.put((byte) 0);
		player.getChannel().write(bldr.toPacket())
				.addListener(new ChannelFutureListener() {
					@Override
					public void operationComplete(ChannelFuture future) {
						if (fReturnCode != 2) {
							player.getChannel().close();
						} else {
							player.getActionSender().sendLogin();
							loginProducer
									.produce(new PlayerLoginEvent.PlayerLogin(
											player));
						}
					}
				});
		if (returnCode == 2) {
			logger.fine("Registered player : " + player + " [online="
					+ players.size() + "]");
		}
	}

	/**
	 * Unregisters a player, and saves their game.
	 * 
	 * @param player
	 *            The player to unregister.
	 */
	public void unregister(final Player player) {
		player.getActionQueue().cancelQueuedActions();
		player.destroy();
		player.getChannel().close();
		players.remove(player);
		logger.fine("Unregistered player : " + player + " [online="
				+ players.size() + "]");
		engine.submitWork(new Runnable() {
			public void run() {
				loader.savePlayer(player);
				playerStorage.remove(player.getChannel());
				loginProducer
						.produce(new PlayerLoginEvent.PlayerLogout(player));
				if (World.getWorld().getLoginServerConnector() != null) {
					World.getWorld().getLoginServerConnector()
							.disconnected(player.getName());
				}
			}
		});
	}

	/**
	 * Gets the player list.
	 * 
	 * @return The player list.
	 */
	public EntityList<Player> getPlayers() {
		return players;
	}

	/**
	 * Get the players online filtered by the specified <code>Filter</code>
	 * 
	 * @param filter
	 *            The filter to check with
	 */
	public List<Player> getPlayers(Filter<Player> filter) {
		LinkedList<Player> found = new LinkedList<Player>();
		for (Player player : players) {
			if (filter.accept(player)) {
				found.add(player);
			}
		}
		return found;
	}

	/**
	 * Gets the npc list.
	 * 
	 * @return The npc list.
	 */
	public EntityList<NPC> getNPCs() {
		return npcs;
	}

	/**
	 * Checks if a player is online.
	 * 
	 * @param name
	 *            The player's name.
	 * @return <code>true</code> if they are online, <code>false</code> if not.
	 */
	public boolean isPlayerOnline(String name) {
		name = NameUtils.formatName(name);
		for (Player player : players) {
			if (player.getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Handles an exception in any of the pools.
	 * 
	 * @param t
	 *            The exception.
	 */
	public void handleError(Throwable t) {
		logger.severe("An error occurred in an executor service! The server will be halted immediately.");
		t.printStackTrace();
		System.exit(1);
	}

	/**
	 * Get the npc at the selected index This function exists so you do not
	 * always have to call the FULL method and cast it to NPC...
	 * 
	 * @param index
	 *            The NPC Index
	 * @return The npc at the index, or null if none.
	 */
	public NPC getNPC(int index) {
		return (NPC) npcs.get(index);
	}

	/**
	 * Get the player at the selected index
	 * 
	 * @param index
	 *            The Player index
	 * @return The player at the index, or null if none.
	 */
	public Player getPlayer(int index) {
		return (Player) players.get(index);
	}

	/**
	 * Finds a player by the specified name
	 * 
	 * @param name
	 *            The name to format and match
	 * @return The player instance if any, or null if not found
	 */
	public Player findPlayer(String name) {
		name = NameUtils.formatName(name);
		for (Player player : players) {
			if (player.getName().equalsIgnoreCase(name)) {
				return player;
			}
		}
		return null;
	}

	/**
	 * Finds a player by the specified name long
	 * 
	 * @param name
	 *            The name to match
	 * @return The player instance if any, or null if not found
	 */
	public Player findPlayer(long name) {
		for (Player player : players) {
			if (player.getNameAsLong() == name) {
				return player;
			}
		}
		return null;
	}

	/**
	 * Sends a message to all players
	 * 
	 * @param msg
	 *            The message to send
	 */
	public void globalMessage(String msg) {
		for (Player player : players) {
			player.getActionSender().sendMessage(msg);
		}
	}

	/**
	 * Handle a command from either input console or remote console
	 * 
	 * @param line
	 *            The line to handle
	 */
	public void consoleCommand(String line) {
		String[] args = line.split(" ");
		String command = args[0];
		if (command.equalsIgnoreCase("exit")) {
			shutdown();
		} else if (command.equalsIgnoreCase("give")) {
			int itemId = Integer.parseInt(args[2]);
			int amount = 1;
			if (args.length > 3) {
				amount = Integer.parseInt(args[3]);
			}
			Player player = findPlayer(args[1]);
			if (player != null) {
				player.getInventory().add(new Item(itemId, amount));
			}
		} else if (command.equalsIgnoreCase("kick")) {
			String toKick = line.substring(line.indexOf(" ") + 1);
			Player player = findPlayer(toKick);
			player.getActionSender().sendLogout();
		} else if (command.equalsIgnoreCase("version")) {
			System.out.println("SaleenRS Version: 1.0a");
		} else {
			System.out.println("Unknown command");
		}
	}

	public void managementCommand(String command, JSONObject params)
			throws JSONException {
		if (command.equalsIgnoreCase("give")) {
			Player player = findPlayer(params.getString("player"));
			Item item = new Item(params.getInt("itemid"),
					params.getInt("itemcount"));
			if (player.getInventory().add(item)) {
				// Successfully added...
			}
		}
	}

	/**
	 * Shutdown the server cleanly, saving players
	 */
	private void shutdown() {
		logger.info("Saving players...");
		Iterator<Player> it$ = players.iterator();
		while (it$.hasNext()) {
			loader.savePlayer(it$.next());
		}
		logger.info("Shutting down...");
		System.exit(1);
	}

	/**
	 * Format all player names into a string for display on the website
	 * 
	 * @return The player name string
	 */
	public String getPlayersOnline() {
		StringBuilder resp = new StringBuilder();
		for (Player player : players) {
			resp.append(NameUtils.formatNameForProtocol(player.getName()))
					.append(",");
		}
		resp.append(players.size());
		return resp.toString();
	}

	/**
	 * Get the object manager instance
	 * 
	 * @return The instance to return.
	 */
	public ObjectManager getObjectManager() {
		return objectManager;
	}

	/**
	 * Get the cache instance
	 * 
	 * @return The instance
	 */
	public Cache getCache() {
		return cache;
	}

	/**
	 * Get the active mysql pool
	 * 
	 * @return The connection pool
	 */
	public ConnectionPool<? extends DatabaseConnection> getConnectionPool() {
		return connectionPool;
	}

	/**
	 * Get the node id
	 * 
	 * @return
	 */
	public int getNodeId() {
		return nodeId;
	}

	/**
	 * Get the channel storage map
	 * 
	 * @return
	 */
	public ChannelStorage<Player> getChannelStorage() {
		return playerStorage;
	}
}

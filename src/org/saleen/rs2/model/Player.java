package org.saleen.rs2.model;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.saleen.buffer.BinaryBuffer;
import org.saleen.buffer.BinaryPart;
import org.saleen.data.Persistable;
import org.saleen.rs2.action.ActionQueue;
import org.saleen.rs2.action.impl.AttackAction;
import org.saleen.rs2.content.chat.FriendsList;
import org.saleen.rs2.content.chat.IgnoreList;
import org.saleen.rs2.content.combat.Damage.Hit;
import org.saleen.rs2.content.dialogue.Dialogue;
import org.saleen.rs2.content.skills.Prayers;
import org.saleen.rs2.event.impl.DeathEvent;
import org.saleen.rs2.event.impl.SkillUpdateEvent;
import org.saleen.rs2.model.UpdateFlags.UpdateFlag;
import org.saleen.rs2.model.container.Bank;
import org.saleen.rs2.model.container.Container;
import org.saleen.rs2.model.container.Equipment;
import org.saleen.rs2.model.container.Inventory;
import org.saleen.rs2.model.region.Region;
import org.saleen.rs2.net.ActionSender;
import org.saleen.rs2.net.ISAACCipher;
import org.saleen.rs2.net.Packet;
import org.saleen.rs2.util.NameUtils;

/**
 * Represents a player-controller character.
 * 
 * @author Graham Edgecombe
 * 
 */
public class Player extends Entity implements Persistable {

	/**
	 * Represents the rights of a player.
	 * 
	 * @author Graham Edgecombe
	 * 
	 */
	public enum Rights {

		/**
		 * A standard account.
		 */
		PLAYER(0),

		/**
		 * A player moderator account
		 */
		MODERATOR(1),

		/**
		 * A head moderator
		 */
		HEADMODERATOR(2),

		/**
		 * An administrator account.
		 */
		ADMINISTRATOR(3),

		/**
		 * Developer
		 */
		DEVELOPER(4),

		/**
		 * Designer
		 */
		DESIGNER(5),

		/**
		 * Veteran
		 */
		VETERAN(6),

		/**
		 * Donator
		 */
		DONATOR(7);

		/**
		 * Gets rights by a specific integer.
		 * 
		 * @param value
		 *            The integer returned by {@link #toInteger()}.
		 * @return The rights level.
		 */
		public static Rights getRights(int value) {
			if (value == 1) {
				return MODERATOR;
			} else if (value == 2) {
				return HEADMODERATOR;
			} else if (value == 3) {
				return ADMINISTRATOR;
			} else if (value == 4) {
				return DEVELOPER;
			} else if (value == 5) {
				return DESIGNER;
			} else if (value == 6) {
				return VETERAN;
			} else if (value == 7) {
				return DONATOR;
			} else {
				return PLAYER;
			}
		}

		/**
		 * The integer representing this rights level.
		 */
		private int value;

		/**
		 * Creates a rights level.
		 * 
		 * @param value
		 *            The integer representing this rights level.
		 */
		private Rights(int value) {
			this.value = value;
		}

		/**
		 * Gets an integer representing this rights level.
		 * 
		 * @return An integer representing this rights level.
		 */
		public int toInteger() {
			return value;
		}
	}

	/*
	 * Attributes specific to our session.
	 */

	/**
	 * The <code>Channel</code>.
	 */
	private final Channel channel;

	/**
	 * The ISAAC cipher for incoming data.
	 */
	private final ISAACCipher inCipher;

	/**
	 * The ISAAC cipher for outgoing data.
	 */
	private final ISAACCipher outCipher;

	/**
	 * The action sender.
	 */
	private final ActionSender actionSender = new ActionSender(this);

	/**
	 * A queue of pending chat messages.
	 */
	private final Queue<ChatMessage> chatMessages = new LinkedList<ChatMessage>();

	/**
	 * A queue of pending chat messages.
	 */
	private final Queue<Packet> queuedPackets = new LinkedList<Packet>();

	/**
	 * A queue of actions.
	 */
	private final ActionQueue actionQueue = new ActionQueue();

	/**
	 * The current chat message.
	 */
	private ChatMessage currentChatMessage;

	/**
	 * Active flag: if the player is not active certain changes (e.g. items)
	 * should not send packets as that indicates the player is still loading.
	 */
	private boolean active = false;

	/**
	 * The interface state.
	 */
	private final InterfaceState interfaceState = new InterfaceState(this);

	/**
	 * A queue of packets that are pending.
	 */
	private final Queue<Packet> pendingPackets = new LinkedList<Packet>();

	/**
	 * The request manager which manages trading and duelling requests.
	 */
	private final RequestManager requestManager = new RequestManager(this);

	/**
	 * An implementation of Runescape's friends list
	 */
	private FriendsList friendsList = new FriendsList(this);

	/**
	 * An implementation of Runescape's ignore list
	 */
	private IgnoreList ignoreList = new IgnoreList(this);

	/**
	 * A map of attributes, used so that plugins can set a value!
	 */
	private Map<String, Object> attributes = new HashMap<String, Object>();

	/**
	 * The name.
	 */
	private String name;

	/**
	 * The name expressed as a long.
	 */
	private long nameLong;

	/**
	 * The UID, i.e. number in <code>random.dat</code>.
	 */
	private final int uid;

	/**
	 * The password.
	 */
	private String password;

	/**
	 * The rights level.
	 */
	private Rights rights = Rights.PLAYER;

	/**
	 * The members flag.
	 */
	private boolean members = true;

	/*
	 * Attributes.
	 */

	/**
	 * The player's appearance information.
	 */
	private final Appearance appearance = new Appearance();

	/**
	 * The player's equipment.
	 */
	private final Container equipment = new Container(Container.Type.STANDARD,
			Equipment.SIZE);

	/**
	 * The player's skill levels.
	 */
	private final Skills skills = new Skills(this);

	/**
	 * The player's inventory.
	 */
	private final Container inventory = new Container(Container.Type.STANDARD,
			Inventory.SIZE);

	/**
	 * The player's bank.
	 */
	private final Container bank = new Container(Container.Type.ALWAYS_STACK,
			Bank.SIZE);

	/**
	 * The player's settings.
	 */
	private final Settings settings = new Settings();

	/*
	 * Cached details.
	 */
	/**
	 * The cached update block.
	 */
	private Packet cachedUpdateBlock;

	/**
	 * The prayer manager
	 */
	private Prayers prayerManager;

	/**
	 * The user's current dialogue
	 */
	private Dialogue currentDialogue = null;

	/**
	 * The user's force chat
	 */
	private String forceChat = "";

	/**
	 * Creates a player based on the details object.
	 * 
	 * @param details
	 *            The details object.
	 */
	public Player(PlayerDetails details) {
		super();
		this.channel = details.getSession();
		this.inCipher = details.getInCipher();
		this.outCipher = details.getOutCipher();
		this.name = details.getName();
		this.nameLong = NameUtils.nameToLong(this.name);
		this.password = details.getPassword();
		this.uid = details.getUID();
		this.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
		this.setTeleporting(true);

		this.prayerManager = new Prayers(this);
		World.getWorld().submit(new SkillUpdateEvent(this));
	}

	@Override
	public void addToRegion(Region region) {
		region.addPlayer(this);
	}

	@Override
	public void deserialize(ChannelBuffer inputBuffer) {
		BinaryBuffer buffer = new BinaryBuffer(inputBuffer);
		while (buffer.hasNextPart()) {
			BinaryPart part = buffer.nextPart();
			switch (part.getOpcode()) {
			case 0: // Login details
				this.name = part.getString();
				this.nameLong = NameUtils.nameToLong(this.name);
				this.password = part.getString();
				break;
			case 1: // Player info
				this.rights = Player.Rights.getRights(part.getUnsigned());
				this.members = part.getUnsigned() == 1 ? true : false;
				break;
			case 2: // Player location
				setLocation(Location.create(part.getUnsignedShort(),
						part.getUnsignedShort(), part.getUnsigned()));
				break;
			case 3: // Appearance
				int[] look = new int[13];
				for (int i = 0; i < 13; i++) {
					look[i] = part.getUnsigned();
				}
				appearance.setLook(look);
				break;
			case 4: // Equipment
				for (int i = 0; i < Equipment.SIZE; i++) {
					int id = part.getUnsignedShort();
					if (id != 65535) {
						int amt = part.getInt();
						Item item = new Item(id, amt);
						equipment.set(i, item);
					}
				}
				break;
			case 5: // Skills
				for (int i = 0; i < Skills.SKILL_COUNT; i++) {
					skills.setSkill(i, part.getUnsigned(), part.getDouble());
				}
				break;
			case 6: // Inventory
				for (int i = 0; i < Inventory.SIZE; i++) {
					int id = part.getUnsignedShort();
					if (id != 65535) {
						int amt = part.getInt();
						Item item = new Item(id, amt);
						inventory.set(i, item);
					}
				}
				break;
			case 7: // Bank
				for (int i = 0; i < Bank.SIZE; i++) {
					int id = part.getUnsignedShort();
					if (id != 65535) {
						int amt = part.getInt();
						Item item = new Item(id, amt);
						bank.set(i, item);
					}
				}
				break;
			}
		}
	}

	/**
	 * Gets the action queue.
	 * 
	 * @return The action queue.
	 */
	public ActionQueue getActionQueue() {
		return actionQueue;
	}

	/**
	 * Gets the action sender.
	 * 
	 * @return The action sender.
	 */
	public ActionSender getActionSender() {
		return actionSender;
	}

	/**
	 * Gets the player's appearance.
	 * 
	 * @return The player's appearance.
	 */
	public Appearance getAppearance() {
		return appearance;
	}

	/**
	 * Gets the player's bank.
	 * 
	 * @return The player's bank.
	 */
	public Container getBank() {
		return bank;
	}

	/**
	 * Gets the cached update block.
	 * 
	 * @return The cached update block.
	 */
	public Packet getCachedUpdateBlock() {
		return cachedUpdateBlock;
	}

	/**
	 * Gets the queue of pending chat messages.
	 * 
	 * @return The queue of pending chat messages.
	 */
	public Queue<ChatMessage> getChatMessageQueue() {
		return chatMessages;
	}

	/**
	 * Get the packet queue
	 * 
	 * @return
	 */
	public Queue<Packet> getPacketQueue() {
		synchronized (queuedPackets) {
			return queuedPackets;
		}
	}

	@Override
	public int getClientIndex() {
		return this.getIndex() + 32768;
	}

	/**
	 * Gets the current chat message.
	 * 
	 * @return The current chat message.
	 */
	public ChatMessage getCurrentChatMessage() {
		return currentChatMessage;
	}

	/**
	 * Get the current dialogue for this player
	 * 
	 * @return The current dialogue
	 */
	public Dialogue getCurrentDialogue() {
		return currentDialogue;
	}

	/**
	 * Gets the player's equipment.
	 * 
	 * @return The player's equipment.
	 */
	public Container getEquipment() {
		return equipment;
	}

	/**
	 * Get the player's friends list
	 * 
	 * @return The friends list
	 */
	public FriendsList getFriendsList() {
		return friendsList;
	}

	/**
	 * Get the player's ignore list
	 * 
	 * @return The ignore list
	 */
	public IgnoreList getIgnoreList() {
		return ignoreList;
	}

	/**
	 * Gets the incoming ISAAC cipher.
	 * 
	 * @return The incoming ISAAC cipher.
	 */
	public ISAACCipher getInCipher() {
		return inCipher;
	}

	/**
	 * Gets the interface state.
	 * 
	 * @return The interface state.
	 */
	public InterfaceState getInterfaceState() {
		return interfaceState;
	}

	/**
	 * Gets the inventory.
	 * 
	 * @return The inventory.
	 */
	public Container getInventory() {
		return inventory;
	}

	/**
	 * Gets the player's name.
	 * 
	 * @return The player's name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the player's name expressed as a long.
	 * 
	 * @return The player's name expressed as a long.
	 */
	public long getNameAsLong() {
		return nameLong;
	}

	/**
	 * Gets the outgoing ISAAC cipher.
	 * 
	 * @return The outgoing ISAAC cipher.
	 */
	public ISAACCipher getOutCipher() {
		return outCipher;
	}

	/**
	 * Gets the player's password.
	 * 
	 * @return The player's password.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Get the prayer manager
	 * 
	 * @return The prayer manager
	 */
	public Prayers getPrayer() {
		return prayerManager;
	}

	/**
	 * Gets the request manager.
	 * 
	 * @return The request manager.
	 */
	public RequestManager getRequestManager() {
		return requestManager;
	}

	/**
	 * Gets the rights.
	 * 
	 * @return The player's rights.
	 */
	public Rights getRights() {
		return rights;
	}

	/**
	 * Gets the <code>Channel</code>.
	 * 
	 * @return The player's <code>Channel</code>.
	 */
	public Channel getChannel() {
		return channel;
	}

	/**
	 * Gets the player's settings.
	 * 
	 * @return The player's settings.
	 */
	public Settings getSettings() {
		return settings;
	}

	/**
	 * Gets the player's skills.
	 * 
	 * @return The player's skills.
	 */
	public Skills getSkills() {
		return skills;
	}

	/**
	 * Gets the player's UID.
	 * 
	 * @return The player's UID.
	 */
	public int getUID() {
		return uid;
	}

	/**
	 * Checks if there is a cached update block for this cycle.
	 * 
	 * @return <code>true</code> if so, <code>false</code> if not.
	 */
	public boolean hasCachedUpdateBlock() {
		return cachedUpdateBlock != null;
	}

	/**
	 * Inflict damage to this player
	 * 
	 * @param hit
	 *            The damage to inflict
	 */
	public void inflict(Hit hit) {
		this.inflict(hit, null);
	}

	/**
	 * Manages updateflags and HP modification when a hit occurs.
	 * 
	 * @param source
	 *            The Entity dealing the blow.
	 */
	public void inflict(Hit inc, Entity source) {
		if (!getUpdateFlags().get(UpdateFlag.HIT)) {
			getDamage().setHit1(inc);
			getUpdateFlags().flag(UpdateFlag.HIT);
		} else {
			if (!getUpdateFlags().get(UpdateFlag.HIT_2)) {
				getDamage().setHit2(inc);
				getUpdateFlags().flag(UpdateFlag.HIT_2);
			}
		}
		skills.detractLevel(Skills.HITPOINTS, inc.getDamage());
		if ((source instanceof Entity) && (source != null)) {
			this.setInCombat(true);
			this.setAggressorState(false);
			if (this.isAutoRetaliating()) {
				this.face(source.getLocation());
				this.getActionQueue().addAction(new AttackAction(this, source));
			}
		}
		if (skills.getLevel(Skills.HITPOINTS) <= 0) {
			if (!this.isDead()) {
				World.getWorld().submit(new DeathEvent(this));
			}
			this.setDead(true);
		}
	}

	/**
	 * Gets the active flag.
	 * 
	 * @return The active flag.
	 */
	public boolean isActive() {
		synchronized (this) {
			return active;
		}
	}

	/**
	 * Checks if this player has a member's account.
	 * 
	 * @return <code>true</code> if so, <code>false</code> if not.
	 */
	public boolean isMembers() {
		return members;
	}

	public void playAnimation(int i) {
		this.playAnimation(Animation.create(i));
	}

	@Override
	public void removeFromRegion(Region region) {
		region.removePlayer(this);
	}

	/**
	 * Resets the cached update block.
	 */
	public void resetCachedUpdateBlock() {
		cachedUpdateBlock = null;
	}

	@Override
	public void serialize(ChannelBuffer buffer) {
		BinaryBuffer buf = new BinaryBuffer();

		buf.startOpcode(0); // Player login details
		buf.putString(NameUtils.formatName(name));
		buf.putString(password);
		buf.finishOpcode();

		buf.startOpcode(1); // Player info
		buf.put((byte) rights.toInteger());
		buf.put((byte) (members ? 1 : 0));
		buf.finishOpcode();

		buf.startOpcode(2); // Player location
		buf.putShort((short) getLocation().getX());
		buf.putShort((short) getLocation().getY());
		buf.put((byte) getLocation().getZ());
		buf.finishOpcode();

		buf.startOpcode(3); // Appearance
		int[] look = appearance.getLook();
		for (int i = 0; i < 13; i++) {
			buf.put((byte) look[i]);
		}
		buf.finishOpcode();

		buf.startOpcode(4); // Equipment
		for (int i = 0; i < Equipment.SIZE; i++) {
			Item item = equipment.get(i);
			if (item == null) {
				buf.putShort((short) 65535);
			} else {
				buf.putShort((short) item.getId());
				buf.putInt(item.getCount());
			}
		}
		buf.finishOpcode();

		buf.startOpcode(5); // Skills
		for (int i = 0; i < Skills.SKILL_COUNT; i++) {
			buf.put((byte) skills.getLevel(i));
			buf.putDouble((double) skills.getExperience(i));
		}
		buf.finishOpcode();

		buf.startOpcode(6); // Inventory
		for (int i = 0; i < Inventory.SIZE; i++) {
			Item item = inventory.get(i);
			if (item == null) {
				buf.putShort((short) 65535);
			} else {
				buf.putShort((short) item.getId());
				buf.putInt(item.getCount());
			}
		}
		buf.finishOpcode();

		buf.startOpcode(7); // Bank
		for (int i = 0; i < Bank.SIZE; i++) {
			Item item = bank.get(i);
			if (item == null) {
				buf.putShort((short) 65535);
			} else {
				buf.putShort((short) item.getId());
				buf.putInt(item.getCount());
			}
		}
		buf.finishOpcode();

		// Write all the data collected to the output
		buffer.writeBytes(buf.toChannelBuffer());
	}

	/**
	 * Sets the active flag.
	 * 
	 * @param active
	 *            The active flag.
	 */
	public void setActive(boolean active) {
		synchronized (this) {
			this.active = active;
		}
	}

	/**
	 * Sets the cached update block for this cycle.
	 * 
	 * @param cachedUpdateBlock
	 *            The cached update block.
	 */
	public void setCachedUpdateBlock(Packet cachedUpdateBlock) {
		this.cachedUpdateBlock = cachedUpdateBlock;
	}

	/**
	 * Sets the current chat message.
	 * 
	 * @param currentChatMessage
	 *            The current chat message to set.
	 */
	public void setCurrentChatMessage(ChatMessage currentChatMessage) {
		this.currentChatMessage = currentChatMessage;
	}

	public void setCurrentDialogue(Dialogue dialogue) {
		this.currentDialogue = dialogue;
	}

	/**
	 * Set this player's headicon, and call the appearance update flag
	 * 
	 * @param headIcon
	 *            The headicon to set to
	 */
	public void setHeadIcon(HeadIcon headIcon) {
		settings.setHeadIcon(headIcon);
		getUpdateFlags().flag(UpdateFlag.APPEARANCE);
	}

	/**
	 * Sets the members flag.
	 * 
	 * @param members
	 *            The members flag.
	 */
	public void setMembers(boolean members) {
		this.members = members;
	}

	/**
	 * Sets the player's password.
	 * 
	 * @param pass
	 *            The password.
	 */
	public void setPassword(String pass) {
		this.password = pass;
	}

	/**
	 * Sets the rights.
	 * 
	 * @param rights
	 *            The rights level to set.
	 */
	public void setRights(Rights rights) {
		this.rights = rights;
	}

	@Override
	public String toString() {
		return Player.class.getName() + " [name=" + name + " rights=" + rights
				+ " members=" + members + " index=" + this.getIndex() + "]";
	}

	/**
	 * Updates the players' options when in a PvP area.
	 */
	public void updatePlayerAttackOptions(boolean enable) {
		if (enable) {
			actionSender.sendInteractionOption("Attack", 1, true);
			// actionSender.sendOverlay(381);
		} else {

		}
	}

	/**
	 * Writes a packet to the <code>Channel</code>. If the player is not yet
	 * active, the packets are queued.
	 * 
	 * @param packet
	 *            The packet.
	 */
	public void write(Packet packet) {
		synchronized (this) {
			if (!active) {
				pendingPackets.add(packet);
			} else {
				for (Packet pendingPacket : pendingPackets) {
					channel.write(pendingPacket);
				}
				pendingPackets.clear();
				channel.write(packet);
			}
		}
	}

	/**
	 * Request force chat with the specified text
	 * 
	 * @param text
	 *            The text to say
	 */
	public void requestForceChat(String text) {
		getUpdateFlags().flag(UpdateFlag.FORCED_CHAT);
		this.forceChat = text;
	}

	/**
	 * Get the force chat text
	 * 
	 * @return The text
	 */
	public String getForceChatText() {
		return forceChat;
	}

	/**
	 * Set an attribute
	 * 
	 * @param key
	 *            The key
	 * @param value
	 *            The value
	 */
	public void setAttribute(String key, Object value) {
		attributes.put(key, value);
	}

	/**
	 * Remove an attribute
	 * 
	 * @param key
	 *            The key
	 */
	public void removeAttribute(String key) {
		attributes.remove(key);
	}

	/**
	 * Get an attribute, ignoring it if it's null
	 * 
	 * @param key
	 *            The key
	 * @return The value
	 */
	public Object getAttribute(String key) {
		return attributes.get(key);
	}

	public <T> T getAttribute(String key, Class<T> clazz) {
		return clazz.cast(getAttribute(key));
	}

	/**
	 * Get the channel's remote host
	 * 
	 * @return The host
	 */
	public String getRemoteHost() {
		return ((InetSocketAddress) channel.getRemoteAddress()).getAddress()
				.getHostAddress();
	}

	/**
	 * Get the player's strength bonus
	 * 
	 * @return The strength bonus
	 */
	public int getStrengthBonus() {
		int total = 0;
		for (int i = 0; i < Equipment.SIZE; i++) {
			Item item = equipment.get(i);
			if (item != null) {
				total += equipment.get(i).getEquipmentDefinition().getBonus(10);
			}
		}
		return total;
	}

}

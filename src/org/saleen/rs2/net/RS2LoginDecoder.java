package org.saleen.rs2.net;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.logging.Logger;

import org.apache.commons.codec.digest.DigestUtils;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import org.saleen.Server;
import org.saleen.rs2.model.PlayerDetails;
import org.saleen.rs2.model.World;
import org.saleen.rs2.net.ondemand.OnDemandPool;
import org.saleen.rs2.net.ondemand.OnDemandRequest;
import org.saleen.rs2.util.ChannelBufferUtils;
import org.saleen.rs2.util.NameUtils;

/**
 * Login protocol decoding class.
 * 
 * @author Graham Edgecombe
 * 
 */
public class RS2LoginDecoder extends FrameDecoder {

	/**
	 * Logger instance.
	 */
	private static final Logger logger = Logger.getLogger(RS2LoginDecoder.class
			.getName());

	/**
	 * Opcode stage.
	 */
	public static final int STATE_OPCODE = 0;

	/**
	 * Login stage.
	 */
	public static final int STATE_LOGIN = 1;

	/**
	 * Precrypted stage.
	 */
	public static final int STATE_PRECRYPTED = 2;

	/**
	 * Crypted stage.
	 */
	public static final int STATE_CRYPTED = 3;

	/**
	 * Update stage.
	 */
	public static final int STATE_UPDATE = -1;

	/**
	 * Game opcode.
	 */
	public static final int OPCODE_GAME = 14;

	/**
	 * Update opcode.
	 */
	public static final int OPCODE_UPDATE = 15;

	/**
	 * Secure random number generator.
	 */
	private static final SecureRandom RANDOM = new SecureRandom();

	/**
	 * Initial login response.
	 */
	private static final byte[] INITIAL_RESPONSE = new byte[] { 0x0, 0x0, 0x0,
			0x0, 0x0, 0x0, 0x0, 0x0 };

	/**
	 * The RSA Module
	 */
	private static final BigInteger RSA_MODULE = new BigInteger(
			"96211196122824347120426164704947218057963370484929372904096258835768847243732750163263930508964997933048427073288214251299827010359754791150294453451584893784728122622375395244002290214178103549288849319125279165700398037392260761895210173289781860863023341721613188416507622641601450789618456498017248048109");
	/**
	 * The RSA private key
	 */
	private static final BigInteger RSA_PRIVATE = new BigInteger(
			"43495206047378423737491591129868260320450108194264117374957107539041448435804414633522799840084455479505604426924992169881307882004651036089551307915582901721495458475205068061727689956317269633211993408902334669182206981509336948360074441398004307710207753063367394730764058574106397093578327199722855170833");

	/**
	 * The login state
	 */
	private int state = STATE_OPCODE;

	/**
	 * The login packet size
	 */
	private int loginSize = -1;

	private int loginEncryptSize = -1;

	private long serverKey = 0L;

	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel,
			ChannelBuffer in) throws Exception {
		switch (state) {
		case STATE_UPDATE:
			if (in.readableBytes() >= 4) {
				/*
				 * Here we read the cache id (idx file), file id and priority.
				 */
				int cacheId = in.readUnsignedByte();
				int fileId = ((in.readByte() & 0xFF) << 8)
						| (in.readByte() & 0xFF);
				int priority = in.readUnsignedByte();

				/*
				 * We push the request into the ondemand pool so it can be
				 * served.
				 */
				OnDemandPool.getOnDemandPool()
						.pushRequest(
								new OnDemandRequest(channel, cacheId, fileId,
										priority));
				return null;
			} else {
				return null;
			}
		case STATE_OPCODE:
			if (in.readableBytes() >= 1) {
				/*
				 * Here we read the first opcode which indicates the type of
				 * connection.
				 * 
				 * 14 = game 15 = update
				 * 
				 * Updating is disabled in the vast majority of 317 clients.
				 */
				int opcode = in.readUnsignedByte();
				switch (opcode) {
				case OPCODE_GAME:
					state = STATE_LOGIN;
					return null;
				case OPCODE_UPDATE:
					state = STATE_UPDATE;
					channel.write(new PacketBuilder().put(INITIAL_RESPONSE)
							.toPacket());
					return null;
				default:
					logger.info("Invalid opcode : " + opcode);
					channel.close();
					break;
				}
			} else {
				return null;
			}
			break;
		case STATE_LOGIN:
			if (in.readableBytes() >= 1) {
				/*
				 * The name hash is a simple hash of the name which is suspected
				 * to be used to select the appropriate login server.
				 */
				@SuppressWarnings("unused")
				int nameHash = in.readUnsignedByte();

				/*
				 * We generated the server session key using a SecureRandom
				 * class for security.
				 */
				serverKey = RANDOM.nextLong();

				/*
				 * The initial response is just 0s which the client is set to
				 * ignore (probably some sort of modification).
				 */
				channel.write(new PacketBuilder().put(INITIAL_RESPONSE)
						.put((byte) 0).putLong(serverKey).toPacket());
				state = STATE_PRECRYPTED;
				return null;
			}
			break;
		case STATE_PRECRYPTED:
			if (in.readableBytes() >= 2) {
				/*
				 * We read the type of login.
				 * 
				 * 16 = normal 18 = reconnection
				 */
				int loginOpcode = in.readUnsignedByte();
				if (loginOpcode != 16 && loginOpcode != 18) {
					logger.info("Invalid login opcode : " + loginOpcode);
					channel.close();
					return null;
				}

				/*
				 * We read the size of the login packet.
				 */
				loginSize = in.readByte() & 0xFF;

				/*
				 * And calculated how long the encrypted block will be.
				 */
				loginEncryptSize = loginSize - (36 + 1 + 1 + 2);

				/*
				 * This could be invalid so if it is we ignore it.
				 */
				if (loginEncryptSize <= 0) {
					logger.info("Encrypted packet size zero or negative : "
							+ loginEncryptSize);
					channel.close();
					return null;
				}
				state = STATE_CRYPTED;
				return null;
			}
			break;
		case STATE_CRYPTED:
			if (in.readableBytes() >= loginSize) {
				/*
				 * We read the magic ID which is 255 (0xFF) which indicates this
				 * is the real login packet.
				 */
				int magicId = in.readByte() & 0xFF;
				if (magicId != 255) {
					logger.fine("Incorrect magic id : " + magicId);
					channel.close();
					return null;
				}

				/*
				 * We now read a short which is the client version and check if
				 * it equals 317.
				 */
				int version = in.readUnsignedShort();
				if (version != Server.VERSION) {
					logger.fine("Incorrect version : " + version);
					channel.close();
					in.resetReaderIndex();
					return null;
				}

				/*
				 * The following byte indicates if we are using a low memory
				 * version.
				 */
				@SuppressWarnings("unused")
				boolean lowMemoryVersion = (in.readByte() & 0xFF) == 1;

				/*
				 * We know read the cache indices.
				 */
				for (int i = 0; i < 9; i++) {
					in.readInt();
				}

				/*
				 * The encrypted size includes the size byte which we don't
				 * need.
				 */
				loginEncryptSize--;

				/*
				 * We check if there is a mismatch in the sizing.
				 */
				int reportedSize = in.readByte() & 0xFF;
				if (reportedSize != loginEncryptSize) {
					logger.fine("Packet size mismatch (expected : "
							+ loginEncryptSize + ", reported : " + reportedSize
							+ ")");
					channel.close();
					return null;
				}

				byte[] encryptionBytes = new byte[loginEncryptSize];

				in.readBytes(encryptionBytes);

				ChannelBuffer encryptedBuffer = ChannelBuffers
						.wrappedBuffer(new BigInteger(encryptionBytes).modPow(
								RSA_PRIVATE, RSA_MODULE).toByteArray());

				/*
				 * We now read the encrypted block opcode (although in most 317
				 * clients and this server the RSA is disabled) and check it is
				 * equal to 10.
				 */
				int blockOpcode = encryptedBuffer.readUnsignedByte();
				if (blockOpcode != 10) {
					logger.fine("Invalid login block opcode : " + blockOpcode);
					channel.close();
					return null;
				}

				/*
				 * We read the client's session key.
				 */
				long clientKey = encryptedBuffer.readLong();

				/*
				 * And verify it has the correct server session key.
				 */
				long reportedServerKey = encryptedBuffer.readLong();
				if (reportedServerKey != serverKey) {
					logger.fine("Server key mismatch (expected : " + serverKey
							+ ", reported : " + reportedServerKey + ")");
					channel.close();
					return null;
				}

				/*
				 * The UID, found in random.dat in newer clients and uid.dat in
				 * older clients is a way of identifying a computer.
				 * 
				 * However, some clients send a hardcoded or random UID, making
				 * it useless in the private server scene.
				 */
				int uid = encryptedBuffer.readInt();

				/*
				 * The hardware profile we calculated of the player's
				 * computer... TODO use for banning, this is harder to change
				 * than ip and mac...
				 */
				String profile = ChannelBufferUtils
						.getRS2String(encryptedBuffer);

				/*
				 * We read and format the name and passwords.
				 */
				String name = NameUtils.formatName(ChannelBufferUtils
						.getRS2String(encryptedBuffer));

				String pass = DigestUtils.sha256Hex(ChannelBufferUtils
						.getRS2String(encryptedBuffer));

				logger.fine("Login request : username=" + name + " password="
						+ pass + " profile=" + profile);

				/*
				 * And setup the ISAAC cipher which is used to encrypt and
				 * decrypt opcodes.
				 * 
				 * However, without RSA, this is rendered useless anyway.
				 */
				int[] sessionKey = new int[4];
				sessionKey[0] = (int) (clientKey >> 32);
				sessionKey[1] = (int) clientKey;
				sessionKey[2] = (int) (serverKey >> 32);
				sessionKey[3] = (int) serverKey;

				ISAACCipher inCipher = new ISAACCipher(sessionKey);
				for (int i = 0; i < 4; i++) {
					sessionKey[i] += 50;
				}
				ISAACCipher outCipher = new ISAACCipher(sessionKey);

				/*
				 * Now, the login has completed, and we do the appropriate
				 * things to fire off the chain of events which will load and
				 * check the saved games etc.
				 */
				channel.getPipeline().remove("protocolDecoder");
				channel.getPipeline().addFirst("protocolDecoder",
						new RS2Decoder());

				PlayerDetails pd = new PlayerDetails(channel, name, pass, uid,
						profile, inCipher, outCipher);
				World.getWorld().load(pd);
			}
			break;
		}
		return null;
	}

}

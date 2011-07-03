package org.saleen.ls;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.saleen.buffer.BinaryPart;
import org.saleen.buffer.BinaryPartUtil;
import org.saleen.rs2.WorldLoader.LoginResult;
import org.saleen.rs2.model.PlayerDetails;
import org.saleen.rs2.util.NameUtils;

public class LoginWorldLoader {

	/**
	 * The loginserver instance
	 */
	private LoginServer server;

	public LoginWorldLoader(LoginServer server) {
		this.server = server;
	}

	/**
	 * Check a login
	 * 
	 * @param pd
	 *            The player detials
	 * @return The LoginResult
	 */
	public LoginResult checkLogin(PlayerDetails pd) {
		PlayerData playerData = null;
		int code = 2;
		if (server.checkBan(pd)) {
			code = 4;
		}
		File f = new File("data/savedGames/"
				+ NameUtils.formatNameForProtocol(pd.getName()) + ".dat.gz");
		if (f.exists() && code == 2) {
			try {
				InputStream is = new GZIPInputStream(new FileInputStream(f));

				String name = null;
				int rights = -1;

				BinaryPart dataPart = BinaryPartUtil.readPartFromInput(is);
				if (dataPart.getOpcode() != 0) {
					code = 18;
				} else {
					name = dataPart.getString();
					String pass = dataPart.getString();
					if (!name.equals(NameUtils.formatName(pd.getName()))) {
						code = 3;
					}
					if (!pass.equals(pd.getPassword())) {
						code = 3;
					}
				}
				BinaryPart infoPart = BinaryPartUtil.readPartFromInput(is);
				if (infoPart.getOpcode() != 1) {
					code = 18;
				} else {
					rights = infoPart.getUnsigned();
				}
				if (code == 2) {
					playerData = new PlayerData(name, rights);
				}
				is.close();
			} catch (IOException ex) {
				code = 18;
			}
		}
		return new LoginResult(code, playerData);
	}

	/**
	 * Load a player file as a ChannelBuffer
	 * 
	 * @param pd
	 *            The player details
	 * @return The buffer
	 */
	public ChannelBuffer loadPlayerFile(PlayerDetails pd) {
		try {
			File f = new File("data/savedGames/"
					+ NameUtils.formatNameForProtocol(pd.getName()) + ".dat.gz");
			InputStream is = new GZIPInputStream(new FileInputStream(f));
			ChannelBuffer buf = ChannelBuffers.dynamicBuffer();
			while (true) {
				byte[] temp = new byte[1024];
				int read = is.read(temp, 0, temp.length);
				if (read == -1) {
					break;
				} else {
					buf.writeBytes(temp, 0, read);
				}
			}
			is.close();
			return buf;
		} catch (IOException ex) {
			return null;
		}
	}

	/**
	 * Save a player
	 * 
	 * @param name
	 *            The name
	 * @param data
	 *            The raw byte data
	 * @return True if saved
	 */
	public boolean savePlayer(String name, byte[] data) {
		try {
			OutputStream os = new GZIPOutputStream(new FileOutputStream(
					"data/savedGames/" + name + ".dat.gz"));
			os.write(data);
			os.flush();
			os.close();
			return true;
		} catch (IOException e) {
			return false;
		}
	}
}

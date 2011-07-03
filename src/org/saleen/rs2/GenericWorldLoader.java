package org.saleen.rs2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.codec.digest.DigestUtils;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.saleen.buffer.BinaryPart;
import org.saleen.buffer.BinaryPartUtil;
import org.saleen.rs2.model.Player;
import org.saleen.rs2.model.PlayerDetails;
import org.saleen.rs2.util.NameUtils;

/**
 * An implementation of the <code>WorldLoader</code> class that saves players in
 * binary, gzip-compressed files in the <code>data/savedGames/</code> directory.
 * 
 * @author Graham Edgecombe
 * @author Nikki
 * 
 */
public class GenericWorldLoader implements WorldLoader {

	@Override
	public LoginResult checkLogin(PlayerDetails pd) {
		Player player = null;
		int code = 2;
		File f = new File("data/savedGames/"
				+ NameUtils.formatNameForProtocol(pd.getName()) + ".dat.gz");
		if (f.exists()) {
			try {
				InputStream is = new GZIPInputStream(new FileInputStream(f));

				BinaryPart part = BinaryPartUtil.readPartFromInput(is);
				if (part.getOpcode() != 0) {
					code = 18;
				} else {
					String name = part.getString();
					String pass = part.getString();
					String hex = DigestUtils.shaHex(pd.getPassword());
					if (!name.equals(NameUtils.formatName(pd.getName()))) {
						code = 3;
					}
					if (!pass.equals(hex)) {
						code = 3;
					}
				}
				is.close();
			} catch (IOException ex) {
				code = 18;
			}
		}
		if (code == 2) {
			player = new Player(pd);
		}
		return new LoginResult(code, player);
	}

	@Override
	public boolean savePlayer(Player player) {
		try {
			OutputStream os = new GZIPOutputStream(new FileOutputStream(
					"data/savedGames/"
							+ NameUtils.formatNameForProtocol(player.getName())
							+ ".dat.gz"));
			ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
			player.serialize(buffer);
			byte[] data = new byte[buffer.readableBytes()];
			buffer.readBytes(data);
			os.write(data);
			os.flush();
			os.close();
			return true;
		} catch (IOException ex) {
			return false;
		}
	}

	@Override
	public boolean loadPlayer(Player player) {
		try {
			File f = new File("data/savedGames/"
					+ NameUtils.formatNameForProtocol(player.getName())
					+ ".dat.gz");
			InputStream is = new GZIPInputStream(new FileInputStream(f));
			ChannelBuffer buffer = ChannelBuffers.buffer((int) f.length());
			while (true) {
				byte[] temp = new byte[1024];
				int read = is.read(temp, 0, temp.length);
				if (read == -1) {
					break;
				} else {
					buffer.writeBytes(temp, 0, read);
				}
			}
			is.close();
			player.deserialize(buffer);
			return true;
		} catch (IOException ex) {
			return false;
		}
	}

}

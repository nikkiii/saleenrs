package org.saleen.rs2.packet;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.saleen.rs2.database.DatabaseConnection;
import org.saleen.rs2.model.Player;
import org.saleen.rs2.model.World;
import org.saleen.rs2.net.Packet;
import org.saleen.rs2.util.NameUtils;

public class ReportAbusePacketHandler implements PacketHandler {

	@Override
	public void handle(Player player, Packet packet) {
		String name = NameUtils.longToName(packet.getLong());
		int rule = packet.get();
		boolean mute = (packet.get() == 1);
		if (!World.getWorld().isPlayerOnline(name)) {
			player.getActionSender().sendMessage("This player is not online.");
			return;
		}
		if (mute) {
			if (player.getRights().toInteger() >= 1) {
				// TODO mute
			} else {
				// They are trying to cheat a mute!
			}
		}
		try {
			DatabaseConnection connection = World.getWorld()
					.getConnectionPool().nextFree();

			PreparedStatement stmt = connection
					.prepareStatement("INSERT INTO reports(`time`, `reportinguser`, `reporteduser`, `rule`, `mute`) VALUES(?, ?, ?, ?, ?)");
			stmt.setInt(1, (int) (System.currentTimeMillis() / 1000));
			stmt.setString(2, NameUtils.formatNameForProtocol(player.getName()));
			stmt.setString(3, name);
			stmt.setInt(4, rule);
			stmt.setBoolean(5, mute);
			stmt.executeUpdate();
			stmt.close();

			connection.returnConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		player.getActionSender().sendMessage(
				"Thank-you, your abuse report has been received.");
	}
}

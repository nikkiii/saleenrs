package org.saleen.rs2.content.dialogue;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.util.HashMap;
import java.util.Map;

import org.saleen.rs2.model.Player;
import org.saleen.rs2.script.FileScriptManager;
import org.saleen.util.Buffers;

/**
 * Dialogue handling class, handles quest etc dialogues via xml
 * 
 * @author Nikki
 */
public class DialogueManager {

	/**
	 * The manager instance
	 */
	private static DialogueManager managerInstance = new DialogueManager();

	/**
	 * The script manager instance
	 */
	private FileScriptManager scriptManager = FileScriptManager
			.getScriptManager();

	/**
	 * The map of dialogues, key = package, list = dialogue list
	 */
	private HashMap<String, Map<String, Dialogue>> dialogues = new HashMap<String, Map<String, Dialogue>>();

	/**
	 * Open a dialogue for a player
	 * 
	 * @param player
	 *            The player to open for
	 * @param source
	 *            The source package
	 * @param dialogue
	 *            The dialogue id to open
	 */
	public void openDialogue(Player player, String source, String name) {
		Dialogue dialogue = dialogues.get(source).get(name);
		player.setCurrentDialogue(dialogue);
	}

	/**
	 * Have a dialogue handle a button, for continue or other
	 * 
	 * @param player
	 *            The player to handle for
	 * @param button
	 *            The button to handle
	 */
	public void handleButton(Player player, int button) {
		Dialogue dialogue = player.getCurrentDialogue();
		scriptManager.invoke("dialogues." + dialogue.getName() + ".button",
				player, button);
	}

	/**
	 * Load the dialogues from xml
	 * 
	 * @param directory
	 *            The directory to list and load from
	 */
	public void load(File directory) {
		try {
			for (File file : directory.listFiles()) {
				if (!file.isDirectory()) {
					// Directories ignored for now..
					String name = file.getName().substring(0,
							file.getName().indexOf("."));
					dialogues.put(name, readDialogues(file));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Read the dialogues from the specified binary file
	 * 
	 * @param file
	 *            The file to load from
	 * @return The map of dialogues, name to dialogue instance
	 * @throws IOException
	 *             If an error occurred
	 */
	public Map<String, Dialogue> readDialogues(File file) throws IOException {
		Map<String, Dialogue> dialogues = new HashMap<String, Dialogue>();
		RandomAccessFile raf = new RandomAccessFile(file, "r");
		try {
			ByteBuffer buffer = raf.getChannel().map(MapMode.READ_ONLY, 0,
					raf.length());
			int count = buffer.getShort();
			for (int i = 0; i < count; i++) {
				String name = Buffers.getString(buffer);

				int lineCount = buffer.get();
				String[] lines = new String[lineCount];
				for (int il = 0; il < lineCount; il++) {
					lines[il] = Buffers.getString(buffer);
				}

				int npcId = buffer.getShort();

				int type = buffer.get();

				boolean hasNext = buffer.get() == 1;
				String next = "null";
				if (hasNext) {
					next = Buffers.getString(buffer);
				}
				dialogues.put(name,
						new Dialogue(name, type, lines, npcId, next));
			}
		} finally {
			raf.close();
		}
		return dialogues;
	}

	/**
	 * Get the manager instance
	 * 
	 * @return The instance of this manager
	 */
	public static DialogueManager getManager() {
		return managerInstance;
	}
}

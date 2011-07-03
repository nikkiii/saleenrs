package org.saleen.util.pluginmanager;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.UIManager;

import org.saleen.util.FileUtils;
import org.saleen.util.Filter;
import org.saleen.util.Streams;
import org.saleen.util.XStreamController;

/**
 * A PluginManager which can install and uninstall plugins
 * 
 * @author Nikki
 * 
 */
public class PluginManager {

	/**
	 * The main entry point
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		new PluginManager();
	}

	/**
	 * The main frame
	 */
	private JFrame frame;

	/**
	 * The panel which contains all plugin functions
	 */
	private PluginPanel panel;

	/**
	 * The list of plugin categories
	 */
	private List<PluginCategory> categoryList = new LinkedList<PluginCategory>();

	/**
	 * The list of installed plugins
	 */
	private List<PluginManifest> installed = new LinkedList<PluginManifest>();

	/**
	 * The map of PluginManifest -> Repository
	 */
	private Map<PluginManifest, Repository> mappings = new HashMap<PluginManifest, Repository>();

	/**
	 * The list of repositories
	 */
	private List<Repository> repositories;

	/**
	 * The main plugin category, for "All" plugins
	 */
	private PluginCategory mainCategory;

	/**
	 * Initiate a PluginManager instance
	 */
	public PluginManager() {
		lookAndFeel();
		initFrame();
		try {
			loadRepositories();
			loadInstalledPlugins();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Add a repository to the manager, then write the repositories to a file
	 * again.
	 * 
	 * @param repo
	 *            The repository
	 */
	public void addRepository(Repository repo) {
		repositories.add(repo);
		writeRepos();
		loadRepository(repo);
	}

	/**
	 * Build the menubar
	 * 
	 * @return The JMenuBar with all menus added
	 */
	private JMenuBar buildMenu() {
		JMenuBar bar = new JMenuBar();
		JMenu file = new JMenu("File");
		JMenuItem exit = new JMenuItem("Exit");
		file.add(exit);
		bar.add(file);

		JMenu server = new JMenu("Server");
		JMenuItem conn = new JMenuItem("Connect");
		conn.setIcon(new ImageIcon("data/resource/pluginmanager/connect.png"));
		server.add(conn);
		bar.add(server);

		JMenu repos = new JMenu("Repositories");
		JMenuItem addRepo = new JMenuItem("Add Repository");
		addRepo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openAddDialog();
			}
		});
		repos.add(addRepo);
		JMenuItem editRepos = new JMenuItem("Edit Repositories");
		editRepos.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openList();
			}
		});
		repos.add(editRepos);
		bar.add(repos);
		return bar;
	}

	/**
	 * Get the frame
	 * 
	 * @return The frame
	 */
	public Frame getFrame() {
		return frame;
	}

	/**
	 * Initialize the frame
	 */
	private void initFrame() {
		frame = new JFrame("Saleen Plugin Manager");
		frame.setIconImage(new javax.swing.ImageIcon(
				"data/resource/pluginmanager/plugin.png").getImage());
		panel = new PluginPanel(this);
		frame.add(buildMenu(), BorderLayout.NORTH);
		frame.add(panel, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * Load installed plugins
	 */
	private void loadInstalledPlugins() {
		List<File> plugins = FileUtils.list(new File("plugins"),
				new Filter<File>() {
					@Override
					public boolean accept(File t) {
						return t.getName().endsWith(".jar");
					}
				});

		for (File file : plugins) {
			PluginManifest manifest = find(file.getName());
			if (manifest != null) {
				installed.add(manifest);
			}
		}
		panel.setInstalledCount(installed.size());
	}

	/**
	 * Find a plugin manifest for the file name
	 * 
	 * @param formatted
	 *            The string which we check against both name and formatted name
	 * @return The plugin if found
	 */
	public PluginManifest find(String formatted) {
		for (PluginManifest manifest : mappings.keySet()) {
			if (manifest.getName().equals(formatted)
					|| manifest.getFormattedName().equals(formatted)) {
				return manifest;
			}
		}
		return null;
	}

	/**
	 * Load repositories from their urls
	 * 
	 * @throws IOException
	 *             If an error occurred
	 */
	@SuppressWarnings("unchecked")
	private void loadRepositories() throws IOException {
		categoryList.clear();

		// Create the main category
		mainCategory = new PluginCategory("All",
				new LinkedList<PluginManifest>());
		categoryList.add(mainCategory);

		FileInputStream input = new FileInputStream(
				"data/resource/pluginmanager/repos.xml");

		repositories = new LinkedList<Repository>();

		// Initialize repositories
		try {
			repositories.addAll((List<Repository>) XStreamController
					.getXStream().fromXML(input));
		} finally {
			input.close();
		}

		for (Repository repo : repositories) {
			loadRepository(repo);
		}

		panel.setRepositoryCount(repositories.size());
	}

	/**
	 * Load a specific repository
	 * 
	 * @param repo
	 *            The repository
	 */
	@SuppressWarnings("unchecked")
	private void loadRepository(Repository repo) {
		try {
			// Main repo
			List<PluginCategory> tempList = (List<PluginCategory>) XStreamController
					.getXStream().fromXML(new URL(repo.getUrl()).openStream());

			List<PluginManifest> allList = new LinkedList<PluginManifest>();
			for (PluginCategory cat : tempList) {
				for (PluginManifest manifest : cat.getPlugins()) {
					mappings.put(manifest, repo);
					allList.add(manifest);
				}
			}
			mainCategory.getPlugins().addAll(allList);

			categoryList.addAll(tempList);

			panel.setCategories(categoryList);
			panel.setRepositoryCount(repositories.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Set the look and feel..
	 */
	private void lookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the "Add Repository" dialog
	 */
	protected void openAddDialog() {
		final RepositoryAdd add = new RepositoryAdd(frame, true, false);
		add.getConfirm().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addRepository(add.toRepository());
				add.dispose();
			}
		});
		add.setVisible(true);
	}

	/**
	 * Open the Repository List dialog
	 */
	private void openList() {
		RepositoryList list = new RepositoryList(this, true);
		list.setRepositories(repositories);
		list.setVisible(true);
	}

	/**
	 * Remove a repository
	 * 
	 * @param repo
	 *            The repository
	 */
	public void removeRepository(Repository repo) {
		repositories.remove(repo);
		writeRepos();
		try {
			loadRepositories();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Set the repository list
	 * 
	 * @param repositories
	 *            The list of repositorys
	 * @param write
	 *            Whether to re-write the list
	 */
	public void setRepositories(List<Repository> repositories, boolean write) {
		this.repositories = repositories;
		if (write) {
			writeRepos();
		}
		try {
			loadRepositories();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * Write the repositories to file
	 */
	public void writeRepos() {
		try {
			FileOutputStream output = new FileOutputStream(
					"data/resource/pluginmanager/repos.xml");
			try {
				XStreamController.getXStream().toXML(repositories, output);
			} finally {
				output.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Update the plugin counts
	 */
	public void updateCounts() {
		panel.setInstalledCount(installed.size());
		panel.setRepositoryCount(repositories.size());
	}

	/**
	 * Install a specific plugin
	 * 
	 * @param plugin
	 *            The plugin data file
	 * @return If successfully installed, true
	 */
	public boolean install(PluginManifest plugin) {
		try {
			Repository repo = mappings.get(plugin);
			if (repo != null) {
				String name = plugin.getFormattedName();
				name = URLEncoder.encode(name, "UTF-8");
				URL url = new URL(repo.getDownloadBase() + "/" + name);

				InputStream input = url.openStream();
				FileOutputStream output = new FileOutputStream("plugins/"
						+ name);
				try {
					Streams.copy(input, output);
				} finally {
					input.close();
					output.close();
				}
				installed.add(plugin);
				updateCounts();
			} else {
				return false;
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Uninstall a specific plugin
	 * 
	 * @param manifest
	 *            The plugin manifest
	 * @return True, if uninstalled
	 */
	public boolean uninstall(PluginManifest manifest) {
		File file = new File("plugins", manifest.getFormattedName());
		if (file.exists()) {
			if (file.delete()) {
				installed.remove(manifest);
				updateCounts();
				return true;
			}
		}
		return false;
	}

	/**
	 * Check if a plugin is installed
	 * 
	 * @param manifest
	 *            The plugin manifest
	 * @return If installed
	 */
	public boolean isInstalled(PluginManifest manifest) {
		return installed.contains(manifest);
	}

	/**
	 * Update a plugin by first uninstalling then installing again..
	 * 
	 * @param manifest
	 *            The manifest
	 * @return True, if reinstalled
	 */
	public boolean update(PluginManifest manifest) {
		if (uninstall(manifest)) {
			return install(manifest);
		} else {
			// We are installing it :|
			return install(manifest);
		}
	}
}

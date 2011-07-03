package org.saleen.util.pluginmanager;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 * A JPanel which contains all plugin basics.. Don't even ask me to document
 * this class -.-
 * 
 * @author Nikki
 */
@SuppressWarnings("serial")
public class PluginPanel extends javax.swing.JPanel {

	public static ImageIcon loadIcon(String string) {
		return new ImageIcon("data/resource/pluginmanager/" + string);
	}

	private ImageIcon closedIcon = loadIcon("book.png");

	private ImageIcon openIcon = loadIcon("book_open.png");

	// Variables declaration - do not modify
	private javax.swing.JButton searchButton;
	private javax.swing.JButton installButton;

	private javax.swing.JButton updateButton;

	private javax.swing.JButton uninstallButton;

	private javax.swing.JLabel categoryLabel;

	private javax.swing.JLabel packageLabel;

	private javax.swing.JLabel descriptionLabel;

	private javax.swing.JLabel statusLabel;

	private javax.swing.JLabel totalLabel;
	private javax.swing.JLabel installedLabel;
	private javax.swing.JLabel repoCountLabel;
	private javax.swing.JList jList1;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JScrollPane jScrollPane2;
	private javax.swing.JScrollPane jScrollPane3;
	private javax.swing.JTable jTable1;
	private javax.swing.JTextArea descriptionArea;
	private javax.swing.JTextField jTextField1;
	private DefaultListModel jList1Model;
	private DefaultTableModel jTable1Model;
	private TableRowSorter<DefaultTableModel> sorter;

	private PluginManager parent;

	// End of variables declaration
	public PluginPanel(PluginManager parent) {
		this.parent = parent;
		initComponents();
	}

	private void clearRows() {
		int count = jTable1Model.getRowCount();
		for (int row = 0; row < count; row++) {
			jTable1Model.removeRow(0);
		}
	}

	private void initComponents() {

		categoryLabel = new javax.swing.JLabel();
		jScrollPane1 = new javax.swing.JScrollPane();
		jList1 = new javax.swing.JList();
		packageLabel = new javax.swing.JLabel();
		jScrollPane2 = new javax.swing.JScrollPane();
		jTable1 = new javax.swing.JTable();
		descriptionLabel = new javax.swing.JLabel();
		jScrollPane3 = new javax.swing.JScrollPane();
		descriptionArea = new javax.swing.JTextArea();
		searchButton = new javax.swing.JButton();
		jTextField1 = new javax.swing.JTextField();
		installButton = new javax.swing.JButton();
		updateButton = new javax.swing.JButton();
		uninstallButton = new javax.swing.JButton();
		statusLabel = new javax.swing.JLabel();
		totalLabel = new javax.swing.JLabel();
		installedLabel = new javax.swing.JLabel();
		repoCountLabel = new javax.swing.JLabel();

		setPreferredSize(new java.awt.Dimension(880, 510));

		categoryLabel.setIcon(loadIcon("table_multiple.png")); // NOI18N
		categoryLabel.setText("Plugin Categories");

		jScrollPane1.setViewportView(jList1);

		jList1Model = new DefaultListModel();
		jList1.setModel(jList1Model);
		jList1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jList1.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					updatePluginList((PluginCategory) jList1.getSelectedValue());
				}
			}
		});

		packageLabel.setIcon(loadIcon("package.png")); // NOI18N
		packageLabel.setText("Plugins");

		jTable1Model = new PluginTableModel();
		jTable1.setModel(jTable1Model);
		jTable1.getColumnModel().getColumn(3)
				.setCellRenderer(new PluginCellRenderer());
		sorter = new TableRowSorter<DefaultTableModel>(jTable1Model);
		jTable1.setRowSorter(sorter);
		jScrollPane2.setViewportView(jTable1);

		jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		jTable1.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {

					@Override
					public void valueChanged(ListSelectionEvent e) {
						if (!e.getValueIsAdjusting()) {
							rowSelected(jTable1.getSelectedRow());
						}
					}
				});

		descriptionLabel.setIcon(loadIcon("book.png")); // NOI18N
		descriptionLabel.setText("Description");

		descriptionArea.setColumns(20);
		descriptionArea.setEditable(false);
		descriptionArea.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
		descriptionArea.setRows(5);
		descriptionArea
				.setText("Select a plugin from the list above to see a description.");
		jScrollPane3.setViewportView(descriptionArea);

		searchButton.setIcon(loadIcon("magnifier.png")); // NOI18N
		searchButton.setText("Search");
		searchButton.setBorder(null);
		searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				searchActionPerformed(e);
			}
		});

		installButton.setIcon(loadIcon("package_add.png")); // NOI18N
		installButton.setText("Install");
		installButton.setBorder(null);
		installButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (jList1.getSelectedValue() != null) {
					int row = jTable1.getSelectedRow();
					if (row != -1) {
						PluginManifest manifest = (PluginManifest) jTable1Model
								.getValueAt(row, 0);
						if (manifest != null) {
							if (parent.install(manifest)) {
								updateInstallStatus(manifest);
								jTable1.updateUI();
								JOptionPane.showMessageDialog(
										parent.getFrame(),
										"Plugin successfully installed!",
										"Plugin Installed",
										JOptionPane.INFORMATION_MESSAGE);
							}
						}
					}
				}
			}
		});

		updateButton.setIcon(loadIcon("package_green.png")); // NOI18N
		updateButton.setText("Update");
		updateButton.setBorder(null);
		updateButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (jList1.getSelectedValue() != null) {
					int row = jTable1.getSelectedRow();
					if (row != -1) {
						PluginManifest manifest = (PluginManifest) jTable1Model
								.getValueAt(row, 0);
						if (manifest != null) {
							if (parent.update(manifest)) {
								updateInstallStatus(manifest);
								jTable1.updateUI();
								JOptionPane.showMessageDialog(
										parent.getFrame(),
										"Plugin successfully updated",
										"Plugin Updated",
										JOptionPane.INFORMATION_MESSAGE);
							}
						}
					}
				}
			}
		});

		uninstallButton.setIcon(loadIcon("package_delete.png")); // NOI18N
		uninstallButton.setText("Uninstall");
		uninstallButton.setBorder(null);
		uninstallButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (jList1.getSelectedValue() != null) {
					int row = jTable1.getSelectedRow();
					if (row != -1) {
						PluginManifest manifest = (PluginManifest) jTable1Model
								.getValueAt(row, 0);
						if (manifest != null) {
							if (parent.uninstall(manifest)) {
								updateInstallStatus(manifest);
								jTable1.updateUI();
								JOptionPane.showMessageDialog(
										parent.getFrame(),
										"Plugin successfully uninstalled!",
										"Plugin Uninstalled",
										JOptionPane.INFORMATION_MESSAGE);
							}
						}
					}
				}
			}
		});
		updateButton.setEnabled(false);
		installButton.setEnabled(false);
		uninstallButton.setEnabled(false);

		statusLabel.setIcon(loadIcon("status_offline.png")); // NOI18N
		statusLabel.setText("Disconnected");

		totalLabel.setText("Total Plugins: 0");

		installedLabel.setText("Installed Plugins: 0");

		repoCountLabel.setText("Repositories: 0");

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
												.addGroup(
														layout.createSequentialGroup()
																.addGroup(
																		layout.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.LEADING)
																				.addGroup(
																						layout.createSequentialGroup()
																								.addComponent(
																										installButton,
																										javax.swing.GroupLayout.PREFERRED_SIZE,
																										64,
																										javax.swing.GroupLayout.PREFERRED_SIZE)
																								.addPreferredGap(
																										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																								.addComponent(
																										updateButton,
																										javax.swing.GroupLayout.PREFERRED_SIZE,
																										66,
																										javax.swing.GroupLayout.PREFERRED_SIZE)
																								.addPreferredGap(
																										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																								.addComponent(
																										uninstallButton,
																										javax.swing.GroupLayout.PREFERRED_SIZE,
																										71,
																										javax.swing.GroupLayout.PREFERRED_SIZE))
																				.addComponent(
																						categoryLabel)
																				.addComponent(
																						jScrollPane1,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						213,
																						Short.MAX_VALUE))
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addGroup(
																		layout.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.LEADING)
																				.addGroup(
																						javax.swing.GroupLayout.Alignment.TRAILING,
																						layout.createSequentialGroup()
																								.addComponent(
																										jTextField1,
																										javax.swing.GroupLayout.PREFERRED_SIZE,
																										121,
																										javax.swing.GroupLayout.PREFERRED_SIZE)
																								.addPreferredGap(
																										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																								.addComponent(
																										searchButton,
																										javax.swing.GroupLayout.PREFERRED_SIZE,
																										72,
																										javax.swing.GroupLayout.PREFERRED_SIZE))
																				.addComponent(
																						descriptionLabel)
																				.addComponent(
																						packageLabel)
																				.addComponent(
																						jScrollPane3,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						641,
																						Short.MAX_VALUE)
																				.addComponent(
																						jScrollPane2,
																						javax.swing.GroupLayout.Alignment.TRAILING,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						641,
																						Short.MAX_VALUE)))
												.addGroup(
														layout.createSequentialGroup()
																.addComponent(
																		statusLabel)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																		441,
																		Short.MAX_VALUE)
																.addComponent(
																		repoCountLabel,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		102,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		installedLabel,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		116,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		totalLabel,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		105,
																		javax.swing.GroupLayout.PREFERRED_SIZE)))
								.addContainerGap()));
		layout.setVerticalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(
														installButton,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														23,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(
														updateButton,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														22,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(
														uninstallButton,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														23,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(
														searchButton,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														22,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(
														jTextField1,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGap(5, 5, 5)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(
														packageLabel,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														16,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(categoryLabel))
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
												.addGroup(
														javax.swing.GroupLayout.Alignment.TRAILING,
														layout.createSequentialGroup()
																.addComponent(
																		jScrollPane2,
																		0,
																		0,
																		Short.MAX_VALUE)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		descriptionLabel,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		16,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		jScrollPane3,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		145,
																		javax.swing.GroupLayout.PREFERRED_SIZE))
												.addComponent(
														jScrollPane1,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														404, Short.MAX_VALUE))
								.addGap(18, 18, 18)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(statusLabel)
												.addComponent(totalLabel)
												.addComponent(installedLabel)
												.addComponent(repoCountLabel))
								.addGap(13, 13, 13)));
	}

	protected void rowSelected(int row) {
		if (row == -1) {
			descriptionArea
					.setText("Select a plugin from the list above to see a description.");
			descriptionLabel.setIcon(closedIcon);
			updateButton.setEnabled(false);
			installButton.setEnabled(false);
			uninstallButton.setEnabled(false);
		} else {
			PluginManifest manifest = (PluginManifest) jTable1Model.getValueAt(
					row, 0);
			descriptionArea.setText(manifest.getDescription());
			descriptionLabel.setIcon(openIcon);
			updateInstallStatus(manifest);
		}
	}

	public void updateInstallStatus(PluginManifest manifest) {
		boolean installed = parent.isInstalled(manifest);
		updateButton.setEnabled(installed);
		installButton.setEnabled(!installed);
		uninstallButton.setEnabled(installed);
	}

	protected void searchActionPerformed(ActionEvent e) {
		String term = jTextField1.getText();
		if (!term.equals("")) {
			RowFilter<DefaultTableModel, Object> rf = null;
			// If current expression doesn't parse, don't update.
			try {
				rf = RowFilter.regexFilter(term, 0, 1);
			} catch (java.util.regex.PatternSyntaxException ex) {
				return;
			}
			sorter.setRowFilter(rf);
		}
	}

	public void setCategories(List<PluginCategory> categories) {
		jList1Model.removeAllElements();
		clearRows();
		int pluginCount = 0;
		Iterator<PluginCategory> it$ = categories.iterator();
		while (it$.hasNext()) {
			PluginCategory category = it$.next();
			jList1Model.addElement(category);
			if (category.getName().equals("All"))
				pluginCount += category.getPlugins().size();
		}
		totalLabel.setText("Total plugins: " + pluginCount);
	}

	public void setRepositoryCount(int size) {
		repoCountLabel.setText("Repositories: " + size);
	}

	protected void updatePluginList(PluginCategory category) {
		if (category == null)
			return;
		clearRows();
		Iterator<PluginManifest> it$ = category.getPlugins().iterator();
		while (it$.hasNext()) {
			PluginManifest manifest = it$.next();
			jTable1Model.addRow(new Object[] { manifest, manifest.getAuthor(),
					manifest.getVersion(), new JLabel("Test") });
		}
	}

	public class PluginTableModel extends DefaultTableModel {

		public PluginTableModel() {
			super(new Object[][] {}, new String[] { "Name", "Author",
					"Version", "Status" });
		}

		private Class<?>[] types = new Class[] { PluginManifest.class,
				java.lang.String.class, java.lang.Double.class,
				java.lang.Object.class };

		private boolean[] canEdit = new boolean[] { false, false, false, false };

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return types[columnIndex];
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return canEdit[columnIndex];
		}
	}

	private ImageIcon installed = loadIcon("plugin.png");
	private ImageIcon disabled = loadIcon("plugin_disabled.png");

	public class PluginCellRenderer extends DefaultTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			PluginManifest manifest = (PluginManifest) jTable1Model.getValueAt(
					row, 0);
			if (parent.isInstalled(manifest)) {
				setText("Installed");
				setIcon(installed);
			} else {
				setText("Not installed");
				setIcon(disabled);
			}
			return this;
		}
	}

	public void setInstalledCount(int size) {
		installedLabel.setText("Installed plugins: " + size);
	}
}
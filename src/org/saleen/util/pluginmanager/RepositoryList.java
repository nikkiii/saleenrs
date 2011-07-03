package org.saleen.util.pluginmanager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

import javax.swing.WindowConstants;

/**
 * A simple dialog to edit repository lists Don't ask me to document it..
 * 
 * @author Nikki
 */
@SuppressWarnings("serial")
public class RepositoryList extends javax.swing.JDialog {

	private PluginManager parent;

	// Variables declaration - do not modify
	private javax.swing.JButton removeButton;

	private javax.swing.JButton editButton;

	private javax.swing.JButton addButton;

	private javax.swing.JButton doneButton;

	private javax.swing.JList jList1;

	private javax.swing.JScrollPane jScrollPane1;

	private javax.swing.DefaultListModel jList1Model;

	// End of variables declaration

	public RepositoryList(PluginManager parent, boolean modal) {
		super(parent.getFrame(), modal);
		this.parent = parent;
		initComponents();
		setLocationRelativeTo(parent.getFrame());
		setTitle("Repository List");
	}

	protected void addPerformed(ActionEvent e) {
		final RepositoryAdd add = new RepositoryAdd(new java.awt.Frame(), true,
				false);
		add.getConfirm().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jList1Model.addElement(add.toRepository());
				add.dispose();
			}
		});
		add.setVisible(true);
	}

	protected void donePerformed(ActionEvent e) {
		Repository[] data = new Repository[jList1Model.size()];
		jList1Model.copyInto(data);
		parent.setRepositories(Arrays.asList(data), true);
		dispose();
	}

	protected void editPerformed(ActionEvent e) {
		final RepositoryAdd add = new RepositoryAdd(new java.awt.Frame(), true,
				true);
		final Repository repo = (Repository) jList1.getSelectedValue();
		add.setValues(repo);
		add.getConfirm().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("EDIT");
				int location = jList1Model.indexOf(repo);
				jList1Model.setElementAt(add.toRepository(), location);
				add.dispose();
			}
		});
		add.setVisible(true);
	}

	private void initComponents() {
		jScrollPane1 = new javax.swing.JScrollPane();
		jList1 = new javax.swing.JList();
		removeButton = new javax.swing.JButton();
		editButton = new javax.swing.JButton();
		addButton = new javax.swing.JButton();
		doneButton = new javax.swing.JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

		jList1Model = new javax.swing.DefaultListModel();
		jList1.setModel(jList1Model);

		jScrollPane1.setViewportView(jList1);

		removeButton.setIcon(PluginPanel.loadIcon("script_delete.png")); // NOI18N
		removeButton.setText("Remove");
		removeButton.setBorder(null);
		removeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (jList1.getSelectedValue() != null) {
					removePerformed(e);
				}
			}
		});

		editButton.setIcon(PluginPanel.loadIcon("script_edit.png")); // NOI18N
		editButton.setText("Edit");
		editButton.setBorder(null);
		editButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (jList1.getSelectedValue() != null) {
					editPerformed(e);
				}
			}
		});

		addButton.setIcon(PluginPanel.loadIcon("script_add.png")); // NOI18N
		addButton.setText("Add");
		addButton.setBorder(null);
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addPerformed(e);
			}
		});

		doneButton.setIcon(PluginPanel.loadIcon("tick.png")); // NOI18N
		doneButton.setText("Done");
		doneButton.setBorder(null);
		doneButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				donePerformed(e);
			}
		});

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						javax.swing.GroupLayout.Alignment.TRAILING,
						layout.createSequentialGroup()
								.addGap(52, 52, 52)
								.addComponent(addButton,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										69,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(28, 28, 28)
								.addComponent(editButton,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										58,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(26, 26, 26)
								.addComponent(removeButton,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										78,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(28, 28, 28)
								.addComponent(doneButton,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										67,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(64, 64, 64))
				.addComponent(jScrollPane1,
						javax.swing.GroupLayout.DEFAULT_SIZE, 470,
						Short.MAX_VALUE));
		layout.setVerticalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						javax.swing.GroupLayout.Alignment.TRAILING,
						layout.createSequentialGroup()
								.addComponent(jScrollPane1,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										250, Short.MAX_VALUE)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(
														doneButton,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														19,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(
														addButton,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														20,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(
														editButton,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														20,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(
														removeButton,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														19,
														javax.swing.GroupLayout.PREFERRED_SIZE))
								.addContainerGap()));

		pack();
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	protected void removePerformed(ActionEvent e) {
		jList1Model.removeElement(jList1.getSelectedValue());
	}

	public void setRepositories(List<Repository> repositories) {
		for (Repository repo : repositories) {
			jList1Model.addElement(repo);
		}
	}

}

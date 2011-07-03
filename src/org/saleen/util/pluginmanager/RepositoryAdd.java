package org.saleen.util.pluginmanager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.WindowConstants;

/**
 * A simple addition panel for adding repositories... Don't ask me to document
 * it..
 * 
 * @author Nikki
 */
@SuppressWarnings("serial")
public class RepositoryAdd extends javax.swing.JDialog {

	// Variables declaration - do not modify
	private javax.swing.JButton confirmButton;

	private javax.swing.JButton cancelButton;

	private javax.swing.JLabel jLabel1;

	private javax.swing.JLabel jLabel2;

	private javax.swing.JTextField urlField;

	private javax.swing.JTextField commentField;

	// End of variables declaration

	public RepositoryAdd(java.awt.Frame parent, boolean modal, boolean edit) {
		super(parent, modal);
		initComponents();
		setLocationRelativeTo(parent);
		setTitle(edit ? "Edit Repository" : "Add Repository");
	}

	public javax.swing.JButton getCancel() {
		return cancelButton;
	}

	public javax.swing.JButton getConfirm() {
		return confirmButton;
	}

	private void initComponents() {

		jLabel1 = new javax.swing.JLabel();
		jLabel2 = new javax.swing.JLabel();
		urlField = new javax.swing.JTextField();
		commentField = new javax.swing.JTextField();
		confirmButton = new javax.swing.JButton();
		cancelButton = new javax.swing.JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

		jLabel1.setText("Repository URL:");

		jLabel2.setText("Repository Comment:");

		confirmButton.setIcon(PluginPanel.loadIcon("tick.png")); // NOI18N
		confirmButton.setText("Confirm");
		confirmButton.setBorder(null);

		cancelButton.setIcon(PluginPanel.loadIcon("delete.png")); // NOI18N
		cancelButton.setText("Cancel");
		cancelButton.setBorder(null);
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
												.addGroup(
														javax.swing.GroupLayout.Alignment.TRAILING,
														layout.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING,
																false)
																.addGroup(
																		layout.createSequentialGroup()
																				.addGap(10,
																						10,
																						10)
																				.addComponent(
																						urlField,
																						javax.swing.GroupLayout.DEFAULT_SIZE,
																						237,
																						Short.MAX_VALUE))
																.addComponent(
																		jLabel1)
																.addComponent(
																		jLabel2)
																.addGroup(
																		layout.createSequentialGroup()
																				.addGap(10,
																						10,
																						10)
																				.addComponent(
																						commentField)))
												.addGroup(
														javax.swing.GroupLayout.Alignment.TRAILING,
														layout.createSequentialGroup()
																.addComponent(
																		confirmButton,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		74,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		cancelButton,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		75,
																		javax.swing.GroupLayout.PREFERRED_SIZE)))
								.addContainerGap(10, Short.MAX_VALUE)));
		layout.setVerticalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addComponent(jLabel1)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(urlField,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(jLabel2)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(commentField,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(
														cancelButton,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														19,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(
														confirmButton,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														20,
														javax.swing.GroupLayout.PREFERRED_SIZE))
								.addContainerGap(16, Short.MAX_VALUE)));

		pack();
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	public void setValues(Repository repo) {
		urlField.setText(repo.getUrl());
		commentField.setText(repo.getComment());
	}

	public Repository toRepository() {
		return new Repository(urlField.getText(), commentField.getText());
	}

}

package edu.wmich.cs.aut.addressbook;

/*
    Copyright (c) 2009-11 froglogic GmbH. All rights reserved.

    This file is part of an example program for Squish---it may be used,
    distributed, and modified, without limitation.
*/

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.Math;
import java.util.*;
import java.util.regex.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.DefaultCellEditor;
import javax.swing.JFormattedTextField;



public class AddressBook extends JFrame
		implements ActionListener, KeyListener, TableModelListener {

	public static class MyFocusTraversalPolicy
			extends FocusTraversalPolicy {
		Vector order;

		public MyFocusTraversalPolicy(Vector order) {
			this.order = new Vector(order.size());
			this.order.addAll(order);
		}

		public Component getComponentAfter(Container focusCycleRoot,
										   Component component)
		{
			int index = (order.indexOf(component) + 1) % order.size();
			return (Component)order.get(index);
		}

		public Component getComponentBefore(Container focusCycleRoot,
											Component component)
		{
			int index = order.indexOf(component) - 1;
			if (index < 0) {
				index = order.size() - 1;
			}
			return (Component)order.get(index);
		}

		public Component getDefaultComponent(Container focusCycleRoot) {
			return (Component)order.get(0);
		}

		public Component getLastComponent(Container focusCycleRoot) {
			return (Component)order.lastElement();
		}

		public Component getFirstComponent(Container focusCycleRoot) {
			return (Component)order.get(0);
		}
	}

	public class AddAddressForm extends JDialog
			implements ActionListener, KeyListener {

		JButton okButton;
		JButton cancelButton;
		JTextField forenameText;
		JTextField surnameText;
		JTextField emailText;
		JTextField phoneText;
		boolean ok;
		String surname;
		String forename;
		String email;
		String phone;

		MyFocusTraversalPolicy focusPolicy;

		public AddAddressForm(Frame parent, String title) {
			super(parent, title, true);
			setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent we) {} });
			ok = false;
			JLabel forenameLabel = new JLabel("Forename:");
			forenameText = new JTextField("forename", 12);
			forenameText.addKeyListener(this);
			JLabel surnameLabel = new JLabel("Surname:");
			surnameText = new JTextField("surname", 12);
			surnameText.addKeyListener(this);
			JLabel emailLabel = new JLabel("Email:");
			emailText = new JTextField("email", 12);
			emailText.addKeyListener(this);
			JLabel phoneLabel = new JLabel("Phone:");
			phoneText = new JTextField("123456", 12);
			phoneText.addKeyListener(this);
			okButton = new JButton("OK");
			okButton.addActionListener(this);
			okButton.setEnabled(true);
			cancelButton = new JButton("Cancel");
			cancelButton.addActionListener(this);
			setLayout(new GridLayout(5, 2));
			add(forenameLabel);
			add(forenameText);
			add(surnameLabel);
			add(surnameText);
			add(emailLabel);
			add(emailText);
			add(phoneLabel);
			add(phoneText);
			add(okButton);
			add(cancelButton);
			pack();
			addComponentListener(new ComponentAdapter() {
				public void componentShown(ComponentEvent ce) {
					forenameText.requestFocusInWindow();
				}
			});
			Vector tabOrder = new Vector(6);
			tabOrder.add(forenameText);
			tabOrder.add(surnameText);
			tabOrder.add(emailText);
			tabOrder.add(phoneText);
			tabOrder.add(okButton);
			tabOrder.add(cancelButton);
			focusPolicy = new MyFocusTraversalPolicy(tabOrder);
			setFocusTraversalPolicy(focusPolicy);
		}

		public String getForename() {
			return forename;
		}

		public String getSurname() {
			return surname;
		}

		public String getEmail() {
			return email;
		}

		public String getPhone() {
			return phone;
		}

		public boolean getOK() {
			return ok;
		}

		public void keyTyped(KeyEvent event) {
		}

		public void keyPressed(KeyEvent event) {
		}

		public void keyReleased(KeyEvent event) {
			JTextField textField = (JTextField)event.getComponent();
			textField.setText(textField.getText().replaceAll("\\|", ""));
			boolean enable = true;
			if (forenameText.getText().trim().length() == 0)
				enable = false;
			else if (surnameText.getText().trim().length() == 0)
				enable = false;
			else if (emailText.getText().trim().length() == 0)
				enable = false;
			else if (phoneText.getText().trim().length() == 0)
				enable = false;
			okButton.setEnabled(enable);
		}

        private boolean checkInput() {
            boolean enable = true;
            if (forenameText.getText().trim().length() == 0)
                enable = false;
            else if (surnameText.getText().trim().length() == 0)
                enable = false;
            else if (emailText.getText().trim().length() == 0)
                enable = false;
            else if (phoneText.getText().trim().length() == 0)
                enable = false;
            return enable;
        }

		public void actionPerformed(ActionEvent event) {
			String command = event.getActionCommand();
			ok = command.equals("OK");
			if (ok) {
                if (checkInput()) {
                    forename = forenameText.getText();
                    surname = surnameText.getText();
                    email = emailText.getText();
                    phone = phoneText.getText();
                    forenameText.setText(null);
                    surnameText.setText(null);
                    emailText.setText(null);
                    phoneText.setText(null);
                }
            }
			setVisible(false);
		}
	}

	JTable table;
	JLabel statusLabel;
	java.util.Timer timer;
	UpdateStatusTask updateStatusTask;
	boolean dirty = false;
	boolean dontSetDirty = false;
	String filename = "";
	static Object[] ColumnNames = {"Forename", "Surname", "Email", "Phone"};
	JMenuItem fileSaveItem;
	JMenuItem fileSaveAsItem;
	JMenu editMenu;
	JMenuItem editAddItem;
	JMenuItem editRemoveItem;
	AddAddressForm addAddressForm = null;
	ListSelectionModel tableSelectionModel;

	class SelectionHandler implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent event) {
			ListSelectionModel model = (ListSelectionModel)event.getSource();
			editRemoveItem.setEnabled(!model.isSelectionEmpty());
		}
	}

	public AddressBook() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		JMenuItem fileNewItem = new JMenuItem("New...");
		fileNewItem.setMnemonic(KeyEvent.VK_N);
		JMenuItem fileOpenItem = new JMenuItem("Open...");
		fileOpenItem.setMnemonic(KeyEvent.VK_O);
		fileSaveItem = new JMenuItem("Save");
		fileSaveItem.setMnemonic(KeyEvent.VK_S);
		fileSaveAsItem = new JMenuItem("Save As...");
		fileSaveAsItem.setMnemonic(KeyEvent.VK_A);
		JMenuItem fileQuitItem = new JMenuItem("Quit");
		fileQuitItem.setMnemonic(KeyEvent.VK_Q);
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		fileMenu.add(fileNewItem);
		fileMenu.add(fileOpenItem);
		fileMenu.add(fileSaveItem);
		fileMenu.add(fileSaveAsItem);
		fileMenu.add(fileQuitItem);
		menuBar.add(fileMenu);
		editAddItem = new JMenuItem("Add...");
		editAddItem.setMnemonic(KeyEvent.VK_A);
		editRemoveItem = new JMenuItem("Remove...");
		editRemoveItem.setMnemonic(KeyEvent.VK_R);
		editRemoveItem.setEnabled(false);
		editMenu = new JMenu("Edit");
		editMenu.setMnemonic(KeyEvent.VK_E);
		editMenu.add(editAddItem);
		editMenu.add(editRemoveItem);
		menuBar.add(editMenu);

		fileNewItem.addActionListener(this);
		fileOpenItem.addActionListener(this);
		fileSaveItem.addActionListener(this);
		fileSaveAsItem.addActionListener(this);
		fileQuitItem.addActionListener(this);
		editAddItem.addActionListener(this);
		editRemoveItem.addActionListener(this);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				if (okToContinue()) {
					//    System.exit(0);
				}
			}});

		table = new JTable();
		table.addKeyListener(this);
		tableSelectionModel = table.getSelectionModel();
		tableSelectionModel.addListSelectionListener(new SelectionHandler());
		table.setSelectionModel(tableSelectionModel);
		JScrollPane tableScroller = new JScrollPane(table);

		timer = new java.util.Timer();
		statusLabel = new JLabel();
		updateStatus("Ready");

		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(tableScroller);
		contentPane.add(statusLabel, BorderLayout.SOUTH);

		setTitle("Address Book");
		updateUi();
	}


	public void updateUi() {
		boolean editable = table.getRowCount() > 0 ||
				getTitle().endsWith("Unnamed");
		editMenu.setEnabled(editable);
		editAddItem.setEnabled(editable);
		editRemoveItem.setEnabled(table.getSelectedRowCount() > 0);
		fileSaveItem.setEnabled(dirty);
		fileSaveAsItem.setEnabled(table.getRowCount() > 0);
	}


	public void setDirty(boolean on) {
		dirty = on;
		updateUi();
	}

	public void keyTyped(KeyEvent event) {
	}

	public void keyPressed(KeyEvent event) {
	}

	public void keyReleased(KeyEvent event) {
		final int row = table.getSelectedRow();
		final int column = table.getSelectedColumn();
		if (row > -1 && column > -1) {
			String text = table.getValueAt(row, column).toString();
			boolean originalDontSetDirty = dontSetDirty;
			dontSetDirty = true;
			table.setValueAt(text.replaceAll("\\|", ""), row, column);
			dontSetDirty = originalDontSetDirty;
		}
	}

	public void actionPerformed(ActionEvent event) {
		String command = event.getActionCommand();
		if (command.equals("New..."))
			fileNew();
		else if (command.equals("Open...")) {
			//	fileOpen();
		}
		else if (command.equals("Save")) {
			//	fileSave();
		}
		else if (command.equals("Save As...")) {
			//	fileSaveAs();
		}
		else if (command.equals("Quit")) {
			if (okToContinue()) {
				//System.exit(0);
			}
		} else if (command.equals("Add..."))
			editAdd();
		else if (command.equals("Remove..."))
			editRemove();
		else {
			//  System.out.println(command);
		}
	}

	private void fileNew() {
		if (!okToContinue()) {
			return;
		}
		DefaultTableModel tableModel = new DefaultTableModel(ColumnNames,
				0);
		table.setModel(tableModel);
		table.getModel().addTableModelListener(this);
		setTitle("Address Book - Unnamed");
		filename = "";
		setDirty(false);
		updateStatus("Created new empty unnamed addressbook");
	}


	private void fileOpen() {
		if (!okToContinue()) {
			return;
		}
		JFileChooser chooser = new JFileChooser(".");
		if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
			return;
		}
		filename = chooser.getCurrentDirectory().toString() +
				File.separator + chooser.getSelectedFile().getName();
		setTitle("Address Book - " + chooser.getSelectedFile().getName());
		load(filename);
	}


	private void load(final String filename) {
		DefaultTableModel tableModel = new DefaultTableModel(ColumnNames,
				0);
		int row = 0;
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(filename));
			String line;
			while ((line = reader.readLine()) != null) {
				String[] fields = line.split("[|]");
				tableModel.setRowCount(row + 1);
				for (int column = 0; column < fields.length; ++column) {
					tableModel.setValueAt(fields[column], row, column);
				}
				++row;
			}
			reader.close();
			table.setModel(tableModel);
			table.getModel().addTableModelListener(this);
			table.grabFocus();
			ListSelectionModel selectionModel = table.getSelectionModel();
			selectionModel.addSelectionInterval(0, 0);
			setDirty(false);
			updateStatus("Loaded " + filename);
		}
		catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(this, "Failed to find " +
							filename, "Address Book - Load Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Failed to load " +
							filename, "Address Book - Load Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
	}


	private boolean fileSave() {
		dontSetDirty = true;
		if (filename.length() == 0) {
			return fileSaveAs();
		}
		BufferedWriter writer;
		try {
			TableModel tableModel = table.getModel();
			writer = new BufferedWriter(new FileWriter(filename));
			for (int row = 0; row < tableModel.getRowCount(); ++row) {
				for (int column = 0; column < tableModel.getColumnCount();
					 ++column) {
					if (column > 0) {
						writer.write("|");
					}
					writer.write(tableModel.getValueAt(row, column)
							.toString().replaceAll("\\|", ""));
				}
				writer.newLine();
			}
			writer.close();
		}
		catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Failed to save " +
							filename, "Address Book - Save Error",
					JOptionPane.ERROR_MESSAGE);
			dontSetDirty = false;
			return false;
		}
		dontSetDirty = false;
		setDirty(false);
		updateStatus("Saved " + filename);
		return true;
	}


	private boolean fileSaveAs() {
		JFileChooser chooser = new JFileChooser(".");
		if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
			return false;
		}
		filename = chooser.getCurrentDirectory().toString() +
				File.separator + chooser.getSelectedFile().getName();
		setTitle("Address Book - " + chooser.getSelectedFile().getName());
		if (!filename.toLowerCase().endsWith(".adr")) {
			filename = filename + ".adr";
		}
		return fileSave();
	}


	private void editAdd() {
		if (addAddressForm == null)
			addAddressForm = new AddAddressForm(this,
					"Address Book - Add");
		addAddressForm.setLocationRelativeTo(this);
		addAddressForm.setVisible(true);
		if (!addAddressForm.getOK())
			return;
		String forename = addAddressForm.getForename();
		String surname = addAddressForm.getSurname();
		String email = addAddressForm.getEmail();
		String phone = addAddressForm.getPhone();
		if (forename.length() == 0 || surname.length() == 0)
			return;
		DefaultTableModel tableModel = (DefaultTableModel)table.getModel();
		int row = tableModel.getRowCount();
		if (row == 0) {
			fileNew();
			tableModel = (DefaultTableModel)table.getModel();
		}
		ListSelectionModel selectionModel = table.getSelectionModel();
		row = selectionModel.getMinSelectionIndex();
		if (row < 0)
			row = 0;
		stopEditing();
		Object[] address = {forename, surname, email, phone};
		tableModel.insertRow(row, address);
		selectionModel.clearSelection();
		selectionModel.addSelectionInterval(row, row);
		table.grabFocus();
		setDirty(true);
		updateStatus("Added " + forename + " " + surname);
	}


	private void editRemove() {
		DefaultTableModel tableModel = (DefaultTableModel)table.getModel();
		if (tableModel.getRowCount() == 0) {
			return;
		}
		ListSelectionModel selectionModel = table.getSelectionModel();
		int row = selectionModel.getMinSelectionIndex();
		if (row == -1) {
			return;
		}
		selectionModel.clearSelection();
		String forename = tableModel.getValueAt(row, 0).toString();
		String surname = tableModel.getValueAt(row, 1).toString();
		int choice = JOptionPane.showInternalConfirmDialog(
				getContentPane(),
				"Delete " + forename + " " + surname + "?",
				"Address Book - Remove", JOptionPane.YES_NO_OPTION);
		if (choice == JOptionPane.NO_OPTION) {
			return;
		}
		stopEditing();
		tableModel.removeRow(row);
		row = row < tableModel.getRowCount()
				? row : tableModel.getRowCount() - 1;
		selectionModel.addSelectionInterval(row, row);
		table.grabFocus();
		setDirty(true);
		updateStatus("Removed " + forename + " " + surname);
	}


	private void stopEditing() {
		int editingRow = table.getEditingRow();
		int column = table.getEditingColumn();
		if (editingRow > -1 && column > -1) {
			TableCellEditor editor = table.getCellEditor(editingRow,
					column);
			editor.stopCellEditing();
		}
	}


	private boolean okToContinue() {
		if (!dirty) {
			return true;
		}
		int reply = JOptionPane.showInternalConfirmDialog(
				getContentPane(), "Save unsaved changes?", "Address Book",
				JOptionPane.YES_NO_CANCEL_OPTION);
		if (reply == JOptionPane.YES_OPTION) {
			return fileSave();
		}
		if (reply == JOptionPane.CANCEL_OPTION) {
			return false;
		}
		return true;
	}


	public void tableChanged(TableModelEvent event) {
		if (!dontSetDirty)
			setDirty(true);
	}


	public void updateStatus(String message) {
		statusLabel.setText(message);
		if (updateStatusTask != null) {
			updateStatusTask.cancel();
		}
		updateStatusTask = new UpdateStatusTask();
		timer.schedule(updateStatusTask, 5 * 1000);
	}


	class UpdateStatusTask extends TimerTask {
		public void run() {
			statusLabel.setText("");
		}
	}


	private static void createAndShowGUI() {
		AddressBook addressBook = new AddressBook();
		addressBook.pack();
		addressBook.setSize(800, 600);
		addressBook.setLocation(300, 200); // for ease of testing
		addressBook.setVisible(true);
	}


	public static void main(String[] args) {
		java.util.Locale.setDefault(java.util.Locale.ENGLISH);
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
}

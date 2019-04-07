package edu.wmich.cs.aut.payment;/*
    Copyright (c) 2008 froglogic GmbH. All rights reserved.

    This file is part of an example program for Squish---it may be used,
    distributed, and modified, without limitation.
*/

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PaymentFormJSpinner extends JFrame
		implements ActionListener, ChangeListener {

	class MyDocumentListener implements DocumentListener {
		private PaymentFormJSpinner paymentForm;

		public MyDocumentListener(PaymentFormJSpinner paymentForm) {
			this.paymentForm = paymentForm;
		}

		public void insertUpdate(DocumentEvent event) {
			paymentForm.updateUi();
		}

		public void removeUpdate(DocumentEvent event) {
			paymentForm.updateUi();
		}

		public void changedUpdate(DocumentEvent event) {
			// Not relevant for plain text documents
		}
	}

	JButton payButton;
	JButton cancelButton;
	JTabbedPane tabbedPane;
	JComponent cashComponent;
	JComponent checkComponent;
	JComponent cardComponent;
	JComboBox invoiceComboBox;
	JTextField descriptionTextField;
	JLabel amountDueAmountLabel;
	JSpinner amountSpinner;
	JTextField bankNameTextField;
	JTextField bankNumberTextField;
	JTextField bankAccountNameTextField;
	JTextField bankAccountNumberTextField;
	JSpinner checkDateSpinner;
	JCheckBox checkSignedCheckBox;
	JComboBox cardTypeComboBox;
	JTextField cardAccountNameTextField;
	JTextField cardAccountNumberTextField;
	JSpinner cardIssueDateSpinner;
	JSpinner cardExpiryDateSpinner;
	final int CASH_TAB = 0;
	final int CHECK_TAB = 1;
	final int CREDIT_CARD_TAB = 2;

	public PaymentFormJSpinner() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new GridLayout(2, 4));
		JLabel invoiceLabel = new JLabel("Invoice:");
		invoiceComboBox = new JComboBox();
		for (int x = 12000; x < 13000; x += 100)
			invoiceComboBox.addItem("AVX-" + x);
	//	invoiceComboBox.addActionListener(this);
		JLabel descriptionLabel = new JLabel("Description:");
		descriptionTextField = new JTextField();
		JLabel amountDueLabel = new JLabel("Amount Due");
		amountDueAmountLabel = new JLabel("$0");
		JLabel thisPaymentLabel = new JLabel("This Payment:");
		amountSpinner = new JSpinner(new SpinnerNumberModel());
	//	amountSpinner.addChangeListener(this);
		amountSpinner.setEditor(new JSpinner.NumberEditor(amountSpinner,
				"$#,##0.##"));
		topPanel.add(invoiceLabel);
		topPanel.add(invoiceComboBox);
		topPanel.add(descriptionLabel);
		topPanel.add(descriptionTextField);
		topPanel.add(amountDueLabel);
		topPanel.add(amountDueAmountLabel);
		topPanel.add(thisPaymentLabel);
		topPanel.add(amountSpinner);

		MyDocumentListener documentListener = new MyDocumentListener(this);
		tabbedPane = new JTabbedPane();
		cashComponent = makeCashPanel();
		tabbedPane.addTab("Cash", cashComponent);
		cashComponent = makeCheckPanel(documentListener);
		tabbedPane.addTab("Check", cashComponent);
		cashComponent = makeCardPanel(documentListener);
		tabbedPane.addTab("Credit Card", cashComponent);
		tabbedPane.addChangeListener(this);

		payButton = new JButton("Pay");
	//	payButton.addActionListener(this);
//		payButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				Component component = tabbedPane.getSelectedComponent();
//				if (component.getName().equals("Check")) {
//					System.out.println("Check tab is selected, pay button clicked");
//
//				} else if (component.getName().equals("Credit Card")) {
//					System.out.println("Credit Card tab is selected, pay button clicked");
//				} else {
//					System.out.println("else tab is selected, pay button clicked");
//				}
//
//			}
//		});
		cancelButton = new JButton("Cancel");
	//	cancelButton.addActionListener(this);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel,
				BoxLayout.LINE_AXIS));
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(payButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPanel.add(cancelButton);
		Container contentPane = getContentPane();
		contentPane.add(topPanel, BorderLayout.NORTH);
		contentPane.add(tabbedPane, BorderLayout.CENTER);
	//	contentPane.add(buttonPanel, BorderLayout.PAGE_END);

		updateUi();
	}

	public void actionPerformed(ActionEvent event) {
		String command = event.getActionCommand();
		if (command.equals("Cancel")) {
			//	System.exit(0);
		}
		else if (command.equals("Pay")) {
			// Here's where we would save the user's data in a real app.
			//	System.exit(0);
		}
		// If we got here something was edited so we must update the UI
		updateUi();
	}

	public void stateChanged(ChangeEvent event) {
		updateUi();
	}

	public void updateUi() {
		String invoiceNumber = (String)invoiceComboBox.getSelectedItem();
		// Fake the amount; in a real app. we'd get the data from
		// somewhere based on the invoice number.
		Pattern pattern = Pattern.compile("\\D+");
		Matcher matcher = pattern.matcher(invoiceNumber);
		invoiceNumber = matcher.replaceAll("");
		int due = Integer.parseInt(invoiceNumber) - 10000;
		amountDueAmountLabel.setText("$" + due);

		// Payment method and business logic
		Date today;
		if (tabbedPane.getSelectedIndex() == CASH_TAB) {
			System.out.println("Cash tab is selected, pay button clicked");
			int maximum = Math.min(2000, due);
			int value = ((SpinnerNumberModel)amountSpinner.getModel())
					.getNumber().intValue();
			value = Math.min(Math.max(value, 1),
					maximum);
			amountSpinner.setModel(new SpinnerNumberModel(
					value, 1, maximum, 1));
			payButton.setEnabled(true);
		}
		else if (tabbedPane.getSelectedIndex() == CHECK_TAB) {
			System.out.println("Check tab is selected, pay button clicked");
			int maximum = Math.min(250, due);
			int value = ((SpinnerNumberModel)amountSpinner.getModel())
					.getNumber().intValue();
			value = Math.min(Math.max(value, 10),
					maximum);
			amountSpinner.setModel(new SpinnerNumberModel(
					value, 10, maximum, 1));
			boolean enable = true;
			if (bankNameTextField.getText().length() == 0 ||
					bankNumberTextField.getText().length() == 0 ||
					bankAccountNameTextField.getText().length() == 0 ||
					bankAccountNumberTextField.getText().length() == 0 ||
					!checkSignedCheckBox.isSelected()) {
				enable = false;
			}
			payButton.setEnabled(enable);
		}
		else if (tabbedPane.getSelectedIndex() == CREDIT_CARD_TAB) {
			System.out.println("Card tab is selected, pay button clicked");
			int minimum = Math.max(10, due / 20);
			int maximum = Math.min(5000, due);
			int value = ((SpinnerNumberModel)amountSpinner.getModel())
					.getNumber().intValue();
			value = Math.min(Math.max(value, minimum),
					maximum);
			amountSpinner.setModel(new SpinnerNumberModel(
					value, minimum, maximum, 1));
			payButton.setEnabled(
					cardAccountNameTextField.getText().length() > 0 &&
							cardAccountNumberTextField.getText().length() > 0);
		}
	}

	protected JComponent makeCashPanel() {
		JLabel filler = new JLabel("Paying Cash");
		filler.setFont(new Font("SansSerif", Font.BOLD, 36));
		filler.setHorizontalAlignment(JLabel.CENTER);
		JPanel panel = new JPanel(false);
		panel.setLayout(new GridLayout(1, 1));
		panel.add(filler);
		return panel;
	}

	protected JComponent makeCheckPanel(MyDocumentListener listener) {
		JLabel bankNameLabel = new JLabel("Bank Name:");
		bankNameTextField = new JTextField("bank");
		bankNameTextField.getDocument().addDocumentListener(listener);
		JLabel bankNumberLabel = new JLabel("Bank Number:");
		bankNumberTextField = new JTextField("123456");
		bankNumberTextField.getDocument().addDocumentListener(listener);
		JLabel bankAccountNameLabel = new JLabel("Account Name:");
		bankAccountNameTextField = new JTextField("account");
		bankAccountNameTextField.getDocument().addDocumentListener(
				listener);
		JLabel bankAccountNumberLabel = new JLabel("Account Number:");
		bankAccountNumberTextField = new JTextField("number");
		bankAccountNumberTextField.getDocument().addDocumentListener(
				listener);
		JLabel checkDateLabel = new JLabel("Check Date:");
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, -30);
		Date earliest = calendar.getTime();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		Date latest = calendar.getTime();
		checkDateSpinner = new JSpinner(new SpinnerDateModel(date,
				earliest, latest, Calendar.DAY_OF_WEEK));
	//	checkDateSpinner.addChangeListener(this);
		checkDateSpinner.setEditor(new JSpinner.DateEditor(
				checkDateSpinner, "yyyy-MMM-dd"));
		checkSignedCheckBox = new JCheckBox("Check Signed");
		checkSignedCheckBox.setSelected(true);
	//	checkSignedCheckBox.addActionListener(this);
		JPanel panel = new JPanel(false);
		panel.setLayout(new GridLayout(4, 4));
		panel.add(bankNameLabel);
		panel.add(bankNameTextField);
		panel.add(bankNumberLabel);
		panel.add(bankNumberTextField);
		panel.add(bankAccountNameLabel);
		panel.add(bankAccountNameTextField);
		panel.add(bankAccountNumberLabel);
		panel.add(bankAccountNumberTextField);
		panel.add(checkDateLabel);
		panel.add(checkDateSpinner);
		panel.add(checkSignedCheckBox);
		panel.add(new JLabel());
		panel.add(new JLabel());

		JButton payButton = new JButton("CheckPay");
		payButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String bankName = bankNameTextField.getText();
				String bankNumber = bankNumberTextField.getText();
				String accountName = bankAccountNameTextField.getText();
				String accountNumber = bankAccountNumberTextField.getText();

				String  invoice = (String) invoiceComboBox.getSelectedItem();
				String description = descriptionTextField.getText();
				int amount = (Integer)amountSpinner.getValue();
				System.out.println("this is from tabbed check, pay button bbbbbb");
			}
		});

		panel.add(payButton);

		return panel;
	}

	protected JComponent makeCardPanel(MyDocumentListener listener) {
		JLabel cardTypeLabel = new JLabel("Card Type:");
		String[] cards = {"Master", "Visa"};
		cardTypeComboBox = new JComboBox(cards);
	//	cardTypeComboBox.addActionListener(this);
		JLabel cardAccountNameLabel = new JLabel("Account Name:");
		cardAccountNameTextField = new JTextField();
		cardAccountNameTextField.getDocument().addDocumentListener(
				listener);
		JLabel cardAccountNumberLabel = new JLabel("Account Number:");
		cardAccountNumberTextField = new JTextField();
		cardAccountNumberTextField.getDocument().addDocumentListener(
				listener);
		JLabel cardIssueDateLabel = new JLabel("Issue Date:");
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, -3);
		Date earliest = calendar.getTime();
		calendar.setTime(new Date());
		calendar.add(Calendar.MONTH, -1);
		Date latest = calendar.getTime();
		cardIssueDateSpinner = new JSpinner(new SpinnerDateModel(
				latest, earliest, latest, Calendar.DAY_OF_WEEK));
	//	cardIssueDateSpinner.addChangeListener(this);
		cardIssueDateSpinner.setEditor(new JSpinner.DateEditor(
				cardIssueDateSpinner, "yyyy-MMM-dd"));
		JLabel cardExpiryDateLabel = new JLabel("Expiry Date:");
		calendar.setTime(new Date());
		calendar.add(Calendar.MONTH, 1);
		earliest = calendar.getTime();
		calendar.setTime(new Date());
		calendar.add(Calendar.YEAR, 5);
		latest = calendar.getTime();
		cardExpiryDateSpinner = new JSpinner(new SpinnerDateModel(
				earliest, earliest, latest, Calendar.DAY_OF_WEEK));
	//	cardExpiryDateSpinner.addChangeListener(this);
		cardExpiryDateSpinner.setEditor(new JSpinner.DateEditor(
				cardExpiryDateSpinner, "yyyy-MMM-dd"));
		JPanel panel = new JPanel(false);
		panel.setLayout(new GridLayout(4, 4));
		panel.add(cardTypeLabel);
		panel.add(cardTypeComboBox);
		panel.add(new JLabel());
		panel.add(new JLabel());
		panel.add(cardAccountNameLabel);
		panel.add(cardAccountNameTextField);
		panel.add(cardAccountNumberLabel);
		panel.add(cardAccountNumberTextField);
		panel.add(cardIssueDateLabel);
		panel.add(cardIssueDateSpinner);
		panel.add(cardExpiryDateLabel);
		panel.add(cardExpiryDateSpinner);
		panel.add(new JLabel());
		panel.add(new JLabel());

        JButton payButton = new JButton("CardPay");
        payButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String cardType = (String) cardTypeComboBox.getSelectedItem();
                String accountName = cardAccountNameTextField.getText();
                String accountNumber = cardAccountNumberTextField.getText();
                Date issueDate = (Date)cardIssueDateSpinner.getValue();
                Date expireDate = (Date)cardExpiryDateSpinner.getValue();

                String  invoice = (String) invoiceComboBox.getSelectedItem();
                String description = descriptionTextField.getText();
                int amount = (Integer)amountSpinner.getValue();
                System.out.println("this is from tabbed card, pay button cccccc");
            }
        });

        panel.add(payButton);
		return panel;
	}

	private static void createAndShowGUI() {
		PaymentFormJSpinner paymentForm = new PaymentFormJSpinner();
		paymentForm.setTitle("Payment Form");
		paymentForm.pack();
		paymentForm.setSize(525, 275);
		paymentForm.setLocation(450, 0); // for ease of testing
		paymentForm.setVisible(true);
	}

	public static void main(String[] args) {
		Locale.setDefault(Locale.ENGLISH);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
}

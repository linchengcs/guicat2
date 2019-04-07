package edu.wmich.cs.aut.payment;/*
    Copyright (c) 2008 froglogic GmbH. All rights reserved.

    This file is part of an example program for Squish---it may be used,
    distributed, and modified, without limitation.
*/

import edu.wmich.cs.carot.util.Olog;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.regex.*;
import javax.swing.*;
import javax.swing.event.*;


public class PaymentForm extends JFrame {

	JButton payButton;
	JButton cancelButton;
	JTabbedPane tabbedPane;
	JComponent cashComponent;
	JComponent checkComponent;
	JComponent cardComponent;
	JComboBox invoiceComboBox;
	JTextField descriptionTextField;
	JLabel amountDueAmountLabel;
	JTextField amountSpinner;
	JTextField bankNameTextField;
	JTextField bankNumberTextField;
	JTextField bankAccountNameTextField;
	JTextField bankAccountNumberTextField;
	JTextField checkDateSpinner;
	JCheckBox checkSignedCheckBox;
	JComboBox cardTypeComboBox;
	JTextField cardAccountNameTextField;
	JTextField cardAccountNumberTextField;
	JTextField cardIssueDateSpinner;
	JTextField cardExpiryDateSpinner;
	final int CASH_TAB = 0;
	final int CHECK_TAB = 1;
	final int CREDIT_CARD_TAB = 2;

	public PaymentForm() {
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
		amountSpinner = new JTextField("20000");

		topPanel.add(invoiceLabel);
		topPanel.add(invoiceComboBox);
		topPanel.add(descriptionLabel);
		topPanel.add(descriptionTextField);
		topPanel.add(amountDueLabel);
		topPanel.add(amountDueAmountLabel);
		topPanel.add(thisPaymentLabel);
		topPanel.add(amountSpinner);

		tabbedPane = new JTabbedPane();
		cashComponent = makeCashPanel();
		tabbedPane.addTab("Cash", cashComponent);
		cashComponent = makeCheckPanel();
		tabbedPane.addTab("Check", cashComponent);
		cashComponent = makeCardPanel();
		tabbedPane.addTab("Credit Card", cashComponent);

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

	protected JComponent makeCheckPanel( ) {
		JLabel bankNameLabel = new JLabel("Bank Name:");
		bankNameTextField = new JTextField("bank");
		JLabel bankNumberLabel = new JLabel("Bank Number:");
		bankNumberTextField = new JTextField("123456");
		JLabel bankAccountNameLabel = new JLabel("Account Name:");
		bankAccountNameTextField = new JTextField("account");
		JLabel bankAccountNumberLabel = new JLabel("Account Number:");
		bankAccountNumberTextField = new JTextField("number");
		JLabel checkDateLabel = new JLabel("Check Date:");
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, -30);
		Date earliest = calendar.getTime();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		Date latest = calendar.getTime();
		checkDateSpinner = new JTextField("11112016");
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
				try {
					String bankName = bankNameTextField.getText();
					String bankNumber = bankNumberTextField.getText();
					String accountName = bankAccountNameTextField.getText();
					String accountNumber = bankAccountNumberTextField.getText();
					checkDateSpinner.getText();
					Date checkDate = new Date();
					Boolean signed = checkSignedCheckBox.isSelected();

					String invoice = (String) invoiceComboBox.getSelectedItem();
					String description = descriptionTextField.getText();
					int amount = Integer.parseInt(amountSpinner.getText());
					System.out.println("this is from tabbed check, pay button bbbbbb");
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});

		panel.add(payButton);

		return panel;
	}

	protected JComponent makeCardPanel( ) {
		JLabel cardTypeLabel = new JLabel("Card Type:");
		String[] cards = {"Master", "Visa"};
		cardTypeComboBox = new JComboBox(cards);
	//	cardTypeComboBox.addActionListener(this);
		JLabel cardAccountNameLabel = new JLabel("Account Name:");
		cardAccountNameTextField = new JTextField("name");
		JLabel cardAccountNumberLabel = new JLabel("Account Number:");
		cardAccountNumberTextField = new JTextField("123456");
		JLabel cardIssueDateLabel = new JLabel("Issue Date:");
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, -3);
		Date earliest = calendar.getTime();
		calendar.setTime(new Date());
		calendar.add(Calendar.MONTH, -1);
		Date latest = calendar.getTime();
		cardIssueDateSpinner = new JTextField("11112016");
		JLabel cardExpiryDateLabel = new JLabel("Expiry Date:");
		calendar.setTime(new Date());
		calendar.add(Calendar.MONTH, 1);
		earliest = calendar.getTime();
		calendar.setTime(new Date());
		calendar.add(Calendar.YEAR, 5);
		latest = calendar.getTime();
		cardExpiryDateSpinner = new JTextField("11112016");
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
				try {
					String cardType = (String) cardTypeComboBox.getSelectedItem();
					String accountName = cardAccountNameTextField.getText();
					String accountNumber = cardAccountNumberTextField.getText();
					cardIssueDateSpinner.getText();
					Date issueDate = new Date();
					cardExpiryDateSpinner.getText();
					Date expireDate = new Date();

					String invoice = (String) invoiceComboBox.getSelectedItem();
					String description = descriptionTextField.getText();
					int amount = Integer.parseInt(amountSpinner.getText());
					System.out.println("this is from tabbed card, pay button cccccc");
				} catch (Exception ex) {
					ex.printStackTrace();
				}
            }
        });

        panel.add(payButton);
		return panel;
	}

	private static void createAndShowGUI() {
		PaymentForm paymentForm = new PaymentForm();
		paymentForm.setTitle("Payment Form");
		paymentForm.pack();
		paymentForm.setSize(525, 275);
		paymentForm.setLocation(450, 0); // for ease of testing
		paymentForm.setVisible(true);
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

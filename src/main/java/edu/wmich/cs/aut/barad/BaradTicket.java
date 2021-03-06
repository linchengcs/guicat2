package edu.wmich.cs.aut.barad;



import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class BaradTicket {
//    private static Logger log = Logger.getLogger("Barad");

    private JButton buyButton;
    private JButton clearButton;
    private JTextField infoField;
    JRadioButton firstClassRadio;
    JRadioButton secondClassRadio;
    ButtonGroup buttonGroup;
    JTextField nameInput;
    JTextField idInput;
    JTextField fromInput;
    JTextField toInput;
    JComboBox ageCombo;
    JCheckBox couponCheckBox;

    String[] ages = {"Adult", "Child"};
    private String myteststring;



    public String name;
    public String ID;
    public int from;
    public int to;
    public int ageLevel;
    public int classLevel;
    public int coupon;
    final public static int CHILD = 1;
    final public static int ADULT = 2;
    final public static int FIRSTCLASS = 1;
    final public static int SECONDCLASS = 2;
    public String msg;
    public double price;

    int a = 0;

    public  void createAndShowGUI() {

        JFrame frame = new JFrame("Ticket");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel contentPane = new JPanel(new GridBagLayout());
        frame.setContentPane(contentPane);

        GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);

        {
            JLabel nameLabel = new JLabel("Name");
            contentPane.add(nameLabel, gbc);

            nameInput = new JTextField("superman", 10);
            //    nameInput.getAccessibleContext().setAccessibleName("manual accesiible name");
            gbc.gridx = 1;
            contentPane.add(nameInput, gbc);
            nameLabel.setLabelFor(nameInput);
        }

        {
            JLabel idLabel = new JLabel("ID");
            gbc.gridx = 0;
            gbc.gridy = 1;
            contentPane.add(idLabel, gbc);

            idInput = new JTextField("123456", 10);
            gbc.gridx = 1;
            gbc.gridy = 1;
            contentPane.add(idInput, gbc);
            idLabel.setLabelFor(idInput);idInput.getAccessibleContext().getAccessibleName();
        }

        {
            JLabel fromLabel = new JLabel("From");
            gbc.gridx = 0;
            gbc.gridy = 2;
            contentPane.add(fromLabel, gbc);

            fromInput = new JTextField("0", 10);
            gbc.gridx = 1;
            gbc.gridy = 2;
            contentPane.add(fromInput, gbc);
            fromLabel.setLabelFor(fromInput);
        }

        {
            JLabel toLabel = new JLabel("To");
            gbc.gridx = 0;
            gbc.gridy = 3;
            contentPane.add(toLabel, gbc);

            toInput = new JTextField("50", 10);
            gbc.gridx = 1;
            gbc.gridy = 3;
            contentPane.add(toInput, gbc);
            toLabel.setLabelFor(toInput);
        }


        {
            JLabel ageLabel = new JLabel("Age Level");
            gbc.gridx = 0;
            gbc.gridy = 4;
            contentPane.add(ageLabel, gbc);


            ageCombo = new JComboBox(ages);
            ageCombo.setSelectedIndex(0);
            gbc.gridx = 1;
            gbc.gridy = 4;
            contentPane.add(ageCombo, gbc);
            ageLabel.setLabelFor(ageCombo);
        }


        {
            JLabel classLabel = new JLabel("Class Level");
            gbc.gridx = 0;
            gbc.gridy = 5;
            contentPane.add(classLabel, gbc);

            firstClassRadio = new JRadioButton("1st");
            firstClassRadio.setActionCommand("1st");
            firstClassRadio.setSelected(true);
            secondClassRadio = new JRadioButton("2nd");
            secondClassRadio.setActionCommand("2nd");

            buttonGroup = new ButtonGroup();
            buttonGroup.add(firstClassRadio);
            buttonGroup.add(secondClassRadio);

            gbc.gridx = 1;
            gbc.gridy = 5;
            contentPane.add(firstClassRadio, gbc);

            gbc.gridx = 2;
            gbc.gridy = 5;
            contentPane.add(secondClassRadio, gbc);
        }

        {
            JLabel checkLabel = new JLabel("Coupon");
            gbc.gridx = 0;
            gbc.gridy = 6;
            contentPane.add(checkLabel, gbc);

            couponCheckBox = new JCheckBox("$100");
            gbc.gridx = 1;
            gbc.gridy = 6;
            contentPane.add(couponCheckBox, gbc);

        }

        {
            buyButton = new JButton("Buy Ticket");
            gbc.gridx = 0;
            gbc.gridy = 16;
            contentPane.add(buyButton, gbc);

            clearButton = new JButton("Save");
            gbc.gridx = 1;
            gbc.gridy = 16;
            contentPane.add(clearButton, gbc);
        }

        {
            JLabel priceLabel = new JLabel("Price");
            gbc.gridx = 0;
            gbc.gridy = 17;
            contentPane.add(priceLabel, gbc);
            priceLabel.setForeground(Color.BLUE);

            infoField = new JTextField("ticket price");
            gbc.gridx = 1;
            gbc.gridy = 17;
            contentPane.add(infoField, gbc);
            infoField.setEnabled(false);
        }



        buyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                name = nameInput.getText();
                ID = idInput.getText();
                from = Integer.parseInt(fromInput.getText());
                to = Integer.parseInt(toInput.getText());

                if (ageCombo.getSelectedIndex() == 0 ) {
                    ageLevel = 1;
                } else  {
                    ageLevel = 2;
                }

                if (couponCheckBox.isSelected()) {
                    coupon = 100;
                } else {
                    coupon = 0;
                }

                if(firstClassRadio.isSelected()) {
                    classLevel = 1;
                } else {
                    classLevel = 2;
                }


                boolean checkModel = true;
                String info = "", msg = "";


                if (name.length() < 3) {
                    msg += "too short name; ";
                    checkModel = false;
                } else if (name.length() > 20) {
                    msg += "too long name";
                    checkModel = false;
                } else {
                    checkModel = true;
                }

                if (ID.equals("oliver")) {
                    msg += "wrong id format";
                    checkModel = false;
                } else {
                    msg += "right id format";
                    checkModel = true;
                }


                //     return msg.isEmpty();


                if(checkModel) {
                    int coeficient = 0;
                    if (classLevel == 1) {
                        coeficient = 1;
                    } else {
                        coeficient = 2;
                    }
                    int dist = to - from;
                    if (ageLevel == 1) {
                        if (dist < 40) {
                            price = 100 * coeficient;
                        }  else  if (dist < 45) {
                            price = 110 * coeficient;
                        } else if (dist < 50) {
                            price = 120 * coeficient;
                        } else if (dist < 70) {
                            price = 140 * coeficient;
                        } else if (dist < 80) {
                            price = 150 * coeficient;
                        } else if (dist < 85) {
                            price = 155 * coeficient;
                        } else   {
                            price = 160 * coeficient;
                        }
                    } else {
                        if (dist < 40) {
                            price = 120 * coeficient;
                        }  else if (dist < 45) {
                            price = 130 * coeficient;
                        } else if (dist < 50) {
                            price = 140 * coeficient;
                        } else if (dist < 70) {
                            price = 150 * coeficient;
                        } else if (dist < 80) {
                            price = 160 * coeficient;
                        } else if (dist < 85) {
                            price = 175 * coeficient;
                        } else   {
                            price = 180 * coeficient;
                        }
                    }

                    price = price - coupon;
                    assert price > 0 : "@ Bug @, Price is not greater than 0!\n";

                    infoField.setText(String.valueOf(price));
                    System.out.println(String.valueOf(price));
                    System.out.println(String.valueOf(price));
                } else {
                    infoField.setText(msg);
                }
            }
        });


            /* remove because it is not necessary to have a listener, just obtain in buy button
            firstClassRadio.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        JRadioButton jrb = (JRadioButton)e.getSource();
                        if (jrb.isSelected()) {
                            ticketModel.classLevel = 1;
                        } else {
                            ticketModel.classLevel = 2;
                        }
                    }
                });

            secondClassRadio.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        JRadioButton jrb = (JRadioButton) e.getSource();
                        if (jrb.isSelected()) {
                            ticketModel.classLevel = 2;
                        } else {
                            ticketModel.classLevel = 1;
                        }
                    }
                });
*/


        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
//                    ticketModel.name = nameInput.getText();
//                    ticketModel.ID = idInput.getText();
//                    ticketModel.from = Integer.parseInt(fromInput.getText());
//                    ticketModel.to = Integer.parseInt(toInput.getText());
//                    infoField.setText(ticketModel.toString());
//                    ticketModel = null;
//                    myteststring = "Hello world!";
//                    infoField.setText("");
                //       int b = a;
                //      int c = b;

                //           a ++;


//                a = 1;
//                a++;

                int b = 0;
                if (b > 0)
                    a = 1;
                else
                    a = 2;
                a++;
            }
        });


        frame.pack();
        frame.setVisible(true);

    }



    public static void main(String[] args){
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new BaradTicket().createAndShowGUI();
            }
        });
    }

}

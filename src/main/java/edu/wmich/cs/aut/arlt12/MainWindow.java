package edu.wmich.cs.aut.arlt12;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class MainWindow extends JFrame {
    private String text = new String();

    public static void main(String[] args) {
        MainWindow mainWindow = new MainWindow();
        mainWindow.setVisible(true);
    }

    public MainWindow() {
        super("MainWindow");
        setDefaultCloseOperation(3);

        getContentPane().setLayout(new FlowLayout());

        JButton b = new JButton("e1");
        getContentPane().add(b);
        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                MainWindow.this.text = "Hello World";
            }
        });
        b = new JButton("e2");
        getContentPane().add(b);
        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                MainWindow.this.text = null;
            }
        });
        b = new JButton("e3");
        getContentPane().add(b);
        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                MainWindow.Dialog d = new MainWindow.Dialog();
                d.setVisible(true);
            }
        });
        setMinimumSize(new Dimension(250, 0));
        pack();
        setLocationRelativeTo(null);
    }

    class Dialog extends JDialog {
        public Dialog() {
            setTitle("Dialog");
            getContentPane().setLayout(new FlowLayout());

            JButton b = new JButton("e4");
            getContentPane().add(b);
            b.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    try {
                        MainWindow.this.text = MainWindow.this.text.trim();
                        MainWindow.Dialog.this.setVisible(false);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(
                                MainWindow.Dialog.this.getContentPane(),
                                "NullPointerException", "Error", 0);
                    }
                }
            });
            setMinimumSize(new Dimension(150, 0));
            pack();
            setLocationRelativeTo(null);
        }


    }
}
package edu.wmich.cs.aut.guess;

import edu.wmich.cs.carot.util.Olog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

public class Main {
    JFrame frame;
    JButton add;
    JButton minus;
    JButton start;
    JTextField info;
    MyDialog dialog;
    Random rand;

    int target;
    int guess;
    int max;
    int incremental = 1;
    String msg;

    public Main() {
        rand = new Random();
        target = rand.nextInt(20);
        frame = new JFrame();
        frame.setLayout(new FlowLayout());
        Container contentPane = frame.getContentPane();

        add = new JButton("Add");
        contentPane.add(add);
        add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guess += incremental;
                info();
            }
        });

        minus = new JButton("Minus");
        contentPane.add(minus);
        minus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               guess -= incremental;
               info();
            }
        });

        start = new JButton("Start");
        contentPane.add(start);
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog = new MyDialog(frame, true, Main.this);
                dialog.setVisible(true);
            }
        });


        info = new JTextField("click Set Init to start");
        contentPane.add(info);
        frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        frame.pack();
        frame.setVisible(true);
    }

    public void info() {
        if (guess == target)
            msg = "Score!";
        if (guess > target)
            msg = "Too big";
        if (guess < target)
            msg = "Too small";
        info.setText(msg);
        System.out.println(msg + ", target=" + target + ", guess=" + guess);
    }

    public void setInit(int init) {
        this.guess = init;
    }

    public static void main(String[] args) {
        new Main();
    }
}

class MyDialog extends JDialog {
    int initGuess;
    JButton setInit;
    Main main;
    
    public MyDialog(JFrame frame, boolean modal, Main main) {
        super(frame, modal);
        this.main = main;
        setInit = new JButton("SetInit");
        this.getContentPane().add(setInit);

        setInit.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    MyDialog.this.main.setInit(MyDialog.this.main.rand.nextInt(20));
                    System.out.println("guess is initialized to " + MyDialog.this.main.guess);
                    MyDialog.this.dispose();
                    MyDialog.this.main.info();
                }
            });
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.pack();
    }
}

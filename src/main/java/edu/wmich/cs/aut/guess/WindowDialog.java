package edu.wmich.cs.aut.guess;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

public class WindowDialog {
    JFrame frame;
    JFrame dialogFrame;
    JButton add;
    JButton minus;
    JButton start;
    JTextField info;
    Random rand;

    int target;
    int guess;
    int max;
    int incremental = 1;
    String msg;

    public WindowDialog() {
        rand = new Random();
        target = rand.nextInt(20);
        frame = new JFrame();
        dialogFrame = new JFrame();
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
                showDialogFrame();
            }
        });


        info = new JTextField("click Set Init to start!");
        info.setEditable(false);
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

    public void showDialogFrame() {
        dialogFrame.setLayout(new FlowLayout());
        JButton setInit = new JButton("SetInit");
        dialogFrame.getContentPane().add(setInit);
        setInit.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    WindowDialog.this.guess = rand.nextInt(20);
                    dialogFrame.dispose();
                    info();
                }});
        dialogFrame.pack();
        dialogFrame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new WindowDialog();
            }
        });

    }
}



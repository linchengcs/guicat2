package edu.wmich.cs.carot.util;


import edu.wmich.cs.radish.ddg.Rip;

import javax.accessibility.AccessibleContext;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.WindowEvent;
import java.lang.reflect.Method;

public class PrintAccessbileName {
    static String indent = "";
    static int  level = 1;
    public static Window newWindow = null;


    public static void main(String[] args) {

        if (args.length < 1) {
            System.out.println("please provide program name");
        }
        String autMain = args[0];

 //       String autMain = "TerpWord";

        try {
            Class c = Class.forName(autMain);
            Method m = c.getMethod("main", String[].class);
            m.invoke(null, (Object)new String[]{});
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Frame[] frames = null;
        frames = Frame.getFrames();
        java.awt.Toolkit.getDefaultToolkit().addAWTEventListener(new WindowOpenListener(), AWTEvent.WINDOW_EVENT_MASK);

        //mot work, frame comes out, but accessible context doesn't come out
//        int i = 0;
//        while ( (frames = Frame.getFrames()) == null ) {
//            System.out.println(frames.toString());
//        };

        int i = 1;
        for (Frame frame : frames) {
            //System.out.println("====== accessibleName for frame: " + i++ + "=============");
            dumpInfo(frame.getAccessibleContext());
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (Frame frame : frames) {
            frame.dispose();
        }
    }




    public static void dumpInfo(AccessibleContext ac) {
        /*
        if( ac.getAccessibleEditableText() != null) {
        System.out.println("------ level " + level++);
        System.out.println("    " + ac.getAccessibleName());
        System.out.println("    " + ac.getAccessibleEditableText());
        System.out.println("Description = " + ac.getAccessibleDescription());
        }
        */
        System.out.println(level + indent + ac.getAccessibleName() + ac.hashCode());
        try {
            ac.getAccessibleAction().doAccessibleAction(0);
        } catch (Exception e) {}

        if (newWindow != null) {
            AccessibleContext newac = newWindow.getAccessibleContext();
            newWindow = null;
            dumpInfo(newac);
        }


        int nChildren = ac.getAccessibleChildrenCount();
        indent += "    ";
        level++;

        for (int i = 0; i < nChildren; i++){
            dumpInfo(ac.getAccessibleChild(i).getAccessibleContext());
            level--;
            indent = indent.substring(0, indent.length()-4);
        }
    }



}


class WindowOpenListener implements AWTEventListener {
    public void eventDispatched(AWTEvent event) {

        switch (event.getID()) {
            case WindowEvent.WINDOW_OPENED:
                Rip.doAccessibleContext(((WindowEvent) event).getWindow().getAccessibleContext());
                processWindowOpened((WindowEvent) event);
                break;
            case WindowEvent.WINDOW_ACTIVATED:
                break;
            case WindowEvent.WINDOW_DEACTIVATED:
                break;
            case WindowEvent.WINDOW_CLOSED:
                processWindowClosed((WindowEvent) event);
                break;

            default:
                break;
        }

    }


    private void processWindowClosed(WindowEvent wEvent) {
        Window window = wEvent.getWindow();

        System.out.println("window closeda: " + wEvent.getWindow().getName() );
        //   tempClosedWinStack.add(window);
    }

    private void processWindowOpened(WindowEvent wEvent) {
        Window window = wEvent.getWindow();
        System.out.println("window opened: " + wEvent.getWindow().getName() );
        PrintAccessbileName.newWindow = wEvent.getWindow();
    }

}
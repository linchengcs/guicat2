package edu.wmich.cs.radish.ddg;


import edu.wmich.cs.carot.util.Olog;

import javax.accessibility.AccessibleAction;
import javax.accessibility.AccessibleContext;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.WindowEvent;
import java.lang.reflect.Method;

public class Rip {
    public static Window newWindow = null;

    private String autMain = null;

    public Rip(String autMain) {
        this.autMain = autMain;
    }

    public String getAutMain() {
        return autMain;
    }

    public void setAutMain(String autMain) {
        this.autMain = autMain;
    }

    public static void doAccessibleContext(AccessibleContext ac) {
        AccessibleAction aa = ac.getAccessibleAction();
        if (aa!=null && aa.getAccessibleActionCount() == 1) {
            try {
                Olog.log.info(ac.getAccessibleName());
                aa.doAccessibleAction(0);
                Thread.sleep(200);
            } catch (Exception ex) {
              //  ex.printStackTrace();
            }
        }


//        if (newWindow != null) {
//            AccessibleContext newac = newWindow.getAccessibleContext();
//            newWindow = null;
//            doAccessibleContext(newac);
//        }


        int nChildren = ac.getAccessibleChildrenCount();
        for (int j = 0; j < nChildren; j++) {
            doAccessibleContext(ac.getAccessibleChild(j).getAccessibleContext());

        }

    }


    public  void run() {
        try {
            Class c = Class.forName(autMain);
            Method m = c.getMethod("main", String[].class);
            m.invoke(null, (Object) new String[]{""});


            Frame[] frames;
            Thread.sleep(2000);

            frames = Frame.getFrames();
            java.awt.Toolkit.getDefaultToolkit().addAWTEventListener(new WindowOpenListener(), AWTEvent.WINDOW_EVENT_MASK);
            for (int i = 0; i < frames.length; i++)
                doAccessibleContext(frames[i].getAccessibleContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Frame frame : Frame.getFrames()) {
            frame.dispose();
        }
    }


    public static void main(String[] args) {
        String autMain = "edu.wmich.cs.aut.barad.BaradTicket";
        Rip rip = new Rip(autMain);
        rip.run();

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
        Rip.newWindow = wEvent.getWindow();
    }

}

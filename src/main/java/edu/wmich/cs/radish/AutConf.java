package edu.wmich.cs.radish;

import edu.wmich.cs.carot.util.Olog;

/**
 * Created by oliver on 30/09/16.
 */
public class AutConf {

    /* instrument these packages for dependency and guitar id*/
    private static String[] aut = {
            "TestASMShouldInstrument",
            "barad",
            "workout",
            "addressbook",
            "payment",
            "rachota",
            "jgp",
            "jnotepad",
            "calc",
            "ClassEditor",
            "regextester",
            "janag",
            "hashvcalc",
            "crosswordsage"
    };

    /* these types are automatically add accessibleName by guitar agent */
    private static String[] swingDataComponents = {
            "javax/swing/JTextField",
            "javax/swing/JCheckBox",
            "javax/swing/JRadioButton",
            "javax/swing/JComboBox",
            "javax/swing/JList",
            "javax/swing/JSlider",
        //    "javax/swing/JSpinner",   //need update agent guitar
            "javax/swing/JPasswordField"
    };

    public static boolean isAut(String className) {
        for (int i = 0; i < aut.length; i++) {
            if (className.contains(aut[i]))
                return true;
        }
        return false;
    }

    public static boolean addAccessibleName(String ss) {
       return isSwingDataConponent(ss);
    }

    public static boolean isSwingDataConponent(String ss) {
       // System.out.println("printing widget class: " + ss);
        String s = ss.replace('.', '/');
        for (int i = 0; i < swingDataComponents.length; i++) {
            if (swingDataComponents[i].equals(s))
                return true;
        }
        return false;
    }
}

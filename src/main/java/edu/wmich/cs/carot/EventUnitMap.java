package edu.wmich.cs.carot;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Created by Lin Cheng on 07/09/16.
 * This is a map between Guitar event and Soot Unit
 */
public class EventUnitMap {

    public static final String SOOT_PKG_BARAD = "barad";
    public static final String SOOT_PKG_TICKET = "ticket";
    public static final String SOOT_PKG_WORKOUT = "workout";
    public static final String SOOT_PKG_RACHOTA = "rachota";
    public static final String SOOT_PKG_TERPWORD = "terpword";
    public static final String SOOT_PKG_TERPSPREADSHEET = "terpspreadsheet";
    public static final String SOOT_PKG_TERPPAINT = "terppaint";
    private static Map<String, String> BARAD;
    private static Map<String, String> WORKOUT;
    private static Map<String, String> rachota;
    private static Map<String, String> terpword;
    private static Map<String, String> terpspreadsheet;
    private static Map<String, String> terppaint;

    static {
        BARAD = new HashMap<>();
        BARAD.put("e1887239368", "<edu.wmich.cs.aut.barad.BaradTicket: javax.swing.JTextField nameInput>");
        BARAD.put("e1877567236", "<edu.wmich.cs.aut.barad.BaradTicket: javax.swing.JTextField idInput>");
        BARAD.put("e1886351932", "<edu.wmich.cs.aut.barad.BaradTicket: javax.swing.JTextField fromInput>");
        BARAD.put("e1877569020", "<edu.wmich.cs.aut.barad.BaradTicket: javax.swing.JTextField toInput>");
        BARAD.put("e1471183336", "<edu.wmich.cs.aut.barad.BaradTicket: javax.swing.JComboBox ageCombo>");
        BARAD.put("e2977765342", "<edu.wmich.cs.aut.barad.BaradTicket: javax.swing.JCheckBox couponCheckBox>");

        WORKOUT = new HashMap<>();
        WORKOUT.put("e1867326300", "<edu.wmich.cs.aut.workout.AbstractMain: javax.swing.JComboBox metabolismCombo>");
        WORKOUT.put("e1533213504", "<edu.wmich.cs.aut.workout.AbstractMain: javax.swing.JComboBox genderCombo>");
        WORKOUT.put("e647517916", "<edu.wmich.cs.aut.workout.AbstractMain: javax.swing.JComboBox experienceCombo>");
        WORKOUT.put("e2736112220", "<edu.wmich.cs.aut.workout.AbstractMain: javax.swing.JTextField ageTextField>");
        WORKOUT.put("e2777134200", "<edu.wmich.cs.aut.workout.AbstractMain: javax.swing.JTextField heightTextField>");
        WORKOUT.put("e199916088", "<edu.wmich.cs.aut.workout.AbstractMain: javax.swing.JTextField weightTextField>");

    }

    public static Map<String, String> getEventUnitMap(String aut) {
        if (aut.equals(SOOT_PKG_BARAD))
            return BARAD;
        if (aut.equals(SOOT_PKG_WORKOUT))
            return WORKOUT;
        return new HashMap<>();
    }

    private Map<String, String> getEventUnitMapBarad() {
        return null;
    }
}

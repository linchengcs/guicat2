package edu.wmich.cs.spinach;

import edu.wmich.cs.carot.util.Olog;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Lin Cheng on 1/9/17.
 */
public class SetTest {
    public static void main(String[] args) {
        Set<String> set1 = new HashSet<>();
        Set<String> set2 = new HashSet<>();

        set1.add("r0.<gui.RelatedClasses: javax.swing.JButton btnOpenClass>");
        set1.add("r0.<gui.RelatedClasses: javax.swing.JLabel jLabel1>");
        set1.add("r0.<gui.RelatedClasses: javax.swing.JLabel jLabel2>");
        set1.add("r0.<classfile.ConstantPool: int iNumPoolInfos>");
        set1.add("$r4.<classfile.ClassFile: classfile.ConstantPool constantPool>");
        set1.add("r0.<gui.RelatedClasses: javax.swing.JComboBox cmbFilter>");
        set1.add("r0.<gui.RelatedClasses: javax.swing.JList lstRelatedClasses>");
        set1.add("r0.<classfile.ConstantPoolInfo: int iTag>");
        set1.add("r6.<classfile.ConstantPoolInfo: int iNameIndex>");
        set1.add("r0.<gui.RelatedClasses: javax.swing.JScrollPane jScrollPane1>");
        set1.add( "r0.<classfile.ConstantPool: java.util.Vector vectConstPool>");
        set1.add( "r6.<classfile.ConstantPoolInfo: java.lang.String sUTFStr>");
        set1.add( "r0.<gui.RelatedClasses: java.lang.String[] asDisplayList>");
        set1.add( "r0.<gui.RelatedClasses: java.lang.String[] asClassList>");
        set1.add( "r0.<gui.ClassEditor: classfile.ClassFile classFile>");
        set1.add( "r0.<gui.RelatedClasses: javax.swing.JButton btnClose>");
        set1.add( "r6.<classfile.ConstantPoolInfo: int iTag>");
        set1.add( "r0.<gui.RelatedClasses: javax.swing.JButton btnBrowse>");
        set1.add( "r0.<gui.ClassEditor$31: gui.ClassEditor this$0>");
        set1.add( "r0.<gui.RelatedClasses: javax.swing.JTextField txtFilePath>");




        set2.add("r0.<gui.RelatedClasses: javax.swing.JButton btnOpenClass>");
        set2.add("r0.<gui.RelatedClasses: javax.swing.JLabel jLabel1>");
        set2.add("r0.<gui.RelatedClasses: javax.swing.JLabel jLabel2>");
        set2.add("r0.<classfile.ConstantPool: int iNumPoolInfos>");
        set2.add("$r4.<classfile.ClassFile: classfile.ConstantPool constantPool>");
        set2.add("r0.<gui.RelatedClasses: javax.swing.JComboBox cmbFilter>");
        set2.add("r0.<gui.RelatedClasses: javax.swing.JList lstRelatedClasses>");
        set2.add("r0.<classfile.ConstantPoolInfo: int iTag>");
        set2.add("r6.<classfile.ConstantPoolInfo: int iNameIndex>");
        set2.add("r0.<gui.RelatedClasses: javax.swing.JScrollPane jScrollPane1>");
        set2.add( "r0.<classfile.ConstantPool: java.util.Vector vectConstPool>");
        set2.add( "r6.<classfile.ConstantPoolInfo: java.lang.String sUTFStr>");
        set2.add( "r0.<gui.ClassEditor$8: gui.ClassEditor this$0>");
        set2.add( "r0.<gui.RelatedClasses: java.lang.String[] asDisplayList>");
        set2.add( "r0.<gui.RelatedClasses: java.lang.String[] asClassList>");
        set2.add( "r0.<gui.ClassEditor: classfile.ClassFile classFile>");
        set2.add( "r0.<gui.RelatedClasses: javax.swing.JButton btnClose>");
        set2.add( "r6.<classfile.ConstantPoolInfo: int iTag>");
        set2.add( "r0.<gui.RelatedClasses: javax.swing.JButton btnBrowse>");
        set2.add( "r0.<gui.RelatedClasses: javax.swing.JTextField txtFilePath>");

        if (set1.equals(set2)) {
            Olog.log.info("equals");
        }
        else {
            Olog.log.info("not equal");
        }

        set1.removeAll(set2);
        Olog.log.info(set1.toString());

    }
}

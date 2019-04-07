package edu.wmich.cs.radish.ddg;

import edu.wmich.cs.radish.AutConf;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.Iterator;

/**
 * Created by oliver on 30/09/16.
 */
public class GuitarAgent implements ClassFileTransformer {
    public static Logger logger = LoggerFactory.getLogger(GuitarAgent.class);


    public static void premain(String agentArgs, Instrumentation inst) {
        inst.addTransformer(new GuitarAgent());
    }

    @Override
    public byte[] transform(ClassLoader loader, String cname, Class<?> cclass, ProtectionDomain d, byte[] cbuf) throws IllegalClassFormatException {
        // if (!cname.contains("barad")) return cbuf;
//        if (!AutConf.isAut(cname))
//            return cbuf;
    //    logger.info("transforming: " + cname);

        try {
            ClassReader cr = new ClassReader(cbuf);
            ClassNode cn = new ClassNode();
            cr.accept(cn, 0);


            for (Object fo : cn.methods) {
                MethodNode f = (MethodNode) fo;

                Iterator<AbstractInsnNode> it = f.instructions.iterator();
                while (it.hasNext()) {
                    AbstractInsnNode i = it.next();
                    AbstractInsnNode j = i.getNext();

                    if (i.getOpcode() == Opcodes.INVOKESPECIAL && j.getOpcode() == Opcodes.PUTFIELD) {
                        MethodInsnNode mn = (MethodInsnNode) i;
                        FieldInsnNode fin = (FieldInsnNode) j;
                     //  if (mn.name.equals("<init>") && mn.owner.equals("javax/swing/JTextField")) {
                        if (mn.name.equals("<init>") && AutConf.addAccessibleName(mn.owner)) {
                                InsnList il = new InsnList();
                            il.add(new VarInsnNode(Opcodes.ALOAD, 0));
                            il.add(new FieldInsnNode(Opcodes.GETFIELD, fin.owner, fin.name, fin.desc));
                            il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, mn.owner, "getAccessibleContext", "()Ljavax/accessibility/AccessibleContext;", false));
                            il.add(new LdcInsnNode(fin.owner + "." + fin.name + ":" + fin.desc));
                            il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "javax/accessibility/AccessibleContext", "setAccessibleName", "(Ljava/lang/String;)V", false));
                            f.instructions.insert(j, il);
                        }
                    }

                }
            }

            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            cn.accept(cw);

            byte[] ret = cw.toByteArray();

            try {
                File file = new File("instrumented/guitar-agent" + "/" + cname + ".class");
                File parent = new File(file.getParent());
                parent.mkdirs();
                FileOutputStream out = new FileOutputStream(file);
                out.write(ret);
                out.close();
            } catch(Exception e) {
                e.printStackTrace();
            }
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cbuf;

    }
}

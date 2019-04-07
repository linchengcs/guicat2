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
import java.util.*;


public class DependencyAgent implements ClassFileTransformer {

    // private Logger logger = Logger.getLogger("SymbolicMirror.class");
    public static Logger logger = LoggerFactory.getLogger(DependencyAgent.class);


    public static void premain(String agentArgs, Instrumentation inst) {
        inst.addTransformer(new DependencyAgent());
    }

    @Override
    public byte[] transform(ClassLoader loader, String cname, Class<?> cclass, ProtectionDomain d, byte[] cbuf) throws IllegalClassFormatException {
       // if (!cname.contains("barad")) return cbuf;
        if (!AutConf.isAut(cname))
            return cbuf;
      //  logger.info("transforming: " + cname);

        try {
            ClassReader cr = new ClassReader(cbuf);
            ClassNode cn = new ClassNode();
            cr.accept(cn, 0);


            for (Object fo : cn.methods) {
                MethodNode f = (MethodNode) fo;
                Set<DataMarker> defs = new HashSet<DataMarker>();
                Set<DataMarker> uses = new HashSet<DataMarker>();
                String key =  cname + "." + f.name;

                InsnList ils = new InsnList();
                ils.add(new LdcInsnNode(key));
                ils.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "edu/wmich/cs/radish/ddg/DependencyData", "markFunStart", "(Ljava/lang/String;)V", false));
                f.instructions.insert(f.instructions.getFirst(), ils);

                if (f.name.equals("actionPerformed")) {
                    InsnList es = new InsnList();
                    es.add(new LdcInsnNode(key));
                    es.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "edu/wmich/cs/radish/ddg/DependencyData", "markEventStart", "(Ljava/lang/String;)V", false));
                    f.instructions.insert(f.instructions.getFirst(), es);
                }

                Iterator<AbstractInsnNode> it = f.instructions.iterator();
                while (it.hasNext()) {
                    AbstractInsnNode i = it.next();

                    if (i.getOpcode() == Opcodes.INVOKESTATIC ) {
                        MethodInsnNode methodInsnNode = (MethodInsnNode) i;
                        System.out.println(methodInsnNode.owner + " : " + methodInsnNode.name);
                        if (methodInsnNode.name.equals("exit") && methodInsnNode.owner.contains("System")) {
                            f.instructions.remove(i);
                        }
                    }

                    if (i.getOpcode() == Opcodes.PUTFIELD || i.getOpcode() == Opcodes.PUTSTATIC) {
                        FieldInsnNode fin = (FieldInsnNode) i;
                        InsnList il = new InsnList();
                        il.add(new LdcInsnNode(key));
                        il.add(new TypeInsnNode(Opcodes.NEW, "edu/wmich/cs/radish/ddg/DataMarker"));
                        il.add(new InsnNode(Opcodes.DUP));
                        il.add(new LdcInsnNode(fin.desc));
                        il.add(new LdcInsnNode(fin.owner));
                        il.add(new LdcInsnNode(fin.name));
                        il.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "edu/wmich/cs/radish/ddg/DataMarker", "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", false));
                        il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "edu/wmich/cs/radish/ddg/DependencyData", "addFunDef", "(Ljava/lang/String;Ledu/wmich/cs/radish/ddg/DataMarker;)V", false));
                        f.instructions.insert(i, il);
                    }
                    if (i.getOpcode() == Opcodes.GETFIELD || i.getOpcode() == Opcodes.GETSTATIC) {
                        FieldInsnNode fin = (FieldInsnNode) i;
                        InsnList il = new InsnList();
                        il.add(new LdcInsnNode(key));
                        il.add(new TypeInsnNode(Opcodes.NEW, "edu/wmich/cs/radish/ddg/DataMarker"));
                        il.add(new InsnNode(Opcodes.DUP));
                        il.add(new LdcInsnNode(fin.desc));
                        il.add(new LdcInsnNode(fin.owner));
                        il.add(new LdcInsnNode(fin.name));
                        il.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "edu/wmich/cs/radish/ddg/DataMarker", "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", false));
                        il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "edu/wmich/cs/radish/ddg/DependencyData", "addFunUse", "(Ljava/lang/String;Ledu/wmich/cs/radish/ddg/DataMarker;)V", false));
                        f.instructions.insert(i, il);
                    }

                    if (i.getOpcode() == Opcodes.RETURN || i.getOpcode() == Opcodes.IRETURN || i.getOpcode() == Opcodes.LRETURN || i.getOpcode() == Opcodes.FRETURN || i.getOpcode() == Opcodes.DRETURN || i.getOpcode() == Opcodes.ARETURN) {
                        InsnList ile = new InsnList();
                        ile.add(new LdcInsnNode(key));
                        ile.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "edu/wmich/cs/radish/ddg/DependencyData", "markFunEnd", "(Ljava/lang/String;)V", false));
                        f.instructions.insert(i.getPrevious(), ile);

                        if (f.name.equals("actionPerformed")) {
                            InsnList ee = new InsnList();
                            ee.add(new LdcInsnNode(key));
                            ee.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "edu/wmich/cs/radish/ddg/DependencyData", "markEventEnd", "(Ljava/lang/String;)V", false));
                            f.instructions.insert(i.getPrevious(), ee);

                        }
                    }


                }
            }

            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            cn.accept(cw);

            byte[] ret = cw.toByteArray();

            try {
                File file = new File("instrumented/dependency-agent" + "/" + cname + ".class");
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

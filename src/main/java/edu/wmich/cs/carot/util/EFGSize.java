package edu.wmich.cs.carot.util;

import edu.umd.cs.guitar.model.XMLHandler;
import edu.umd.cs.guitar.model.data.EFG;
import edu.wmich.cs.spinach.AutEFG;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by Lin Cheng on 1/14/17.
 */
public class EFGSize {
    private String efgFile;
    private String outputFile;
    public EFGSize(String efgFile, String outputFile) {
        this.efgFile = efgFile;
        this.outputFile = outputFile;
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            args = new String[]{
                    "./log/barad/barad.EFG",
                    "./log/barad/efg.size"
            };
        }
        EFGSize efgSize = new EFGSize(args[0], args[1]);
        XMLHandler xml = new XMLHandler();
        EFG efg = (EFG) xml.readObjFromFile(efgSize.efgFile, EFG.class);
        AutEFG autEFG = new AutEFG(efg);
        int numOfEvents = efg.getEvents().getEvent().size();
        int numOfEges = autEFG.getNumOfEdges();

        String text = numOfEvents + "\n" + numOfEges;
        try {
            Files.write(Paths.get(efgSize.outputFile), text.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

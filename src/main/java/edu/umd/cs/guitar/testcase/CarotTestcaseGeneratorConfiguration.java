package edu.umd.cs.guitar.testcase;

import edu.umd.cs.guitar.testcase.JimpleAnalysisConfiguration;
import org.kohsuke.args4j.Option;

/**
 * Created by oliver on 08/09/16.
 */
public class CarotTestcaseGeneratorConfiguration extends JimpleAnalysisConfiguration {

    @Option(name = "--gui", usage = "path to gui file")
    public static String GUI;
}

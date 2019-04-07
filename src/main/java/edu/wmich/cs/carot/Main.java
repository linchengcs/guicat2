package edu.wmich.cs.carot;


import edu.umd.cs.guitar.model.XMLHandler;
import edu.umd.cs.guitar.model.data.EFG;
import edu.umd.cs.guitar.model.data.EventType;
import edu.umd.cs.guitar.testcase.JimpleAnalysisConfiguration;
import edu.umd.cs.guitar.testcase.plugin.JimpleAnalysis;
import edu.umd.cs.guitar.testcase.plugin.ct.CTBodyTransformer;
import edu.umd.cs.guitar.testcase.plugin.ct.CTSlicer;
import edu.umd.cs.guitar.testcase.plugin.ct.CTSootRunner;
import edu.umd.cs.guitar.testcase.plugin.ct.entity.event.CTEfgEvent;
import edu.umd.cs.guitar.testcase.plugin.ct.entity.event.CTEvent;
import edu.wmich.cs.carot.ddg.SliceDDG;
import edu.wmich.cs.carot.util.Olog;
import edu.wmich.cs.graph.DiGraph;
import edu.wmich.cs.graph.Matrix;
import edu.wmich.cs.graph.Node;
import edu.wmich.cs.carot.util.Graph2Gephi;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by rick on 8/12/16.
 */
public class Main {

    public static void main(String[] args) {
        String[] args_ticket = new String[] {
            "./aut/ticket.EFG",
            "./aut/ticket.jar"
        };

        String[] args_rachota = new String[] {
            "./aut/rachota.EFG",
            "./aut/Rachota.jar"
        };

        String[] args_terpword = new String[] {
            "./aut/terpword.EFG",
            "./aut/terpword.jar"
        };

        String[] args_barad = new String[] {
            "./log/barad/barad.EFG",
            "./aut/barad-ticket.jar"
        };

        String[] args_jnotepad = new String[] {
            "./aut/jnotepad.EFG",
            "./aut/jnotepad.jar"
        };

        String[] args_terpspreadsheet = new String[] {
            "./aut/terpspreadsheet.EFG",
            "./aut/terpspreadsheet.jar"
        };

        String[] args_terppaint = new String[] {
            "./aut/terppaint.EFG",
            "./aut/TerpPaint3"
        };

        String[] args_workout = new String[] {
                "./aut/workout.EFG",
                "./aut/workout.jar"
        };

        //you need change **args** and **PACKAGE**
        args = args_barad;
        // read EFG
        XMLHandler xml = new XMLHandler();
        EFG efg = (EFG) xml.readObjFromFile(args[0], EFG.class);

        // setup analysis
        JimpleAnalysis analysis = new JimpleAnalysis();
        JimpleAnalysisConfiguration.SCOPE = args[1];
        JimpleAnalysisConfiguration.LENGTH = 1;
        JimpleAnalysisConfiguration.PACKAGE = "barad";  //**ticket** for Ticket, **barad** for BaradTicket  **workout** for workout


        // get events
        List<EventType> eventTypes = efg.getEvents().getEvent();
        List<CTEvent> efgEvents = CTEfgEvent.eventType2ctEvent(eventTypes);

        // execute Soot runner
        CTSootRunner sootRunner = new CTSootRunner();
        CTBodyTransformer bodyTransformer = sootRunner.run(
                                                           JimpleAnalysisConfiguration.SCOPE,
                                                           JimpleAnalysisConfiguration.CLASSPATH,
                                                           JimpleAnalysisConfiguration.PACKAGE);

        // run slicer
        CTSlicer slicer = new CTSlicer(bodyTransformer, efgEvents);
        slicer.run();

        String mat_unlabel = slicer.generateDependencyMatrix(false);
        Matrix m1 = new Matrix();
        m1.readInString(mat_unlabel);
        Olog.log.info("unlabeld matrix\n" + m1.toString());

        String mat = slicer.generateDependencyMatrix();
        Matrix m = new Matrix();
        m.readInString(mat);
        Olog.log.info("label matrix\n" + m.toString());



        DiGraph<CTEvent> diGraph = new DiGraph<>();
        diGraph.addNodesFromEvents(efgEvents);
        diGraph.addEdgesFromMatrix(m);

      //  diGraph.setRootByLabel("e57924816");  //barad terminal event
               diGraph.setRootByLabel("e1385016382"); //rachota terminal event
        //    diGraph.setRootByLabel("e2856389150"); //terpword terminal event
        //        diGraph.setRootByLabel("e1022911290"); //terpword terminal event
        //     diGraph.setRootByLabel("e3783432250"); //terpword terminal event
        //      DTree<CTEvent> myTree = diGraph.generateDTree();



        Olog.log.info(Graph2Gephi.graph2Gephi(efgEvents, m));
        //        Olog.log.info(myTree.toString());
        //        Olog.log.info(Graph2Gephi.dTree2Gephi(myTree));


        DiGraph<CTEvent> graphLabeled = slicer.generateDiGraph(false);
        //        Olog.log.info(graphLabeled.toString());

        DiGraph<CTEvent> graphLabeled1 = slicer.generateDiGraph();
     //   graphLabeled1.removeIsolatedNodes();
        Olog.log.info(graphLabeled1.toString());

        Olog.log.info(graphLabeled.edgeSize()+", unlabeled");
        Olog.log.info(graphLabeled1.edgeSize()+", labeled");
        Olog.log.info(graphLabeled1.orphanNodeCount()+", orphan labeled");
        Olog.log.info(graphLabeled1.twoWayEdgeCount()+", two way edges labeled");
        Olog.log.info(graphLabeled.orphanNodeCount()+", orphan unlabeled");
        Olog.log.info(graphLabeled.twoWayEdgeCount()+", two way edges unlabeled");
        Olog.log.info(graphLabeled1.cyclicEdgeCount()+", cyclic edges labeled ");
        Olog.log.info(graphLabeled.cyclicEdgeCount()+", cyclic edges unlabeled");

        // graphLabeled.printEdgeTwoWay();
        //        graphLabeled1.printEdgeTwoWay();
        Olog.log.info(graphLabeled1.twoEdgeNodes().toString());


        String rachota_e1 = "e81410502";  //time useage leaf
        String rachota_e2 = "e1385016382";
     //   int d = graphLabeled1.findShortestPath(graphLabeled1.getNodeByLabel("e834838008"), graphLabeled1.getNodeByLabel("e2184256830"));
     //   Olog.log.info("dist="+d);

        SliceDDG sliceDdg = new SliceDDG<>(graphLabeled1, efg);
        sliceDdg.findLeaves();
        Olog.log.info("size of leaves : " + sliceDdg.getLeaves().size() );
        Olog.log.info("size of leaves group: " + sliceDdg.getLeafgroups().size() );

        sliceDdg.findLeafGroups();
        Olog.log.info("size of leaves : " + sliceDdg.getLeaves().size() );
        Olog.log.info("size of leaves group: " + sliceDdg.getLeafgroups().size() );


        //bad, graph from root to leaf, tree from leaf to root
//        graphLabeled1.printPathBetweetTwoNodes(rachota_e1, rachota_e2);
/*
        Node<CTEvent> rachota_leaf = graphLabeled1.getNodeByLabel("e81410502");
       Node<CTEvent> rachota_root = graphLabeled1.getNodeByLabel("e1385016382");

        Node<CTEvent> barad_root = graphLabeled1.getNodeByLabel("e220300476");

        Olog.log.info(sliceDdg.getNextEFGEvents(graphLabeled1.getNodeByLabel("e81410502").getElement()).toString());
        Olog.log.info(sliceDdg.getNextEFGEvents(graphLabeled1.getNodeByLabel("e1385016382").getElement()).toString());

        List<CTEvent> efgpath = sliceDdg.shortestEFGPathBetween(rachota_leaf.getElement(), rachota_root.getElement());
        Olog.log.info(efgpath.toString());
*/
  //      Olog.log.info(sliceDdg.printEFG());

        List<Node<CTEvent>> roots = new ArrayList<>();
      //  roots.add(rachota_root);
                sliceDdg.setRoots(roots);
        sliceDdg.setRoots(graphLabeled1.getNodes());
        sliceDdg.generateTestCase();

        Set<List<CTEvent>> eventSequences = sliceDdg.getEventSequences();
        Olog.log.info("event seq size " + eventSequences.size());
        Olog.log.info(eventSequences.toString());
     //   sliceDdg.writeTestCase("loggers/barad/testcase");
    //    sliceDdg.writeTestCase("loggers/rachota/testcase");
        sliceDdg.writeTestCase("loggers/terpspreadsheet/testcase");

    }
}


// 3 places to change including adding textfield
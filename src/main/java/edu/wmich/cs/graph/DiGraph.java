package edu.wmich.cs.graph;

import edu.umd.cs.guitar.testcase.plugin.ct.entity.event.CTEvent;
import edu.wmich.cs.carot.util.Olog;

import java.util.*;

public class DiGraph<E> {
    protected List<Node<E>> nodes;
    protected List<Edge<E>> edges;

    private Node<E> root;
    private List<Node<E>> isolatedNodes;

    public static final int MAX_NODES_BETWEEN_LEAF_ROOT = 3;

    public DiGraph() {
        nodes = new LinkedList<>();
        edges = new LinkedList<>();
        isolatedNodes = new LinkedList<>();
    }


    public Node<E> getRoot () {
        return root;
    }

    public boolean setRoot(Node<E> node) {
        if (nodes.contains(node)) {
            root = node;
            return true;
        }
        return false;
    }

    public void addNode(Node<E> n) {
        if (!nodes.contains(n))
            nodes.add(n);
    }

    public int size() {
        return this.nodes.size();
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public int edgeSize() {
        return this.edges.size();
    }

    public void addNodes(List<E> n) {
        for (E e : n) {
            nodes.add(new Node(e));
        }
    }

    public List<Node<E>> getNodes() {
        return nodes;
    }

    public void addNodesFromEvents(List<CTEvent> events) {
        for (CTEvent e : events) {
            nodes.add(new Node(e, e.getIdentifier()));
        }
    }

    public boolean addEdge(Node<E> from, Node<E> to) {
        if (!nodes.contains(from) || !nodes.contains(to))
            return false;
        Edge<E> edge = new Edge(from, to);

        if (haveEdge(from, to))
            return true;
        edges.add(edge);
        return from.addEdge(edge) && to.addEdge(edge);
    }

    public void resetVisited() {
        for (Node<E> node : nodes)
            node.setVisited(false);
    }

    public boolean addEdge(int i, int j) {
        return addEdge(nodes.get(i), nodes.get(j));
    }

    public boolean addEdge(Node<E> from, Node<E> to, Label<E> label) {
        if (!nodes.contains(from) || !nodes.contains(to))
            return false;
        Edge<E> edge = new Edge(from, to, label);

        if (haveEdge(from, to))
            return true;
        edges.add(edge);
        return from.addEdge(edge) && to.addEdge(edge);
    }

    public Node<E> getNodeByLabel(String label) { //not edge label
        for (Node<E> node : nodes)
            if (node.getLabel().equals(label))
                return node;
        return null;
    }

    public Node<E> getNodeByElement(E element) {
        for (Node<E>  node : nodes)
            if (node.getElement() == element)
                return node;
        return null;
    }

    private boolean haveEdge(Node<E> from, Node<E> to) {
        for (Edge<E> edge : edges) {
            if (edge.getFrom() == from && edge.getTo() == to)
                return true;
        }
        return false;
    }

    public void addEdgesFromMatrix(Matrix matrix) {
        assert matrix.ncol == size() && matrix.nrow == size();
        for (int i = 0; i < matrix.nrow; i++) {
            for (int j = 0; j < matrix.ncol; j++) {
                switch (matrix.value(i,j)) {
                    case 1:
                        addEdge(i, j);
                        break;
                    case 0:
                        break;
                    default:
                        Olog.log.error("Sorry, elements in this graph matrix should be 0 or 1");
                        break;
                }
            }
        }
    }

    //cp graphNode.element to treeNode.element parellelly, maintain the parent/children relationship
    public DTree<E> generateDTree(Node<E> graphNode, DTree<E> treeNode) {
        E element = graphNode.getElement();
        DTree<E> newNode = new DTree<>(element);
        if (treeNode == null) {
            //if this is a root node, add current node to root.
            treeNode = newNode;
        }  else {
            //if this is not root, add current node to children
            treeNode = treeNode.addChild(element);
        }

        for (Edge<E> edge : graphNode.getInEdges()) {
            Node<E> graphChild = edge.getFrom();
            //break circles
            if (treeNode.contains(graphChild.getElement()))
                continue;
            generateDTree(graphChild, treeNode);
        }

        return treeNode;
    }

    public DTree<E> generateDTree() {
        return generateDTree(root, null).root();
    }

    public void setRootByLabel(String label) {
        for (Node<E> node : nodes) {
            if (label.equals(node.getLabel())) {
                root = node;
                return;
            }
        }
    }

    public Edge<E> getEdge(Node<E> from, Node<E> to) {
        for (Edge<E> edge : edges) {
            if (edge.getFrom() == from && edge.getTo() == to) {
                return edge;
            }
        }
        return null;
    }

    public int orphanNodeCount() {
        int ret = 0;
        for (Node<E> node : nodes) {
            if (node.isOrphan())
                ret ++;
        }
        return ret;
    }

    public int twoWayEdgeCount() {
        int ret = 0;
        for (Edge<E> edge : edges) {
            if (edge.isTwoWay())
                ret ++;
        }
        return ret;
    }

    public int cyclicEdgeCount() {
        int ret = 0;
        for (Edge<E> edge : edges) {
            if (edge.isCyclic())
                ret ++;
        }
        return ret;
    }

    public Edge<E> findReverseEdge(Edge<E> edge) {
        for (Edge<E> e : edges) {
            if (e.getFrom() == edge.getTo() && e.getTo() == edge.getFrom())
                return e;
        }
        return null;
    }

    public void printEdgeTwoWay() {
        Olog.log.info("\n====printing two way edges====");
        for (Edge<E> edge : edges) {
            if (edge.isTwoWay()) {
                Olog.log.info("\n------printing node: " + edge.getFrom() + ", and " + edge.getTo() + "------");
                Olog.log.info(edge.toString());
                Olog.log.info(findReverseEdge(edge).toString());
            }
        }
    }

/*return a set of nodes whose element has a at least one two way edge
*/
    public Set<Node<E>> twoEdgeNodes() {
        Set<Node<E>> ret = new HashSet<>();
        for (Edge<E> edge : edges) {
            if (edge.isTwoWay()) {
                ret.add(edge.getFrom());
                ret.add(edge.getTo());
            }
        }
        return ret;
    }

    public void printEdge(){
        Olog.log.info("====printing contrast edges====");
        for (Node<E> node : nodes) {
            Olog.log.info("++++++" + node.toString() + "++++++");
            Olog.log.info("------" + "in edges" + "------");
            for (Edge<E> in : node.getInEdges()) {
                Olog.log.info(in.toString());
            }
            Olog.log.info("------" + "out edges" + "------");
            for (Edge<E> out : node.getOutEdges()) {
                Olog.log.info(out.toString());
            }
        }
    }


    //don't use, because isolated noses is useful when make feasible
    public void removeIsolatedNodes() {
        List<Node<E>> newNodes = new LinkedList<>();
        for (Node<E> node : nodes) {
            if (node.isOrphan()) {
                isolatedNodes.add(node);
            } else {
                newNodes.add(node);
            }
        }
        nodes = newNodes;
    }


    public List<E> findShortestPath(E src, Set<E> tgts) {
        Set<Node<E>> unsettled = new HashSet<>();
        Set<Node<E>> settled = new HashSet<>();
        Map<Node<E>, Integer> distance = new HashMap<>();
        Map<Node<E>, Node<E>> preDecessor = new HashMap<>();
        for (Node<E> node : nodes) {
            distance.put(node, Integer.MAX_VALUE);
            unsettled.add(node);
        }
        distance.put(getNodeByElement(src), 0);
        preDecessor.put(getNodeByElement(src), null);

        while (!unsettled.isEmpty()) {
            Node<E> next = findMinValue(distance, unsettled);
            int dist = distance.get(next);
            if (dist == Integer.MAX_VALUE)
                break;

            settled.add(next);
            unsettled.remove(next);
            for (Edge<E> edge : next.getOutEdges()) {
                int tmp = dist + edge.getWeight();
                if (tmp < distance.get(edge.getTo())) {
                    distance.put(edge.getTo(), tmp);
                    preDecessor.put(edge.getTo(), next);
                }
            }
        }

        int len = Integer.MAX_VALUE;
        E shortTarget = null;
        for (E tgt : tgts) {
            Node<E> tgtNode = getNodeByElement(tgt);
            if (distance.get(tgtNode) < len) {
                len = distance.get(tgtNode);
                shortTarget = tgt;
            }
        }

        List<E> ans = new ArrayList<>();
        Node<E> tgtNode1 = getNodeByElement(shortTarget);
        while (tgtNode1 != null) {
            ans.add(0, tgtNode1.getElement());
            tgtNode1 = preDecessor.get(tgtNode1);
        }
        return ans;
    }

    public int findShortestPath(Node<E> src, Node<E> tgt) {
        Set<Node<E>> unsettled = new HashSet<>();
        Set<Node<E>> settled = new HashSet<>();
        Map<Node<E>, Integer> distance = new HashMap<>();
        Map<Node<E>, Node<E>> preDecessor = new HashMap<>();
        for (Node<E> node : nodes) {
            distance.put(node, Integer.MAX_VALUE);
            unsettled.add(node);
        }
        distance.put(src, 0);
        preDecessor.put(src, null);

        while (!unsettled.isEmpty()) {
            Node<E> next = findMinValue(distance, unsettled);
            int dist = distance.get(next);
            if (dist == Integer.MAX_VALUE)
                break;

            settled.add(next);
            unsettled.remove(next);
            for (Edge<E> edge : next.getOutEdges()) {
                int tmp = dist + edge.getWeight();
                if (tmp < distance.get(edge.getTo())) {
                    distance.put(edge.getTo(), tmp);
                    preDecessor.put(edge.getTo(), next);
                }
            }
        }

        int tmp =  distance.get(tgt);
        return tmp == Integer.MAX_VALUE ? -1 : tmp;
    }

    private Node<E> findMinValue(Map<Node<E>, Integer> distance, Set<Node<E>> unsettled) {
        Node<E> ret = null;
        int min = -1;
        for (Node<E> node : unsettled) {
            int tmp  = distance.get(node);
            if (ret == null) {
                ret = node;
                min = tmp;
            } else if (tmp < min) {
                ret = node;
                min = tmp;
            }
        }
        return ret;
    }

    public int findDistBetweenLeaves(Node<E> left, Node<E> right) {
        int ret = Integer.MAX_VALUE;
        for(Node<E> node : nodes) {
            int dl = findShortestPath(left, node);
            int dr = findShortestPath(right, node);
            if (dl >= 0 && dr >= 0 && dl + dr < ret) { // handle unreachable
                ret = dl + dr;
            }
        }
        ret = ret == Integer.MAX_VALUE ? -1 : ret;
  //      Olog.log.info("distance between " + left.toString() + " and " + right.toString() + " is " + ret);
        return  ret;
    }

    public void searchTreeFromLeavesToRoot(Set<Node<E>> leaves, Node<E> root) {
//        generate one sequence from leaf1 to root
//            from sequence node 0 , generate leaf 2 to node 0
//            until all leaves included, or length reached

    }

    public void printPathBetweetTwoNodes(Node<E> src, Node<E> tgt, List<Node<E>> path, List<List<E>> paths) {
        if (path.isEmpty()) {
            path.add(tgt);
        }
        if (src == tgt) {
            paths.add(getEventPath(path));
       //     Olog.log.info(path.toString());
            return;
        }
        if (path.size() >= MAX_NODES_BETWEEN_LEAF_ROOT + 2 ) {
   //         Olog.log.info(path.toString());
            return;
        }

        for (Edge<E> edge : tgt.getInEdges()) {
            Node<E> node = edge.getFrom();
            if (path.contains(node))
                continue;
            path.add(node);
            printPathBetweetTwoNodes(src, node, path, paths);
            path.remove(path.size()-1);
        }
    }

    //reverse the order, because DTree is constructed from leaf to root
    private List<E> getEventPath(List<Node<E>> path) {
        List<E> ret = new ArrayList<E>();
        for (Node<E> node : path) {
            ret.add(0, node.getElement());
        }
        return ret;
    }

    public void printPathBetweetTwoNodes(String srcstr, String tgtstr) {
        Node<E> src = getNodeByLabel(srcstr);
        Node<E> tgt = getNodeByLabel(tgtstr);
        List<List<E>> paths = new ArrayList<>();
        printPathBetweetTwoNodes(src, tgt, new ArrayList<Node<E>>(), paths);
        Olog.log.info(paths.toString());
        DTree<E> dTree = new DTree<E>();
        dTree = dTree.merge(paths);
    //    Olog.log.info("");
    }

    public List<List<E>> generatePathsBetweenTwoNodes(Node<E> leaf, Node<E> root) {
        List<List<E>> paths = new ArrayList<>();
        printPathBetweetTwoNodes(leaf, root, new ArrayList<Node<E>>(), paths);
        return paths;
    }

    public List<List<List<E>>> generatePathsSetBetweenRootAndLeaves(List<Node<E>> leaves, Node<E> root) {
        List<List<List<E>>> pathsSet = new ArrayList<>();
        for (Node<E> leaf : leaves) {
            pathsSet.add(generatePathsBetweenTwoNodes(leaf, root));
        }
        return pathsSet;
    }


    public void combination(List<Integer> array, int counter, List<Integer> ans) {
        if (ans.size() == counter) {
            Olog.log.info("printing combination: " + ans);
            return;
        }
        for (int i = 0; i < array.size(); i++) {
            ans.add(array.get(i));
            ArrayList<Integer> newArray = new ArrayList<>();
            for (int j = i+1;  j < array.size(); j++) {
                if (j == i)
                    continue;
                newArray.add(array.get(j));
            }
            combination(newArray, counter, ans);
            ans.remove(ans.size()-1);
        }
    }
    public void combinationTest() {
        List<Integer> array = Arrays.asList(1,2,3,4);
        int counter = 2;
        List<Integer> ans = new ArrayList<>();
        combination(array, counter, ans);
    }

    public Set<List<E>> generatePath(int len, boolean cyclic, boolean includeShort) {
        Set<List<E>> ans = new HashSet<>();

        for (Node<E> node : nodes) {
            List<E> current = new ArrayList<>();
            generatePath(len, cyclic, includeShort, node, current, ans);
        }
        return ans;
    }

    private void generatePath(int len, boolean cyclic, boolean includeShort, Node<E> newNode, List<E> current, Set<List<E>> ans) {
        current.add(newNode.getElement());
        if (current.size() >= len) {
            ans.add(current);
            return;
        }
        for (Edge<E> edge : newNode.getOutEdges()) {
            Node<E> newnewNode = edge.getTo();
            if (!cyclic && current.contains(newnewNode.getElement()))
                break;
            generatePath(len, cyclic, includeShort, newnewNode, current, ans);
        }
        if (includeShort)
            ans.add(current);
    }

    public void dfs(Node<E> node, IDiGraphVisitor<E> visitor) {
        visitor.visit(node);
        for (Edge<E> edge : node.getOutEdges()) {
            Node<E> child = edge.getTo();
            visitor.visit(child);
        }
    }

    public void generateBranchMarker() {
          for (Edge<E> edge : edges) {
              if (!edge.isCyclic() && generatePathsBetweenTwoNodes(edge.getFrom(), edge.getTo()).size() == 1) {
                  edge.setBranchMarker(true);
              }
          }
    }

    @Override
    public String toString() {
        return "DiGraph{" +
                "\nnodes=" + nodes +
                "\n,edges=" + edges +
                "\n,root=" + root +
                "\n,size=" + size() +
                "\n,connected nodes" + (nodes.size()-orphanNodeCount()) +
                "\n,edgeSize=" + edgeSize() +
                '}';
    }

    public static void main(String[] args) {
               new DiGraph().combinationTest();
      //  DiGraph<E> diGraph = new DiGraph();
    }
}

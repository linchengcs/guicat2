package edu.wmich.cs.graph;

/**
 * Created by oliver on 11/08/16.
 */
public class Edge<E> {
    private Node<E> from;
    private Node<E> to;
    private Label<E> label;
    private int weight;
    private boolean branchMarker;

    public Edge(Node<E> from, Node<E> to) {
        this.from = from;
        this.to = to;
        this.weight = 1;
        this.branchMarker = false;
    }

    public Edge(Node<E> from, Node<E> to, Label<E> label) {
        this(from, to);
        this.label = label;
    }

    public Node<E> getFrom() {
        return from;
    }

    public Node<E> getTo() {
        return to;
    }

    public Label<E> getLabel() {
        return label;
    }

    public void setFrom(Node<E> from) {
        this.from = from;
    }

    public void setTo(Node<E> to) {
        this.to = to;
    }

    public int getWeight() {
        return this.weight;
    }

    public boolean isTwoWay() {
        //        return from.getInEdges().contains(this); //wrong
        for (Edge<E> edge : from.getInEdges()) {
            if (edge.getFrom() == to && edge.getTo() == from)
                return true;
        }
        return false;
    }

    public boolean isOneWay() {
        return !isTwoWay();
    }

    public boolean isCyclic() {
        return from == to;
    }

    public void setLabel(Label<E> label) {
        this.label = label;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public boolean isBranchMarker() {
        return branchMarker;
    }

    public void setBranchMarker(boolean branchMarker) {
        this.branchMarker = branchMarker;
    }

    @Override
    public String toString() {
        if (label == null )
            return "\nEdge{" +  from + " ---> " + to + "branchMarker=" + (branchMarker?"true":"false") + "}";
        return "\nEdge{" +  from + " ---> " + to + ", label:" + label.toString() + "branchMarker=" + (branchMarker?"true":"false") + "}";
    }
}


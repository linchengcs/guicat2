package edu.wmich.cs.graph;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by oliver on 11/08/16.
 */
public class Node<E> {
    private E element;
    private List<Edge<E>> inEdges;
    private List<Edge<E>> outEdges;
    private String label;
    private boolean visited;
    private int mark;

    public Node() {
        this.inEdges = new LinkedList<>();
        this.outEdges = new LinkedList<>();
        this.visited = false;
        this.mark = 0;
    }

    public Node (E element) {
        this();
        this.element = element;
    }

    public Node(E element, String label) {
        this(element);
        this.label = label;
    }

    public E getElement() {
        return element;
    }

    public void setElement(E element) {
        this.element = element;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<Edge<E>> getInEdges() {
        return inEdges;
    }

    public List<Edge<E>> getOutEdges() {
        return outEdges;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public boolean addEdge(Node<E> from, Node<E> to) {
        Edge<E> edge = new Edge<>(from, to);
        return addEdge(edge);
    }

    public boolean addEdge(Edge<E> edge) {
        if (edge.getFrom() == edge.getTo()) {
            if (!this.outEdges.contains(edge))
                this.outEdges.add(edge);
            if (!this.inEdges.contains(edge))
                this.inEdges.add(edge);
        } else if (this == edge.getFrom()) {
            if (!this.outEdges.contains(edge))
                this.outEdges.add(edge);
        } else if (this == edge.getTo()) {
            if (!this.inEdges.contains(edge))
                this.inEdges.add(edge);
        } else
            return false;
        return true;
    }

    public boolean isOrphan() {
       // return inEdges.isEmpty() && outEdges.isEmpty();
        for (Edge<E> in : inEdges) {
            if (!in.isCyclic()) {
                return false;
            }
        }
        for (Edge<E> out : outEdges) {
            if (!out.isCyclic()) {
                return false;
            }
        }
        return true;
    }

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    @Override
    public String toString() {
        if (label == null)
            return this.element.toString();
        return label;
        //        return "Node:" + label + ", listen:" +  ((EventType)((CTEfgEvent)element).getObject()).getAction();
    }
}

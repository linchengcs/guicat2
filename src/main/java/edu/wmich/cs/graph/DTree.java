package edu.wmich.cs.graph;

import edu.umd.cs.guitar.testcase.plugin.ct.entity.event.CTEvent;
import edu.wmich.cs.carot.util.Olog;

import java.util.*;

/**
 * Created by oliver on 12/08/16.
 * in this DTree, each node the (E value) should be unique
 * should use Node<E> as element, rather than E value
 */
public class DTree<E> implements Comparable<DTree<E>>{
    private E value;
    private DTree<E> parent;
    private SortedSet<DTree<E>> children;

    public DTree() {

    }

    public DTree(E value) {
        this.value = value;
        parent = null;
        children = new TreeSet<DTree<E>>();
    }

    //copy a tree
    public DTree(DTree<E> dTree) {
        this.value = value;
        this.parent = dTree.getParent();
        this.children = dTree.getChildren();
    }

    public DTree(List<E> list) {
        int len = list.size();
        DTree<E> root = new DTree<E>(list.get(len-1));
        DTree<E> current = root;
        for (int i = len - 2; i >= 0; i--) {
            current.addChild(list.get(i));
            current = current.getChildren().first();
        }
        this.value = root.value;
        this.parent = root.getParent();
        this.children = root.children;
    }

    public E getValue() {
        return value;
    }

    public void setValue(E value) {
        this.value = value;
    }

    public DTree<E> getParent() {
        return parent;
    }

    public void setParent(DTree<E> parent) {
        this.parent = parent;
        parent.addToChildrenList(this);
    }

    public SortedSet<DTree<E>> getChildren() {
        return children;
    }

    //to add branch, do not use this
    private void addToChildrenList(DTree<E> dTree) {
        if (!children.contains(dTree))
            children.add(dTree);
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    public boolean isRoot() {
        return parent == null;
    }

    public boolean isEmpty() {
        return value == null;
    }

    public boolean contains(final E target) {
       return find(target) != null;
    }

    public DTree<E> find(E target) {
        FindVisitor<E> findVisitor = new FindVisitor<>(target);
        dfs(this.root(), findVisitor);
        return findVisitor.find();
    }

    public void dfs(DTree<E> dTree, Visitor<E> visitor) {
        visitor.visit(dTree);
        if (visitor.exit())
            return;
        for(DTree<E> child : dTree.getChildren())
            dfs(child, visitor);
    }

    //add a child, return the node contains the value;
    public DTree<E> addChild(E value) {
        //if the value already exists in some node; not necessary, this check is done in digraph
        DTree<E> find = find(value);
        if ( find != null )
            return find;
        //if this is a new value, add to child
        DTree<E> dTree = new DTree<>(value);
        dTree.parent = this;
        children.add(dTree);
        return  dTree;
    }

    public DTree<E> root() {
        if (isRoot())
            return this;
        return getParent().root();
    }

    //here the element is not a node, is an event; tree itself is a tree node, graph node is inside graph
    //sequence is a list of event, not list of graph node
    //sequence should be from leaf to root


    //!!!!!!wrong, should equals after a single event
    private void merge(DTree<E> dTree, List<E> sequence) {
        int counter = 0;
        int index = 0;
        E shared = null;
        DTree<E> sharedTree = null;
        /*
        for(E e : sequence) {
            DTree<E> findNode = dTree.find(e);
            if (findNode == null || findNode.isRoot() || findNode.isLeaf())
                continue;
            counter++;
            shared = e;
            sharedTree = findNode;
        }
        if (counter ==0 || counter > SliceDDG.MAX_SEQUENCE_SHARE_NODE) {
            return;
        }
        */
        for(E e : sequence) {
            DTree<E> findNode = dTree.find(e);
            if (findNode != null) {
                shared = e;
                sharedTree = findNode;
                break;
            }
        }
        assert shared != null : "not possible, shared should never be null";

        //now should have exactly one share node beside root and leaves
        index = sequence.indexOf(shared);
        DTree<E> branch = new DTree<E>(sequence.subList(0, index));
        branch.setParent(sharedTree);
    }

    //shoud merge all dTrees
    public DTree<E> merge(List<List<E>> lists) {
        for (int i = 0; i < lists.size(); i++) {
            for (int j = i+1; j < lists.size(); j++) {
                if (!hasSameTail(lists.get(i), lists.get(j))) {
                    return null;
                }
            }
        }

        DTree<E> ret = new DTree<E>(lists.get(0));
        for (int i = 1; i < lists.size(); i++) {
            merge(ret, lists.get(i));
        }
        return ret;
    }

    private boolean hasSameTail(List<E> list1, List<E> list2) {
        int size1 = list1.size();
        int size2 = list2.size();
        int len = size1 < size2 ? size1 : size2;
        int flag = 0;
        assert list1.get(size1-1) == list2.get(size2-1) : "lists has no same root";
        for (int i = 2; i <= len; i++) {
            E value1 = list1.get(size1 - i);
            E value2 = list2.get(size2 - i);
            if (value1 != value2) {
                flag = 1;
            }
            if (flag == 1 && value1 == value2) {
                return false;
            }
        }
        return true;
    }

    public List<E> toSequence(DTree<E> dTree, List<E> res) {
        res.add(dTree.getValue());
        for (DTree<E> child : dTree.getChildren()) {
            toSequence(child, res);
        }
        return res;
    }

    public List<E> toSequence() {
        List<E> res = new ArrayList<>();
        return toSequence(this, res);
    }

    @Override
    public String toString() {
        dfs(this, new Visitor<E>() {
            @Override
            public void visit(DTree<E> value) {
     //           Olog.log.info(((CTEvent)value.getValue()).getIdentifier());
                Olog.log.info(value.toString());
            }

            @Override
            public boolean exit() {
                return false;
            }
        });
        return null;
    }

    @Override
    public int compareTo(DTree<E> dTree) {
        return ((CTEvent)this.value).getIdentifier().compareTo(((CTEvent)dTree.getValue()).getIdentifier());
    }
}

class FindVisitor<E> implements Visitor<E> {
    E target;
    DTree<E> find;

    public FindVisitor (E target) {
        this.target = target;
        this.find = null;
    }

    public DTree<E> find() {
        return find;
    }

    @Override
    public void visit(DTree<E> node) {
        if (node.getValue().equals(target)) {
            find = node;
        }
    }

    @Override
    public boolean exit() {
        return find() != null;
    }
}



interface Visitor<E> {
    void visit(DTree<E> value);
    boolean exit();
}

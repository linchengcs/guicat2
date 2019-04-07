package edu.wmich.cs.radish.sequence;

import edu.wmich.cs.aut.guess.TestGraph;
import edu.wmich.cs.carot.util.Olog;
import edu.wmich.cs.carot.util.StaticFunc;

import java.util.*;

/**
 * Created by rick on 10/7/16.
 */
public class DPOR<E> {
    private IDDG<E> ddg;
    private IEFG<E> efg;
  //  private List<E> modules;

    private List<List<E>> startSequences;

    public static int MAX_LENGTH = 3;  //5 for payment, 3 for addressbook

    private List<Queue<E>> lq;
    private List<Queue<E>> ls;

    public Stacks<E> stacks;

    private static int helpCounter = 0;

    public Set<List<E>> results;


    public DPOR(IEFG efg, IDDG ddg, Set<E> modules) {
        this.efg = efg;
        this.ddg = ddg;
        this.startSequences = new ArrayList<>();
      //  this.modules = new ArrayList<E>(modules);
        this.lq = new ArrayList<>();
        this.ls = new ArrayList<>();
        this.stacks = new Stacks<E>();
        this.results = new HashSet<>();
    }


    public void generate(Stacks<E> stacks, int count) {
        generateStartSequences();
        for (List<E> startSequence : startSequences) {
            generate(stacks, count, startSequence);
        }
    //    this.stacks = new Stacks<E>();
    }

    /*
     * @count is the current  is a necessary parameter when backtracks
     */
    public void generate(Stacks<E> stacks, int count, List<E> startSequence) {
        if (stacks.getEventsCounter() >= DPOR.MAX_LENGTH ) {
            Olog.log.info("=============== max transition sequence length reached, exit "  + ++helpCounter + " ============");
          //  Olog.log.info(startSequence.toString());
            Olog.log.info(stacks.getEventSequence().toString());
            Olog.log.info(stacks.getEventsCounter() + "");
            this.results.add(new ArrayList<>(stacks.getEventSequence()));
            return;
        }

        //init add first event
        if (stacks.isEmpty()) {
            E init = startSequence.get(startSequence.size() - 1);
            //   stacks.addToSequence(init);  // no need, add to backtrack set, then dfs
            stacks.addBackTrackSet(startSequence.size() - 1, new BackTrackSet<E>(init, false));
            stacks.setEventSequence(startSequence.subList(0, startSequence.size() - 1));
            count = startSequence.size() - 1;
        }
        if (stacks.backTrackHead() == null) {
            Olog.log.info("=============== out of events "  + ++helpCounter + " ============");
            Olog.log.info(stacks.getEventSequence().toString());
            return;
        }

        BackTrackSet<E> currentBackTrackSet = stacks.backTrackHead();
        Queue<Transition<E>> todo = currentBackTrackSet.getBacktrackSet();
        while (!todo.isEmpty()) {
            Olog.log.info(stacks.toString());
            stacks.cleanAfter(count);
            stacks.setEventsCounter(countDataAndLoginEventsInTransition(stacks.getEventSequence())); //maintain the counter to exit
            Transition<E> b = todo.peek();
            E e = b.end();

            //todo, refer to next part
            if (b.isBackTrack()) {
                List<E> currentSeq = stacks.getEventSequence();
                int depIndexFirst = -1;  // closest
                int depIndexSecond = -1;
                for (int i = currentSeq.size() - 1; i >= 0; i--) {
                    E tmp = currentSeq.get(i);
                    if (depIndexFirst == -1 && ddg.hasDependency(e, tmp)) {
                        depIndexFirst = i;
                        continue;
                    }
                    if (depIndexFirst >= 0 && ddg.hasDependency(e, tmp)) {
                        depIndexSecond = i;
                        break;
                    }

                }

                Transition<E> backTrackTransition = new Transition<E>(new ArrayList<E>(currentSeq.subList(depIndexFirst + 1, currentSeq.size())), true);
                backTrackTransition.append(b);
                addTransitionToBackTrackSet(backTrackTransition, depIndexFirst, depIndexSecond);


            }

            stacks.addToSequence(b);
            stacks.setEventsCounter(stacks.getEventsCounter() + countDataAndLoginEventsInTransition(b.getEvents()));

            List<E> candidates = efg.availableAfter(e);
            for (E candidate : candidates) {
                int depIndexFirst = -1;  // closest
                int depIndexSecond = -1;
                List<E> currentSeq = stacks.getEventSequence();
                for (int i = currentSeq.size() - 1; i >= 0; i--) {
                    E tmp = currentSeq.get(i);
                    if (depIndexFirst == -1 && ddg.hasDependency(candidate, tmp)) {
                        depIndexFirst = i;
                        continue;
                    }
                    if (depIndexFirst >= 0 && ddg.hasDependency(candidate, tmp)) {
                        depIndexSecond = i;
                        break;
                    }
                }

                Transition<E> backTrackTransition = new Transition<E>(new ArrayList<E>(currentSeq.subList(depIndexFirst + 1, currentSeq.size())), true);
                backTrackTransition.append(new Transition<E>(candidate, true));
                addTransitionToBackTrackSet(backTrackTransition, depIndexFirst, depIndexSecond);

            }

//            candidates.removeAll(stacks.getEventSequence());
            removeDataAndLogicEventsFromCandidates(candidates, stacks.getEventSequence());
            Stacks<E> nStacks = stacks;
            if (!candidates.isEmpty()) {
                List<E> nonBranches = new ArrayList<E>();
                for (E candidate : candidates) {
                    if (efg.isBranchEdge(e, candidate)) {  // no need here, because the leaves can always reach others if the control events are in place
                        stacks.addBackTrackSet(stacks.size(), new BackTrackSet<E>(candidate, false));
                    } else {
                        nonBranches.add(candidate);
                    }
                }
                // E init = StaticFunc.randFromList(candidates);
                E init = chooseNextEvent(candidates);
                //    stacks.addToSequence(init);  // no need, add to backtrack set, then dfs
                nStacks.addBackTrackSet(stacks.size(), new BackTrackSet<E>(init, false));
            } else {
                nStacks.addBackTrackSet(stacks.size(), null);
            }
            generate(nStacks, count + b.getEvents().size(), startSequence);

            currentBackTrackSet.addBackTracked(b);
        }

    }

    private E chooseNextEvent(List<E> events) {
        for (E e : events) {
            if (!efg.isControlEvent(e)) {
                return e;
            }
        }
        return StaticFunc.randFromList(events);
    }

    private void removeDataAndLogicEventsFromCandidates(List<E> candidates, List<E> eventSequence) {
        for (E e : eventSequence) {
            if (!efg.isControlEvent(e)) {
                candidates.remove(e);
            }
        }
    }

    private void addTransitionToBackTrackSet(Transition<E> transition, int depIndexFirst, int depIndexSecond) {
        if (depIndexFirst < 0)
            return;
        //   assert depIndexFirst >= 0 : "No dependentent events, don't add to any back track sets!";
        assert depIndexSecond < depIndexFirst;

        E transitionStart = transition.getEvents().get(0);
        E transitionEnd = transition.getEvents().get(transition.size() - 1);

        boolean enabled = false;  // if true add only candidate to backtrack; if false add all enabled to backtrack
        boolean added = false;
        List<E> transitionEvents = transition.getEvents();
        List<E> currentSeq = stacks.getEventSequence();

        // no second dependent     ... dep1 ... end
        if (depIndexSecond < 0) {
            outer:
            for (int i = transitionEvents.size() - 1; i >= 0; i--) {
                E curTranEvent = transitionEvents.get(i);
                // add to not first back track set
                for (int j = depIndexFirst; j > 0; j--) {  // j is the backtrack set index
                    E curSeqEvent = currentSeq.get(j - 1);
                    if (efg.availableAfter(curSeqEvent).contains(curTranEvent)) {
                        doAddSubTransitionToBackTrackSet(transition, j, i);
                        added = true;
                        break outer;
                    }
                }

                if (efg.getInitials().contains(transitionEvents.get(i))) {
                    //add to first back track set
                    doAddSubTransitionToBackTrackSet(transition, 0, i);
                    added = true;
                     break outer;
                }
            }
            // if no addable backtrack set, add all initial events to first back track set
            if (!added) {
                for (E e : efg.getInitials()) {
                    doAddTransitionToBackTrackSet(new Transition<E>(e, false), 0);
                }
                Olog.log.info("add all initial events to backtrack set!!");
            }

        } else {
            // dependent with not the first one
            outer:
            for (int i = transitionEvents.size() - 1; i >= 0; i--) {
                E curTranEvent = transitionEvents.get(i);
                // add to not first back track set
                for (int j = depIndexFirst; j > depIndexSecond; j--) {
                    E curSeqEvent = currentSeq.get(j - 1);
                    if (efg.availableAfter(curSeqEvent).contains(curTranEvent)) {
                        doAddSubTransitionToBackTrackSet(transition, j, i);
                        added = true;
                        break outer;
                    }
                }
            }
            // if no addable backtrack set, add all initial events to first back track set
            if (!added) {
                for (E e : efg.availableAfter(currentSeq.get(depIndexSecond))) {
                    doAddSubTransitionToBackTrackSet(new Transition<E>(e, false), depIndexSecond, 0);
                }
                Olog.log.info("add all enabled to backtrack set!! " + currentSeq.get(depIndexSecond).toString());
            }
        }
    }

    /*
     * add transition
     * find enable between two transitions,
     * if not add all first transition backtracks
     * @depIndex: index of event in the stacks.eventSequence, which transition is dependent with
     */
    private void addTransitionToBackTrackSet(Transition<E> transition, int depIndex) {
        E transitionStart = transition.getEvents().get(0);
        E transitionEnd = transition.getEvents().get(transition.size() - 1);

        boolean enabled = false;  // if true add only candidate to backtrack; if false add all enabled to backtrack
        boolean added = false;
        List<E> transitionEvents = transition.getEvents();

        // dependent with first one
        if (depIndex == 0) {
            //search for addable backtrack set
            for (int i = transitionEvents.size() - 1; i >= 0; i--) {
                if (efg.getInitials().contains(transitionEvents.get(i))) {
                    doAddSubTransitionToBackTrackSet(transition, 0, i);
                    added = true;
                    break;
                }
            }
            // if no addable backtrack set, add all initial events to first back track set
            if (!added) {
                for (E e : efg.getInitials()) {
                    doAddTransitionToBackTrackSet(new Transition<E>(e, false), 0);
                }
            }

        } else {
            // dependent with not the first one
//            E before = stacks.getEventSequence().get(depIndex - 1);
//            if (efg.availableAfter(before).contains(transitionStart)) {
//                enabled = true;
//            }
            List<E> currentSeq = stacks.getEventSequence();
            int i = depIndex;
            boolean flag = false;
            while (i-- > 0) {
                E current = currentSeq.get(i+1);
                if (i+1 != depIndex && ddg.hasDependency(current, transitionEnd)) {
                    // no addable backtrack set
                    break;
                } else {
                    E before = currentSeq.get(i);
//???
                }
            }
        }

        if (enabled) {
            doAddTransitionToBackTrackSet(transition, depIndex);
        } else {
            // todo: add all enabled to backtrack set
            BackTrackSet<E> toAdd = stacks.get(depIndex);
            List<E> allEnabled = new ArrayList<E>();
            if (depIndex > 0 ) {
                allEnabled = efg.availableAfter(stacks.getEventSequence().get(depIndex - 1));
            } else {
                allEnabled = efg.getInitials();
            }
            for (E oneEnabled : allEnabled) {
                Transition<E> oneEnabledTransition = new Transition<E>(oneEnabled, true);
                if (!toAdd.getBacktrackSet().contains(oneEnabledTransition) && !toAdd.getBacktracked().contains(oneEnabledTransition)) {
                    toAdd.getBacktrackSet().add(oneEnabledTransition);
                }
            }
        }
    }

    /*
     write to, after done checking
     */
    private void doAddTransitionToBackTrackSet(Transition<E> transition, int depIndex) {
        BackTrackSet<E> toAdd = stacks.get(depIndex);  // add transition before depIndex
        if (!toAdd.getBacktrackSet().contains(transition) && !toAdd.getBacktracked().contains(transition)) {
            toAdd.getBacktrackSet().add(transition);
        }
    }

    private void doAddSubTransitionToBackTrackSet(Transition<E> transition, int depIndex, int from) {
        if (stacks.getEventSequence().contains(transition.head()))
            return;
        BackTrackSet<E> toAdd = stacks.get(depIndex);  // add transition before depIndex
        Transition<E> sTransition = transition.subTransition(from);
      //  if (!toAdd.getBacktrackSet().contains(transition) && !toAdd.getBacktracked().contains(transition)) {
        if (!toAdd.isBackedTracked(sTransition) && !toAdd.isInBackTrackSet(sTransition)) {
                toAdd.getBacktrackSet().add(sTransition);
        }
    }

    public static void main(String[] args) {
        TestGraph testGraph = new TestGraph();
        DPOR<String> dpor = new DPOR<String>(testGraph.efg, testGraph.ddg, testGraph.modules);
        dpor.generate(dpor.stacks, 0);
        //    DPOR<Module> dpor = new DPOR<>();

    }

    public static void test1(String[] args) {
        List<Queue<Integer>> list = new ArrayList<>();

        Queue<Integer> q1 = new LinkedList<>();
        q1.add(1);
        q1.add(2);
        list.add(q1);

        Queue<Integer> q2 = new LinkedList<>();
        q2.add(3);
        q2.add(4);
        list.add(q2);

        dfs(list);

        for (Queue<Integer> q : list) {
            while (!q.isEmpty()) {
                Integer i = q.remove();
                Olog.log.info(i.toString());
            }
        }


    }

    public static void dfs(List<Queue<Integer>> list) {
        if (list.isEmpty()) return;
        Queue<Integer> head = list.get(0);
        while (!head.isEmpty()) {
            Integer i = head.remove();
            Olog.log.info(i.toString());
            dfs(list.subList(1,list.size()));
        }
    }

    public void generateStartSequences() {
        Set<Set<E>> disconnectedSubGraphs = ddg.getDisconnectedSubGraphs();
        for (Set<E> subgraph : disconnectedSubGraphs) {
            List<E> sp = findShortestPathFromInitialTo(subgraph);
            startSequences.add(sp);
        }
    }

    private List<E> findShortestPathFromInitialTo(Set<E> subgraph) {
        int len = Integer.MAX_VALUE;
        List<E> ans = null;
        for (E e : efg.getInitials()) {
            List<E> path = efg.findShortestPath(e, subgraph);
            if (path.size() < len) {
                len = path.size();
                ans = path;
            }
        }
        return ans;
    }

    private int countDataAndLoginEventsInTransition(List<E> events) {
        int ret = 0;
        for (E e : events) {
            if (!efg.isControlEvent(e)) {
                ret ++;
            }
        }
        return ret;
    }

    // every time remove one from queue, store in a list!!
    // or I can remove after return, when backtrack
    // how to pass arg?
    public void generate2(List<E> seq) {
        if (lq.size() >= 3) {
            Olog.log.info(lq.get(lq.size()-1).peek().toString());
            return;
        }
        Queue<E> currentQueue = null;
        try {
            currentQueue = lq.get(lq.size() - 1);
        } catch (Exception ex) {}

        while (!currentQueue.isEmpty()) {
            E e = currentQueue.peek();
            seq.add(e);
            List<E> currentCandidates = efg.availableAfter(e);
            for (E currendCandidate : currentCandidates) {
                for (int i = 0; i < lq.size(); i++) {
                    Queue<E> backtrack = lq.get(i);
                    E oldi = backtrack.peek();
                    if (ddg.hasDependency(currendCandidate, oldi)) {
                        backtrack.offer(currendCandidate);
                    }
                }
            }
            Olog.log.info(e.toString());
            Queue<E> next = new LinkedList<E>();
            List<E> nextCandidates = efg.availableAfter(e);
            if (efg.availableAfter(e).isEmpty()) {
                return;
            }
            //E nextEvent = StaticFunc.randFromList(efg.availableAfter(e));
            E nextEvent = null;
            for (E ei : efg.availableAfter(e)) {
                if (!seq.contains(ei) ) {
                    nextEvent = ei;
                }
            }

            if (nextEvent == null) return;

            next.add(nextEvent);
            lq.add(next);
            generate2(seq);
            currentQueue.remove();
            Olog.log.info("==========");
        }
    }
}

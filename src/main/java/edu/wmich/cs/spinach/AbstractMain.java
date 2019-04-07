package edu.wmich.cs.spinach;

import edu.wmich.cs.aut.arlt12.EFGClass;
import edu.wmich.cs.carot.util.Olog;
import edu.wmich.cs.radish.sequence.IDDG;
import edu.wmich.cs.radish.sequence.IEFG;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Lin Cheng on 12/22/16.
 */
public abstract class AbstractMain<E> {
    IEFG<E> efg;
    IDDG<E> ddg;
    IBacktrackUpdator backtrackUpdator;
    int DFS_MAX_LENGTH = 1;
    private IEventSequenceHandler eventSequenceHandler;

    public AbstractMain() {}

    public State getInitState() {
        State initState = new State(null);
        List<IEvent> events = new LinkedList<>();
        for (E e : efg.getInitials()) {
            events.add(new SimpleEvent<E>(e, efg, ddg) );//hard coded SimpleEvent type
        }
        initState.setEnabled(events);
        initState.addBacktrack(events.get(0));
        return initState;
    }

    public void  run() {
        if (DFS_MAX_LENGTH <= 1) {
            Olog.log.info("Please set DFS_MAX_LENGTH to be greater than 1");
            return;
        }
        DFSSearch dfs = new DFSSearch();
        dfs.setEventSequenceHandler(eventSequenceHandler);
        dfs.setBacktrackUpdator(backtrackUpdator);
        dfs.setInitState(getInitState());
        DFSSearch.MAX_LENGTH = DFS_MAX_LENGTH;
        dfs.run();
    }

    public IEFG<E> getEfg() {
        return efg;
    }

    public void setEfg(IEFG<E> efg) {
        this.efg = efg;
    }

    public IDDG<E> getDdg() {
        return ddg;
    }

    public void setDdg(IDDG<E> ddg) {
        this.ddg = ddg;
    }

    public IBacktrackUpdator getBacktrackUpdator() {
        return backtrackUpdator;
    }

    public void setBacktrackUpdator(IBacktrackUpdator backtrackUpdator) {
        this.backtrackUpdator = backtrackUpdator;
    }

    public int getDFS_MAX_LENGTH() {
        return DFS_MAX_LENGTH;
    }

    public void setDFS_MAX_LENGTH(int DFS_MAX_LENGTH) {
        this.DFS_MAX_LENGTH = DFS_MAX_LENGTH;
    }

    public void setEventSequenceHandler(IEventSequenceHandler eventSequenceHandler) {
        this.eventSequenceHandler = eventSequenceHandler;
    }
}

package edu.wmich.cs.spinach;


/**
 * Created by Lin Cheng on 12/22/16.
 */
public class DPORBacktrackUpdator  implements IBacktrackUpdator {

    @Override
    public void updateBacktrackInfo(State state, IEvent event) {
        State conflictState, backtrackState, backtrackPlaceState;
        IEvent conflictEvent, backtrackEvent;

        //find conflict state
        conflictState = state;
        do {
            if (conflictState.isInit())
                return;  // no conflict states
            conflictEvent = conflictState.getFrom();
            if (event.conflictsWith(conflictEvent))
                break;
            conflictState = conflictState.getPrev();
        } while (true);

        //add backtrack points just before conflict state
        boolean added = false; // true for successfully added the backtrack point, false otherwise
        backtrackPlaceState = conflictState.getPrev();
        if (backtrackPlaceState.getEnabled().contains(event)) {
            backtrackPlaceState.addBacktrack(event);
            added = true;
        } else {
            backtrackState = state;
            do {
                if (backtrackState == conflictState)  // only after conflict state
                    break;
                backtrackEvent = backtrackState.getFrom();
                if (backtrackPlaceState.getEnabled().contains(backtrackEvent)) {
                    backtrackPlaceState.addBacktrack(backtrackEvent);
                    added = true;
                    break;
                }
                backtrackState = backtrackState.getPrev();
            } while (true);
        }

        if (!added) {
            backtrackPlaceState.addBacktrack(backtrackPlaceState.getEnabled());
        }
    }

    @Override
    public void beforeNextState(State state) {
        state.addBacktrack(state.getEnabled().get(0));
    }
}

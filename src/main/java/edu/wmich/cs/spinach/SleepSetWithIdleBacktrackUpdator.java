package edu.wmich.cs.spinach;

/**
 * Created by Lin Cheng on 12/23/16.
 */
public class SleepSetWithIdleBacktrackUpdator implements IBacktrackUpdator {

    public void atCheckingPoint(State state) {
        state.addSleep(state.getBacktracked());
    }

    @Override
    public void updateBacktrackInfo(State state, IEvent event) {
        if (!state.getSleep().contains(event))
            state.addBacktrack(event);
    }

    public void beforeNextState(State state) {
      //  state.addBacktrack(state.getEnabled().get(0));
    }

    public void afterNextState(State newState, IEvent event) {
        State preState = newState.getPrev();
       // preState.addSleep(event);
        for (IEvent preSleepEvent : preState.getSleep()) {
            if (!preSleepEvent.conflictsWith(event)) {
                newState.addSleep(preSleepEvent);
            }
        }
        if (event.idle()) {
            newState.addSleep(event);
        }

    }
}

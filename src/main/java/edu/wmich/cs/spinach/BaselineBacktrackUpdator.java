package edu.wmich.cs.spinach;

/**
 * Created by Lin Cheng on 12/22/16.
 */
public class BaselineBacktrackUpdator implements IBacktrackUpdator{
    @Override
    public void updateBacktrackInfo(State state, IEvent event) {
        state.addBacktrack(event);
    }
}

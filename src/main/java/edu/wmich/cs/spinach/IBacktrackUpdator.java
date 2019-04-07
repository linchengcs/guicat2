package edu.wmich.cs.spinach;

/**
 * Created by Lin Cheng on 12/22/16.
 */
public interface IBacktrackUpdator {
    default void updateBacktrackInfo(State state, IEvent event){}

    /*
     * when DFSSearch goes back to a backtrack checking point, this function is called
     * usually used to set init status for a new (sub) sequence
     */
    default void atCheckingPoint(State state) {}

    /* while advancing the sequence, before going to a new state
     * dpor can use this to select next one event
     */
    default void beforeNextState(State state) {}


    /* while advancing the sequence, after going to a new state
     * sleep set can use this to update sleep set
     */
    default void afterNextState(State newState, IEvent event) {}

    default boolean isRedundant(State state) {return false;}

    default String getMsg() {return "";}

}

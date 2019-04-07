package edu.wmich.cs.spinach;

import edu.wmich.cs.carot.util.StaticFunc;

/**
 * Created by Lin Cheng on 1/4/17.
 */
public class SleepSetWithReductionUpdator extends SleepSetBacktrackUpdator {
    private String redundancyMsg = "";

    @Override
    public boolean isRedundant(State state) {
        if (state.isInit())
            return false;
        if (isIdemPotent(state)) {
            redundancyMsg = "IdemPotent Redundancy";
            return true;
        }
        if (isCoverWrite(state)) {
            redundancyMsg = "CoverWrite Redundancy";
            return true;
        }
        if (isGeneralizedCoverWrite(state)) {
            redundancyMsg = "GeneralizedCoverWrite Redundancy";
            return true;
        }
        if (isIrrelevantTail(state)) {
            redundancyMsg = "IrrelevantTail Redundancy";
            return true;
        }
        if (isExtraRoot(state)) {
            redundancyMsg = "ExtraRoot Redundancy";
            return true;
        }
        return false;
    }

    private boolean isIdemPotent(State state) {
        IEvent secondEvent = state.getFrom();
        State prevState = state.getPrev();
        if (prevState.isInit())
            return false;
        IEvent firstEvent = prevState.getFrom();
        if (firstEvent.equals(secondEvent)
                && noReadWrite(secondEvent, secondEvent) )
            return true;
        return false;
    }

    private boolean isCoverWrite(State state) {
        IEvent secondEvent = state.getFrom();
        State prevState = state.getPrev();
        if (prevState.isInit())
            return false;
        IEvent firstEvent = prevState.getFrom();
        if (noWriteRead(firstEvent, secondEvent) && coverPrev(firstEvent, secondEvent) )
            return true;
        return false;
    }

    private boolean isGeneralizedCoverWrite(State state) {
        IEvent lastEvent = state.getFrom();
        State prevState = state.getPrev();
        while (!prevState.isInit()) {
            IEvent firstEvent = prevState.getFrom();

            State tmp = state;
            boolean dep = false;
            while (tmp != prevState) {
                if (!noReadWrite(tmp.getFrom(), firstEvent))   {
                    dep = true;
                    break;
                }
                tmp = tmp.getPrev();
            }
            if (!dep &&  coverPrev(firstEvent, lastEvent) )
                return true;
            prevState = prevState.getPrev();
        }

        return false;
    }

    private boolean isIrrelevantTail(State state) {
        if (state.index() < DFSSearch.MAX_LENGTH - 1 )  // not the last state
            return false;

        //first event is never irrelevant
        if (state.index() == 1)
            return false;

        IEvent lastEvent = state.getFrom();
        State prevState = state.getPrev();
        while (!prevState.isInit()) {
            IEvent prevEvent = prevState.getFrom();
            if (!noWriteRead(prevEvent, lastEvent))
                return false;
            prevState = prevState.getPrev();
        }

        return true;
    }

    private boolean isExtraRoot(State state) {
        if (state.index() < DFSSearch.MAX_LENGTH - 1 )
            return false;

        int count = 0;
        State prevState = state;
        while (!prevState.isInit()) {
            if ( isRoot(state, prevState) )
                count++;
            if (count > 1)
                return true;
            prevState = prevState.getPrev();
        }
        return false;
    }

    private boolean isRoot(State state, State root) {
        IEvent lastEvent = state.getFrom();

        // handle root is the last state, if last event write set is empty, return false, else return true by default
        if (lastEvent.write().isEmpty())
            return false;

        IEvent rootEvent = root.getFrom();
        State prevState = state;
        while (prevState != root) {
            IEvent tmpEvent = prevState.getFrom();
            if (!noWriteRead(rootEvent, tmpEvent))
                return false;
            prevState = prevState.getPrev();
        }
        return true;
    }

    public String getMsg() {
        return redundancyMsg;
    }

    private boolean noReadWrite(IEvent first, IEvent last) {
     //  return StaticFunc.intersection(first.read(), last.write()).isEmpty();
      return !StaticFunc.checkExistsCommonField(first.read(), last.write());
    }

    private boolean noWriteRead(IEvent first, IEvent last) {
        return noReadWrite(last, first);
    }

    private boolean coverPrev(IEvent prev, IEvent cur) {
        return  !prev.write().isEmpty() && cur.write().containsAll(prev.write());
    }

}

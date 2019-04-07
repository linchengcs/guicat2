package edu.wmich.cs.spinach;

import edu.wmich.cs.carot.util.StaticFunc;

/**
 * Created by Lin Cheng on 1/4/17.
 */
public class SleepSetWithCoverWriteUpdator extends SleepSetBacktrackUpdator {
    private String redundancyMsg = "";

    @Override
    public boolean isRedundant(State state) {
        if (state.isInit())
            return false;
        if (isCoverWrite(state)) {
            redundancyMsg = "CoverWrite Redundancy";
            return true;
        }
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

package edu.wmich.cs.spinach;


import edu.wmich.cs.carot.util.Olog;

public class DFSSearch {
    StateStack stateStack;
    int counter = 0;
    public static int MAX_LENGTH = 1;
    private State initState;

    private IBacktrackUpdator backtrackUpdator;  //default is baseline;
    private IEventSequenceHandler eventSequenceHandler;

    public DFSSearch() {
        this.stateStack = new StateStack();
        this.backtrackUpdator = new BaselineBacktrackUpdator();
        this.eventSequenceHandler = new LogEventSequenceHandler();
    }

    public void run() {

        stateStack.push(initState);
        while (stateStack.hasBacktrackPoints()) {
            counter++;
            backtrackChecking();
        }

        Olog.log.info("total valid test cases: " + counter + "\n" + counter);  // for shell extract data
    }

    public void backtrackChecking() {
        //go to backtrack point
        State state = stateStack.top();
        while (state.allBacktracked()) {
            stateStack.pop();
            state = stateStack.top();
        }

        backtrackUpdator.atCheckingPoint(state);

        //advance to MAX_LENGTH
        while (true) {
            if (stateStack.size() >= MAX_LENGTH ) {
                eventSequenceHandler.handle(stateStack.getEvents());
                break;
            }

            for (IEvent event : state.getEnabled()) {
                backtrackUpdator.updateBacktrackInfo(state, event);
            }

            backtrackUpdator.beforeNextState(state);
            IEvent event = state.getEvent();
            if (event == null) {
                String ts = stateStack.output();
                Olog.log.info(ts +    "-- stop, no more backtrack points");
                counter--;
                break;
            }

            state = state.next(event);
            // reduct the redundancy
            if (backtrackUpdator.isRedundant(state)) {
                String ts = stateStack.output();
                Olog.log.info(ts +  event.toString() +  " --" + backtrackUpdator.getMsg());
                counter--;
                break;
            }

            backtrackUpdator.afterNextState(state, event);
            stateStack.push(state);
        }
    }


    public void setBacktrackUpdator(IBacktrackUpdator backtrackUpdator) {
        this.backtrackUpdator = backtrackUpdator;
    }

    public void setInitState(State initState) {
        this.initState = initState;
    }

    public void setEventSequenceHandler(IEventSequenceHandler eventSequenceHandler) {
        this.eventSequenceHandler = eventSequenceHandler;
    }
}

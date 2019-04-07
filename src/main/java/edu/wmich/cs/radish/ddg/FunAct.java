package edu.wmich.cs.radish.ddg;

/**
 * Created by oliver on 30/09/16.
 */
public class FunAct {
    private String fun;
    private  int action;

    public static final int ENTER = 1;
    public static final int EXIT = -1;


    public FunAct(String fun, int action) {
        this.fun = fun;
        this.action = action;
    }

    @Override
    public String toString() {

        return fun + " " + action;
    }

}


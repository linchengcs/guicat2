package edu.wmich.cs.carot;

interface ICarot {
//    DiGraph<CTEvent> ddg = null;
//    EFG efg = null;

    //after leaves are done
    void populateDDG();

    void addLeaves();

    void groupLeaves();


     void generateTestcases(int len, boolean cyclic, boolean includeShort);
}

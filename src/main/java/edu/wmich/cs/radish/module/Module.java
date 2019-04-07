package edu.wmich.cs.radish.module;



import java.util.ArrayList;
import java.util.List;

import edu.umd.cs.guitar.testcase.plugin.ct.entity.event.CTEvent;

/**
 * Created by rick on 10/6/16.
 */
public class Module {
    List<CTEvent> elements;

    public Module() {
        this.elements = new ArrayList<>();
    }


    public List<CTEvent> getElements() {
        return elements;
    }

    public void setElements(List<CTEvent> elements) {
        this.elements = elements;
    }

    @Override
    public boolean equals(Object o) {
        boolean ret = false;
        Module om = (Module) o;

        if (this.size() != om.size())
            return false;

        for (int i = 0; i < this.size(); i++)
            if (!this.get(i).getIdentifier().equals(om.get(i).getIdentifier()))
                return false;
        return true;
    }

    @Override
    public int hashCode() {
        String ids = "";
        for (CTEvent event : elements) {
            ids += event.getIdentifier();
        }
        return ids.hashCode();
    }

    public int size() {
        return this.elements.size();
    }

    public CTEvent get(int i) {
        return this.elements.get(i);
    }

    @Override
    public String toString() {
        return this.elements.toString();
    }

}

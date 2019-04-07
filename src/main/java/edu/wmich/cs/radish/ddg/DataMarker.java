package edu.wmich.cs.radish.ddg;


/**
 * Created by oliver on 17/09/16.
 */
public class DataMarker  {

    private String desc;
    private String owner;
    private String name;

    public DataMarker() {
        desc = "";
        owner = "";
        name = "";
    }

    public DataMarker(String desc, String owner, String name) {
        this.desc = desc;
        this.owner = owner;
        this.name = name;
    }

    public DataMarker(String ownerDnameCdesc) {
        int start =0, end = 0;
        for (int i = 0; i < ownerDnameCdesc.length(); i++) {
            if (ownerDnameCdesc.charAt(i) == '.') {
                end = i;
                this.owner = ownerDnameCdesc.substring(start, end);
                start = end =  i + 1;
            }
            if (ownerDnameCdesc.charAt(i) == ':') {
                end = i;
                this.name = ownerDnameCdesc.substring(start, end);
                start = i + 1;
                end = ownerDnameCdesc.length();
                this.desc = ownerDnameCdesc.substring(start, end);
            }
        }
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object dataMarker) {
//        assert dataMarker != null;
//        assert desc != null;
//        assert owner != null;
//        assert name != null;
        if (dataMarker == null || desc == null || owner == null || name == null)
            return false;
        DataMarker dm = (DataMarker) dataMarker;
        return desc.equals(dm.getDesc()) && owner.equals(dm.getOwner()) && name.equals(dm.getName());
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public String toString() {
        return owner + "." + name + ":" + desc;
    }
}

package com.videumcorp.desarrolladorandroid.navigationdrawerandroiddesignsupportlibrarywithfragments.Lib;

import com.videumcorp.desarrolladorandroid.navigationdrawerandroiddesignsupportlibrarywithfragments.Lib.ChildRow;

import java.util.ArrayList;

/**
 * Created by marangelo.php on 13/06/2016.
 */
public class ParentRow {
    private String name;
    private ArrayList<ChildRow> childList;

    public ParentRow(String name, ArrayList<ChildRow> childList) {
        this.name = name;
        this.childList = childList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<ChildRow> getChildList() {
        return childList;
    }

    public void setChildList(ArrayList<ChildRow> childList) {
        this.childList = childList;
    }
}

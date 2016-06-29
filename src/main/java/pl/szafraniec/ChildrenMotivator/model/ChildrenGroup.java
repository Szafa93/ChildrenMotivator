/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.szafraniec.ChildrenMotivator.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Maciek
 */
public class ChildrenGroup {
    private int id;
    private String name;
    private List<Children> listOfChildren = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Children> getListOfChildren() {
        return listOfChildren;
    }

    public void setListOfChildren(List<Children> listOfChildren) {
        this.listOfChildren = listOfChildren;
    }

}

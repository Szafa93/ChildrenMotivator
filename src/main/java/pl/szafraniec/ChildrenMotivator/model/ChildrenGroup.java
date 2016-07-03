/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.szafraniec.ChildrenMotivator.model;

import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Maciek
 */
@Entity
public class ChildrenGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank
    private String name;

    @OneToMany(mappedBy = "childrenGroup")
    private List<Child> children = new ArrayList<>();

    @OneToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "ChildrenGroup_id", referencedColumnName = "id")
    private BehaviorTable behaviorTable;

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

    public List<Child> getChildren() {
        return children;
    }

    public void setChildren(List<Child> children) {
        this.children = children;
    }

    public BehaviorTable getBehaviorTable() {
        return behaviorTable;
    }

    public void setBehaviorTable(BehaviorTable behaviorTable) {
        this.behaviorTable = behaviorTable;
    }
}

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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import java.util.ArrayList;
import java.util.Collections;
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

    @OneToMany(mappedBy = "childrenGroup", cascade = CascadeType.ALL)
    private List<Child> children;

    @OneToOne(optional = false, cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
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
        Collections.sort(children, (o1, o2) -> {
            if (o1.getSurname().compareTo(o2.getSurname()) == 0) {
                return o1.getName().compareTo(o2.getName());
            }
            return o1.getSurname().compareTo(o2.getSurname());
        });
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

    public static class ChildrenGroupFactory {
        public static ChildrenGroup create(String name) {
            ChildrenGroup childrenGroup = new ChildrenGroup();
            childrenGroup.name = name;
            childrenGroup.behaviorTable = BehaviorTable.BehaviorTableFactory.create(childrenGroup);
            childrenGroup.children = new ArrayList<>();
            return childrenGroup;
        }
    }
}

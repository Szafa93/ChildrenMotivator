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
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;

/**
 * @author Maciek
 */
@Entity
public class Child {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank
    private String name;

    @NotBlank
    private String surname;

    @NotBlank
    private String pesel;

    private String parentEmail;

    @OneToOne(optional = false, cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private ChildActivitiesTable childActivitiesTable;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
    private ChildrenGroup childrenGroup;

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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPesel() {
        return pesel;
    }

    public void setPesel(String pesel) {
        this.pesel = pesel;
    }

    public String getParentEmail() {
        return parentEmail;
    }

    public void setParentEmail(String parentEmail) {
        this.parentEmail = parentEmail;
    }

    public ChildActivitiesTable getChildActivitiesTable() {
        return childActivitiesTable;
    }

    public void setChildActivitiesTable(ChildActivitiesTable childActivitiesTable) {
        this.childActivitiesTable = childActivitiesTable;
    }

    public ChildrenGroup getChildrenGroup() {
        return childrenGroup;
    }

    public void setChildrenGroup(ChildrenGroup childrenGroup) {
        this.childrenGroup = childrenGroup;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Child child = (Child) o;

        return id == child.id;

    }

    @Override
    public int hashCode() {
        return id;
    }

}

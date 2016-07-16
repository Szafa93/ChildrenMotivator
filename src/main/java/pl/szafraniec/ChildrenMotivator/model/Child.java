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
import javax.persistence.OneToMany;
import java.util.List;

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

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "child")
    private List<ChildActivitiesTable> activitiesTableList;

    @ManyToOne(optional = false)
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

    public List<ChildActivitiesTable> getActivitiesTableList() {
        return activitiesTableList;
    }

    public void setActivitiesTableList(List<ChildActivitiesTable> activitiesTableList) {
        this.activitiesTableList = activitiesTableList;
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

    public static class ChildFactory {

        public static Child create(String name, String surname, String pesel, String parentMail, ChildrenGroup childrenGroup) {
            Child child = new Child();
            child.setName(name);
            child.setSurname(surname);
            child.setPesel(pesel);
            child.setParentEmail(parentMail);
            child.setChildrenGroup(childrenGroup);
            childrenGroup.getChildren().add(child);
            childrenGroup.getBehaviorTable().getDays().forEach(day -> {
                day.getGrades().put(child, TableCell.TableCellBuilder.create());
            });

            return child;
        }
    }
}

/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package pl.szafraniec.ChildrenMotivator.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.List;

/**
 * @author Maciek
 */
@Entity
public class ChildActivitiesTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(optional = false)
    private ActivitiesTableScheme activitiesTableScheme;

    @ManyToOne(optional = false)
    private Child child;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "ChildActivitiesTable_id", referencedColumnName = "id")
    private List<ChildActivitiesTableDay> days;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ActivitiesTableScheme getActivitiesTableScheme() {
        return activitiesTableScheme;
    }

    public void setActivitiesTableScheme(ActivitiesTableScheme activitiesTableScheme) {
        this.activitiesTableScheme = activitiesTableScheme;
    }

    public Child getChild() {
        return child;
    }

    public void setChild(Child child) {
        this.child = child;
    }

    public List<ChildActivitiesTableDay> getDays() {
        return days;
    }

    public void setDays(List<ChildActivitiesTableDay> days) {
        this.days = days;
    }
}

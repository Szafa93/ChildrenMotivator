/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.szafraniec.ChildrenMotivator.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Maciek
 */
@Entity
public class ActivitiesTableScheme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @ManyToMany
    @OrderBy("id")
    private List<Activity> listOfActivities = new ArrayList<>();

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

    public List<Activity> getListOfActivities() {
        return listOfActivities;
    }

    public void setListOfActivities(List<Activity> listOfActivities) {
        this.listOfActivities = listOfActivities;
    }

    public static class ActivitiesTableSchemeFactory {
        public static ActivitiesTableScheme create(String name, List<Activity> activities) {
            ActivitiesTableScheme scheme = new ActivitiesTableScheme();
            scheme.setName(name);
            scheme.setListOfActivities(activities);
            return scheme;
        }
    }
}

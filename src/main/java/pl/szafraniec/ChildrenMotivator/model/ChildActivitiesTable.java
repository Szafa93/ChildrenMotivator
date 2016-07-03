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

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "ChildActivitiesTable_id", referencedColumnName = "id")
    private List<ChildActivitiesTableDay> days;
}

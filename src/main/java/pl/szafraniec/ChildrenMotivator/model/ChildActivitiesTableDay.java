package pl.szafraniec.ChildrenMotivator.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.OneToMany;
import java.util.Date;
import java.util.Map;

@Entity
public class ChildActivitiesTableDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private Date date;

    @OneToMany   // unidirectional
    @JoinTable(name = "ChildActivitiesTableDay_Grades",
            joinColumns = @JoinColumn(name = "ChildActivitiesTableDay"),
            inverseJoinColumns = @JoinColumn(name = "TableCell"))
    @MapKeyJoinColumn(name = "Activity")
    private Map<Activity, TableCell> grades;
}

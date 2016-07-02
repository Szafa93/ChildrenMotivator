package pl.szafraniec.ChildrenMotivator.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import java.util.Date;
import java.util.Map;

@Entity
public class ChildActivitiesTableDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private Date date;

    @OneToMany(targetEntity = TableCell.class, fetch = FetchType.LAZY)
    @MapKey(name = "id")
    private Map<Activity, TableCell> grades;
}

package pl.szafraniec.ChildrenMotivator.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.OneToMany;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

@Entity
public class ChildActivitiesTableDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private Date date;

    // unidirectional
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "ChildActivitiesTableDay_Grades",
            joinColumns = @JoinColumn(name = "ChildActivitiesTableDay"),
            inverseJoinColumns = @JoinColumn(name = "TableCell"))
    @MapKeyJoinColumn(name = "Activity")
    private Map<Activity, TableCell> grades;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Map<Activity, TableCell> getGrades() {
        return grades;
    }

    public void setGrades(Map<Activity, TableCell> grades) {
        this.grades = grades;
    }

    public LocalDate getLocalDate() {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

}

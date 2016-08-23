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
public class BehaviorTableDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private Date date;

    // unidirectional
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "BehaviorTableDay_Grades",
            joinColumns = @JoinColumn(name = "BehaviorTableDay"),
            inverseJoinColumns = @JoinColumn(name = "TableCell"))
    @MapKeyJoinColumn(name = "Child")
    private Map<Child, TableCell> grades;

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

    public Map<Child, TableCell> getGrades() {
        return grades;
    }

    public void setGrades(Map<Child, TableCell> grades) {
        this.grades = grades;
    }

    public LocalDate getLocalDate() {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

}

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
public class BehaviorTableDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private Date date;

    @OneToMany   // unidirectional
    @JoinTable(name = "BehaviorTableDay_Grades",
            joinColumns = @JoinColumn(name = "BehaviorTableDay"),
            inverseJoinColumns = @JoinColumn(name = "TableCell"))
    @MapKeyJoinColumn(name = "Child")
    private Map<Child, TableCell> grades;
}

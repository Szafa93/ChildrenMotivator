/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package pl.szafraniec.ChildrenMotivator.model;

import pl.szafraniec.ChildrenMotivator.model.factories.ChildActivitiesTableDayFactory;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Maciek
 */
@Entity
public class ChildActivitiesTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(optional = true)
    private ActivitiesTableScheme activitiesTableScheme;

    @OneToOne(mappedBy = "childActivitiesTable")
    @PrimaryKeyJoinColumn
    private Child child;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "ChildActivitiesTable_id", referencedColumnName = "id")
    private List<ChildActivitiesTableDay> days;

    @ManyToOne
    private BackgroundImage backgroundImage;

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

    public BackgroundImage getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(BackgroundImage backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public Optional<List<ChildActivitiesTableDay>> getDays(LocalDate startDate, LocalDate endDate) {
        long daysAmount = Stream.iterate(startDate, date -> date.plusDays(1))
                .limit(ChronoUnit.DAYS.between(startDate, endDate) + 1)
                .filter(date -> !date.getDayOfWeek().equals(DayOfWeek.SUNDAY) && !date.getDayOfWeek().equals(DayOfWeek.SATURDAY))
                .count();
        return Optional.of(getDays().stream().filter(day -> (day.getLocalDate().isAfter(startDate) && day.getLocalDate().isBefore(endDate))
                || day.getLocalDate().isEqual(startDate) || day.getLocalDate().isEqual(endDate)).collect(Collectors.toList())).filter(
                list -> list.size() == daysAmount);
    }

    public ChildActivitiesTable generateDay(LocalDate startDate, LocalDate endDate) {
        long daysAmount = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        days.addAll(Stream.iterate(startDate, date -> date.plusDays(1))
                .limit(daysAmount)
                .filter(date -> !date.getDayOfWeek().equals(DayOfWeek.SUNDAY) && !date.getDayOfWeek().equals(DayOfWeek.SATURDAY))
                .filter(date -> !days.stream().map(ChildActivitiesTableDay::getLocalDate).anyMatch(localDate -> localDate.isEqual(date)))
                .map(date -> ChildActivitiesTableDayFactory.create(date, activitiesTableScheme.getListOfActivities()))
                .collect(Collectors.toList()));
        return this;
    }

}

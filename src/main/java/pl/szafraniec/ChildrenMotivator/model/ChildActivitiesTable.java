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
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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

    public Optional<List<ChildActivitiesTableDay>> getDays(LocalDate startDate, LocalDate endDate) {
        return Optional.of(getDays().stream().filter(day -> (day.getLocalDate().isAfter(startDate) && day.getLocalDate().isBefore(endDate))
                || day.getLocalDate().isEqual(startDate) || day.getLocalDate().isEqual(endDate)).collect(Collectors.toList())).filter(
                list -> !list.isEmpty());
    }

    public ChildActivitiesTable generateDay(LocalDate startDate, LocalDate endDate) {
        Optional<LocalDate> last = days.stream().map(ChildActivitiesTableDay::getLocalDate).max(LocalDate::compareTo);
        Optional<LocalDate> first = days.stream().map(ChildActivitiesTableDay::getLocalDate).min(LocalDate::compareTo);
        LocalDate lastDate;
        LocalDate firstDate;
        if (endDate.isBefore(first.orElse(endDate))) {
            firstDate = startDate;
            lastDate = first.map(date -> date.minusDays(1)).orElse(endDate);
        } else {
            firstDate = last.map(date -> date.plusDays(1)).orElse(startDate);
            lastDate = endDate;
        }

        long daysAmount = ChronoUnit.DAYS.between(firstDate, lastDate) + 1;
        days.addAll(Stream.iterate(firstDate, date -> date.plusDays(1)).limit(daysAmount).filter(
                date -> !date.getDayOfWeek().equals(java.time.DayOfWeek.SUNDAY) && !date.getDayOfWeek().equals(
                        java.time.DayOfWeek.SATURDAY)).map(date -> ChildActivitiesTableDay.ChildActivitiesTableDayBuilder.create(date,
                activitiesTableScheme.getListOfActivities())).collect(Collectors.toList()));
        return this;
    }

    public static class ChildActivitiesTableFactory {
        public static ChildActivitiesTable create(Child child, ActivitiesTableScheme activitiesTableScheme) {
            ChildActivitiesTable table = new ChildActivitiesTable();
            table.child = child;
            table.activitiesTableScheme = activitiesTableScheme;
            table.days = new ArrayList<>();
            child.getActivitiesTableList().add(table);
            return table;
        }
    }
}

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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Maciek
 */
@Entity
public class BehaviorTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "BehaviorTable_id", referencedColumnName = "id")
    private List<BehaviorTableDay> days;

    @OneToOne(mappedBy = "behaviorTable")
    @PrimaryKeyJoinColumn
    private ChildrenGroup childrenGroup;

    public List<BehaviorTableDay> getDays() {
        Collections.sort(days, (o1, o2) -> o1.getLocalDate().compareTo(o2.getLocalDate()));
        return days;
    }

    public void setDays(List<BehaviorTableDay> days) {
        this.days = days;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Optional<List<BehaviorTableDay>> getDays(LocalDate startDate, LocalDate endDate) {
        return Optional.of(getDays().stream().filter(day -> (day.getLocalDate().isAfter(startDate) && day.getLocalDate().isBefore(endDate))
                || day.getLocalDate().isEqual(startDate) || day.getLocalDate().isEqual(endDate)).collect(Collectors.toList())).filter(
                list -> !list.isEmpty());
    }

    public BehaviorTable generateDay(LocalDate startDate, LocalDate endDate) {
        Optional<LocalDate> last = days.stream().map(BehaviorTableDay::getLocalDate).max(LocalDate::compareTo);
        Optional<LocalDate> first = days.stream().map(BehaviorTableDay::getLocalDate).min(LocalDate::compareTo);
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
        days.addAll(Stream.iterate(firstDate, date -> date.plusDays(1))
                .limit(daysAmount)
                .filter(date -> !date.getDayOfWeek().equals(DayOfWeek.SUNDAY) && !date.getDayOfWeek().equals(DayOfWeek.SATURDAY))
                .map(date -> BehaviorTableDay.BehaviorTableDayBuilder.create(date, childrenGroup.getChildren()))
                .collect(Collectors.toList()));
        return this;
    }

    public static class BehaviorTableFactory {
        public static BehaviorTable create(ChildrenGroup childrenGroup) {
            BehaviorTable table = new BehaviorTable();
            table.childrenGroup = childrenGroup;
            table.setDays(new ArrayList<>());
            return table;
        }
    }
}

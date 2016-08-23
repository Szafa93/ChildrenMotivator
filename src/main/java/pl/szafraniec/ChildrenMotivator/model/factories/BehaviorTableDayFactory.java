package pl.szafraniec.ChildrenMotivator.model.factories;

import pl.szafraniec.ChildrenMotivator.model.BehaviorTableDay;
import pl.szafraniec.ChildrenMotivator.model.Child;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class BehaviorTableDayFactory {
    public static BehaviorTableDay create(LocalDate localDate, List<Child> children) {
        BehaviorTableDay day = new BehaviorTableDay();
        day.setDate(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        day.setGrades(children.stream().collect(Collectors.toMap(child -> child, child -> TableCellFactory.create())));
        return day;
    }
}

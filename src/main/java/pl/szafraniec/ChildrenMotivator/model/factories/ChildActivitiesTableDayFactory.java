package pl.szafraniec.ChildrenMotivator.model.factories;

import pl.szafraniec.ChildrenMotivator.model.Activity;
import pl.szafraniec.ChildrenMotivator.model.ChildActivitiesTableDay;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ChildActivitiesTableDayFactory {
    public static ChildActivitiesTableDay create(LocalDate localDate, List<Activity> activities) {
        ChildActivitiesTableDay day = new ChildActivitiesTableDay();
        day.setDate(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        day.setGrades(
                activities.stream().collect(Collectors.toMap(activity -> activity, activity -> TableCellFactory.create())));
        return day;
    }
}

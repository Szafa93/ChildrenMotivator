package pl.szafraniec.ChildrenMotivator.model.factories;

import pl.szafraniec.ChildrenMotivator.model.Child;
import pl.szafraniec.ChildrenMotivator.model.ChildActivitiesTable;

import java.util.ArrayList;

public class ChildActivitiesTableFactory {
    public static ChildActivitiesTable create(Child child) {
        ChildActivitiesTable table = new ChildActivitiesTable();
        table.setChild(child);
        table.setDays(new ArrayList<>());
        return table;
    }
}

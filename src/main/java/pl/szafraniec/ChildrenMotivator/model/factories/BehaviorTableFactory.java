package pl.szafraniec.ChildrenMotivator.model.factories;

import pl.szafraniec.ChildrenMotivator.model.BehaviorTable;
import pl.szafraniec.ChildrenMotivator.model.ChildrenGroup;

import java.util.ArrayList;

public class BehaviorTableFactory {
    public static BehaviorTable create(ChildrenGroup childrenGroup) {
        BehaviorTable table = new BehaviorTable();
        table.setChildrenGroup(childrenGroup);
        table.setDays(new ArrayList<>());
        return table;
    }
}

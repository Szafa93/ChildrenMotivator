package pl.szafraniec.ChildrenMotivator.model.factories;

import pl.szafraniec.ChildrenMotivator.model.ChildrenGroup;

import java.util.ArrayList;

public class ChildrenGroupFactory {
    public static ChildrenGroup create(String name) {
        ChildrenGroup childrenGroup = new ChildrenGroup();
        childrenGroup.setName(name);
        childrenGroup.setBehaviorTable(BehaviorTableFactory.create(childrenGroup));
        childrenGroup.setChildren(new ArrayList<>());
        return childrenGroup;
    }
}

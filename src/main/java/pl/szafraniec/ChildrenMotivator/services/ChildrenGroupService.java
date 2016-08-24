package pl.szafraniec.ChildrenMotivator.services;

import pl.szafraniec.ChildrenMotivator.model.BackgroundImage;
import pl.szafraniec.ChildrenMotivator.model.BehaviorTableDay;
import pl.szafraniec.ChildrenMotivator.model.Child;
import pl.szafraniec.ChildrenMotivator.model.ChildrenGroup;
import pl.szafraniec.ChildrenMotivator.model.Holder;

import java.time.LocalDate;
import java.util.List;

public interface ChildrenGroupService {
    List<BehaviorTableDay> getDays(Holder<ChildrenGroup> holder, LocalDate from, LocalDate to);

    ChildrenGroup removeChildFromGroup(Child child);

    ChildrenGroup assignChildToGroup(Holder<Child> child, ChildrenGroup childrenGroup);

    Child changeGroup(Child child, ChildrenGroup childrenGroup);

    ChildrenGroup create(String name);

    List<ChildrenGroup> findAll();

    void removeGroup(ChildrenGroup childrenGroup);

    ChildrenGroup edit(ChildrenGroup childrenGroup, String groupName);

    ChildrenGroup recalculateGrades(Child child);

    ChildrenGroup setBackground(ChildrenGroup childrenGroup, BackgroundImage backgroundImage);
}

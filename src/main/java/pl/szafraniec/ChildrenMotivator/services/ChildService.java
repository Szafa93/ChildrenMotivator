package pl.szafraniec.ChildrenMotivator.services;

import pl.szafraniec.ChildrenMotivator.model.ActivitiesTableScheme;
import pl.szafraniec.ChildrenMotivator.model.Child;
import pl.szafraniec.ChildrenMotivator.model.ChildrenGroup;

import java.util.Optional;

public interface ChildService {
    Child recalculateGrades(Child child);

    Child findOne(int id);

    Child removeActivityTable(Child child);

    Child setActivityTable(Child child, ActivitiesTableScheme scheme);

    ChildrenGroup removeChild(Child child);

    Child editChild(Child child, String name, String surname, String pesel, String parentEmail);

    Child changeGroup(Child child, ChildrenGroup childrenGroup);

    Optional<Child> findByPesel(String pesel);

    Child create(String name, String surname, String pesel, String parentMail, ChildrenGroup childrenGroup);
}

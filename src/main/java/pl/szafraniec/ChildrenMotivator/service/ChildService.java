package pl.szafraniec.ChildrenMotivator.service;

import pl.szafraniec.ChildrenMotivator.model.Child;
import pl.szafraniec.ChildrenMotivator.model.ChildrenGroup;

import java.util.List;

public interface ChildService {
    Child create(String name, String surname, String pesel, ChildrenGroup childrenGroup);

    Child save(Child child);

    Child save(Child... child);

    Child changeGroup(Child child, ChildrenGroup toGroup);

    boolean delete(Child child);

    List<Child> findAll();

    // TODO add some finds
}

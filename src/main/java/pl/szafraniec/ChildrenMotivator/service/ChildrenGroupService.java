package pl.szafraniec.ChildrenMotivator.service;

import pl.szafraniec.ChildrenMotivator.model.ChildrenGroup;

import java.util.List;

public interface ChildrenGroupService {
    ChildrenGroup create(String name);

    ChildrenGroup save(ChildrenGroup childrenGroup);

    boolean save(ChildrenGroup... childrenGroup);

    boolean delete(ChildrenGroup childrenGroup);

    List<ChildrenGroup> findAll();

    // TODO add some finds
}

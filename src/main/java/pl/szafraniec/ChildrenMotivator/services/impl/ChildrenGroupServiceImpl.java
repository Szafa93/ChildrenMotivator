package pl.szafraniec.ChildrenMotivator.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.szafraniec.ChildrenMotivator.model.BehaviorTableDay;
import pl.szafraniec.ChildrenMotivator.model.Child;
import pl.szafraniec.ChildrenMotivator.model.ChildrenGroup;
import pl.szafraniec.ChildrenMotivator.model.Holder;
import pl.szafraniec.ChildrenMotivator.model.TableCell;
import pl.szafraniec.ChildrenMotivator.repository.ChildrenGroupRepository;
import pl.szafraniec.ChildrenMotivator.services.ChildrenGroupService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Component
public class ChildrenGroupServiceImpl implements ChildrenGroupService {

    @Autowired
    private ChildrenGroupRepository childrenGroupRepository;

    @Override
    public List<BehaviorTableDay> getDays(Holder<ChildrenGroup> holder, LocalDate from, LocalDate to) {
        return holder.get().getBehaviorTable().getDays(from, to).orElseGet(() -> {
            holder.get().getBehaviorTable().generateDay(from, to);
            holder.set(childrenGroupRepository.saveAndFlush(holder.get()));
            return holder.get().getBehaviorTable().getDays(from, to).get();
        });
    }

    @Override
    public ChildrenGroup removeDaysForChild(Child child) {
        child.getChildrenGroup()
                .getBehaviorTable()
                .getDays()
                .stream()
                .map(BehaviorTableDay::getGrades)
                .forEach(gradeMap -> gradeMap.remove(child));
        return child.getChildrenGroup();
    }

    @Override
    public ChildrenGroup generateDaysForChild(Child child) {
        child.getChildrenGroup()
                .getBehaviorTable()
                .getDays()
                .forEach(day -> day.getGrades().put(child, TableCell.TableCellBuilder.create()));
        return child.getChildrenGroup();
    }

    @Override
    public ChildrenGroup removeChildFromGroup(Child child) {
        removeDaysForChild(child);
        ChildrenGroup group = child.getChildrenGroup();
        group.getChildren().remove(child);
        child.setChildrenGroup(null);
        return childrenGroupRepository.save(group);
    }

    @Override
    public ChildrenGroup assignChildToGroup(Child child, ChildrenGroup childrenGroup) {
        child.setChildrenGroup(childrenGroup);
        childrenGroup.getChildren().add(child);
        generateDaysForChild(child);
        return childrenGroupRepository.save(childrenGroup);
    }

    @Override
    public ChildrenGroup create(String name) {
        ChildrenGroup childrenGroup = ChildrenGroup.ChildrenGroupFactory.create(name);
        return childrenGroupRepository.saveAndFlush(childrenGroup);
    }

    @Override
    public List<ChildrenGroup> findAll() {
        return Collections.unmodifiableList(childrenGroupRepository.findAll());
    }

    @Override
    public void removeGroup(ChildrenGroup childrenGroup) {
        childrenGroupRepository.delete(childrenGroup);
        childrenGroupRepository.flush();
    }

    @Override
    public ChildrenGroup edit(ChildrenGroup childrenGroup, String groupName) {
        childrenGroup.setName(groupName);
        return childrenGroupRepository.saveAndFlush(childrenGroup);
    }
}

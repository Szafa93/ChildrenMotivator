package pl.szafraniec.ChildrenMotivator.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.szafraniec.ChildrenMotivator.model.BackgroundImage;
import pl.szafraniec.ChildrenMotivator.model.BehaviorTableDay;
import pl.szafraniec.ChildrenMotivator.model.Child;
import pl.szafraniec.ChildrenMotivator.model.ChildActivitiesTable;
import pl.szafraniec.ChildrenMotivator.model.ChildActivitiesTableDay;
import pl.szafraniec.ChildrenMotivator.model.ChildrenGroup;
import pl.szafraniec.ChildrenMotivator.model.GradeScheme;
import pl.szafraniec.ChildrenMotivator.model.Holder;
import pl.szafraniec.ChildrenMotivator.model.TableCell;
import pl.szafraniec.ChildrenMotivator.model.factories.ChildrenGroupFactory;
import pl.szafraniec.ChildrenMotivator.model.factories.TableCellFactory;
import pl.szafraniec.ChildrenMotivator.repository.ChildrenGroupRepository;
import pl.szafraniec.ChildrenMotivator.services.ChildrenGroupService;
import pl.szafraniec.ChildrenMotivator.services.GradeSchemeService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

@Component
public class ChildrenGroupServiceImpl implements ChildrenGroupService {

    @Autowired
    private ChildrenGroupRepository childrenGroupRepository;

    @Autowired
    private GradeSchemeService gradeSchemeService;

    @Override
    public List<BehaviorTableDay> getDays(Holder<ChildrenGroup> holder, LocalDate from, LocalDate to) {
        return holder.get().getBehaviorTable().getDays(from, to).orElseGet(() -> {
            holder.get().getBehaviorTable().generateDay(from, to);
            holder.set(childrenGroupRepository.saveAndFlush(holder.get()));
            return holder.get().getBehaviorTable().getDays(from, to).get();
        });
    }

    private ChildrenGroup generateDaysForChild(Child child) {
        child.getChildrenGroup()
                .getBehaviorTable()
                .getDays()
                .forEach(day -> day.getGrades().put(child, TableCellFactory.create()));
        return child.getChildrenGroup();
    }

    @Override
    public ChildrenGroup removeChildFromGroup(Child child) {
        removeDaysForChild(child);
        ChildrenGroup group = child.getChildrenGroup();
        group.getChildren().remove(child);
        return childrenGroupRepository.save(group);
    }

    private ChildrenGroup removeDaysForChild(Child child) {
        child.getChildrenGroup()
                .getBehaviorTable()
                .getDays()
                .stream()
                .map(BehaviorTableDay::getGrades)
                .forEach(gradeMap -> gradeMap.remove(child));
        return child.getChildrenGroup();
    }

    @Override
    public ChildrenGroup assignChildToGroup(Child child, ChildrenGroup childrenGroup) {
        child.setChildrenGroup(childrenGroup);
        childrenGroup.getChildren().add(child);
        generateDaysForChild(child);
        ChildrenGroup group = childrenGroupRepository.save(childrenGroup);
        child.setChildrenGroup(group);
        return group;
    }

    @Override
    public ChildrenGroup create(String name) {
        ChildrenGroup childrenGroup = ChildrenGroupFactory.create(name);
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

    @Override
    public ChildrenGroup recalculateGrades(Child child) {
        ChildActivitiesTable table = child.getChildActivitiesTable();
        LocalDate start = table.getDays().stream()
                .map(ChildActivitiesTableDay::getLocalDate)
                .sorted()
                .findFirst()
                .get();
        LocalDate end = table.getDays().stream()
                .map(ChildActivitiesTableDay::getLocalDate)
                .sorted(Comparator.reverseOrder())
                .findFirst()
                .get();
        Map<LocalDate, TableCell> grades = child.getChildrenGroup()
                .getBehaviorTable()
                .getDays(start, end).orElseGet(() -> {
                    child.getChildrenGroup().getBehaviorTable().generateDay(start, end);
                    return child.getChildrenGroup().getBehaviorTable().getDays(start, end).get();
                })
                .stream()
                .collect(Collectors.toMap(BehaviorTableDay::getLocalDate, day -> day.getGrades().get(child)));
        Map<LocalDate, OptionalDouble> averageGrades = table.getDays()
                .stream()
                .collect(Collectors.toMap(
                        ChildActivitiesTableDay::getLocalDate,
                        day -> day.getGrades()
                                .values()
                                .stream()
                                .map(TableCell::getGradeScheme)
                                .filter(gradeScheme -> gradeScheme != null)
                                .mapToInt(GradeScheme::getValue)
                                .average()
                ));

        averageGrades.entrySet().stream().forEach(entry -> {
            TableCell grade = grades.get(entry.getKey());
            if (entry.getValue().isPresent()) {
                long roundAvg = Math.round(entry.getValue().getAsDouble());
                GradeScheme gradeScheme = gradeSchemeService.findAll()
                        .stream()
                        .sorted((first, second) ->
                                Double.compare(Math.abs(roundAvg - first.getValue()), Math.abs(roundAvg - second.getValue())))
                        .findFirst().orElse(null);
                grade.setGradeScheme(gradeScheme);
                grade.setGradeComment(Double.toString(entry.getValue().getAsDouble()));
            } else {
                grade.setGradeScheme(null);
                grade.setGradeComment("");
            }
        });

        ChildrenGroup group = childrenGroupRepository.save(child.getChildrenGroup());
        child.setChildrenGroup(group);
        return group;
    }

    @Override
    public ChildrenGroup setBackground(ChildrenGroup childrenGroup, BackgroundImage backgroundImage) {
        childrenGroup.getBehaviorTable().setBackgroundImage(backgroundImage);
        return childrenGroupRepository.saveAndFlush(childrenGroup);
    }
}

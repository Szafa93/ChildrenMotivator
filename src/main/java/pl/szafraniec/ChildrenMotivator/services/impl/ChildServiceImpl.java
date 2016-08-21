package pl.szafraniec.ChildrenMotivator.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.szafraniec.ChildrenMotivator.model.ActivitiesTableScheme;
import pl.szafraniec.ChildrenMotivator.model.BehaviorTableDay;
import pl.szafraniec.ChildrenMotivator.model.Child;
import pl.szafraniec.ChildrenMotivator.model.ChildActivitiesTable;
import pl.szafraniec.ChildrenMotivator.model.ChildActivitiesTableDay;
import pl.szafraniec.ChildrenMotivator.model.ChildrenGroup;
import pl.szafraniec.ChildrenMotivator.model.GradeScheme;
import pl.szafraniec.ChildrenMotivator.model.TableCell;
import pl.szafraniec.ChildrenMotivator.repository.ChildRepository;
import pl.szafraniec.ChildrenMotivator.services.ChildService;
import pl.szafraniec.ChildrenMotivator.services.ChildrenGroupService;
import pl.szafraniec.ChildrenMotivator.services.GradeSchemeService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

@Component
public class ChildServiceImpl implements ChildService {

    @Autowired
    private ChildRepository childRepository;

    @Autowired
    private GradeSchemeService gradeSchemeService;

    @Autowired
    private ChildrenGroupService childrenGroupService;

    @Override
    public Child recalculateGrades(Child child) {
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

        return childRepository.saveAndFlush(child);
    }

    @Override
    public Child findOne(int id) {
        return childRepository.findOne(id);
    }

    @Override
    public Child removeActivityTable(Child child) {
        child.getChildActivitiesTable().setActivitiesTableScheme(null);
        child.getChildActivitiesTable().setDays(new ArrayList<>());
        return childRepository.saveAndFlush(child);
    }

    @Override
    public Child setActivityTable(Child child, ActivitiesTableScheme scheme) {
        ActivitiesTableScheme oldScheme = child.getChildActivitiesTable().getActivitiesTableScheme();
        if (oldScheme != null) {
            migrateGrade(child, oldScheme, scheme);
        }
        child.getChildActivitiesTable().setActivitiesTableScheme(scheme);
        return childRepository.saveAndFlush(child);
    }

    private void migrateGrade(Child child, ActivitiesTableScheme oldScheme, ActivitiesTableScheme newScheme) {
        oldScheme.getListOfActivities()
                .stream()
                .filter(activity -> !newScheme.getListOfActivities().contains(activity))
                .forEach(activity -> child.getChildActivitiesTable()
                        .getDays()
                        .forEach(day -> day.getGrades().remove(activity)));
        newScheme.getListOfActivities()
                .stream()
                .filter(activity -> !oldScheme.getListOfActivities().contains(activity))
                .forEach(activity -> child.getChildActivitiesTable()
                        .getDays()
                        .forEach(day -> day.getGrades().put(activity, TableCell.TableCellBuilder.create())));
    }

    @Override
    public ChildrenGroup removeChild(Child child) {
        ChildrenGroup group = childrenGroupService.removeChildFromGroup(child);
        childRepository.delete(child);
        childRepository.flush();
        return group;
    }

    @Override
    public Child editChild(Child child, String name, String surname, String pesel, String parentEmail) {
        child.setName(name);
        child.setSurname(surname);
        child.setParentEmail(parentEmail);
        child.setPesel(pesel);
        return childRepository.saveAndFlush(child);
    }

    @Override
    public Child changeGroup(Child child, ChildrenGroup childrenGroup) {
        childrenGroupService.removeChildFromGroup(child);
        childrenGroupService.assignChildToGroup(child, childrenGroup);
        return childRepository.saveAndFlush(child);
    }

    @Override
    public Optional<Child> findByPesel(String pesel) {
        return Optional.ofNullable(childRepository.findByPesel(pesel));
    }

    @Override
    public Child create(String name, String surname, String pesel, String parentMail, ChildrenGroup childrenGroup) {
        Child child = Child.ChildFactory.create(name, surname, pesel, parentMail);
        childrenGroupService.assignChildToGroup(child, childrenGroup);
        return child;
    }
}

package pl.szafraniec.ChildrenMotivator.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.szafraniec.ChildrenMotivator.model.BehaviorTableDay;
import pl.szafraniec.ChildrenMotivator.model.Child;
import pl.szafraniec.ChildrenMotivator.model.ChildActivitiesTable;
import pl.szafraniec.ChildrenMotivator.model.ChildActivitiesTableDay;
import pl.szafraniec.ChildrenMotivator.model.GradeScheme;
import pl.szafraniec.ChildrenMotivator.model.TableCell;
import pl.szafraniec.ChildrenMotivator.repository.ChildRepository;
import pl.szafraniec.ChildrenMotivator.services.ChildService;
import pl.szafraniec.ChildrenMotivator.services.GradeSchemeService;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

@Component
public class ChildServiceImpl implements ChildService {

    @Autowired
    private ChildRepository childRepository;

    @Autowired
    private GradeSchemeService gradeSchemeService;

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
}

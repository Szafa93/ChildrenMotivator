package pl.szafraniec.ChildrenMotivator.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.szafraniec.ChildrenMotivator.model.ActivitiesTableScheme;
import pl.szafraniec.ChildrenMotivator.model.Activity;
import pl.szafraniec.ChildrenMotivator.model.Child;
import pl.szafraniec.ChildrenMotivator.model.ChildActivitiesTable;
import pl.szafraniec.ChildrenMotivator.model.TableCell;
import pl.szafraniec.ChildrenMotivator.repository.ActivitiesTableSchemesRepository;
import pl.szafraniec.ChildrenMotivator.repository.ChildRepository;
import pl.szafraniec.ChildrenMotivator.services.ActivitiesTableSchemeService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ActivitiesTableSchemeServiceImpl implements ActivitiesTableSchemeService {

    @Autowired
    private ChildRepository childRepository;

    @Autowired
    private ActivitiesTableSchemesRepository activitiesTableSchemesRepository;

    @Override
    public List<ActivitiesTableScheme> findAll() {
        return Collections.unmodifiableList(activitiesTableSchemesRepository.findAll());
    }

    @Override
    public ActivitiesTableScheme create(String name, List<Activity> activities) {
        ActivitiesTableScheme scheme = ActivitiesTableScheme.ActivitiesTableSchemeFactory.create(name, activities);
        return activitiesTableSchemesRepository.saveAndFlush(scheme);
    }

    @Override
    public ActivitiesTableScheme edit(ActivitiesTableScheme scheme, String name, List<Activity> activities) {
        scheme.setName(name);
        scheme.setListOfActivities(activities);

        scheme.getChildActivitiesTables().stream().flatMap(table -> table.getDays().stream()).forEach(day -> {
            // remove all removed activities
            day.getGrades().keySet().stream().filter(activity -> !activities.contains(activity)).collect(Collectors.toList()).forEach(
                    activity -> day.getGrades().remove(activity));
            // add all new activities
            activities.stream().filter(activity -> !day.getGrades().keySet().contains(activity)).collect(Collectors.toList()).forEach(
                    activity -> day.getGrades().put(activity, TableCell.TableCellBuilder.create()));
        });

        scheme.setChildActivitiesTables(
                scheme.getChildActivitiesTables()
                        .stream()
                        .map(ChildActivitiesTable::getChild)
                        .map(childRepository::save)
                        .map(Child::getChildActivitiesTable)
                        .collect(Collectors.toList()));
        return activitiesTableSchemesRepository.saveAndFlush(scheme);
        //        return activitiesTableSchemesRepository.getOne(activitiesTableScheme.getId());
    }

    @Override
    public void remove(ActivitiesTableScheme scheme) {
        activitiesTableSchemesRepository.delete(scheme);
        activitiesTableSchemesRepository.flush();
    }
}

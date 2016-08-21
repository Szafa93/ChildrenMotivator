package pl.szafraniec.ChildrenMotivator.services;

import pl.szafraniec.ChildrenMotivator.model.ActivitiesTableScheme;
import pl.szafraniec.ChildrenMotivator.model.Activity;

import java.util.List;

public interface ActivitiesTableSchemeService {
    List<ActivitiesTableScheme> findAll();

    ActivitiesTableScheme create(String name, List<Activity> activities);

    ActivitiesTableScheme edit(ActivitiesTableScheme scheme, String name, List<Activity> activities);

    void remove(ActivitiesTableScheme scheme);
}

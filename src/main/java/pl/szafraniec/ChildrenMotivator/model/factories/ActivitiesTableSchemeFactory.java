package pl.szafraniec.ChildrenMotivator.model.factories;

import pl.szafraniec.ChildrenMotivator.model.ActivitiesTableScheme;
import pl.szafraniec.ChildrenMotivator.model.Activity;

import java.util.ArrayList;
import java.util.List;

public class ActivitiesTableSchemeFactory {
    public static ActivitiesTableScheme create(String name, List<Activity> activities) {
        ActivitiesTableScheme scheme = new ActivitiesTableScheme();
        scheme.setName(name);
        scheme.setListOfActivities(activities);
        scheme.setChildActivitiesTables(new ArrayList<>());
        return scheme;
    }
}

package pl.szafraniec.ChildrenMotivator.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.szafraniec.ChildrenMotivator.model.ActivitiesTableScheme;
import pl.szafraniec.ChildrenMotivator.model.Activity;
import pl.szafraniec.ChildrenMotivator.repository.ActivitiesTableSchemesRepository;
import pl.szafraniec.ChildrenMotivator.services.ActivitiesTableSchemeService;

import java.util.Collections;
import java.util.List;

@Component
public class ActivitiesTableSchemeServiceImpl implements ActivitiesTableSchemeService {

    @Autowired
    private ActivitiesTableSchemesRepository activitiesTableSchemesRepository;

    @Override
    public List<ActivitiesTableScheme> findAll() {
        return Collections.unmodifiableList(activitiesTableSchemesRepository.findAll());
    }

    @Override
    public ActivitiesTableScheme addActivitiesTableSchema(String name, List<Activity> activities) {
        ActivitiesTableScheme scheme = ActivitiesTableScheme.ActivitiesTableSchemeFactory.create(name, activities);
        return activitiesTableSchemesRepository.saveAndFlush(scheme);
    }
}

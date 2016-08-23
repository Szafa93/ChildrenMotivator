package pl.szafraniec.ChildrenMotivator.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.szafraniec.ChildrenMotivator.model.ActivitiesTableScheme;
import pl.szafraniec.ChildrenMotivator.model.Activity;
import pl.szafraniec.ChildrenMotivator.model.factories.ActivityFactory;
import pl.szafraniec.ChildrenMotivator.repository.ActivityRepository;
import pl.szafraniec.ChildrenMotivator.services.ActivitiesTableSchemeService;
import pl.szafraniec.ChildrenMotivator.services.ActivityService;

import java.util.Collections;
import java.util.List;

@Component
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private ActivitiesTableSchemeService activitiesTableSchemeService;

    @Override
    public Activity addActivity(String activityName, byte[] image) {
        Activity activity = ActivityFactory.create(activityName, image);
        return activityRepository.saveAndFlush(activity);
    }

    @Override
    public List<Activity> findAll() {
        return Collections.unmodifiableList(activityRepository.findAll());
    }

    @Override
    public boolean canRemove(Activity activity) {
        return !activitiesTableSchemeService.findAll()
                .stream()
                .map(ActivitiesTableScheme::getListOfActivities)
                .flatMap(List::stream)
                .mapToInt(Activity::getId)
                .anyMatch(id -> id == activity.getId());
    }

    @Override
    public void remove(Activity activity) {
        if (canRemove(activity)) {
            activityRepository.delete(activity);
            activityRepository.flush();
        }
    }

    @Override
    public Activity editActivity(Activity activity, String activityName, byte[] imageByte) {
        activity.setName(activityName);
        activity.setImage(imageByte);
        return activityRepository.saveAndFlush(activity);
    }
}

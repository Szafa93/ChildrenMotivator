package pl.szafraniec.ChildrenMotivator.services;

import pl.szafraniec.ChildrenMotivator.model.Activity;

import java.util.List;

public interface ActivityService {
    Activity addActivity(String activityName, byte[] image);

    List<Activity> findAll();

    boolean canRemove(Activity activity);

    void remove(Activity activity);

    Activity editActivity(Activity activity, String activityName, byte[] imageByte);
}

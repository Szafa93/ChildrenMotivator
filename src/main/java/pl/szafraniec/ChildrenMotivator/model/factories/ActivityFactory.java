package pl.szafraniec.ChildrenMotivator.model.factories;

import pl.szafraniec.ChildrenMotivator.model.Activity;

public class ActivityFactory {
    public static Activity create(String name, byte[] image) {
        Activity activity = new Activity();
        activity.setName(name);
        activity.setImage(image);
        return activity;
    }
}

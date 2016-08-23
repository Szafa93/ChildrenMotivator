package pl.szafraniec.ChildrenMotivator.model.factories;

import pl.szafraniec.ChildrenMotivator.model.BackgroundImage;

public class BackgroundImageFactory {
    public static BackgroundImage create(String name, byte[] image) {
        BackgroundImage backgroundImage = new BackgroundImage();
        backgroundImage.setImage(image);
        backgroundImage.setName(name);
        return backgroundImage;
    }
}

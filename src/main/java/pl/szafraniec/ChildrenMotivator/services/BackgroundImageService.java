package pl.szafraniec.ChildrenMotivator.services;

import pl.szafraniec.ChildrenMotivator.model.BackgroundImage;

import java.util.List;

public interface BackgroundImageService {
    List<BackgroundImage> findAll();

    BackgroundImage create(String name, byte[] image);

    boolean canRemove(BackgroundImage backgroundImage);

    void remove(BackgroundImage backgroundImage);

    BackgroundImage edit(BackgroundImage backgroundImage, String name, byte[] image);
}

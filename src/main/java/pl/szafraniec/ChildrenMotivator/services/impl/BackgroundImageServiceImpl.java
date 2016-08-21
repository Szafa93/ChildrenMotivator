package pl.szafraniec.ChildrenMotivator.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.szafraniec.ChildrenMotivator.model.BackgroundImage;
import pl.szafraniec.ChildrenMotivator.model.Child;
import pl.szafraniec.ChildrenMotivator.model.ChildActivitiesTable;
import pl.szafraniec.ChildrenMotivator.model.ChildrenGroup;
import pl.szafraniec.ChildrenMotivator.repository.BackgroundImageRepository;
import pl.szafraniec.ChildrenMotivator.services.BackgroundImageService;
import pl.szafraniec.ChildrenMotivator.services.ChildrenGroupService;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Component
public class BackgroundImageServiceImpl implements BackgroundImageService {

    @Autowired
    private BackgroundImageRepository backgroundImageRepository;

    @Autowired
    private ChildrenGroupService childrenGroupService;

    @Override
    public List<BackgroundImage> findAll() {
        return Collections.unmodifiableList(backgroundImageRepository.findAll());
    }

    @Override
    public BackgroundImage create(String name, byte[] image) {
        BackgroundImage backgroundImage = BackgroundImage.BackgroundImageFactory.create(name, image);
        return backgroundImageRepository.saveAndFlush(backgroundImage);
    }

    @Override
    public boolean canRemove(BackgroundImage backgroundImage) {
        return !childrenGroupService.findAll()
                .stream()
                .map(ChildrenGroup::getChildren)
                .flatMap(Collection::stream)
                .map(Child::getChildActivitiesTable)
                .map(ChildActivitiesTable::getBackgroundImage)
                .filter(image -> image != null)
                .anyMatch(image -> image.getId() == backgroundImage.getId());
    }

    @Override
    public void remove(BackgroundImage backgroundImage) {
        if (canRemove(backgroundImage)) {
            backgroundImageRepository.delete(backgroundImage);
            backgroundImageRepository.flush();
        }
    }

    @Override
    public BackgroundImage edit(BackgroundImage backgroundImage, String name, byte[] image) {
        backgroundImage.setName(name);
        backgroundImage.setImage(image);
        return backgroundImageRepository.saveAndFlush(backgroundImage);
    }
}

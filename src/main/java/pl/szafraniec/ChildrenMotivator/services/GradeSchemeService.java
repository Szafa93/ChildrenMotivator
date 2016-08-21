package pl.szafraniec.ChildrenMotivator.services;

import pl.szafraniec.ChildrenMotivator.model.GradeScheme;

import java.util.List;
import java.util.Optional;

public interface GradeSchemeService {
    List<GradeScheme> findAll();

    GradeScheme create(int value, byte[] image);

    Optional<GradeScheme> findByValue(int value);

    boolean canRemove(GradeScheme gradeScheme);

    void remove(GradeScheme gradeScheme);

    GradeScheme edit(GradeScheme gradeScheme, int gradeValue, byte[] imageByte);
}

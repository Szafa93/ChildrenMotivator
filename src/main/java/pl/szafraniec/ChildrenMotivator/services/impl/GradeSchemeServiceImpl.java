package pl.szafraniec.ChildrenMotivator.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.szafraniec.ChildrenMotivator.model.ChildrenGroup;
import pl.szafraniec.ChildrenMotivator.model.GradeScheme;
import pl.szafraniec.ChildrenMotivator.model.TableCell;
import pl.szafraniec.ChildrenMotivator.model.factories.GradeSchemeFactory;
import pl.szafraniec.ChildrenMotivator.repository.GradeSchemeRepository;
import pl.szafraniec.ChildrenMotivator.repository.TableCellRepository;
import pl.szafraniec.ChildrenMotivator.services.ChildrenGroupService;
import pl.szafraniec.ChildrenMotivator.services.GradeSchemeService;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class GradeSchemeServiceImpl implements GradeSchemeService {

    @Autowired
    private GradeSchemeRepository gradeSchemeRepository;

    @Autowired
    private TableCellRepository tableCellRepository;

    @Autowired
    private ChildrenGroupService childrenGroupService;

    @Override
    public List<GradeScheme> findAll() {
        return Collections.unmodifiableList(gradeSchemeRepository.findAll());
    }

    @Override
    public GradeScheme create(int value, byte[] image) {
        GradeScheme gradeScheme = GradeSchemeFactory.create(value, image);
        return gradeSchemeRepository.saveAndFlush(gradeScheme);
    }

    @Override
    public Optional<GradeScheme> findByValue(int value) {
        return Optional.ofNullable(gradeSchemeRepository.findByValue(value));
    }

    @Override
    public boolean canRemove(GradeScheme gradeScheme) {
        return !tableCellRepository.findAll()
                .stream()
                .map(TableCell::getGradeScheme)
                .filter(gs -> gs != null)
                .mapToInt(GradeScheme::getId)
                .anyMatch(id -> id == gradeScheme.getId());
    }

    @Override
    public void remove(GradeScheme gradeScheme) {
        if (canRemove(gradeScheme)) {
            gradeSchemeRepository.delete(gradeScheme);
            gradeSchemeRepository.flush();
        }
    }

    @Override
    public GradeScheme edit(GradeScheme gradeScheme, int gradeValue, byte[] imageByte) {
        int oldValue = gradeScheme.getValue();
        gradeScheme.setValue(gradeValue);
        gradeScheme.setImage(imageByte);
        GradeScheme grade = gradeSchemeRepository.saveAndFlush(gradeScheme);
        if (oldValue != gradeValue) {
            childrenGroupService.findAll()
                    .stream()
                    .map(ChildrenGroup::getChildren)
                    .flatMap(Collection::stream)
                    .forEach(child -> childrenGroupService.recalculateGrades(child));
        }
        gradeSchemeRepository.flush();
        return grade;
    }
}

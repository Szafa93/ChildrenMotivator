package pl.szafraniec.ChildrenMotivator.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.szafraniec.ChildrenMotivator.model.GradeScheme;
import pl.szafraniec.ChildrenMotivator.repository.GradeSchemeRepository;
import pl.szafraniec.ChildrenMotivator.services.GradeSchemeService;

import java.util.Collections;
import java.util.List;

@Component
public class GradeSchemeServiceImpl implements GradeSchemeService {

    @Autowired
    private GradeSchemeRepository gradeSchemeRepository;

    @Override
    public List<GradeScheme> findAll() {
        return Collections.unmodifiableList(gradeSchemeRepository.findAll());
    }
}

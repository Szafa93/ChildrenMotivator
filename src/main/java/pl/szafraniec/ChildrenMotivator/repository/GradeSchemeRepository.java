package pl.szafraniec.ChildrenMotivator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.szafraniec.ChildrenMotivator.model.GradeScheme;

@Repository
public interface GradeSchemeRepository extends JpaRepository<GradeScheme, Integer> {
}

package pl.szafraniec.ChildrenMotivator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.szafraniec.ChildrenMotivator.model.ActivitiesTableScheme;

@Repository
public interface ActivitiesTableSchemeRepository extends JpaRepository<ActivitiesTableScheme, Integer> {
}

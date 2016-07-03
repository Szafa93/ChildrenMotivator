package pl.szafraniec.ChildrenMotivator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.szafraniec.ChildrenMotivator.model.ChildActivitiesTable;

@Repository
public interface ChildActivitiesTableRepository extends JpaRepository<ChildActivitiesTable, Integer> {
}

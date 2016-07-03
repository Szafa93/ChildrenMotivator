package pl.szafraniec.ChildrenMotivator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.szafraniec.ChildrenMotivator.model.ChildActivitiesTableDay;

@Repository
public interface ChildActivitiesTableDayRepository extends JpaRepository<ChildActivitiesTableDay, Integer> {
}

package pl.szafraniec.ChildrenMotivator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.szafraniec.ChildrenMotivator.model.BehaviorTableDay;

@Repository
public interface BehaviorTableDayRepository extends JpaRepository<BehaviorTableDay, Integer> {
}

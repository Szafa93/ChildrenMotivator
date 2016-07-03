package pl.szafraniec.ChildrenMotivator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.szafraniec.ChildrenMotivator.model.BehaviorTable;

@Repository
public interface BehaviorTableRepository extends JpaRepository<BehaviorTable, Integer> {
}

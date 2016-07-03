package pl.szafraniec.ChildrenMotivator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.szafraniec.ChildrenMotivator.model.ChildrenGroup;

@Repository
public interface ChildrenGroupRepository extends JpaRepository<ChildrenGroup, Integer> {
}

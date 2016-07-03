package pl.szafraniec.ChildrenMotivator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.szafraniec.ChildrenMotivator.model.Activity;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Integer> {
}

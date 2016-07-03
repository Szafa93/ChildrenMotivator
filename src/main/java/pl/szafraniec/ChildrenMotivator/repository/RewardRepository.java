package pl.szafraniec.ChildrenMotivator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.szafraniec.ChildrenMotivator.model.Reward;

@Repository
public interface RewardRepository extends JpaRepository<Reward, Integer> {
}

package pl.szafraniec.ChildrenMotivator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.szafraniec.ChildrenMotivator.model.Configuration;

public interface ConfigurationRepository extends JpaRepository<Configuration, Integer> {
}

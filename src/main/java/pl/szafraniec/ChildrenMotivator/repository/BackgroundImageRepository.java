package pl.szafraniec.ChildrenMotivator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.szafraniec.ChildrenMotivator.model.BackgroundImage;

public interface BackgroundImageRepository extends JpaRepository<BackgroundImage, Integer> {
}

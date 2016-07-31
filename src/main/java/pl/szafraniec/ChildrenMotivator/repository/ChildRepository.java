package pl.szafraniec.ChildrenMotivator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.szafraniec.ChildrenMotivator.model.Child;

@Repository
public interface ChildRepository extends JpaRepository<Child, Integer> {
    Child findByPesel(String pesel);
}

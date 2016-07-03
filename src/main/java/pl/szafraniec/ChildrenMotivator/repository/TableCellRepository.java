package pl.szafraniec.ChildrenMotivator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.szafraniec.ChildrenMotivator.model.TableCell;

@Repository
public interface TableCellRepository extends JpaRepository<TableCell, Integer> {
}

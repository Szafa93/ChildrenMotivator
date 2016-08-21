package pl.szafraniec.ChildrenMotivator.services;

import pl.szafraniec.ChildrenMotivator.model.ChildActivitiesTable;
import pl.szafraniec.ChildrenMotivator.model.ChildActivitiesTableDay;
import pl.szafraniec.ChildrenMotivator.model.Holder;

import java.time.LocalDate;
import java.util.List;

public interface ChildActivityTableService {
    List<ChildActivitiesTableDay> getDays(Holder<ChildActivitiesTable> holder, LocalDate from, LocalDate to);
}

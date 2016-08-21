package pl.szafraniec.ChildrenMotivator.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.szafraniec.ChildrenMotivator.model.ChildActivitiesTable;
import pl.szafraniec.ChildrenMotivator.model.ChildActivitiesTableDay;
import pl.szafraniec.ChildrenMotivator.model.Holder;
import pl.szafraniec.ChildrenMotivator.repository.ChildRepository;
import pl.szafraniec.ChildrenMotivator.services.ChildActivityTableService;

import java.time.LocalDate;
import java.util.List;

@Component
public class ChildActivityTableServiceImpl implements ChildActivityTableService {

    // TODO remove?
    @Autowired
    private ChildRepository childRepository;

    @Override
    public List<ChildActivitiesTableDay> getDays(Holder<ChildActivitiesTable> holder, LocalDate from, LocalDate to) {
        return holder.get().getDays(from, to).orElseGet(() -> {
            holder.get().generateDay(from, to);
            holder.set(childRepository.saveAndFlush(holder.get().getChild()).getChildActivitiesTable());
            return holder.get().getDays(from, to).get();
        });
    }
}

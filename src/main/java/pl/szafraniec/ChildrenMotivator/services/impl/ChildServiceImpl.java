package pl.szafraniec.ChildrenMotivator.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.szafraniec.ChildrenMotivator.model.ActivitiesTableScheme;
import pl.szafraniec.ChildrenMotivator.model.Child;
import pl.szafraniec.ChildrenMotivator.model.ChildActivitiesTableDay;
import pl.szafraniec.ChildrenMotivator.model.ChildrenGroup;
import pl.szafraniec.ChildrenMotivator.model.Holder;
import pl.szafraniec.ChildrenMotivator.model.TableCell;
import pl.szafraniec.ChildrenMotivator.repository.ChildRepository;
import pl.szafraniec.ChildrenMotivator.services.ChildService;
import pl.szafraniec.ChildrenMotivator.services.ChildrenGroupService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ChildServiceImpl implements ChildService {

    @Autowired
    private ChildRepository childRepository;

    @Autowired
    private ChildrenGroupService childrenGroupService;

    @Override
    public Child editGrades(Child child) {
        childrenGroupService.recalculateGrades(child);
        return childRepository.saveAndFlush(child);
    }

    @Override
    public Child findOne(int id) {
        return childRepository.findOne(id);
    }

    @Override
    public Child removeActivityTable(Child child) {
        child.getChildActivitiesTable().setActivitiesTableScheme(null);
        child.getChildActivitiesTable().setDays(new ArrayList<>());
        return childRepository.saveAndFlush(child);
    }

    @Override
    public Child setActivityTable(Child child, ActivitiesTableScheme scheme) {
        ActivitiesTableScheme oldScheme = child.getChildActivitiesTable().getActivitiesTableScheme();
        if (oldScheme != null) {
            migrateGrade(child, oldScheme, scheme);
        }
        child.getChildActivitiesTable().setActivitiesTableScheme(scheme);
        return childRepository.saveAndFlush(child);
    }

    private void migrateGrade(Child child, ActivitiesTableScheme oldScheme, ActivitiesTableScheme newScheme) {
        oldScheme.getListOfActivities()
                .stream()
                .filter(activity -> !newScheme.getListOfActivities().contains(activity))
                .forEach(activity -> child.getChildActivitiesTable()
                        .getDays()
                        .forEach(day -> day.getGrades().remove(activity)));
        newScheme.getListOfActivities()
                .stream()
                .filter(activity -> !oldScheme.getListOfActivities().contains(activity))
                .forEach(activity -> child.getChildActivitiesTable()
                        .getDays()
                        .forEach(day -> day.getGrades().put(activity, TableCell.TableCellBuilder.create())));
    }

    @Override
    public ChildrenGroup removeChild(Child child) {
        ChildrenGroup group = childrenGroupService.removeChildFromGroup(child);
        childRepository.delete(child);
        childRepository.flush();
        return group;
    }

    @Override
    public Child editChild(Child child, String name, String surname, String pesel, String parentEmail) {
        child.setName(name);
        child.setSurname(surname);
        child.setParentEmail(parentEmail);
        child.setPesel(pesel);
        return childRepository.saveAndFlush(child);
    }

    @Override
    public Child changeGroup(Child child, ChildrenGroup childrenGroup) {
        childrenGroupService.removeChildFromGroup(child);
        childrenGroupService.assignChildToGroup(child, childrenGroup);
        return childRepository.saveAndFlush(child);
    }

    @Override
    public Optional<Child> findByPesel(String pesel) {
        return Optional.ofNullable(childRepository.findByPesel(pesel));
    }

    @Override
    public Child create(String name, String surname, String pesel, String parentMail, ChildrenGroup childrenGroup) {
        Child child = Child.ChildFactory.create(name, surname, pesel, parentMail);
        childrenGroupService.assignChildToGroup(child, childrenGroup);
        return child;
    }

    @Override
    public List<ChildActivitiesTableDay> getDays(Holder<Child> holder, LocalDate from, LocalDate to) {
        return holder.get().getChildActivitiesTable().getDays(from, to).orElseGet(() -> {
            holder.get().getChildActivitiesTable().generateDay(from, to);
            holder.set(childRepository.saveAndFlush(holder.get()));
            return holder.get().getChildActivitiesTable().getDays(from, to).get();
        });
    }
}

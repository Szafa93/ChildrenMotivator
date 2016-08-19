package pl.szafraniec.ChildrenMotivator.services;

import pl.szafraniec.ChildrenMotivator.model.ChildrenGroup;

import java.time.LocalDate;

public interface RaportService {
    void sendRaport(ChildrenGroup childrenGroup, LocalDate from, LocalDate to);
}

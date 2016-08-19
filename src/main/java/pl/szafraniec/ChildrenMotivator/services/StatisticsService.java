package pl.szafraniec.ChildrenMotivator.services;

import pl.szafraniec.ChildrenMotivator.model.ChildrenGroup;

import java.time.LocalDate;

public interface StatisticsService {
    void sendStatistics(ChildrenGroup childrenGroup, LocalDate from, LocalDate to);
}

package pl.szafraniec.ChildrenMotivator.services;

import pl.szafraniec.ChildrenMotivator.model.ChildrenGroup;

import java.time.LocalDate;

public interface ReportService {
    void sendReports(ChildrenGroup childrenGroup, LocalDate from, LocalDate to);

    boolean canSendReports();
}

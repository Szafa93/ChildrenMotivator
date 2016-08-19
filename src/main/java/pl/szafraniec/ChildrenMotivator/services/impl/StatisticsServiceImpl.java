package pl.szafraniec.ChildrenMotivator.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.szafraniec.ChildrenMotivator.model.Child;
import pl.szafraniec.ChildrenMotivator.model.ChildrenGroup;
import pl.szafraniec.ChildrenMotivator.services.EmailService;
import pl.szafraniec.ChildrenMotivator.services.StatisticsService;

import java.time.LocalDate;

@Component
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private EmailService emailService;

    @Override
    public void sendStatistics(ChildrenGroup childrenGroup, LocalDate from, LocalDate to) {
        for (Child child : childrenGroup.getChildren()) {
            if (child.getParentEmail() != null) {
                byte[] xlsx = generateStatisticsXlsx(child, from, to);
                String message = "Raport dla " + child.getName() + " " + child.getSurname() + " z dni " + from + "-" + to;
                emailService.sendEmail(child.getParentEmail(), message, message, xlsx, "raport.xlsx");
            }
        }
    }

    private byte[] generateStatisticsXlsx(Child child, LocalDate from, LocalDate to) {
        return new byte[0];
    }
}

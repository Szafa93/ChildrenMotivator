package pl.szafraniec.ChildrenMotivator.services.impl;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.szafraniec.ChildrenMotivator.model.Activity;
import pl.szafraniec.ChildrenMotivator.model.Child;
import pl.szafraniec.ChildrenMotivator.model.ChildActivitiesTableDay;
import pl.szafraniec.ChildrenMotivator.model.ChildrenGroup;
import pl.szafraniec.ChildrenMotivator.model.GradeScheme;
import pl.szafraniec.ChildrenMotivator.services.ConfigurationService;
import pl.szafraniec.ChildrenMotivator.services.EmailService;
import pl.szafraniec.ChildrenMotivator.services.ReportService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReportServiceImpl implements ReportService {

    @Autowired
    private EmailService emailService;

    @Autowired
    private ConfigurationService configurationService;

    @Override
    public void sendReports(ChildrenGroup childrenGroup, LocalDate from, LocalDate to) {
        childrenGroup.getChildren().stream().filter(child -> child.getParentEmail() != null).forEach(child -> {
            try {
                byte[] xlsx = generateStatisticsXlsx(child, from, to);
                String message = "Raport dla " + child.getName() + " " + child.getSurname() + " z dni " + from + "-" + to;
                emailService.sendEmail(child.getParentEmail(), message, message, xlsx, "raport.xlsx");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public boolean canSendReports() {
        return configurationService.getConfiguration().isConfigured();
    }

    private byte[] generateStatisticsXlsx(Child child, LocalDate startDate, LocalDate endDate) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Raport");
            int i = 0;
            XSSFRow row = sheet.createRow(i++);
            row.createCell(0).setCellValue("Imię");
            row.createCell(1).setCellValue(child.getName());
            row = sheet.createRow(i++);
            row.createCell(0).setCellValue("Nazwisko");
            row.createCell(1).setCellValue(child.getSurname());
            row = sheet.createRow(i++);
            row.createCell(0).setCellValue("Od");
            row.createCell(1).setCellValue(startDate.toString());
            row.createCell(2).setCellValue("Do");
            row.createCell(3).setCellValue(endDate.toString());
            sheet.createRow(i++);
            sheet.createRow(i++);
            XSSFRow headerRow = sheet.createRow(i++);
            List<Activity> activities = child.getChildActivitiesTable().getActivitiesTableScheme().getListOfActivities();
            int j = 1;
            for (Activity activity : activities) {
                sheet.autoSizeColumn(j);
                headerRow.createCell(j++).setCellValue(activity.getName());
            }
            List<ChildActivitiesTableDay> days = child.getChildActivitiesTable()
                    .getDays()
                    .stream()
                    .filter(day -> (day.getLocalDate().isAfter(startDate) && day.getLocalDate().isBefore(endDate))
                            || day.getLocalDate().isEqual(startDate) || day.getLocalDate().isEqual(endDate))
                    .collect(Collectors.toList());
            for (ChildActivitiesTableDay day : days) {
                XSSFRow dayRow = sheet.createRow(i++);
                dayRow.createCell(0).setCellValue(day.getLocalDate().toString());
                j = 1;
                for (Activity activity : activities) {
                    GradeScheme gradeScheme = day.getGrades().get(activity).getGradeScheme();
                    if (gradeScheme != null) {
                        dayRow.createCell(j++).setCellValue(gradeScheme.getValue());
                    }
                }
            }
            for (int k = 0; k < activities.size() + 1; k++) {
                sheet.autoSizeColumn(k);
            }
            workbook.write(outputStream);
        }
        outputStream.close();
        return outputStream.toByteArray();
    }
}

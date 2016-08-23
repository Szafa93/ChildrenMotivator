package pl.szafraniec.ChildrenMotivator.ui.services.impl;

import org.eclipse.swt.widgets.Shell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import pl.szafraniec.ChildrenMotivator.model.ActivitiesTableScheme;
import pl.szafraniec.ChildrenMotivator.model.Activity;
import pl.szafraniec.ChildrenMotivator.model.BackgroundImage;
import pl.szafraniec.ChildrenMotivator.model.ChildrenGroup;
import pl.szafraniec.ChildrenMotivator.model.GradeScheme;
import pl.szafraniec.ChildrenMotivator.ui.activities.dialogs.ActivityTableSelectorDialog;
import pl.szafraniec.ChildrenMotivator.ui.activities.dialogs.EditActivityDialog;
import pl.szafraniec.ChildrenMotivator.ui.activitiesTableSchemes.dialogs.EditActivitiesTableSchemeDialog;
import pl.szafraniec.ChildrenMotivator.ui.backgroundImages.dialogs.BackgroundImageDialog;
import pl.szafraniec.ChildrenMotivator.ui.backgroundImages.dialogs.BackgroundImageSelectorDialog;
import pl.szafraniec.ChildrenMotivator.ui.child.dialogs.EditChildDialog;
import pl.szafraniec.ChildrenMotivator.ui.gradesSchemes.dialogs.EditGradeSchemeDialog;
import pl.szafraniec.ChildrenMotivator.ui.gradesSchemes.dialogs.GradeSelectorDialog;
import pl.szafraniec.ChildrenMotivator.ui.groups.GroupSelectorDialog;
import pl.szafraniec.ChildrenMotivator.ui.groups.dialogs.EditChildrenGroupDialog;
import pl.szafraniec.ChildrenMotivator.ui.groups.dialogs.SelectDatesForReportsDialog;
import pl.szafraniec.ChildrenMotivator.ui.services.DialogProvider;
import pl.szafraniec.ChildrenMotivator.ui.start.dialogs.ConfigurationDialog;

import java.util.ArrayList;
import java.util.List;

@Component
public class DialogProviderImpl implements DialogProvider {

    @Autowired
    private Shell shell;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public ConfigurationDialog createConfigurationDialog(String fromEmail, String smtpHost, String smtpPort, String mailUser,
            String mailPassword, boolean sslConnection) {
        return injectDependency(new ConfigurationDialog(shell, fromEmail, smtpHost, smtpPort, mailUser, mailPassword, sslConnection));
    }

    @Override
    public ActivityTableSelectorDialog createActivityTableSelectorDialog(ActivitiesTableScheme activitiesTableScheme) {
        return injectDependency(new ActivityTableSelectorDialog(shell, activitiesTableScheme));
    }

    @Override
    public EditActivityDialog createEditActivityDialog() {
        return injectDependency(new EditActivityDialog(shell, "", null, "Dodaj aktywność"));
    }

    @Override
    public EditActivityDialog createEditActivityDialog(String activityName, byte[] fileData) {
        return injectDependency(new EditActivityDialog(shell, activityName, fileData, "Edytuj aktywność"));
    }

    @Override
    public EditActivitiesTableSchemeDialog createEditActivitiesTableSchemeDialog() {
        return injectDependency(new EditActivitiesTableSchemeDialog(shell, "", new ArrayList<>(), "Dodaj schemat"));
    }

    @Override
    public EditActivitiesTableSchemeDialog createEditActivitiesTableSchemeDialog(String activitiesTableSchemeName,
            List<Activity> activities) {
        return injectDependency(new EditActivitiesTableSchemeDialog(shell, activitiesTableSchemeName, activities,
                "Edytuj schemat tabeli aktywności"));
    }

    @Override
    public BackgroundImageDialog createBackgroundImageDialog() {
        return injectDependency(new BackgroundImageDialog(shell, "", null, "Dodaj tło"));
    }

    @Override
    public BackgroundImageDialog createBackgroundImageDialog(String name, byte[] fileData) {
        return injectDependency(new BackgroundImageDialog(shell, name, fileData, "Edytuj tło"));
    }

    @Override
    public BackgroundImageSelectorDialog createBackgroundImageSelectorDialog(BackgroundImage backgroundImage) {
        return injectDependency(new BackgroundImageSelectorDialog(shell, backgroundImage));
    }

    @Override
    public EditChildDialog createEditChildDialog() {
        return injectDependency(new EditChildDialog(shell, "", "", "", "", "Wprowadź dane dziecka", -1));
    }

    @Override
    public EditChildDialog createEditChildDialog(String name, String surname, String pesel, String parentEmail, int id) {
        return injectDependency(new EditChildDialog(shell, name, surname, pesel, parentEmail, "Edytuj dane dziecka", id));
    }

    @Override
    public EditGradeSchemeDialog createEditGradeSchemeDialog() {
        return injectDependency(new EditGradeSchemeDialog(shell, null, null, "Dodaj ocenę", -1));
    }

    @Override
    public EditGradeSchemeDialog createEditGradeSchemeDialog(Integer gradeValue, byte[] fileData, int id) {
        return injectDependency(new EditGradeSchemeDialog(shell, gradeValue, fileData, "Edytuj ocenę", id));
    }

    @Override
    public GradeSelectorDialog createGradeSelectorDialog(GradeScheme gradeScheme, String gradeComment) {
        return injectDependency(new GradeSelectorDialog(shell, gradeScheme, gradeComment));
    }

    @Override
    public EditChildrenGroupDialog createEditChildrenGroupDialog() {
        return injectDependency(new EditChildrenGroupDialog(shell, "", "Dodaj grupę"));
    }

    @Override
    public EditChildrenGroupDialog createEditChildrenGroupDialog(String groupName) {
        return injectDependency(new EditChildrenGroupDialog(shell, groupName, "Edytuj dane grupy"));
    }

    @Override
    public GroupSelectorDialog createGroupSelectorDialog(ChildrenGroup childrenGroup) {
        return injectDependency(new GroupSelectorDialog(shell, childrenGroup));
    }

    @Override
    public SelectDatesForReportsDialog createSendStatisticDialog() {
        return injectDependency(new SelectDatesForReportsDialog(shell));
    }

    private <T> T injectDependency(T object) {
        applicationContext.getAutowireCapableBeanFactory().autowireBean(object);
        return object;
    }
}

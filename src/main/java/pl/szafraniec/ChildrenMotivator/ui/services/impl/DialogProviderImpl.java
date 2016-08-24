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
import pl.szafraniec.ChildrenMotivator.ui.activities.dialogs.ActivityDialog;
import pl.szafraniec.ChildrenMotivator.ui.activitiesTableSchemes.dialogs.ActivitiesTableSchemeDialog;
import pl.szafraniec.ChildrenMotivator.ui.activitiesTableSchemes.dialogs.ActivitiesTableSchemeSelectorDialog;
import pl.szafraniec.ChildrenMotivator.ui.backgroundImages.dialogs.BackgroundImageDialog;
import pl.szafraniec.ChildrenMotivator.ui.backgroundImages.dialogs.BackgroundImageSelectorDialog;
import pl.szafraniec.ChildrenMotivator.ui.child.dialogs.ChildDialog;
import pl.szafraniec.ChildrenMotivator.ui.gradesSchemes.dialogs.GradeSchemeDialog;
import pl.szafraniec.ChildrenMotivator.ui.gradesSchemes.dialogs.GradeSchemeSelectorDialog;
import pl.szafraniec.ChildrenMotivator.ui.groups.dialogs.ChildrenGroupDialog;
import pl.szafraniec.ChildrenMotivator.ui.groups.dialogs.ChildrenGroupSelectorDialog;
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
    public ActivitiesTableSchemeSelectorDialog createActivitiesTableSchemeSelectorDialog(ActivitiesTableScheme activitiesTableScheme) {
        return injectDependency(new ActivitiesTableSchemeSelectorDialog(shell, activitiesTableScheme));
    }

    @Override
    public ActivityDialog createActivityDialog() {
        return injectDependency(new ActivityDialog(shell, "", null, "Dodaj aktywność"));
    }

    @Override
    public ActivityDialog createActivityDialog(String activityName, byte[] fileData) {
        return injectDependency(new ActivityDialog(shell, activityName, fileData, "Edytuj aktywność"));
    }

    @Override
    public ActivitiesTableSchemeDialog createActivitiesTableSchemeDialog() {
        return injectDependency(new ActivitiesTableSchemeDialog(shell, "", new ArrayList<>(), "Dodaj schemat"));
    }

    @Override
    public ActivitiesTableSchemeDialog createActivitiesTableSchemeDialog(String activitiesTableSchemeName,
            List<Activity> activities) {
        return injectDependency(new ActivitiesTableSchemeDialog(shell, activitiesTableSchemeName, activities,
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
    public ChildDialog createChildDialog() {
        return injectDependency(new ChildDialog(shell, "", "", "", "", "Wprowadź dane dziecka", -1));
    }

    @Override
    public ChildDialog createChildDialog(String name, String surname, String pesel, String parentEmail, int id) {
        return injectDependency(new ChildDialog(shell, name, surname, pesel, parentEmail, "Edytuj dane dziecka", id));
    }

    @Override
    public GradeSchemeDialog createGradeSchemeDialog() {
        return injectDependency(new GradeSchemeDialog(shell, null, null, "Dodaj ocenę", -1));
    }

    @Override
    public GradeSchemeDialog createGradeSchemeDialog(Integer gradeValue, byte[] fileData, int id) {
        return injectDependency(new GradeSchemeDialog(shell, gradeValue, fileData, "Edytuj ocenę", id));
    }

    @Override
    public GradeSchemeSelectorDialog createGradeSchemeSelectorDialog(GradeScheme gradeScheme, String gradeComment) {
        return injectDependency(new GradeSchemeSelectorDialog(shell, gradeScheme, gradeComment));
    }

    @Override
    public ChildrenGroupDialog createChildrenGroupDialog() {
        return injectDependency(new ChildrenGroupDialog(shell, "", "Dodaj grupę"));
    }

    @Override
    public ChildrenGroupDialog createChildrenGroupDialog(String groupName) {
        return injectDependency(new ChildrenGroupDialog(shell, groupName, "Edytuj dane grupy"));
    }

    @Override
    public ChildrenGroupSelectorDialog createChildrenGroupSelectorDialog(ChildrenGroup childrenGroup) {
        return injectDependency(new ChildrenGroupSelectorDialog(shell, childrenGroup));
    }

    @Override
    public SelectDatesForReportsDialog createSelectDatesForReportsDialog() {
        return injectDependency(new SelectDatesForReportsDialog(shell));
    }

    private <T> T injectDependency(T object) {
        applicationContext.getAutowireCapableBeanFactory().autowireBean(object);
        return object;
    }
}

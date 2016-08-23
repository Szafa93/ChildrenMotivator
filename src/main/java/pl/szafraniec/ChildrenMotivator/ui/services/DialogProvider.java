package pl.szafraniec.ChildrenMotivator.ui.services;

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
import pl.szafraniec.ChildrenMotivator.ui.start.dialogs.ConfigurationDialog;

import java.util.List;

public interface DialogProvider {
    ConfigurationDialog createConfigurationDialog(String fromEmail, String smtpHost, String smtpPort, String mailUser,
            String mailPassword, boolean sslConnection);

    ActivityTableSelectorDialog createActivityTableSelectorDialog(ActivitiesTableScheme activitiesTableScheme);

    EditActivityDialog createEditActivityDialog();

    EditActivityDialog createEditActivityDialog(String activityName, byte[] fileData);

    EditActivitiesTableSchemeDialog createEditActivitiesTableSchemeDialog();

    EditActivitiesTableSchemeDialog createEditActivitiesTableSchemeDialog(String activitiesTableSchemeName, List<Activity> activities);

    BackgroundImageDialog createBackgroundImageDialog();

    BackgroundImageDialog createBackgroundImageDialog(String name, byte[] fileData);

    BackgroundImageSelectorDialog createBackgroundImageSelectorDialog(BackgroundImage backgroundImage);

    EditChildDialog createEditChildDialog();

    EditChildDialog createEditChildDialog(String name, String surname, String pesel, String parentEmail, int id);

    EditGradeSchemeDialog createEditGradeSchemeDialog();

    EditGradeSchemeDialog createEditGradeSchemeDialog(Integer gradeValue, byte[] fileData, int id);

    GradeSelectorDialog createGradeSelectorDialog(GradeScheme gradeScheme, String gradeComment);

    EditChildrenGroupDialog createEditChildrenGroupDialog();

    EditChildrenGroupDialog createEditChildrenGroupDialog(String groupName);

    GroupSelectorDialog createGroupSelectorDialog(ChildrenGroup childrenGroup);

    SelectDatesForReportsDialog createSendStatisticDialog();
}

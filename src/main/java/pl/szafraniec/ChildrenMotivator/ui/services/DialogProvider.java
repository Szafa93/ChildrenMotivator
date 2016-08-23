package pl.szafraniec.ChildrenMotivator.ui.services;

import pl.szafraniec.ChildrenMotivator.model.ActivitiesTableScheme;
import pl.szafraniec.ChildrenMotivator.model.Activity;
import pl.szafraniec.ChildrenMotivator.model.BackgroundImage;
import pl.szafraniec.ChildrenMotivator.model.ChildrenGroup;
import pl.szafraniec.ChildrenMotivator.model.GradeScheme;
import pl.szafraniec.ChildrenMotivator.ui.activities.dialogs.ActivityDialog;
import pl.szafraniec.ChildrenMotivator.ui.activitiesTableSchemes.dialogs.ActivitiesTableSchemeDialog;
import pl.szafraniec.ChildrenMotivator.ui.activitiesTableSchemes.dialogs.ActivityTableSelectorDialog;
import pl.szafraniec.ChildrenMotivator.ui.backgroundImages.dialogs.BackgroundImageDialog;
import pl.szafraniec.ChildrenMotivator.ui.backgroundImages.dialogs.BackgroundImageSelectorDialog;
import pl.szafraniec.ChildrenMotivator.ui.child.dialogs.ChildDialog;
import pl.szafraniec.ChildrenMotivator.ui.gradesSchemes.dialogs.GradeSchemeDialog;
import pl.szafraniec.ChildrenMotivator.ui.gradesSchemes.dialogs.GradeSelectorDialog;
import pl.szafraniec.ChildrenMotivator.ui.groups.GroupSelectorDialog;
import pl.szafraniec.ChildrenMotivator.ui.groups.dialogs.ChildrenGroupDialog;
import pl.szafraniec.ChildrenMotivator.ui.groups.dialogs.SelectDatesForReportsDialog;
import pl.szafraniec.ChildrenMotivator.ui.start.dialogs.ConfigurationDialog;

import java.util.List;

public interface DialogProvider {
    ConfigurationDialog createConfigurationDialog(String fromEmail, String smtpHost, String smtpPort, String mailUser,
            String mailPassword, boolean sslConnection);

    ActivityTableSelectorDialog createActivityTableSelectorDialog(ActivitiesTableScheme activitiesTableScheme);

    ActivityDialog createEditActivityDialog();

    ActivityDialog createEditActivityDialog(String activityName, byte[] fileData);

    ActivitiesTableSchemeDialog createEditActivitiesTableSchemeDialog();

    ActivitiesTableSchemeDialog createEditActivitiesTableSchemeDialog(String activitiesTableSchemeName, List<Activity> activities);

    BackgroundImageDialog createBackgroundImageDialog();

    BackgroundImageDialog createBackgroundImageDialog(String name, byte[] fileData);

    BackgroundImageSelectorDialog createBackgroundImageSelectorDialog(BackgroundImage backgroundImage);

    ChildDialog createEditChildDialog();

    ChildDialog createEditChildDialog(String name, String surname, String pesel, String parentEmail, int id);

    GradeSchemeDialog createEditGradeSchemeDialog();

    GradeSchemeDialog createEditGradeSchemeDialog(Integer gradeValue, byte[] fileData, int id);

    GradeSelectorDialog createGradeSelectorDialog(GradeScheme gradeScheme, String gradeComment);

    ChildrenGroupDialog createEditChildrenGroupDialog();

    ChildrenGroupDialog createEditChildrenGroupDialog(String groupName);

    GroupSelectorDialog createGroupSelectorDialog(ChildrenGroup childrenGroup);

    SelectDatesForReportsDialog createSendStatisticDialog();
}

package pl.szafraniec.ChildrenMotivator.ui.start;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.szafraniec.ChildrenMotivator.model.Configuration;
import pl.szafraniec.ChildrenMotivator.services.ConfigurationService;
import pl.szafraniec.ChildrenMotivator.ui.AbstractMainComposite;
import pl.szafraniec.ChildrenMotivator.ui.Fonts;
import pl.szafraniec.ChildrenMotivator.ui.activities.ActivitiesComposite;
import pl.szafraniec.ChildrenMotivator.ui.activitiesTableSchemes.ActivitiesTableSchemesComposite;
import pl.szafraniec.ChildrenMotivator.ui.backgroundImages.BackgroundImagesComposite;
import pl.szafraniec.ChildrenMotivator.ui.gradesSchemes.GradesSchemesComposite;
import pl.szafraniec.ChildrenMotivator.ui.groups.ChildrenGroupsComposite;
import pl.szafraniec.ChildrenMotivator.ui.start.dialogs.ConfigurationDialog;

@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class StartComposite extends AbstractMainComposite {

    @Autowired
    private ConfigurationService configurationService;

    public StartComposite(Composite parent) {
        super(parent, SWT.NONE);
    }

    protected void createTopPart() {
        Composite topPart = new Composite(this, SWT.NONE);
        topPart.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).equalWidth(false).create());
        topPart.setLayoutData(GridDataFactory.swtDefaults().grab(true, false).align(SWT.CENTER, SWT.CENTER).create());

        createLabel(topPart, "System motywacji dzieci");
    }

    protected Composite createDownPart() {
        Composite downPart = new Composite(this, SWT.NONE);
        downPart.setLayout(GridLayoutFactory.swtDefaults()
                .numColumns(1)
                .spacing(20, 20)
                .create());
        downPart.setLayoutData(GridDataFactory.swtDefaults()
                .grab(true, true)
                .align(SWT.CENTER, SWT.CENTER)
                .create());
        createButton(downPart, "Grupy", ChildrenGroupsComposite.class);
        createButton(downPart, "Możliwe oceny", GradesSchemesComposite.class);
        createButton(downPart, "Możliwe aktywności", ActivitiesComposite.class);
        createButton(downPart, "Możliwe tła", BackgroundImagesComposite.class);
        createButton(downPart, "Ustawienia", this::editConfiguration);
        createButton(downPart, "Schematy tablic aktywności", ActivitiesTableSchemesComposite.class);
        return downPart;
    }

    private void editConfiguration() {
        Configuration configuration = configurationService.getConfiguration();
        ConfigurationDialog dialog = new ConfigurationDialog(getShell(),
                configuration.getFromEmail(),
                configuration.getSmtpHost(),
                configuration.getSmtpPort(),
                configuration.getMailUser(),
                configuration.getMailPassword(),
                configuration.isSslConnection());
        if (Window.OK == dialog.open()) {
            configurationService.editConfiguration(dialog.getFromEmail(),
                    dialog.getSmtpHost(),
                    dialog.getSmtpPort(),
                    dialog.getMailUser(),
                    dialog.getMailPassword(),
                    dialog.isSslConnection());
        }
    }

    private void createButton(Composite parent, String text, Class compositeClass) {
        createButton(parent, text, () -> {
            applicationContext.getBean(compositeClass, shell);
            dispose();
            shell.layout(true, true);
        });
    }

    private void createButton(Composite parent, String text, Runnable onClickAction) {
        Button button = new Button(parent, SWT.PUSH);
        button.setLayoutData(GridDataFactory.swtDefaults()
                .grab(true, false)
                .align(SWT.CENTER, SWT.CENTER)
                .minSize(500, 35)
                .hint(500, 35)
                .create());
        button.setText(text);
        button.setFont(FontDescriptor.createFrom(Fonts.DEFAULT_FONT_DATA).createFont(button.getDisplay()));
        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                onClickAction.run();
            }
        });
    }
}

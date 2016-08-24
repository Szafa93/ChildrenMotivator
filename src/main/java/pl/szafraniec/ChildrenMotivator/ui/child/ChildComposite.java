package pl.szafraniec.ChildrenMotivator.ui.child;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.szafraniec.ChildrenMotivator.model.Child;
import pl.szafraniec.ChildrenMotivator.model.Holder;
import pl.szafraniec.ChildrenMotivator.services.ChildService;
import pl.szafraniec.ChildrenMotivator.ui.AbstractMainComposite;
import pl.szafraniec.ChildrenMotivator.ui.Fonts;
import pl.szafraniec.ChildrenMotivator.ui.activitiesTableSchemes.dialogs.ActivitiesTableSchemeSelectorDialog;
import pl.szafraniec.ChildrenMotivator.ui.child.childActivitiesTable.ChildActivitiesTableChartComposite;
import pl.szafraniec.ChildrenMotivator.ui.child.childActivitiesTable.ChildActivitiesTableStatisticsComposite;
import pl.szafraniec.ChildrenMotivator.ui.child.dialogs.ChildDialog;
import pl.szafraniec.ChildrenMotivator.ui.groups.ChildrenGroupComposite;
import pl.szafraniec.ChildrenMotivator.ui.groups.dialogs.ChildrenGroupSelectorDialog;

@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ChildComposite extends AbstractMainComposite {

    @Autowired
    private ChildService childService;

    private Holder<Child> child;

    private Composite childPropertiesComposite;

    private Label label;

    public ChildComposite(Composite parent, Holder<Child> child) {
        super(parent, SWT.NONE);
        this.child = child;
    }

    @Override
    protected void createTopPart() {
        Composite topPart = new Composite(this, SWT.NONE);
        topPart.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).equalWidth(false).create());
        topPart.setLayoutData(GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).create());

        label = createLabel(topPart, child.get().getName() + " " + child.get().getSurname());
        createTopControlsButtonsComposite(topPart);
    }

    private Composite createTopControlsButtonsComposite(Composite parent) {
        Composite controlsButtonsComposite = new Composite(parent, SWT.NONE);
        controlsButtonsComposite.setLayoutData(GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.TOP).create());
        controlsButtonsComposite.setLayout(GridLayoutFactory.swtDefaults().numColumns(1).create());

        createBackButton(controlsButtonsComposite, applicationContext, shell, ChildrenGroupComposite.class,
                () -> new Object[] { child.get().getChildrenGroup() });
        return controlsButtonsComposite;
    }

    @Override
    protected Composite createDownPart() {
        Composite downPart = new Composite(this, SWT.NONE);
        downPart.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).create());
        downPart.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
        createChildComposite(downPart);
        createDownControlsButtonsComposite(downPart);
        return downPart;
    }

    private Composite createChildComposite(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
        composite.setLayout(GridLayoutFactory.fillDefaults().numColumns(1).create());
        childPropertiesComposite = new Composite(composite, SWT.NONE);
        fillChildProperties(childPropertiesComposite);
        return composite;
    }

    private void fillChildProperties(Composite childPropertiesComposite) {
        childPropertiesComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
        childPropertiesComposite.setLayout(GridLayoutFactory.fillDefaults().numColumns(2).create());
        createChildProperty(childPropertiesComposite, "Imię:", child.get().getName());
        createChildProperty(childPropertiesComposite, "Nazwisko:", child.get().getSurname());
        createChildProperty(childPropertiesComposite, "PESEL:", child.get().getPesel());
        createChildProperty(childPropertiesComposite, "Email rodzica:", child.get().getParentEmail());
        createChildProperty(childPropertiesComposite, "Grupa:", child.get().getChildrenGroup().getName());
        createActivityTableProperties(childPropertiesComposite, "Tablica aktywności:");
        createActivityTableStatistics(childPropertiesComposite, "Średnia ocen z aktywności");
    }

    private void createChildProperty(Composite parent, String propertyName, String propertyValue) {
        Label propertyNameLabel = new Label(parent, SWT.NONE);
        propertyNameLabel.setLayoutData(GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false).create());
        propertyNameLabel.setText(propertyName);
        Label propertyValueLabel = new Label(parent, SWT.RIGHT);
        propertyValueLabel.setLayoutData(GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).grab(true, false).create());
        propertyValueLabel.setText(propertyValue);
    }

    private void createActivityTableProperties(Composite parent, String propertyName) {
        Label activityTableLabel = new Label(parent, SWT.NONE);
        activityTableLabel.setLayoutData(GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false).create());
        activityTableLabel.setText(propertyName);
        if (child.get().getChildActivitiesTable().getActivitiesTableScheme() != null) {
            //            Label propertyValueLabel = new Label(parent, SWT.RIGHT | SWT.BORDER);
            Button propertyValueLabel = new Button(parent, SWT.RIGHT);
            propertyValueLabel.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).create());
            propertyValueLabel.setText(child.get().getChildActivitiesTable().getActivitiesTableScheme().getName());
            propertyValueLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseUp(MouseEvent e) {
                    applicationContext.getBean("ActivityTableComposite", shell, child);
                    dispose();
                    shell.layout(true, true);
                }
            });
        }
    }

    private void createActivityTableStatistics(Composite parent, String compositeName) {
        Label separator = new Label(parent, SWT.HORIZONTAL | SWT.SEPARATOR);
        separator.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.FILL).span(2, 1).create());
        Label label = new Label(parent, SWT.NONE);
        label.setText(compositeName);
        label.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).align(SWT.CENTER, SWT.CENTER).span(2, 1).create());
        if (child.get().getChildActivitiesTable().getActivitiesTableScheme() != null) {
            ChildActivitiesTableStatisticsComposite composite = (ChildActivitiesTableStatisticsComposite)
                    applicationContext.getBean("ActivitiesTableSchemeStatisticsComposite", parent, child);
            composite.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).span(2, 1).create());
        }
    }

    private Composite createDownControlsButtonsComposite(Composite parent) {
        Composite controlsButtonsComposite = new Composite(parent, SWT.NONE);
        controlsButtonsComposite.setLayoutData(GridDataFactory.swtDefaults().grab(false, true).align(SWT.CENTER, SWT.FILL).create());
        controlsButtonsComposite.setLayout(GridLayoutFactory.swtDefaults().numColumns(1).create());
        createEditButton(controlsButtonsComposite);
        createChangeGroupButton(controlsButtonsComposite);
        createSelectActivityTableButton(controlsButtonsComposite);
        createRemoveActivityTableButton(controlsButtonsComposite);
        createShowChartButton(controlsButtonsComposite);
        createRemoveButton(controlsButtonsComposite, this::removeChild,
                "Usunięte zostaną wszystkie dane tego dziecka, oceny tego dziecka oraz tablica aktywności przypisana do tego dziecka");
        return controlsButtonsComposite;
    }

    private void createRemoveActivityTableButton(Composite parent) {
        Button removeTable = new Button(parent, SWT.PUSH | SWT.WRAP);
        removeTable.setLayoutData(DEFAULT_CONTROL_BUTTON_FACTORY.create());
        removeTable.setText("Usuń tablicę aktywności");
        removeTable.setFont(FontDescriptor.createFrom(Fonts.DEFAULT_FONT_DATA).createFont(removeTable.getDisplay()));
        removeTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                if (MessageDialog.openConfirm(shell, "Potwierdzenie usunięcia",
                        "Usunięta zostanie ta tablica wraz ze wszystkimi ocenami" + System.lineSeparator() + "Czy chcesz kontynuować?")) {
                    child.set(childService.removeActivityTable(child.get()));
                    applicationContext.getBean(ChildComposite.class, shell, child);
                    dispose();
                    shell.layout(true, true);
                }
            }
        });
        removeTable.setEnabled(child.get().getChildActivitiesTable().getActivitiesTableScheme() != null);
    }

    private void createShowChartButton(Composite parent) {
        Button showChart = new Button(parent, SWT.PUSH);
        showChart.setLayoutData(DEFAULT_CONTROL_BUTTON_FACTORY.create());
        showChart.setText("Wykres ocen");
        showChart.setFont(FontDescriptor.createFrom(Fonts.DEFAULT_FONT_DATA).createFont(showChart.getDisplay()));
        showChart.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                applicationContext.getBean(ChildActivitiesTableChartComposite.class, shell, child);
                dispose();
                shell.layout(true, true);
            }
        });
        showChart.setEnabled(child.get().getChildActivitiesTable().getActivitiesTableScheme() != null);
    }

    private void createChangeGroupButton(Composite parent) {
        Button addGroupButton = new Button(parent, SWT.PUSH);
        addGroupButton.setLayoutData(DEFAULT_CONTROL_BUTTON_FACTORY.create());
        addGroupButton.setText("Zmień grupę");
        addGroupButton.setFont(FontDescriptor.createFrom(Fonts.DEFAULT_FONT_DATA).createFont(addGroupButton.getDisplay()));
        addGroupButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                changeGroup();
            }
        });
    }

    private void createSelectActivityTableButton(Composite parent) {
        Button selectActivityTableButton = new Button(parent, SWT.PUSH | SWT.WRAP);
        selectActivityTableButton.setLayoutData(DEFAULT_CONTROL_BUTTON_FACTORY.create());
        selectActivityTableButton.setText("Wybierz tablicę aktywności");
        selectActivityTableButton.setFont(FontDescriptor.createFrom(Fonts.DEFAULT_FONT_DATA).createFont(selectActivityTableButton.getDisplay()));
        selectActivityTableButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                selectActivityTable();
            }
        });
    }

    private void removeChild() {
        applicationContext.getBean(ChildrenGroupComposite.class, shell, childService.removeChild(child.get()));
        dispose();
        shell.layout(true, true);
    }

    private void createEditButton(Composite parent) {
        Button addGroupButton = new Button(parent, SWT.PUSH);
        addGroupButton.setLayoutData(DEFAULT_CONTROL_BUTTON_FACTORY.create());
        addGroupButton.setText("Edytuj");
        addGroupButton.setFont(FontDescriptor.createFrom(Fonts.DEFAULT_FONT_DATA).createFont(addGroupButton.getDisplay()));
        addGroupButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                editChild();
            }
        });
    }

    private void editChild() {
        ChildDialog dialog = new ChildDialog(shell, child.get().getName(), child.get().getSurname(), child.get().getPesel(), child.get().getParentEmail(),
                "Edytuj", child.get().getId());
        applicationContext.getAutowireCapableBeanFactory().autowireBean(dialog);
        if (Window.OK == dialog.open()) {
            child.set(childService.editChild(child.get(),
                    dialog.getName(),
                    dialog.getSurname(),
                    dialog.getPesel(),
                    dialog.getParentEmail()));
            for (Control control : childPropertiesComposite.getChildren()) {
                control.dispose();
            }
            label.setText(child.get().getName() + " " + child.get().getSurname());
            fillChildProperties(childPropertiesComposite);
            childPropertiesComposite.layout(true, true);
        }
    }

    private void changeGroup() {
        ChildrenGroupSelectorDialog dialog = new ChildrenGroupSelectorDialog(shell, child.get().getChildrenGroup());
        applicationContext.getAutowireCapableBeanFactory().autowireBean(dialog);
        if (Window.OK == dialog.open()) {
            child.set(childService.changeGroup(child.get(), dialog.getChildrenGroup()));
            for (Control control : childPropertiesComposite.getChildren()) {
                control.dispose();
            }
            label.setText(child.get().getName() + " " + child.get().getSurname());
            fillChildProperties(childPropertiesComposite);
            childPropertiesComposite.layout(true, true);
        }
    }

    private void selectActivityTable() {
        ActivitiesTableSchemeSelectorDialog dialog = new ActivitiesTableSchemeSelectorDialog(shell, child.get().getChildActivitiesTable().getActivitiesTableScheme());
        applicationContext.getAutowireCapableBeanFactory().autowireBean(dialog);
        if (Window.OK == dialog.open()) {
            child.set(childService.setActivityTable(child.get(), dialog.getActivitiesTableScheme()));
            applicationContext.getBean(ChildComposite.class, shell, child);
            dispose();
            shell.layout(true, true);
        }
    }

}

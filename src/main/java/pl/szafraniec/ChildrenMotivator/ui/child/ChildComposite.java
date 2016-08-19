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
import pl.szafraniec.ChildrenMotivator.model.ActivitiesTableScheme;
import pl.szafraniec.ChildrenMotivator.model.BehaviorTableDay;
import pl.szafraniec.ChildrenMotivator.model.Child;
import pl.szafraniec.ChildrenMotivator.model.ChildrenGroup;
import pl.szafraniec.ChildrenMotivator.model.TableCell;
import pl.szafraniec.ChildrenMotivator.repository.ChildRepository;
import pl.szafraniec.ChildrenMotivator.repository.ChildrenGroupRepository;
import pl.szafraniec.ChildrenMotivator.ui.AbstractMainComposite;
import pl.szafraniec.ChildrenMotivator.ui.Fonts;
import pl.szafraniec.ChildrenMotivator.ui.activities.dialog.ActivityTableSelectorDialog;
import pl.szafraniec.ChildrenMotivator.ui.activities.tableSchemes.ActivitiesTableSchemeStatisticsComposite;
import pl.szafraniec.ChildrenMotivator.ui.activities.tableSchemes.ChartComposite;
import pl.szafraniec.ChildrenMotivator.ui.child.dialog.EditChildDialog;
import pl.szafraniec.ChildrenMotivator.ui.groups.dialog.GroupSelectorDialog;
import pl.szafraniec.ChildrenMotivator.ui.groups.group.ChildrenGroupComposite;

import java.util.ArrayList;

@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ChildComposite extends AbstractMainComposite {
    // TODO add button for activity table

    @Autowired
    private ChildRepository childRepository;

    @Autowired
    private ChildrenGroupRepository childrenGroupRepository;

    private Child child;

    private Composite childPropertiesComposite;

    private Label label;

    public ChildComposite(Composite parent, Child child) {
        super(parent, SWT.NONE);
        this.child = child;
    }

    @Override
    protected void createTopPart() {
        Composite topPart = new Composite(this, SWT.NONE);
        topPart.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).equalWidth(false).create());
        topPart.setLayoutData(GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).create());

        label = createLabel(topPart, child.getName() + " " + child.getSurname());
        createTopControlsButtonsComposite(topPart);
    }

    private Composite createTopControlsButtonsComposite(Composite parent) {
        Composite controlsButtonsComposite = new Composite(parent, SWT.NONE);
        controlsButtonsComposite.setLayoutData(GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.TOP).create());
        controlsButtonsComposite.setLayout(GridLayoutFactory.swtDefaults().numColumns(1).create());

        createBackButton(controlsButtonsComposite, applicationContext, shell, ChildrenGroupComposite.class,
                () -> new Object[] { child.getChildrenGroup() });
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
        createChildProperty(childPropertiesComposite, "Imię:", child.getName());
        createChildProperty(childPropertiesComposite, "Nazwisko:", child.getSurname());
        createChildProperty(childPropertiesComposite, "PESEL:", child.getPesel());
        createChildProperty(childPropertiesComposite, "Email rodzica:", child.getParentEmail());
        createChildProperty(childPropertiesComposite, "Grupa:", child.getChildrenGroup().getName());
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
        if (child.getChildActivitiesTable().getActivitiesTableScheme() != null) {
            //            Label propertyValueLabel = new Label(parent, SWT.RIGHT | SWT.BORDER);
            Button propertyValueLabel = new Button(parent, SWT.RIGHT);
            propertyValueLabel.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).create());
            propertyValueLabel.setText(child.getChildActivitiesTable().getActivitiesTableScheme().getName());
            propertyValueLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseUp(MouseEvent e) {
                    applicationContext.getBean("ActivityTableComposite", shell, child.getChildActivitiesTable());
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
        ActivitiesTableSchemeStatisticsComposite composite = (ActivitiesTableSchemeStatisticsComposite)
                applicationContext.getBean("ActivitiesTableSchemeStatisticsComposite", parent, child.getChildActivitiesTable());
        composite.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).span(2, 1).create());
    }

    private Control createTableButton(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(DEFAULT_CONTROL_BUTTON_FACTORY.copy().grab(true, false).align(SWT.FILL,
                SWT.CENTER).create());
        composite.setLayout(GridLayoutFactory.fillDefaults().numColumns(3).create());
        Button tableButton = new Button(composite, SWT.WRAP);
        tableButton.setText(child.getChildActivitiesTable().getActivitiesTableScheme().getName());
        //tableButton.setFont(FontDescriptor.createFrom(Fonts.DEFAULT_FONT_DATA).createFont(tableButton.getDisplay()));

        tableButton.setLayoutData(DEFAULT_CONTROL_BUTTON_FACTORY.copy().grab(true, true).align(SWT.END,
                SWT.CENTER).create());

        return composite;
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
        Button addGroupButton = new Button(parent, SWT.PUSH | SWT.WRAP);
        addGroupButton.setLayoutData(DEFAULT_CONTROL_BUTTON_FACTORY.create());
        addGroupButton.setText("Usuń tablicę aktywności");
        addGroupButton.setFont(FontDescriptor.createFrom(Fonts.DEFAULT_FONT_DATA).createFont(addGroupButton.getDisplay()));
        addGroupButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                if (MessageDialog.openConfirm(shell, "Potwierdzenie usunięcia",
                        "Usunięta zostanie ta tablica wraz ze wszystkimi ocenami" + System.lineSeparator() + "Czy chcesz kontynuować?")) {
                    child.getChildActivitiesTable().setActivitiesTableScheme(null);
                    child.getChildActivitiesTable().setDays(new ArrayList<>());
                    child = childRepository.saveAndFlush(child);

                    applicationContext.getBean(ChildComposite.class, shell, child);
                    dispose();
                    shell.layout(true, true);
                }
            }
        });
    }

    private void createShowChartButton(Composite parent) {
        Button addGroupButton = new Button(parent, SWT.PUSH);
        addGroupButton.setLayoutData(DEFAULT_CONTROL_BUTTON_FACTORY.create());
        addGroupButton.setText("Wykres ocen");
        addGroupButton.setFont(FontDescriptor.createFrom(Fonts.DEFAULT_FONT_DATA).createFont(addGroupButton.getDisplay()));
        addGroupButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                applicationContext.getBean(ChartComposite.class, shell, child.getChildActivitiesTable());
                dispose();
                shell.layout(true, true);
            }
        });
        addGroupButton.setEnabled(child.getChildActivitiesTable().getActivitiesTableScheme() != null);
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
        ChildrenGroup group = child.getChildrenGroup();
        group.getChildren().remove(child);
        childRepository.delete(child);
        group.getBehaviorTable().getDays().stream().map(BehaviorTableDay::getGrades).forEach(gradeMap -> gradeMap.remove(child));
        group = childrenGroupRepository.saveAndFlush(group);
        applicationContext.getBean(ChildrenGroupComposite.class, shell, group);
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
        EditChildDialog dialog = new EditChildDialog(shell, child.getName(), child.getSurname(), child.getPesel(), child.getParentEmail(),
                "Edytuj");
        applicationContext.getAutowireCapableBeanFactory().autowireBean(dialog);
        if (Window.OK == dialog.open()) {
            child.setName(dialog.getName());
            child.setSurname(dialog.getSurname());
            child.setParentEmail(dialog.getParentEmail());
            child.setPesel(dialog.getPesel());
            child = childRepository.saveAndFlush(child);
            for (Control control : childPropertiesComposite.getChildren()) {
                control.dispose();
            }
            label.setText(child.getName() + " " + child.getSurname());
            fillChildProperties(childPropertiesComposite);
            childPropertiesComposite.layout(true, true);
        }
    }

    private void changeGroup() {
        GroupSelectorDialog dialog = new GroupSelectorDialog(shell, child.getChildrenGroup());
        applicationContext.getAutowireCapableBeanFactory().autowireBean(dialog);
        if (Window.OK == dialog.open()) {
            ChildrenGroup originalGroup = child.getChildrenGroup();
            ChildrenGroup newGroup = dialog.getChildrenGroup();
            child.setChildrenGroup(dialog.getChildrenGroup());
            newGroup.getChildren().add(child);
            newGroup.getBehaviorTable().getDays().forEach(day -> day.getGrades().put(child, TableCell.TableCellBuilder.create()));
            originalGroup.getChildren().remove(child);
            originalGroup.getBehaviorTable().getDays().stream().map(BehaviorTableDay::getGrades).forEach(
                    gradeMap -> gradeMap.remove(child));
            childrenGroupRepository.save(newGroup);
            child = childRepository.saveAndFlush(child);
            for (Control control : childPropertiesComposite.getChildren()) {
                control.dispose();
            }
            label.setText(child.getName() + " " + child.getSurname());
            fillChildProperties(childPropertiesComposite);
            childPropertiesComposite.layout(true, true);
        }
    }

    private void selectActivityTable() {
        ActivityTableSelectorDialog dialog = new ActivityTableSelectorDialog(shell, child.getChildActivitiesTable().getActivitiesTableScheme());
        applicationContext.getAutowireCapableBeanFactory().autowireBean(dialog);
        if (Window.OK == dialog.open()) {
            ActivitiesTableScheme scheme = dialog.getActivitiesTableScheme();
            ActivitiesTableScheme oldScheme = child.getChildActivitiesTable().getActivitiesTableScheme();
            if (oldScheme != null) {
                oldScheme.getListOfActivities()
                        .stream()
                        .filter(activity -> !scheme.getListOfActivities().contains(activity))
                        .forEach(activity -> child.getChildActivitiesTable()
                                .getDays()
                                .forEach(day -> day.getGrades().remove(activity)));
                scheme.getListOfActivities()
                        .stream()
                        .filter(activity -> !oldScheme.getListOfActivities().contains(activity))
                        .forEach(activity -> child.getChildActivitiesTable()
                                .getDays()
                                .forEach(day -> day.getGrades().put(activity, TableCell.TableCellBuilder.create())));
            }
            child.getChildActivitiesTable().setActivitiesTableScheme(scheme);


            child = childRepository.saveAndFlush(child);
            applicationContext.getBean(ChildComposite.class, shell, child);
            dispose();
            shell.layout(true, true);
        }
    }

}

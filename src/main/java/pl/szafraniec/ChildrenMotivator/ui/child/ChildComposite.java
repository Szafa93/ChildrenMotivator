package pl.szafraniec.ChildrenMotivator.ui.child;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.szafraniec.ChildrenMotivator.model.BehaviorTableDay;
import pl.szafraniec.ChildrenMotivator.model.Child;
import pl.szafraniec.ChildrenMotivator.model.ChildActivitiesTable;
import pl.szafraniec.ChildrenMotivator.model.ChildrenGroup;
import pl.szafraniec.ChildrenMotivator.model.TableCell;
import pl.szafraniec.ChildrenMotivator.repository.ChildRepository;
import pl.szafraniec.ChildrenMotivator.repository.ChildrenGroupRepository;
import pl.szafraniec.ChildrenMotivator.ui.AbstractMainComposite;
import pl.szafraniec.ChildrenMotivator.ui.Fonts;
import pl.szafraniec.ChildrenMotivator.ui.activities.dialog.ActivityTableSelectorDialog;
import pl.szafraniec.ChildrenMotivator.ui.activities.tableSchemes.ChartComposite;
import pl.szafraniec.ChildrenMotivator.ui.child.dialog.EditChildDialog;
import pl.szafraniec.ChildrenMotivator.ui.groups.dialog.GroupSelectorDialog;
import pl.szafraniec.ChildrenMotivator.ui.groups.group.ChildrenGroupComposite;

import java.util.stream.Collectors;

@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ChildComposite extends AbstractMainComposite {

    @Autowired
    private ChildRepository childRepository;

    @Autowired
    private ChildrenGroupRepository childrenGroupRepository;

    private Child child;

    private Composite childComposite;

    private ScrolledComposite scrolledComposite;

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
    protected void createDownPart() {
        Composite downPart = new Composite(this, SWT.NONE);
        downPart.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).create());
        downPart.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
        createChildComposite(downPart);
        createDownControlsButtonsComposite(downPart);
    }

    private Composite createChildComposite(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
        composite.setLayout(GridLayoutFactory.fillDefaults().numColumns(1).create());
        childPropertiesComposite = new Composite(composite, SWT.NONE);
        fillChildProperties(childPropertiesComposite);
        Label label = new Label(composite, SWT.NONE);
        label.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).align(SWT.CENTER, SWT.FILL).create());
        label.setText("Tablice aktywności");
        scrolledComposite = new ScrolledComposite(composite, SWT.V_SCROLL | SWT.BORDER);
        scrolledComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
        scrolledComposite.setExpandVertical(true);
        scrolledComposite.setExpandHorizontal(true);

        childComposite = new Composite(scrolledComposite, SWT.NONE);
        childComposite.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
        childComposite.setLayout(GridLayoutFactory.swtDefaults().numColumns(1).create());

        child.getActivitiesTableList().stream().map(table -> createTableButton(table, childComposite)).collect(Collectors.toList());

        childComposite.pack(true);
        scrolledComposite.setContent(childComposite);
        scrolledComposite.addControlListener(new ControlAdapter() {
            public void controlResized(ControlEvent e) {
                Rectangle r = scrolledComposite.getClientArea();
                scrolledComposite.setMinSize(childComposite.computeSize(r.width, SWT.DEFAULT));
            }
        });
        scrolledComposite.layout(true, true);
        return scrolledComposite;
    }

    private void fillChildProperties(Composite childPropertiesComposite) {
        childPropertiesComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.FILL).create());
        childPropertiesComposite.setLayout(GridLayoutFactory.fillDefaults().numColumns(2).create());
        createChildProperty(childPropertiesComposite, "Imię:", child.getName());
        createChildProperty(childPropertiesComposite, "Nazwisko:", child.getSurname());
        createChildProperty(childPropertiesComposite, "PESEL:", child.getPesel());
        createChildProperty(childPropertiesComposite, "Email rodzica:", child.getParentEmail());
        createChildProperty(childPropertiesComposite, "Grupa:", child.getChildrenGroup().getName());
    }

    private void createChildProperty(Composite parent, String propertyName, String propertyValue) {
        Label propertyNameLabel = new Label(parent, SWT.NONE);
        propertyNameLabel.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).create());
        propertyNameLabel.setText(propertyName);
        Label propertyValueLabel = new Label(parent, SWT.RIGHT);
        propertyValueLabel.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.END).grab(false, false).create());
        propertyValueLabel.setText(propertyValue);
    }

    private Control createTableButton(ChildActivitiesTable table, Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(DEFAULT_CONTROL_BUTTON_FACTORY.copy().grab(true, false).align(SWT.FILL,
                SWT.CENTER).create());
        composite.setLayout(GridLayoutFactory.fillDefaults().numColumns(3).create());
        Button tableButton = new Button(composite, SWT.WRAP);
        tableButton.setText(table.getActivitiesTableScheme().getName());
        tableButton.setFont(FontDescriptor.createFrom(Fonts.DEFAULT_FONT_DATA).createFont(tableButton.getDisplay()));

        tableButton.setLayoutData(DEFAULT_CONTROL_BUTTON_FACTORY.copy().grab(true, true).align(SWT.FILL,
                SWT.CENTER).create());

        tableButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                applicationContext.getBean("ActivityTableComposite", shell, table);
                dispose();
                shell.layout(true, true);
            }
        });

        Button chartButton = new Button(composite, SWT.NONE);
        chartButton.setImage(new Image(Display.getCurrent(), getClass().getClassLoader().getResource("icons/chart.png").getFile()));
        chartButton.setLayoutData(GridDataFactory.fillDefaults()
                .grab(false, true)
                .align(SWT.FILL, SWT.FILL)
                .create());
        chartButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                applicationContext.getBean(ChartComposite.class, shell, table);
                dispose();
                shell.layout(true, true);
            }
        });

        Button removeButton = new Button(composite, SWT.NONE);
        removeButton.setImage(new Image(Display.getCurrent(), getClass().getClassLoader().getResource("icons/remove.png").getFile()));
        removeButton.setLayoutData(GridDataFactory.fillDefaults()
                .grab(false, true)
                .align(SWT.FILL, SWT.FILL)
                .create());
        removeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                if (MessageDialog.openConfirm(shell, "Potwierdzenie usunięcia",
                        "Usunięta zostanie ta tablica wraz ze wszystkimi ocenami" + System.lineSeparator() + "Czy chcesz kontynuować?")) {
                    Child child = table.getChild();
                    child.getActivitiesTableList().remove(table);
                    child = childRepository.saveAndFlush(child);

                    applicationContext.getBean(ChildComposite.class, shell, child);
                    dispose();
                    shell.layout(true, true);
                }
            }
        });

        return composite;
    }

    private Composite createDownControlsButtonsComposite(Composite parent) {
        Composite controlsButtonsComposite = new Composite(parent, SWT.NONE);
        controlsButtonsComposite.setLayoutData(GridDataFactory.swtDefaults().grab(false, true).align(SWT.CENTER, SWT.FILL).create());
        controlsButtonsComposite.setLayout(GridLayoutFactory.swtDefaults().numColumns(1).create());
        createEditButton(controlsButtonsComposite);
        createChangeGroupButton(controlsButtonsComposite);
        createAddActivityTableButton(controlsButtonsComposite);
        createRemoveButton(controlsButtonsComposite, this::removeChild,
                "Usunięte zostaną wszystkie dane tego dziecka, oceny tego dziecka i oraz tablice aktywności przypisane do tego dziecka");
        return controlsButtonsComposite;
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

    private void createAddActivityTableButton(Composite parent) {
        Button addActibityTableButton = new Button(parent, SWT.PUSH | SWT.WRAP);
        addActibityTableButton.setLayoutData(DEFAULT_CONTROL_BUTTON_FACTORY.create());
        addActibityTableButton.setText("Dodaj tablicę aktywności");
        addActibityTableButton.setFont(FontDescriptor.createFrom(Fonts.DEFAULT_FONT_DATA).createFont(addActibityTableButton.getDisplay()));
        addActibityTableButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                addActivityTable();
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

    private void addActivityTable() {
        ActivityTableSelectorDialog dialog = new ActivityTableSelectorDialog(shell);
        applicationContext.getAutowireCapableBeanFactory().autowireBean(dialog);
        if (Window.OK == dialog.open()) {
            ChildActivitiesTable childActivitiesTable = ChildActivitiesTable.ChildActivitiesTableFactory.create(child,
                    dialog.getActivitiesTableScheme());
            child = childRepository.saveAndFlush(child);
            for (Control widget : childComposite.getChildren()) {
                widget.dispose();
            }
            child.getActivitiesTableList().stream().map(table -> createTableButton(table, childComposite)).collect(Collectors.toList());
            this.layout(true, true);

            Rectangle r = scrolledComposite.getClientArea();
            scrolledComposite.setMinSize(childComposite.computeSize(r.width, SWT.DEFAULT));
        }
    }

}

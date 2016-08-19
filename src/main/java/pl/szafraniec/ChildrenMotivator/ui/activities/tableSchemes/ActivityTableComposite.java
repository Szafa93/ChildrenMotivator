package pl.szafraniec.ChildrenMotivator.ui.activities.tableSchemes;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Widget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.szafraniec.ChildrenMotivator.model.Activity;
import pl.szafraniec.ChildrenMotivator.model.ChildActivitiesTable;
import pl.szafraniec.ChildrenMotivator.model.ChildActivitiesTableDay;
import pl.szafraniec.ChildrenMotivator.model.TableCell;
import pl.szafraniec.ChildrenMotivator.repository.ChildActivitiesTableRepository;
import pl.szafraniec.ChildrenMotivator.ui.AbstractMainComposite;
import pl.szafraniec.ChildrenMotivator.ui.DayOfWeekLocalization;
import pl.szafraniec.ChildrenMotivator.ui.Fonts;
import pl.szafraniec.ChildrenMotivator.ui.Images;
import pl.szafraniec.ChildrenMotivator.ui.child.ChildComposite;

import java.io.ByteArrayInputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Stream;

@Component("ActivityTableComposite")
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ActivityTableComposite extends AbstractMainComposite {
    protected static final GridData TABLE_CELL_LAYOUT_DATA = GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).hint(
            Images.IMAGE_WIDTH, Images.IMAGE_HEIGHT).create();

    protected static final GridData INSIDE_TABLE_CELL_LAYOUT_DATA = GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).grab(true,
            true).create();

    @Autowired
    private ChildActivitiesTableRepository childActivitiesTableRepository;

    protected ChildActivitiesTable table;

    private ScrolledComposite scrolledComposite;
    private Composite tableBehaviorComposite;

    protected LocalDate startDate;
    protected LocalDate endDate;
    protected Button forwardButton;

    public ActivityTableComposite(Composite parent, ChildActivitiesTable table) {
        super(parent, SWT.NONE);
        this.table = table;
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        if (today.equals(DayOfWeek.SATURDAY) || today.equals(DayOfWeek.SUNDAY)) {
            startDate = LocalDate.now().with(TemporalAdjusters.previous(DayOfWeek.MONDAY));
            endDate = LocalDate.now().with(TemporalAdjusters.previous(DayOfWeek.FRIDAY));
        } else {
            startDate = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            endDate = LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY));
        }
    }

    @Override
    protected void createTopPart() {
        Composite topPart = new Composite(this, SWT.NONE);
        topPart.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).equalWidth(false).create());
        topPart.setLayoutData(GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).create());

        createLabel(topPart, table.getActivitiesTableScheme().getName());
        createTopControlsButtonsComposite(topPart);
    }

    protected Composite createTopControlsButtonsComposite(Composite parent) {
        Composite controlsButtonsComposite = new Composite(parent, SWT.NONE);
        controlsButtonsComposite.setLayoutData(GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.TOP).create());
        controlsButtonsComposite.setLayout(GridLayoutFactory.swtDefaults().numColumns(1).create());

        createBackButton(controlsButtonsComposite, applicationContext, shell, ChildComposite.class,
                () -> new Object[] { table.getChild() });
        createEditButton(controlsButtonsComposite);
        return controlsButtonsComposite;
    }

    private void createEditButton(Composite parent) {
        Button editButton = new Button(parent, SWT.PUSH);
        editButton.setLayoutData(DEFAULT_CONTROL_BUTTON_FACTORY.create());
        editButton.setText("Edytuj");
        editButton.setFont(FontDescriptor.createFrom(Fonts.DEFAULT_FONT_DATA).createFont(editButton.getDisplay()));
        editButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                applicationContext.getBean("EditActivityTableComposite", shell, table);
                dispose();
                shell.layout(true, true);
            }
        });
    }

    @Override
    protected Composite createDownPart() {
        Composite downPart = new Composite(this, SWT.NONE);
        downPart.setLayout(GridLayoutFactory.swtDefaults().numColumns(3).create());
        downPart.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
        createBackButton(downPart);
        createTableBehaviorComposite(downPart);
        createForwardButton(downPart);
        return downPart;
    }

    protected Button createBackButton(Composite parent) {
        return createBackButton(parent, SWT.PUSH | SWT.WRAP);
    }

    protected Button createBackButton(Composite parent, int style) {
        Button backButton = new Button(parent, style);
        StringJoiner joiner = new StringJoiner(System.lineSeparator());
        "Poprzedni tydzień".chars().mapToObj(i -> (char) i).map(String::valueOf).forEachOrdered(joiner::add);
        backButton.setText(joiner.toString());
        backButton.setLayoutData(GridDataFactory.swtDefaults().hint(35, SWT.DEFAULT).align(SWT.CENTER, SWT.FILL).create());
        backButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                startDate = startDate.with(TemporalAdjusters.previous(DayOfWeek.MONDAY));
                endDate = endDate.with(TemporalAdjusters.previous(DayOfWeek.FRIDAY));
                forwardButton.setEnabled(canShowNextWeek());
                Stream.of(tableBehaviorComposite.getChildren()).forEach(Widget::dispose);
                fillTableBehaviorComposite(tableBehaviorComposite);
                scrolledComposite.layout(true, true);
            }
        });
        return backButton;
    }

    protected Button createForwardButton(Composite parent) {
        return createForwardButton(parent, SWT.PUSH | SWT.WRAP);
    }

    protected Button createForwardButton(Composite parent, int style) {
        forwardButton = new Button(parent, style);
        StringJoiner joiner = new StringJoiner(System.lineSeparator());
        "Następny tydzień".chars().mapToObj(i -> (char) i).map(String::valueOf).forEachOrdered(joiner::add);
        forwardButton.setText(joiner.toString());
        forwardButton.setLayoutData(GridDataFactory.swtDefaults().hint(35, SWT.DEFAULT).align(SWT.CENTER, SWT.FILL).create());
        forwardButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                startDate = startDate.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
                endDate = endDate.with(TemporalAdjusters.next(DayOfWeek.FRIDAY));
                forwardButton.setEnabled(canShowNextWeek());
                Stream.of(tableBehaviorComposite.getChildren()).forEach(Widget::dispose);
                fillTableBehaviorComposite(tableBehaviorComposite);
                scrolledComposite.layout(true, true);
            }
        });
        forwardButton.setEnabled(canShowNextWeek());
        return forwardButton;
    }

    private boolean canShowNextWeek() {
        LocalDate today = LocalDate.now();
        LocalDate lastPossibleDate;
        if (today.getDayOfWeek().equals(DayOfWeek.SATURDAY) || today.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
            lastPossibleDate = today.with(TemporalAdjusters.previous(DayOfWeek.FRIDAY));
        } else {
            lastPossibleDate = LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY));
        }
        return endDate.isBefore(lastPossibleDate);
    }

    protected Composite createTableBehaviorComposite(Composite parent) {
        return createTableBehaviorComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
    }

    protected Composite createTableBehaviorComposite(Composite parent, int style) {
        scrolledComposite = new ScrolledComposite(parent, style);
        scrolledComposite.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
        scrolledComposite.setExpandVertical(true);
        scrolledComposite.setExpandHorizontal(true);

        tableBehaviorComposite = new Composite(scrolledComposite, SWT.NONE);
        tableBehaviorComposite.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
        tableBehaviorComposite.setLayout(GridLayoutFactory.fillDefaults().numColumns(6).create());

        fillTableBehaviorComposite(tableBehaviorComposite);

        tableBehaviorComposite.pack(true);
        scrolledComposite.setContent(tableBehaviorComposite);
        scrolledComposite.addControlListener(new ControlAdapter() {
            public void controlResized(ControlEvent e) {
                scrolledComposite.setMinSize(tableBehaviorComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
            }
        });
        scrolledComposite.layout(true, true);
        return scrolledComposite;
    }

    protected void fillTableBehaviorComposite(Composite parent) {
        List<ChildActivitiesTableDay> days = table.getDays(startDate, endDate).orElseGet(() -> {
            table.generateDay(startDate, endDate);
            table = childActivitiesTableRepository.saveAndFlush(table);
            return table.getDays(startDate, endDate).get();
        });
        generateHeader(days, parent);
        table.getActivitiesTableScheme().getListOfActivities().stream().forEachOrdered(activity -> generateRowFor(activity, days, parent));
    }

    private void generateHeader(List<ChildActivitiesTableDay> days, Composite parent) {
        Composite cornerComposite = new Composite(parent, SWT.BORDER);
        cornerComposite.setLayoutData(TABLE_CELL_LAYOUT_DATA);
        cornerComposite.setLayout(GridLayoutFactory.fillDefaults().create());
        Label corner = new Label(cornerComposite, SWT.NONE);
        corner.setLayoutData(INSIDE_TABLE_CELL_LAYOUT_DATA);
        days.stream().forEachOrdered(day -> {
            Composite dayHeaderComposite = new Composite(parent, SWT.BORDER);
            dayHeaderComposite.setLayoutData(TABLE_CELL_LAYOUT_DATA);
            dayHeaderComposite.setLayout(GridLayoutFactory.fillDefaults().create());
            Label dayHeader = new Label(dayHeaderComposite, SWT.NONE);
            dayHeader.setText(DayOfWeekLocalization.getDayOfWeekName(day.getLocalDate().getDayOfWeek())
                    + System.lineSeparator()
                    + day.getLocalDate().toString());
            dayHeader.setFont(FontDescriptor.createFrom(Fonts.DEFAULT_FONT_DATA).createFont(dayHeader.getDisplay()));
            dayHeader.setAlignment(SWT.CENTER);
            dayHeader.setLayoutData(INSIDE_TABLE_CELL_LAYOUT_DATA);
        });
    }

    private void generateRowFor(Activity activity, List<ChildActivitiesTableDay> days, Composite parent) {
        Composite childRowHeaderComposite = new Composite(parent, SWT.BORDER);
        childRowHeaderComposite.setLayoutData(TABLE_CELL_LAYOUT_DATA);
        childRowHeaderComposite.setLayout(GridLayoutFactory.fillDefaults().create());
        Label childRowHeader = new Label(childRowHeaderComposite, SWT.NONE);
        childRowHeader.setLayoutData(INSIDE_TABLE_CELL_LAYOUT_DATA);
        childRowHeader.setText(activity.getName());
        childRowHeader.setFont(FontDescriptor.createFrom(Fonts.DEFAULT_FONT_DATA).createFont(childRowHeader.getDisplay()));
        childRowHeader.setToolTipText(activity.getName());
        Image imageData = new Image(getShell().getDisplay(), new ByteArrayInputStream(activity.getImage()));
        imageData = Images.resize(getShell().getDisplay(), imageData);
        childRowHeader.setImage(imageData);

        days.stream().forEachOrdered(day -> createTableCell(activity, day, parent));
    }

    protected void createTableCell(Activity activity, ChildActivitiesTableDay day, Composite parent) {
        TableCell grade = day.getGrades().get(activity);
        Composite dayGradeComposite = new Composite(parent, SWT.BORDER);
        dayGradeComposite.setLayoutData(TABLE_CELL_LAYOUT_DATA);
        dayGradeComposite.setLayout(GridLayoutFactory.fillDefaults().create());
        Label dayGrade = new Label(dayGradeComposite, SWT.NONE);
        dayGrade.setLayoutData(INSIDE_TABLE_CELL_LAYOUT_DATA);
        if (grade.getGradeScheme() == null) {
            dayGrade.setToolTipText("Brak oceny");
        } else {
            dayGrade.setToolTipText(grade.getGradeComment());
            Image imageData = new Image(getShell().getDisplay(), new ByteArrayInputStream(grade.getGradeScheme().getImage()));
            imageData = Images.resize(getShell().getDisplay(), imageData);
            dayGrade.setImage(imageData);
        }
    }
}
package pl.szafraniec.ChildrenMotivator.ui.behaviorTable;

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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Widget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.szafraniec.ChildrenMotivator.model.BehaviorTableDay;
import pl.szafraniec.ChildrenMotivator.model.Child;
import pl.szafraniec.ChildrenMotivator.model.ChildrenGroup;
import pl.szafraniec.ChildrenMotivator.model.Holder;
import pl.szafraniec.ChildrenMotivator.model.TableCell;
import pl.szafraniec.ChildrenMotivator.services.ChildrenGroupService;
import pl.szafraniec.ChildrenMotivator.ui.AbstractMainComposite;
import pl.szafraniec.ChildrenMotivator.ui.DayOfWeekLocalization;
import pl.szafraniec.ChildrenMotivator.ui.Fonts;
import pl.szafraniec.ChildrenMotivator.ui.ImageCanvas;
import pl.szafraniec.ChildrenMotivator.ui.Images;
import pl.szafraniec.ChildrenMotivator.ui.backgroundImage.BackgroundImageSelectorDialog;
import pl.szafraniec.ChildrenMotivator.ui.groups.group.ChildrenGroupComposite;

import java.io.ByteArrayInputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Stream;

@Component("BehaviorTableComposite")
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BehaviorTableComposite extends AbstractMainComposite {
    protected static final int MARGINS = 25;

    protected static final GridData TABLE_CELL_LAYOUT_DATA = GridDataFactory.fillDefaults()
            .align(SWT.FILL, SWT.FILL)
            .hint(Images.IMAGE_WIDTH, Images.IMAGE_HEIGHT)
            .create();

    protected static final GridData INSIDE_TABLE_CELL_LAYOUT_DATA = GridDataFactory.fillDefaults()
            .align(SWT.FILL, SWT.FILL)
            .grab(true, true)
            .create();

    protected static final GridData TEXT_INSIDE_TABLE_CELL_LAYOUT_DATA = GridDataFactory.fillDefaults()
            .align(SWT.CENTER, SWT.CENTER)
            .grab(true, true)
            .create();

    @Autowired
    private ChildrenGroupService childrenGroupService;

    protected ChildrenGroup childrenGroup;
    private ScrolledComposite scrolledComposite;
    private Composite tableBehaviorComposite;

    private LocalDate startDate;
    private LocalDate endDate;
    private Button forwardButton;
    private Image imageData;

    public BehaviorTableComposite(Composite parent, ChildrenGroup childrenGroup) {
        super(parent, SWT.NONE);
        this.childrenGroup = childrenGroup;
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

        createLabel(topPart, childrenGroup.getName());
        createTopControlsButtonsComposite(topPart);
    }

    protected Composite createTopControlsButtonsComposite(Composite parent) {
        Composite controlsButtonsComposite = new Composite(parent, SWT.NONE);
        controlsButtonsComposite.setLayoutData(GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.TOP).create());
        controlsButtonsComposite.setLayout(GridLayoutFactory.swtDefaults().numColumns(1).create());

        createBackButton(controlsButtonsComposite, applicationContext, shell, ChildrenGroupComposite.class,
                () -> new Object[] { childrenGroup });
        createSelectBackgroundImageButton(controlsButtonsComposite);
        return controlsButtonsComposite;
    }

    private void createSelectBackgroundImageButton(Composite parent) {
        Button saveButton = new Button(parent, SWT.PUSH);
        saveButton.setLayoutData(DEFAULT_CONTROL_BUTTON_FACTORY.create());
        saveButton.setText("Wybierz tło");
        saveButton.setFont(FontDescriptor.createFrom(Fonts.DEFAULT_FONT_DATA).createFont(saveButton.getDisplay()));
        saveButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                BackgroundImageSelectorDialog dialog = new BackgroundImageSelectorDialog(getShell(), childrenGroup.getBehaviorTable().getBackgroundImage());
                applicationContext.getAutowireCapableBeanFactory().autowireBean(dialog);
                if (Window.OK == dialog.open()) {
                    childrenGroup = childrenGroupService.setBackground(childrenGroup, dialog.getBackgroundImage());
                    imageData = null;
                    refreshBackground();
                }
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
        forwardButton = createForwardButton(downPart);
        return downPart;
    }

    private Button createBackButton(Composite parent) {
        Button backButton = new Button(parent, SWT.PUSH | SWT.WRAP);
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

    private Button createForwardButton(Composite parent) {
        Button forwardButton = new Button(parent, SWT.PUSH | SWT.WRAP);
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

    private Composite createTableBehaviorComposite(Composite parent) {
        scrolledComposite = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
        scrolledComposite.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
        scrolledComposite.setExpandVertical(true);
        scrolledComposite.setExpandHorizontal(true);

        tableBehaviorComposite = new Composite(scrolledComposite, SWT.NONE);
        tableBehaviorComposite.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
        tableBehaviorComposite.setLayout(GridLayoutFactory.fillDefaults().numColumns(6).create());
        tableBehaviorComposite.addControlListener(new ControlAdapter() {
            @Override
            public void controlResized(ControlEvent e) {
                refreshBackground();
            }
        });
        scrolledComposite.setBackgroundMode(SWT.INHERIT_FORCE);

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

    private void refreshBackground() {
        if (childrenGroup.getBehaviorTable().getBackgroundImage() != null) {
            if (imageData == null) {
                imageData = new Image(getShell().getDisplay(), new ByteArrayInputStream(childrenGroup.getBehaviorTable().getBackgroundImage().getImage()));
            }
            Rectangle clientArea = scrolledComposite.getClientArea();
            if (clientArea.width * clientArea.height > 0) {
                Image scaled = new Image(getDisplay(), clientArea.width, clientArea.height);
                GC gc = new GC(scaled);
                gc.setAntialias(SWT.ON);
                gc.setInterpolation(SWT.HIGH);
                gc.drawImage(imageData, 0, 0,
                        imageData.getImageData().width, imageData.getImageData().height, 0, 0, clientArea.width, clientArea.height);
                gc.dispose();
                scrolledComposite.setBackgroundImage(scaled);
            }
        }
    }

    private void fillTableBehaviorComposite(Composite parent) {
        Holder<ChildrenGroup> holder = Holder.of(childrenGroup);
        List<BehaviorTableDay> days = childrenGroupService.getDays(holder, startDate, endDate);
        childrenGroup = holder.get();
        generateHeader(days, parent);
        childrenGroup.getChildren().stream().forEachOrdered(child -> generateRowFor(child, days, parent));
    }

    private void generateHeader(List<BehaviorTableDay> days, Composite parent) {
        Composite cornerComposite = new Composite(parent, SWT.NONE);
        cornerComposite.setLayoutData(GridDataFactory
                .createFrom(TABLE_CELL_LAYOUT_DATA)
                .hint(Images.IMAGE_WIDTH + MARGINS, Images.IMAGE_HEIGHT + MARGINS)
                .create());
        cornerComposite.setLayout(GridLayoutFactory.fillDefaults().create());
        Label corner = new Label(cornerComposite, SWT.NONE);
        corner.setLayoutData(INSIDE_TABLE_CELL_LAYOUT_DATA);
        days.stream().forEachOrdered(day -> {
            Composite composite = new Composite(parent, SWT.NONE);
            composite.setLayout(GridLayoutFactory.fillDefaults().spacing(0, 0).create());
            composite.setLayoutData(GridDataFactory.fillDefaults().create());
            Composite dayHeaderComposite = new Composite(composite, SWT.BORDER);
            dayHeaderComposite.setLayoutData(TABLE_CELL_LAYOUT_DATA);
            dayHeaderComposite.setLayout(GridLayoutFactory.fillDefaults().create());
            Label dayHeader = new Label(dayHeaderComposite, SWT.WRAP);
            dayHeader.setText(DayOfWeekLocalization.getDayOfWeekName(day.getLocalDate().getDayOfWeek())
                    + System.lineSeparator()
                    + day.getLocalDate().toString());
            dayHeader.setFont(FontDescriptor.createFrom(Fonts.DEFAULT_FONT_DATA).createFont(dayHeader.getDisplay()));
            dayHeader.setAlignment(SWT.CENTER);
            dayHeader.setLayoutData(TEXT_INSIDE_TABLE_CELL_LAYOUT_DATA);
        });
    }

    private void generateRowFor(Child child, List<BehaviorTableDay> days, Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(GridLayoutFactory.fillDefaults().spacing(0, 0).create());
        composite.setLayoutData(GridDataFactory.fillDefaults().create());
        Composite childRowHeaderComposite = new Composite(composite, SWT.BORDER);
        childRowHeaderComposite.setLayoutData(TABLE_CELL_LAYOUT_DATA);
        childRowHeaderComposite.setLayout(GridLayoutFactory.fillDefaults().create());
        Label childRowHeader = new Label(childRowHeaderComposite, SWT.NONE);
        childRowHeader.setLayoutData(TEXT_INSIDE_TABLE_CELL_LAYOUT_DATA);
        childRowHeader.setText(String.format("%s\n%s", child.getName(), child.getSurname()));
        childRowHeader.setFont(FontDescriptor.createFrom(Fonts.DEFAULT_FONT_DATA).createFont(childRowHeader.getDisplay()));
        days.stream().forEachOrdered(day -> createTableCell(child, day, parent));
    }

    protected void createTableCell(Child child, BehaviorTableDay day, Composite parent) {
        TableCell grade = day.getGrades().get(child);
        Composite dayGradeComposite = new Composite(parent, SWT.BORDER);
        dayGradeComposite.setLayoutData(TABLE_CELL_LAYOUT_DATA);
        dayGradeComposite.setLayout(GridLayoutFactory.fillDefaults().create());
        ImageCanvas canvas = new ImageCanvas(dayGradeComposite, SWT.NO_REDRAW_RESIZE);
        canvas.setLayoutData(INSIDE_TABLE_CELL_LAYOUT_DATA);
        if (grade.getGradeScheme() == null) {
            canvas.setToolTipText("Brak oceny");
        } else {
            canvas.setToolTipText(grade.getGradeComment());
            canvas.setImage(grade.getGradeScheme().getImage());
        }
    }
}

package pl.szafraniec.ChildrenMotivator.ui.activities.tableSchemes;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.szafraniec.ChildrenMotivator.model.Activity;
import pl.szafraniec.ChildrenMotivator.model.ChildActivitiesTable;
import pl.szafraniec.ChildrenMotivator.model.ChildActivitiesTableDay;
import pl.szafraniec.ChildrenMotivator.model.GradeScheme;
import pl.szafraniec.ChildrenMotivator.model.TableCell;
import pl.szafraniec.ChildrenMotivator.repository.ChildActivitiesTableRepository;
import pl.szafraniec.ChildrenMotivator.ui.AbstractMainComposite;
import pl.szafraniec.ChildrenMotivator.ui.DoubleBufferedChartPanel;
import pl.szafraniec.ChildrenMotivator.ui.child.ChildComposite;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ChartComposite extends AbstractMainComposite {

    @Autowired
    private ChildActivitiesTableRepository childActivitiesTableRepository;

    private ChildActivitiesTable table;

    private Composite chartComposite;

    private Frame chartPanel;

    private LocalDate startDate;
    private LocalDate endDate;
    private Button forwardButton;

    private ChartPanel jfreeChartPanel;

    public ChartComposite(Composite parent, ChildActivitiesTable table) {
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

        createLabel(topPart, table.getChild().getName() + " " + table.getChild().getSurname());
        createTopControlsButtonsComposite(topPart);
    }

    private Composite createTopControlsButtonsComposite(Composite parent) {
        Composite controlsButtonsComposite = new Composite(parent, SWT.NONE);
        controlsButtonsComposite.setLayoutData(GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.TOP).create());
        controlsButtonsComposite.setLayout(GridLayoutFactory.swtDefaults().numColumns(1).create());

        createBackButton(controlsButtonsComposite, applicationContext, shell, ChildComposite.class,
                () -> new Object[] { table.getChild() });
        return controlsButtonsComposite;
    }

    @Override
    protected void createDownPart() {
        Composite downPart = new Composite(this, SWT.NONE);
        downPart.setLayout(GridLayoutFactory.swtDefaults().numColumns(3).create());
        downPart.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
        createBackButton(downPart);
        createTableBehaviorComposite(downPart);
        forwardButton = createForwardButton(downPart);
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
                Stream.of(chartComposite.getChildren()).forEach(Widget::dispose);
                chartPanel.dispose();
                fillChart(chartComposite);
                chartComposite.layout(true, true);
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
                Stream.of(chartComposite.getChildren()).forEach(Widget::dispose);
                chartPanel.dispose();
                fillChart(chartComposite);
                chartComposite.layout(true, true);
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
        chartComposite = new Composite(parent, SWT.BORDER | SWT.EMBEDDED | SWT.NO_BACKGROUND);
        chartComposite.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
        chartComposite.setLayout(GridLayoutFactory.fillDefaults().numColumns(1).create());

        fillChart(chartComposite);

        //        chartComposite.addControlListener(new ControlAdapter() {
        //            @Override
        //            public void controlResized(ControlEvent e) {
        //                //                chartPanel
        //                chartPanel.invalidate();
        //                chartPanel.validate();
        //                chartPanel.repaint();
        //                chartPanel.setSize(chartComposite.getSize().x, chartComposite.getSize().y);
        //                chartPanel.pack();
        //            }
        //        });
        return chartComposite;
    }

    private void fillChart(Composite chartComposite) {
        List<ChildActivitiesTableDay> days = table.getDays(startDate, endDate).orElseGet(() -> {
            table.generateDay(startDate, endDate);
            table = childActivitiesTableRepository.saveAndFlush(table);
            return table.getDays(startDate, endDate).get();
        });

        OptionalDouble maxGrade = days.stream()
                .map(ChildActivitiesTableDay::getGrades)
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .map(Map.Entry::getValue)
                .map(TableCell::getGradeScheme)
                .filter(gradeScheme -> gradeScheme != null)
                .mapToDouble(GradeScheme::getValue)
                .map(value -> value * 1.1)
                .max();

        CategoryDataset dataset = new GradeChartCategoryDataset(startDate, endDate,
                table.getActivitiesTableScheme()
                        .getListOfActivities()
                        .stream()
                        .collect(Collectors.toMap(
                                Activity::getName,
                                activity -> days.stream().collect(
                                        Collectors.toMap(ChildActivitiesTableDay::getLocalDate,
                                                day -> Optional.ofNullable(day.getGrades().get(activity).getGradeScheme()).map(GradeScheme::getValue)
                                        ))
                        )));

        JFreeChart chart = ChartFactory.createLineChart(table.getActivitiesTableScheme().getName(), "Dzień", "Ocena", dataset);
        ValueAxis rangeAxis = chart.getCategoryPlot().getRangeAxis();
        rangeAxis.setAutoRange(false);
        rangeAxis.setLowerBound(0);
        rangeAxis.setUpperBound(maxGrade.orElse(1));
        ((LineAndShapeRenderer) chart.getCategoryPlot().getRenderer()).setBaseShapesVisible(true);
        Color backgroundColor = chartComposite.getBackground();
        chartPanel = SWT_AWT.new_Frame(chartComposite);
        chartPanel.setLayout(new BorderLayout());
        chartPanel.setBackground(new java.awt.Color(backgroundColor.getRed(),
                backgroundColor.getGreen(),
                backgroundColor.getBlue()));
        jfreeChartPanel = new DoubleBufferedChartPanel(chart, false);
        chartPanel.add(jfreeChartPanel, BorderLayout.CENTER);
        chartPanel.pack();
    }
}
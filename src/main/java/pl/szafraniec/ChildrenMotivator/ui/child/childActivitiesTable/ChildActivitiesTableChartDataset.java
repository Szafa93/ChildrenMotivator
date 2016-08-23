package pl.szafraniec.ChildrenMotivator.ui.child.childActivitiesTable;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.AbstractDataset;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ChildActivitiesTableChartDataset extends AbstractDataset implements CategoryDataset {
    // Row -> Activity
    // Column -> Date
    // Cross -> grade

    private LocalDate startDate;

    private LocalDate endDate;

    private Map<String, Map<LocalDate, Optional<Integer>>> grades;

    private List<LocalDate> localDates;

    private List<String> rowKeys;

    public ChildActivitiesTableChartDataset(LocalDate startDate, LocalDate endDate, Map<String, Map<LocalDate, Optional<Integer>>> grades) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.grades = grades;
        localDates = Stream.iterate(startDate, date -> date.plusDays(1)).limit(5).collect(Collectors.toList());
        rowKeys = grades.keySet().stream().collect(Collectors.toList());
    }

    @Override
    public Comparable getColumnKey(int row) {
        return localDates.get(row);
    }

    @Override
    public int getColumnIndex(Comparable key) {
        return localDates.indexOf(key);
    }

    @Override
    public List getColumnKeys() {
        return localDates;
    }

    @Override
    public Comparable getRowKey(int column) {
        return rowKeys.get(column);
    }

    @Override
    public int getRowIndex(Comparable key) {
        return rowKeys.indexOf(key);
    }

    @Override
    public List getRowKeys() {
        return rowKeys;
    }

    @Override
    public Number getValue(Comparable rowKey, Comparable columnKey) {
        if (!grades.containsKey(rowKey)) {
            return null;
        }
        return grades.get(rowKey).get(columnKey).orElse(null);
    }

    @Override
    public int getRowCount() {
        return rowKeys.size();
    }

    @Override
    public int getColumnCount() {
        return localDates.size();
    }

    @Override
    public Number getValue(int row, int column) {
        return getValue(getRowKey(row), getColumnKey(column));
    }
}

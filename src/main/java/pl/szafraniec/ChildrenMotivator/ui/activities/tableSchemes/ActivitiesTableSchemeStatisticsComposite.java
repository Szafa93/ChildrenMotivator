package pl.szafraniec.ChildrenMotivator.ui.activities.tableSchemes;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.szafraniec.ChildrenMotivator.model.Child;
import pl.szafraniec.ChildrenMotivator.model.ChildActivitiesTableDay;
import pl.szafraniec.ChildrenMotivator.model.GradeScheme;
import pl.szafraniec.ChildrenMotivator.model.Holder;
import pl.szafraniec.ChildrenMotivator.model.TableCell;
import pl.szafraniec.ChildrenMotivator.services.ChildService;

import java.util.List;
import java.util.OptionalDouble;

@Component("ActivitiesTableSchemeStatisticsComposite")
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ActivitiesTableSchemeStatisticsComposite extends ActivityTableComposite {

    @Autowired
    private ChildService childService;

    public ActivitiesTableSchemeStatisticsComposite(Composite parent, Holder<Child> child) {
        super(parent, child);
    }

    @Override
    protected void createTopPart() {
    }

    @Override
    protected Composite createDownPart() {
        Composite composite = super.createDownPart();
        composite.setLayout(GridLayoutFactory.swtDefaults().numColumns(3).margins(0, 0).create());
        return composite;
    }

    @Override
    protected Button createBackButton(Composite parent) {
        return super.createBackButton(parent, SWT.ARROW | SWT.LEFT);
    }

    @Override
    protected Button createForwardButton(Composite parent) {
        return super.createForwardButton(parent, SWT.ARROW | SWT.RIGHT);
    }

    protected Composite createTableBehaviorComposite(Composite parent) {
        return createTableBehaviorComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
    }

    protected void fillTableBehaviorComposite(Composite parent) {
        parent.setLayout(GridLayoutFactory.fillDefaults().numColumns(2).create());
        Label label = new Label(parent, SWT.NONE);
        label.setText(startDate.toString() + " - " + endDate.toString());
        label.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).align(SWT.CENTER, SWT.CENTER).span(2, 1).create());
        List<ChildActivitiesTableDay> days = childService.getDays(child, startDate, endDate);
        child.get().getChildActivitiesTable().getActivitiesTableScheme().getListOfActivities().stream().forEach(activity -> {
            OptionalDouble avg = days.stream()
                    .map(ChildActivitiesTableDay::getGrades)
                    .map(map -> map.get(activity))
                    .map(TableCell::getGradeScheme)
                    .filter(grade -> grade != null)
                    .mapToInt(GradeScheme::getValue)
                    .average();
            createActivityProperty(parent, activity.getName(), avg.isPresent() ? Double.toString(avg.getAsDouble()) : "Brak ocen");
        });
    }

    private void createActivityProperty(Composite parent, String propertyName, String propertyValue) {
        Label propertyNameLabel = new Label(parent, SWT.NONE);
        propertyNameLabel.setLayoutData(GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).grab(false, false).create());
        propertyNameLabel.setText(propertyName);
        Label propertyValueLabel = new Label(parent, SWT.RIGHT);
        propertyValueLabel.setLayoutData(GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).grab(true, false).create());
        propertyValueLabel.setText(propertyValue);
    }
}

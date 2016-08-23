package pl.szafraniec.ChildrenMotivator.ui.groups.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.function.Consumer;

public class SelectDatesForReportsDialog extends Dialog {

    private LocalDate start;

    private LocalDate end;

    public SelectDatesForReportsDialog(Shell parentShell) {
        super(parentShell);
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        if (today.equals(DayOfWeek.SATURDAY) || today.equals(DayOfWeek.SUNDAY)) {
            start = LocalDate.now().with(TemporalAdjusters.previous(DayOfWeek.MONDAY));
            end = LocalDate.now().with(TemporalAdjusters.previous(DayOfWeek.FRIDAY));
        } else {
            start = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            end = LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY));
        }
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText("Wyślij statystyki");
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite dialogArea = (Composite) super.createDialogArea(parent);
        createChildProperties(dialogArea);
        return dialogArea;
    }

    private void createChildProperties(Composite parent) {
        Composite groupPropertiesComposite = new Composite(parent, SWT.NONE);
        groupPropertiesComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
        groupPropertiesComposite.setLayout(GridLayoutFactory.fillDefaults().numColumns(2).create());
        createProperty(groupPropertiesComposite, "Od: ", start, newValue -> start = newValue);
        createProperty(groupPropertiesComposite, "Do: ", end, newValue -> end = newValue);
    }

    private void createProperty(Composite parent, String propertyName, LocalDate initialValue, Consumer<LocalDate> modifyValueConsumer) {
        Label propertyNameLabel = new Label(parent, SWT.NONE);
        propertyNameLabel.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(false, false).create());
        propertyNameLabel.setText(propertyName);
        DateTime dateTime = new DateTime(parent, SWT.BORDER | SWT.RIGHT | SWT.DATE);
        dateTime.setDate(initialValue.getYear(), initialValue.getMonthValue(), initialValue.getDayOfMonth());
        dateTime.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).create());
        dateTime.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                modifyValueConsumer.accept(LocalDate.of(dateTime.getYear(), dateTime.getMonth(), dateTime.getDay()));
                getButton(OK).setEnabled(checkConstrains());
            }
        });
    }

    private boolean checkConstrains() {
        return start != null && end != null && start.isBefore(end);
    }

    public LocalDate getStart() {
        return start;
    }

    public LocalDate getEnd() {
        return end;
    }
}

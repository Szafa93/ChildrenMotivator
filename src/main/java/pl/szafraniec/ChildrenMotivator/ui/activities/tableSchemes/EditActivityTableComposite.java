package pl.szafraniec.ChildrenMotivator.ui.activities.tableSchemes;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.szafraniec.ChildrenMotivator.model.Activity;
import pl.szafraniec.ChildrenMotivator.model.ChildActivitiesTable;
import pl.szafraniec.ChildrenMotivator.model.ChildActivitiesTableDay;
import pl.szafraniec.ChildrenMotivator.model.TableCell;
import pl.szafraniec.ChildrenMotivator.services.ChildService;
import pl.szafraniec.ChildrenMotivator.ui.Fonts;
import pl.szafraniec.ChildrenMotivator.ui.Images;
import pl.szafraniec.ChildrenMotivator.ui.gradesSchemes.dialog.GradeSelectorDialog;

import java.io.ByteArrayInputStream;

@Component("EditActivityTableComposite")
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class EditActivityTableComposite extends ActivityTableComposite {

    //    @Autowired
    //    private ChildRepository childRepository;

    //    @Autowired
    //    private GradeSchemeRepository gradeSchemeRepository;

    @Autowired
    private ChildService childService;

    public EditActivityTableComposite(Composite parent, ChildActivitiesTable childActivitiesTable) {
        super(parent, childActivitiesTable, () -> {
        });
    }

    @Override
    protected Composite createTopControlsButtonsComposite(Composite parent) {
        Composite controlsButtonsComposite = new Composite(parent, SWT.NONE);
        controlsButtonsComposite.setLayoutData(GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.TOP).create());
        controlsButtonsComposite.setLayout(GridLayoutFactory.swtDefaults().numColumns(1).create());

        createSaveButton(controlsButtonsComposite);

        return controlsButtonsComposite;
    }

    private void createSaveButton(Composite parent) {
        Button saveButton = new Button(parent, SWT.PUSH);
        saveButton.setLayoutData(DEFAULT_CONTROL_BUTTON_FACTORY.create());
        saveButton.setText("Zapisz");
        saveButton.setFont(FontDescriptor.createFrom(Fonts.DEFAULT_FONT_DATA).createFont(saveButton.getDisplay()));
        saveButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                table.set(childService.recalculateGrades(table.get().getChild()).getChildActivitiesTable());
                // recalculate grades

                applicationContext.getBean("ActivityTableComposite", shell, table.get(), (Runnable) () -> {
                });
                dispose();
                shell.layout(true, true);
            }
        });
    }

    @Override
    protected void createTableCell(Activity activity, ChildActivitiesTableDay day, Composite parent) {
        TableCell grade = day.getGrades().get(activity);
        Composite tableCellComposite = new Composite(parent, SWT.BORDER);
        tableCellComposite.setLayoutData(TABLE_CELL_LAYOUT_DATA);
        tableCellComposite.setLayout(GridLayoutFactory.fillDefaults().create());
        Label tableCell = new Label(tableCellComposite, SWT.NONE);
        fillTableCellLabel(tableCell, grade);
        MouseListener mouseListener = new MouseAdapter() {

            @Override
            public void mouseUp(MouseEvent e) {
                GradeSelectorDialog dialog = new GradeSelectorDialog(Display.getCurrent().getActiveShell(), grade.getGradeScheme(),
                        grade.getGradeComment());
                applicationContext.getAutowireCapableBeanFactory().autowireBean(dialog);
                if (Window.OK == dialog.open()) {
                    grade.setGradeScheme(dialog.getGradeScheme());
                    grade.setGradeComment(dialog.getGradeComment());
                    fillTableCellLabel(tableCell, grade);
                    tableCellComposite.layout(true, true);
                }
            }
        };
        tableCell.addMouseListener(mouseListener);
        tableCellComposite.addMouseListener(mouseListener);
    }

    private void fillTableCellLabel(Label tableCell, TableCell grade) {
        tableCell.setLayoutData(INSIDE_TABLE_CELL_LAYOUT_DATA);
        if (grade.getGradeScheme() == null) {
            tableCell.setToolTipText("Brak oceny");
            tableCell.setText("Brak oceny");
        } else {
            tableCell.setToolTipText(grade.getGradeComment());
            Image imageData = new Image(getShell().getDisplay(), new ByteArrayInputStream(grade.getGradeScheme().getImage()));
            imageData = Images.resize(getShell().getDisplay(), imageData);
            tableCell.setImage(imageData);
        }
    }
}

package pl.szafraniec.ChildrenMotivator.ui.child.childActivitiesTable;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.szafraniec.ChildrenMotivator.model.Activity;
import pl.szafraniec.ChildrenMotivator.model.Child;
import pl.szafraniec.ChildrenMotivator.model.ChildActivitiesTableDay;
import pl.szafraniec.ChildrenMotivator.model.Holder;
import pl.szafraniec.ChildrenMotivator.model.TableCell;
import pl.szafraniec.ChildrenMotivator.services.ChildService;
import pl.szafraniec.ChildrenMotivator.ui.Fonts;
import pl.szafraniec.ChildrenMotivator.ui.backgroundImages.dialogs.BackgroundImageSelectorDialog;
import pl.szafraniec.ChildrenMotivator.ui.gradesSchemes.dialogs.GradeSchemeSelectorDialog;
import pl.szafraniec.ChildrenMotivator.ui.utils.ImageCanvas;

@Component("EditActivityTableComposite")
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class EditChildActivitiesTableComposite extends ChildActivitiesTableComposite {

    @Autowired
    private ChildService childService;

    public EditChildActivitiesTableComposite(Composite parent, Holder<Child> child) {
        super(parent, child);
    }

    @Override
    protected Composite createTopControlsButtonsComposite(Composite parent) {
        Composite controlsButtonsComposite = new Composite(parent, SWT.NONE);
        controlsButtonsComposite.setLayoutData(GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.TOP).create());
        controlsButtonsComposite.setLayout(GridLayoutFactory.swtDefaults().numColumns(1).create());

        createSaveButton(controlsButtonsComposite);
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
                BackgroundImageSelectorDialog dialog = new BackgroundImageSelectorDialog(getShell(), getTable().getBackgroundImage());
                applicationContext.getAutowireCapableBeanFactory().autowireBean(dialog);
                if (Window.OK == dialog.open()) {
                    getTable().setBackgroundImage(dialog.getBackgroundImage());
                    imageData = null;
                    refreshBackground();
                }
            }
        });
    }

    private void createSaveButton(Composite parent) {
        Button saveButton = new Button(parent, SWT.PUSH);
        saveButton.setLayoutData(DEFAULT_CONTROL_BUTTON_FACTORY.create());
        saveButton.setText("Zapisz");
        saveButton.setFont(FontDescriptor.createFrom(Fonts.DEFAULT_FONT_DATA).createFont(saveButton.getDisplay()));
        saveButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                child.set(childService.editGrades(child.get()));
                applicationContext.getBean("ActivityTableComposite", shell, child);
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
        ImageCanvas tableCell = new ImageCanvas(tableCellComposite, SWT.NONE);
        fillTableCellLabel(tableCell, grade);
        MouseListener mouseListener = new MouseAdapter() {

            @Override
            public void mouseUp(MouseEvent e) {
                GradeSchemeSelectorDialog dialog = new GradeSchemeSelectorDialog(Display.getCurrent().getActiveShell(), grade.getGradeScheme(),
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

    private void fillTableCellLabel(ImageCanvas tableCell, TableCell grade) {
        tableCell.setLayoutData(INSIDE_TABLE_CELL_LAYOUT_DATA);
        if (grade.getGradeScheme() == null) {
            tableCell.setToolTipText("Brak oceny");
            tableCell.setText("Brak oceny");
        } else {
            tableCell.setToolTipText(grade.getGradeComment());
            tableCell.setImage(grade.getGradeScheme().getImage());
        }
    }
}

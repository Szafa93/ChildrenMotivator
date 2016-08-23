package pl.szafraniec.ChildrenMotivator.ui.activitiesTableSchemes.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.springframework.beans.factory.annotation.Autowired;
import pl.szafraniec.ChildrenMotivator.model.ActivitiesTableScheme;
import pl.szafraniec.ChildrenMotivator.services.ActivitiesTableSchemeService;
import pl.szafraniec.ChildrenMotivator.ui.Fonts;
import pl.szafraniec.ChildrenMotivator.ui.Images;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ActivityTableSelectorDialog extends Dialog {

    @Autowired
    private ActivitiesTableSchemeService activitiesTableSchemeService;

    private ActivitiesTableScheme activitiesTableScheme;

    private List<Button> activitiesTableSchemesButtons;

    public ActivityTableSelectorDialog(Shell shell, ActivitiesTableScheme activitiesTableScheme) {
        super(shell);
        this.activitiesTableScheme = activitiesTableScheme;
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText("Wybierz schemat");
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite dialogArea = (Composite) super.createDialogArea(parent);
        createGroupProperties(dialogArea);
        return dialogArea;
    }

    private void createGroupProperties(Composite parent) {
        Composite groupPropertiesComposite = new Composite(parent, SWT.NONE);
        groupPropertiesComposite.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).create());
        groupPropertiesComposite.setLayout(GridLayoutFactory.swtDefaults().numColumns(1).create());

        Composite gradeSchemeComposite = createGradeSchemeSelectorComposite(groupPropertiesComposite);
        gradeSchemeComposite.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());

        groupPropertiesComposite.layout(true, true);
    }

    private Composite createGradeSchemeSelectorComposite(Composite parent) {
        ScrolledComposite scrolledComposite = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
        scrolledComposite.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
        scrolledComposite.setExpandVertical(true);
        scrolledComposite.setExpandHorizontal(true);

        Composite activitiesComposite = new Composite(scrolledComposite, SWT.NONE);
        activitiesComposite.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
        activitiesComposite.setLayout(GridLayoutFactory.swtDefaults().spacing(25, 25).numColumns(4).create());

        activitiesTableSchemesButtons = activitiesTableSchemeService.findAll().stream().map(
                gradeScheme -> createTableSchemeButton(gradeScheme, activitiesComposite, button -> {
                    activitiesTableSchemesButtons.stream().filter(b -> !b.equals(button)).forEach(b -> b.setSelection(false));
                })).collect(Collectors.toList());

        activitiesComposite.pack(true);
        scrolledComposite.setContent(activitiesComposite);
        scrolledComposite.addControlListener(new ControlAdapter() {
            public void controlResized(ControlEvent e) {
                Rectangle r = scrolledComposite.getClientArea();
                scrolledComposite.setMinSize(activitiesComposite.computeSize(r.width, SWT.DEFAULT));
            }
        });
        scrolledComposite.layout(true, true);

        return scrolledComposite;
    }

    private Button createTableSchemeButton(ActivitiesTableScheme activitiesTableScheme, Composite parent, Consumer<Button> uncheckAll) {
        Button gradeSchemeButton = new Button(parent, SWT.CHECK | SWT.BORDER | SWT.WRAP);
        gradeSchemeButton.setToolTipText(activitiesTableScheme.getName());
        gradeSchemeButton.setText(activitiesTableScheme.getName());
        gradeSchemeButton.setFont(FontDescriptor.createFrom(Fonts.DEFAULT_FONT_DATA).createFont(gradeSchemeButton.getDisplay()));
        gradeSchemeButton.setData(activitiesTableScheme);
        gradeSchemeButton.setLayoutData(GridDataFactory.swtDefaults().hint(Images.IMAGE_WIDTH, Images.IMAGE_HEIGHT).create());
        gradeSchemeButton.setSelection(activitiesTableScheme.equals(this.activitiesTableScheme));

        gradeSchemeButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (gradeSchemeButton.getSelection()) {
                    uncheckAll.accept(gradeSchemeButton);
                    ActivityTableSelectorDialog.this.activitiesTableScheme = activitiesTableScheme;
                } else {
                    ActivityTableSelectorDialog.this.activitiesTableScheme = null;
                }
                getButton(Dialog.OK).setEnabled(checkConstrains());
            }
        });
        return gradeSchemeButton;
    }

    private boolean checkConstrains() {
        return activitiesTableScheme != null;
    }

    @Override
    protected Control createButtonBar(Composite parent) {
        Control buttonBar = super.createButtonBar(parent);
        getButton(Dialog.OK).setEnabled(checkConstrains());
        return buttonBar;
    }

    public ActivitiesTableScheme getActivitiesTableScheme() {
        return activitiesTableScheme;
    }
}

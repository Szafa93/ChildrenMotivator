package pl.szafraniec.ChildrenMotivator.ui.gradesSchemes.dialog;

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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.springframework.beans.factory.annotation.Autowired;
import pl.szafraniec.ChildrenMotivator.model.GradeScheme;
import pl.szafraniec.ChildrenMotivator.repository.GradeSchemeRepository;
import pl.szafraniec.ChildrenMotivator.ui.Fonts;
import pl.szafraniec.ChildrenMotivator.ui.Images;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class GradeSelectorDialog extends Dialog {

    @Autowired
    private GradeSchemeRepository gradeSchemeRepository;

    private GradeScheme gradeScheme;

    private List<Button> gradeSchemeButtons;
    private String gradeComment;

    public GradeSelectorDialog(Shell shell, GradeScheme gradeScheme, String gradeComment) {
        super(shell);
        this.gradeScheme = gradeScheme;
        if (gradeComment == null) {
            this.gradeComment = "";
        } else {
            this.gradeComment = gradeComment;
        }
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText("Wybierz ocenę");
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
        groupPropertiesComposite.setLayout(GridLayoutFactory.swtDefaults().numColumns(3).create());

        Label labelComment = new Label(groupPropertiesComposite, SWT.NONE);
        labelComment.setText("Komentarz: ");
        Text textComment = new Text(groupPropertiesComposite, SWT.BORDER);
        textComment.setText(gradeComment);
        textComment.setLayoutData(GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.FILL).create());
        textComment.addModifyListener(event -> {
            gradeComment = textComment.getText();
            getButton(Dialog.OK).setEnabled(checkConstrains());
        });

        Composite gradeSchemeComposite = createGradeSchemeSelectorComposite(groupPropertiesComposite);
        gradeSchemeComposite.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).span(2, 1).create());

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

        gradeSchemeButtons = gradeSchemeRepository.findAll().stream().map(
                gradeScheme -> createGradeSchemeButton(gradeScheme, activitiesComposite, button -> {
                    gradeSchemeButtons.stream().filter(b -> !b.equals(button)).forEach(b -> b.setSelection(false));
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

    private Button createGradeSchemeButton(GradeScheme gradeScheme, Composite parent, Consumer<Button> uncheckAll) {
        Button gradeSchemeButton = new Button(parent, SWT.CHECK | SWT.BORDER);
        gradeSchemeButton.setToolTipText(Integer.toString(gradeScheme.getValue()));
        gradeSchemeButton.setFont(FontDescriptor.createFrom(Fonts.DEFAULT_FONT_DATA).createFont(gradeSchemeButton.getDisplay()));
        // TODO uncommect this if you want image as button
        Image imageData = new Image(getShell().getDisplay(), new ByteArrayInputStream(gradeScheme.getImage()));
        imageData = Images.resize(getShell().getDisplay(), imageData);
        gradeSchemeButton.setBackgroundImage(imageData);
        gradeSchemeButton.setData(gradeScheme);
        gradeSchemeButton.setLayoutData(GridDataFactory.swtDefaults().hint(Images.IMAGE_WIDTH, Images.IMAGE_HEIGHT).create());
        gradeSchemeButton.setSelection(gradeScheme.equals(this.gradeScheme));

        gradeSchemeButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (gradeSchemeButton.getSelection()) {
                    uncheckAll.accept(gradeSchemeButton);
                    GradeSelectorDialog.this.gradeScheme = gradeScheme;
                } else {
                    GradeSelectorDialog.this.gradeScheme = null;
                }
                getButton(Dialog.OK).setEnabled(checkConstrains());
            }
        });
        return gradeSchemeButton;
    }

    private boolean checkConstrains() {
        return true;
    }

    @Override
    protected Control createButtonBar(Composite parent) {
        Control buttonBar = super.createButtonBar(parent);
        getButton(Dialog.OK).setEnabled(checkConstrains());
        return buttonBar;
    }

    public GradeScheme getGradeScheme() {
        return gradeScheme;
    }

    public String getGradeComment() {
        return gradeComment;
    }
}

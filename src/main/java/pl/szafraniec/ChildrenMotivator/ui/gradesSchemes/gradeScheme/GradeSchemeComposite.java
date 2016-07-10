package pl.szafraniec.ChildrenMotivator.ui.gradesSchemes.gradeScheme;

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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.szafraniec.ChildrenMotivator.model.GradeScheme;
import pl.szafraniec.ChildrenMotivator.repository.GradeSchemeRepository;
import pl.szafraniec.ChildrenMotivator.ui.AbstractMainComposite;
import pl.szafraniec.ChildrenMotivator.ui.Fonts;
import pl.szafraniec.ChildrenMotivator.ui.Images;
import pl.szafraniec.ChildrenMotivator.ui.gradesSchemes.GradesSchemesComposite;
import pl.szafraniec.ChildrenMotivator.ui.gradesSchemes.dialog.EditGradeSchemeDialog;

import java.io.ByteArrayInputStream;

@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GradeSchemeComposite extends AbstractMainComposite {

    @Autowired
    private GradeSchemeRepository gradeSchemeRepository;

    private GradeScheme gradeScheme;

    private Composite gradeSchemeComposite;

    private ScrolledComposite scrolledComposite;

    private Label label;

    private Label image;

    public GradeSchemeComposite(Composite parent, GradeScheme gradeScheme) {
        super(parent, SWT.NONE);
        this.gradeScheme = gradeScheme;
    }

    @Override
    protected void createTopPart() {
        Composite topPart = new Composite(this, SWT.NONE);
        topPart.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).equalWidth(false).create());
        topPart.setLayoutData(GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).create());

        label = createLabel(topPart, Integer.toString(gradeScheme.getValue()));
        createTopControlsButtonsComposite(topPart);
    }

    private Composite createTopControlsButtonsComposite(Composite parent) {
        Composite controlsButtonsComposite = new Composite(parent, SWT.NONE);
        controlsButtonsComposite.setLayoutData(GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.TOP).create());
        controlsButtonsComposite.setLayout(GridLayoutFactory.swtDefaults().numColumns(1).create());

        createBackButton(controlsButtonsComposite, applicationContext, shell, GradesSchemesComposite.class);
        return controlsButtonsComposite;
    }

    @Override
    protected void createDownPart() {
        Composite downPart = new Composite(this, SWT.NONE);
        downPart.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).create());
        downPart.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
        createGradeSchemeComposite(downPart);
        createDownControlsButtonsComposite(downPart);
    }

    private Composite createGradeSchemeComposite(Composite parent) {
        scrolledComposite = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
        scrolledComposite.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
        scrolledComposite.setExpandVertical(true);
        scrolledComposite.setExpandHorizontal(true);

        gradeSchemeComposite = new Composite(scrolledComposite, SWT.NONE);
        gradeSchemeComposite.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
        gradeSchemeComposite.setLayout(GridLayoutFactory.swtDefaults().numColumns(1).create());

        image = new Label(gradeSchemeComposite, SWT.WRAP | SWT.BORDER);
        Image imageData = new Image(getShell().getDisplay(), new ByteArrayInputStream(gradeScheme.getImage()));
        imageData = Images.resize(getShell().getDisplay(), imageData);
        image.setImage(imageData);
        image.setLayoutData(GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.CENTER).grab(true, true).minSize(Images.IMAGE_WIDTH,
                Images.IMAGE_HEIGHT).hint(Images.IMAGE_WIDTH, Images.IMAGE_HEIGHT).create());
        image.pack(true);

        gradeSchemeComposite.pack(true);
        scrolledComposite.setContent(gradeSchemeComposite);
        scrolledComposite.addControlListener(new ControlAdapter() {
            public void controlResized(ControlEvent e) {
                Rectangle r = scrolledComposite.getClientArea();
                scrolledComposite.setMinSize(gradeSchemeComposite.computeSize(r.width, SWT.DEFAULT));
            }
        });
        scrolledComposite.layout(true, true);
        return gradeSchemeComposite;
    }

    private Composite createDownControlsButtonsComposite(Composite parent) {
        Composite controlsButtonsComposite = new Composite(parent, SWT.NONE);
        controlsButtonsComposite.setLayoutData(GridDataFactory.swtDefaults().grab(false, true).align(SWT.CENTER, SWT.FILL).create());
        controlsButtonsComposite.setLayout(GridLayoutFactory.swtDefaults().numColumns(1).create());

        createEditButton(controlsButtonsComposite);
        createRemoveButton(controlsButtonsComposite, this::removeActivity);
        return controlsButtonsComposite;
    }

    private void createEditButton(Composite parent) {
        Button addGroupButton = new Button(parent, SWT.PUSH);
        addGroupButton.setLayoutData(DEFAULT_CONTROL_BUTTON_FACTORY);
        addGroupButton.setText("Edytuj");
        addGroupButton.setFont(FontDescriptor.createFrom(Fonts.DEFAULT_FONT_DATA).createFont(addGroupButton.getDisplay()));
        addGroupButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                editActivity();
            }
        });
    }

    private void removeActivity() {
        gradeSchemeRepository.delete(gradeScheme);
        gradeSchemeRepository.flush();

        applicationContext.getBean(GradesSchemesComposite.class, shell);
        dispose();
        shell.layout(true, true);
    }

    private void editActivity() {
        EditGradeSchemeDialog dialog = new EditGradeSchemeDialog(shell, gradeScheme.getValue(), gradeScheme.getImage(),
                "Edytuj schemat oceny");
        if (Window.OK == dialog.open()) {
            editActivity(dialog.getGradeValue(), dialog.getImageByte());

            scrolledComposite.layout(true, true);
        }
    }

    private void editActivity(int gradeValue, byte[] imageByte) {
        gradeScheme.setValue(gradeValue);
        gradeScheme.setImage(imageByte);
        gradeScheme = gradeSchemeRepository.saveAndFlush(gradeScheme);
        label.setText(Integer.toString(gradeValue));
        Image imageData = new Image(getShell().getDisplay(), new ByteArrayInputStream(gradeScheme.getImage()));
        imageData = Images.resize(getShell().getDisplay(), imageData);
        image.setImage(imageData);
    }
}

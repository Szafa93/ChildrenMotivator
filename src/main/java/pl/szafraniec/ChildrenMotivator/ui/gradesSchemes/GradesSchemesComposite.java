package pl.szafraniec.ChildrenMotivator.ui.gradesSchemes;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.RowDataFactory;
import org.eclipse.jface.layout.RowLayoutFactory;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
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
import pl.szafraniec.ChildrenMotivator.ui.gradesSchemes.dialog.EditGradeSchemeDialog;
import pl.szafraniec.ChildrenMotivator.ui.gradesSchemes.gradeScheme.GradeSchemeComposite;
import pl.szafraniec.ChildrenMotivator.ui.start.StartComposite;

import java.io.ByteArrayInputStream;
import java.util.stream.Collectors;

@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GradesSchemesComposite extends AbstractMainComposite {

    @Autowired
    private GradeSchemeRepository gradeSchemeRepository;

    private Composite gradeSchemesComposite;

    private ScrolledComposite scrolledComposite;

    public GradesSchemesComposite(Composite parent) {
        super(parent, SWT.NONE);
    }

    @Override
    protected void createTopPart() {
        Composite topPart = new Composite(this, SWT.NONE);
        topPart.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).equalWidth(false).create());
        topPart.setLayoutData(GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).create());

        createLabel(topPart, "Możliwe oceny");
        createControlsButtonsComposite(topPart);
    }

    private Composite createControlsButtonsComposite(Composite parent) {
        Composite controlsButtonsComposite = new Composite(parent, SWT.NONE);
        controlsButtonsComposite.setLayoutData(GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.TOP).create());
        controlsButtonsComposite.setLayout(GridLayoutFactory.swtDefaults().numColumns(1).create());

        createBackButton(controlsButtonsComposite, applicationContext, shell, StartComposite.class);
        createAddGradeShemeButton(controlsButtonsComposite);
        return controlsButtonsComposite;
    }

    private void createAddGradeShemeButton(Composite parent) {
        Button addGradeSchemeButton = new Button(parent, SWT.PUSH);
        addGradeSchemeButton.setLayoutData(DEFAULT_CONTROL_BUTTON_FACTORY.create());
        addGradeSchemeButton.setText("Dodaj ocenę");
        addGradeSchemeButton.setFont(FontDescriptor.createFrom(Fonts.DEFAULT_FONT_DATA).createFont(addGradeSchemeButton.getDisplay()));
        addGradeSchemeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                addGradeScheme();
            }
        });
    }

    private void addGradeScheme() {
        EditGradeSchemeDialog dialog = new EditGradeSchemeDialog(Display.getCurrent().getActiveShell());
        applicationContext.getAutowireCapableBeanFactory().autowireBean(dialog);
        if (Window.OK == dialog.open()) {
            GradeScheme gradeScheme = addGradeScheme(dialog.getGradeValue(), dialog.getImageByte());
            createGradeSchemeButton(gradeScheme, gradeSchemesComposite);
            scrolledComposite.layout(true, true);
        }
    }

    private GradeScheme addGradeScheme(int value, byte[] image) {
        GradeScheme gradeScheme = GradeScheme.GradeSchemeFactory.create(value, image);
        return gradeSchemeRepository.saveAndFlush(gradeScheme);
    }

    @Override
    protected Composite createDownPart() {
        Composite downPart = new Composite(this, SWT.NONE);
        downPart.setLayout(GridLayoutFactory.swtDefaults().numColumns(1).create());
        downPart.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
        createGradeSchemesComposite(downPart);
        return downPart;
    }

    private Composite createGradeSchemesComposite(Composite parent) {
        scrolledComposite = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
        scrolledComposite.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
        scrolledComposite.setExpandVertical(true);
        scrolledComposite.setExpandHorizontal(true);

        gradeSchemesComposite = new Composite(scrolledComposite, SWT.NONE);
        gradeSchemesComposite.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
        gradeSchemesComposite.setLayout(RowLayoutFactory.swtDefaults().pack(false).spacing(25).wrap(true).type(SWT.HORIZONTAL).create());

        gradeSchemeRepository.findAll().stream().map(gradeScheme -> createGradeSchemeButton(gradeScheme, gradeSchemesComposite)).collect(
                Collectors.toList());

        gradeSchemesComposite.pack(true);
        scrolledComposite.setContent(gradeSchemesComposite);
        scrolledComposite.addControlListener(new ControlAdapter() {
            public void controlResized(ControlEvent e) {
                Rectangle r = scrolledComposite.getClientArea();
                scrolledComposite.setMinSize(gradeSchemesComposite.computeSize(r.width, SWT.DEFAULT));
            }
        });
        scrolledComposite.layout(true, true);
        return gradeSchemesComposite;
    }

    private Control createGradeSchemeButton(GradeScheme gradeScheme, Composite parent) {
        Label activityButton = new Label(parent, SWT.WRAP | SWT.BORDER);
        activityButton.setText(Integer.toString(gradeScheme.getValue()));
        activityButton.setToolTipText(Integer.toString(gradeScheme.getValue()));
        activityButton.setFont(FontDescriptor.createFrom(Fonts.DEFAULT_FONT_DATA).createFont(activityButton.getDisplay()));
        // TODO uncommect this if you want image as button
        Image imageData = new Image(getShell().getDisplay(), new ByteArrayInputStream(gradeScheme.getImage()));
        imageData = Images.resize(getShell().getDisplay(), imageData);
        activityButton.setImage(imageData);

        activityButton.setLayoutData(RowDataFactory.swtDefaults().hint(Images.IMAGE_WIDTH, Images.IMAGE_HEIGHT).create());
        activityButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                applicationContext.getBean(GradeSchemeComposite.class, shell, gradeScheme);
                dispose();
                shell.layout(true, true);
            }
        });
        return activityButton;
    }
}
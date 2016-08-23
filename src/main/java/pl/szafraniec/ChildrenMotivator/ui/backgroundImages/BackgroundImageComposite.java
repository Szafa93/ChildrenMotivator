package pl.szafraniec.ChildrenMotivator.ui.backgroundImages;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.szafraniec.ChildrenMotivator.model.BackgroundImage;
import pl.szafraniec.ChildrenMotivator.services.BackgroundImageService;
import pl.szafraniec.ChildrenMotivator.ui.AbstractMainComposite;
import pl.szafraniec.ChildrenMotivator.ui.Fonts;
import pl.szafraniec.ChildrenMotivator.ui.backgroundImages.dialogs.BackgroundImageDialog;
import pl.szafraniec.ChildrenMotivator.ui.gradesSchemes.GradesSchemesComposite;

import java.io.ByteArrayInputStream;

@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BackgroundImageComposite extends AbstractMainComposite {

    @Autowired
    private BackgroundImageService backgroundImageService;

    private BackgroundImage backgroundImage;

    private Composite backgroundImageComposite;

    private Label label;

    private Canvas image;

    private Image imageData;

    public BackgroundImageComposite(Composite parent, BackgroundImage backgroundImage) {
        super(parent, SWT.NONE);
        this.backgroundImage = backgroundImage;
    }

    @Override
    protected void createTopPart() {
        Composite topPart = new Composite(this, SWT.NONE);
        topPart.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).equalWidth(false).create());
        topPart.setLayoutData(GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).create());

        label = createLabel(topPart, backgroundImage.getName());
        createTopControlsButtonsComposite(topPart);
    }

    private Composite createTopControlsButtonsComposite(Composite parent) {
        Composite controlsButtonsComposite = new Composite(parent, SWT.NONE);
        controlsButtonsComposite.setLayoutData(GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.TOP).create());
        controlsButtonsComposite.setLayout(GridLayoutFactory.swtDefaults().numColumns(1).create());

        createBackButton(controlsButtonsComposite, applicationContext, shell, BackgroundImagesComposite.class);
        return controlsButtonsComposite;
    }

    @Override
    protected Composite createDownPart() {
        Composite downPart = new Composite(this, SWT.NONE);
        downPart.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).create());
        downPart.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
        createBackgroundImageComposite(downPart);
        createDownControlsButtonsComposite(downPart);
        return downPart;
    }

    private Composite createBackgroundImageComposite(Composite parent) {
        backgroundImageComposite = new Composite(parent, SWT.NONE);
        backgroundImageComposite.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
        backgroundImageComposite.setLayout(GridLayoutFactory.swtDefaults().numColumns(1).create());

        image = new Canvas(backgroundImageComposite, SWT.WRAP | SWT.BORDER);
        image.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).create());
        image.addPaintListener(e -> {
            if (imageData == null) {
                imageData = new Image(getShell().getDisplay(), new ByteArrayInputStream(backgroundImage.getImage()));
            }
            e.gc.setAntialias(SWT.ON);
            e.gc.setInterpolation(SWT.HIGH);
            e.gc.drawImage(imageData, 0, 0, imageData.getBounds().width, imageData.getBounds().height, 0, 0, e.width, e.height);
        });

        backgroundImageComposite.pack(true);
        return backgroundImageComposite;
    }

    private Composite createDownControlsButtonsComposite(Composite parent) {
        Composite controlsButtonsComposite = new Composite(parent, SWT.NONE);
        controlsButtonsComposite.setLayoutData(GridDataFactory.swtDefaults().grab(false, true).align(SWT.CENTER, SWT.FILL).create());
        controlsButtonsComposite.setLayout(GridLayoutFactory.swtDefaults().numColumns(1).create());

        createEditButton(controlsButtonsComposite);
        Button removeButton = createRemoveButton(controlsButtonsComposite, this::removeBackgroundImage);
        removeButton.setEnabled(backgroundImageService.canRemove(backgroundImage));
        return controlsButtonsComposite;
    }

    private void createEditButton(Composite parent) {
        Button addGroupButton = new Button(parent, SWT.PUSH);
        addGroupButton.setLayoutData(DEFAULT_CONTROL_BUTTON_FACTORY.create());
        addGroupButton.setText("Edytuj");
        addGroupButton.setFont(FontDescriptor.createFrom(Fonts.DEFAULT_FONT_DATA).createFont(addGroupButton.getDisplay()));
        addGroupButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                editBackgroundImage();
            }
        });
    }

    private void removeBackgroundImage() {
        backgroundImageService.remove(backgroundImage);
        applicationContext.getBean(GradesSchemesComposite.class, shell);
        dispose();
        shell.layout(true, true);
    }

    private void editBackgroundImage() {
        BackgroundImageDialog dialog = new BackgroundImageDialog(shell, backgroundImage.getName(), backgroundImage.getImage(),
                "Edytuj tło");
        applicationContext.getAutowireCapableBeanFactory().autowireBean(dialog);
        if (Window.OK == dialog.open()) {
            editBackgroundImage(dialog.getName(), dialog.getImageByte());
        }
    }

    private void editBackgroundImage(String name, byte[] imageByte) {
        backgroundImage = backgroundImageService.edit(backgroundImage, name, imageByte);
        imageData = null;
        image.redraw();
    }
}


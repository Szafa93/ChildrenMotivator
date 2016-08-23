package pl.szafraniec.ChildrenMotivator.ui.backgroundImages.dialogs;

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
import org.eclipse.swt.widgets.Shell;
import org.springframework.beans.factory.annotation.Autowired;
import pl.szafraniec.ChildrenMotivator.model.BackgroundImage;
import pl.szafraniec.ChildrenMotivator.services.BackgroundImageService;
import pl.szafraniec.ChildrenMotivator.ui.Fonts;
import pl.szafraniec.ChildrenMotivator.ui.Images;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class BackgroundImageSelectorDialog extends Dialog {

    @Autowired
    private BackgroundImageService backgroundImageService;

    private BackgroundImage backgroundImage;

    private List<Button> backgroundImageButtons;

    public BackgroundImageSelectorDialog(Shell shell, BackgroundImage backgroundImage) {
        super(shell);
        this.backgroundImage = backgroundImage;
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText("Wybierz tło");
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

        Composite backgroundImageComposite = createBackgroundImageSelectorComposite(groupPropertiesComposite);
        backgroundImageComposite.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).span(2, 1).create());

        groupPropertiesComposite.layout(true, true);
    }

    private Composite createBackgroundImageSelectorComposite(Composite parent) {
        ScrolledComposite scrolledComposite = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
        scrolledComposite.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
        scrolledComposite.setExpandVertical(true);
        scrolledComposite.setExpandHorizontal(true);

        Composite activitiesComposite = new Composite(scrolledComposite, SWT.NONE);
        activitiesComposite.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
        activitiesComposite.setLayout(GridLayoutFactory.swtDefaults().spacing(25, 25).numColumns(4).create());

        backgroundImageButtons = backgroundImageService.findAll().stream().map(
                backgroundImage -> createBackgroundImageButton(backgroundImage, activitiesComposite, button -> {
                    backgroundImageButtons.stream().filter(b -> !b.equals(button)).forEach(b -> b.setSelection(false));
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

    private Button createBackgroundImageButton(BackgroundImage backgroundImage, Composite parent, Consumer<Button> uncheckAll) {
        Button backgroundImageButton = new Button(parent, SWT.CHECK | SWT.BORDER);
        backgroundImageButton.setToolTipText(backgroundImage.getName());
        backgroundImageButton.setFont(FontDescriptor.createFrom(Fonts.DEFAULT_FONT_DATA).createFont(backgroundImageButton.getDisplay()));
        // TODO uncommect this if you want image as button
        Image imageData = new Image(getShell().getDisplay(), new ByteArrayInputStream(backgroundImage.getImage()));
        imageData = Images.resize(getShell().getDisplay(), imageData);
        backgroundImageButton.setBackgroundImage(imageData);
        backgroundImageButton.setData(backgroundImage);
        backgroundImageButton.setLayoutData(GridDataFactory.swtDefaults().hint(Images.IMAGE_WIDTH, Images.IMAGE_HEIGHT).create());
        backgroundImageButton.setSelection(backgroundImage.equals(this.backgroundImage));

        backgroundImageButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (backgroundImageButton.getSelection()) {
                    uncheckAll.accept(backgroundImageButton);
                    BackgroundImageSelectorDialog.this.backgroundImage = backgroundImage;
                } else {
                    BackgroundImageSelectorDialog.this.backgroundImage = null;
                }
                getButton(Dialog.OK).setEnabled(checkConstrains());
            }
        });
        return backgroundImageButton;
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

    public BackgroundImage getBackgroundImage() {
        return backgroundImage;
    }
}

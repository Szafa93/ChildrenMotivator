package pl.szafraniec.ChildrenMotivator.ui.backgroundImages.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import pl.szafraniec.ChildrenMotivator.ui.Images;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BackgroundImageDialog extends Dialog {
    private String name;
    private String shellName;
    private byte[] fileData;
    private Canvas image;
    private Image imageData;

    public BackgroundImageDialog(Shell shell, String name, byte[] fileData, String shellName) {
        super(shell);
        this.name = name;
        this.fileData = fileData;
        this.shellName = shellName;
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(shellName);
        shell.setMinimumSize(240, 325);
    }

    @Override
    protected boolean isResizable() {
        return true;
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
        groupPropertiesComposite.setLayout(GridLayoutFactory.fillDefaults().numColumns(2).create());

        Label labelName = new Label(groupPropertiesComposite, SWT.NONE);
        labelName.setLayoutData(GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).create());
        labelName.setText("Nazwa tła: ");
        Text textName = new Text(groupPropertiesComposite, SWT.BORDER);
        textName.setLayoutData(GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.FILL).create());
        textName.setText(name);
        textName.addModifyListener(event -> {
            name = textName.getText();
            getButton(Dialog.OK).setEnabled(checkConstrains());
        });

        Label labelImage = new Label(groupPropertiesComposite, SWT.NONE);
        labelImage.setLayoutData(GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).create());
        labelImage.setText("Obrazek: ");
        Button buttonImage = new Button(groupPropertiesComposite, SWT.PUSH);
        buttonImage.setLayoutData(GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.FILL).create());
        buttonImage.setText("Wybierz plik");
        buttonImage.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                FileDialog fileDialog = new FileDialog(getShell(), SWT.OPEN);
                fileDialog.setText("Open");
                String[] filterExt = Images.FILE_EXTENSIONS;
                fileDialog.setFilterExtensions(filterExt);
                String selected = fileDialog.open();
                if (selected != null) {
                    try {
                        fileData = Files.readAllBytes(Paths.get(selected));
                        imageData = null;
                        image.redraw();
                    } catch (IOException e1) {

                    }
                }
                getButton(Dialog.OK).setEnabled(checkConstrains());
                groupPropertiesComposite.layout(true, true);
            }
        });

        image = new Canvas(groupPropertiesComposite, SWT.BORDER);
        image.setLayoutData(GridDataFactory.swtDefaults()
                .span(2, 1)
                .minSize(Images.IMAGE_WIDTH, Images.IMAGE_HEIGHT)
                .align(SWT.FILL, SWT.FILL)
                .grab(true, true)
                .create());

        image.addPaintListener(e -> {
            if (fileData != null) {
                if (imageData == null) {
                    imageData = new Image(getShell().getDisplay(), new ByteArrayInputStream(fileData));
                }
                e.gc.setAntialias(SWT.ON);
                e.gc.setInterpolation(SWT.HIGH);
                e.gc.drawImage(imageData, 0, 0, imageData.getBounds().width, imageData.getBounds().height, 0, 0, e.width, e.height);
            }
        });
        groupPropertiesComposite.layout(true, true);
    }

    private boolean checkConstrains() {
        return name.trim().length() != 0 && fileData != null;
    }

    @Override
    protected Control createButtonBar(Composite parent) {
        Control buttonBar = super.createButtonBar(parent);
        getButton(Dialog.OK).setEnabled(checkConstrains());
        return buttonBar;
    }

    public String getName() {
        return name;
    }

    public byte[] getImageByte() {
        return fileData;
    }
}

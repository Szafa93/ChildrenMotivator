package pl.szafraniec.ChildrenMotivator.ui.activities.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
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

public class EditActivityDialog extends Dialog {
    private String activityName;
    private String shellName;
    private byte[] fileData;
    private Label image;

    public EditActivityDialog(Shell shell) {
        this(shell, "", null, "Dodaj aktywność");
    }

    public EditActivityDialog(Shell shell, String activityName, byte[] fileData, String shellName) {
        super(shell);
        this.activityName = activityName;
        this.fileData = fileData;
        this.shellName = shellName;
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(shellName);
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
        labelName.setText("Nazwa aktywności: ");
        Text textName = new Text(groupPropertiesComposite, SWT.BORDER);
        textName.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
        textName.setText(activityName);
        textName.addModifyListener(event -> {
            activityName = textName.getText();
            getButton(Dialog.OK).setEnabled(checkConstrains());
        });

        Label labelImage = new Label(groupPropertiesComposite, SWT.NONE);
        labelImage.setLayoutData(GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).create());
        labelImage.setText("Obrazek: ");
        Button buttonImage = new Button(groupPropertiesComposite, SWT.PUSH);
        buttonImage.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
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
                        Image imageData = new Image(getShell().getDisplay(), new ByteArrayInputStream(fileData));
                        imageData = Images.resize(getShell().getDisplay(), imageData);
                        image.setImage(imageData);
                    } catch (IOException e1) {

                    }
                }
                getButton(Dialog.OK).setEnabled(checkConstrains());
                groupPropertiesComposite.layout(true, true);
            }
        });

        image = new Label(groupPropertiesComposite, SWT.BORDER);
        image.setLayoutData(GridDataFactory.swtDefaults()
                .span(2, 1)
                .align(SWT.CENTER, SWT.CENTER)
                .hint(Images.IMAGE_WIDTH, Images.IMAGE_HEIGHT)
                .minSize(Images.IMAGE_WIDTH, Images.IMAGE_HEIGHT)
                .create());
        if (fileData != null) {
            Image imageData = new Image(getShell().getDisplay(), new ByteArrayInputStream(fileData));
            imageData = Images.resize(getShell().getDisplay(), imageData);
            image.setImage(imageData);
        }
        groupPropertiesComposite.layout(true, true);
    }

    private boolean checkConstrains() {
        return activityName.trim().length() != 0 && fileData != null;
    }

    @Override
    protected Control createButtonBar(Composite parent) {
        Control buttonBar = super.createButtonBar(parent);
        getButton(Dialog.OK).setEnabled(checkConstrains());
        return buttonBar;
    }

    public String getActivityName() {
        return activityName;
    }

    public byte[] getImageByte() {
        return fileData;
    }
}

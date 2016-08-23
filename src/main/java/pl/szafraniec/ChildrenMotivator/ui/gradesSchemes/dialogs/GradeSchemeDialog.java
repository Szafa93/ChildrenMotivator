package pl.szafraniec.ChildrenMotivator.ui.gradesSchemes.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.window.DefaultToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.springframework.beans.factory.annotation.Autowired;
import pl.szafraniec.ChildrenMotivator.model.GradeScheme;
import pl.szafraniec.ChildrenMotivator.services.GradeSchemeService;
import pl.szafraniec.ChildrenMotivator.ui.Images;
import pl.szafraniec.ChildrenMotivator.ui.utils.ImageCanvas;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class GradeSchemeDialog extends Dialog {

    @Autowired
    private GradeSchemeService gradeSchemeService;

    private final int id;

    private String gradeValue;

    private String shellName;

    private byte[] fileData;

    private ImageCanvas image;

    public GradeSchemeDialog(Shell shell, Integer gradeValue, byte[] fileData, String shellName, int id) {
        super(shell);
        this.id = id;
        if (gradeValue != null) {
            this.gradeValue = gradeValue.toString();
        } else {
            this.gradeValue = "";
        }
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
        createGradeSchemeProperties(dialogArea);
        return dialogArea;
    }

    private void createGradeSchemeProperties(Composite parent) {
        Composite gradeSchemePropertiesComposite = new Composite(parent, SWT.NONE);
        gradeSchemePropertiesComposite.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).create());
        gradeSchemePropertiesComposite.setLayout(GridLayoutFactory.fillDefaults().numColumns(2).create());

        Label labelName = new Label(gradeSchemePropertiesComposite, SWT.NONE);
        labelName.setLayoutData(GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).create());
        labelName.setText("Wartość oceny: ");
        Text textName = new Text(gradeSchemePropertiesComposite, SWT.BORDER);
        textName.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
        textName.setText(gradeValue);
        textName.addModifyListener(event -> {
            gradeValue = textName.getText();
            List<String> errors = checkConstrains();
            StringJoiner stringJoiner = new StringJoiner(System.lineSeparator());
            errors.forEach(stringJoiner::add);
            getButton(Dialog.OK).setEnabled(errors.isEmpty());
            getButton(Dialog.OK).setToolTipText(stringJoiner.toString());
        });

        Label labelImage = new Label(gradeSchemePropertiesComposite, SWT.NONE);
        labelImage.setLayoutData(GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).create());
        labelImage.setText("Obrazek: ");
        Button buttonImage = new Button(gradeSchemePropertiesComposite, SWT.PUSH);
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
                        image.setImage(fileData);
                    } catch (IOException e1) {

                    }
                }
                List<String> errors = checkConstrains();
                StringJoiner stringJoiner = new StringJoiner(System.lineSeparator());
                errors.forEach(stringJoiner::add);
                getButton(Dialog.OK).setEnabled(errors.isEmpty());
                getButton(Dialog.OK).setToolTipText(stringJoiner.toString());
                gradeSchemePropertiesComposite.layout(true, true);
            }
        });

        image = new ImageCanvas(gradeSchemePropertiesComposite, SWT.BORDER);
        image.setLayoutData(GridDataFactory.swtDefaults().span(2, 1).align(SWT.CENTER, SWT.CENTER).hint(Images.IMAGE_WIDTH,
                Images.IMAGE_HEIGHT).minSize(Images.IMAGE_WIDTH, Images.IMAGE_HEIGHT).create());
        if (fileData != null) {
            image.setImage(fileData);
        }
        gradeSchemePropertiesComposite.layout(true, true);
    }

    private List<String> checkConstrains() {
        List<String> errors = new ArrayList<>();
        if (gradeValue.trim().length() == 0)
            errors.add("Brak podanej oceny");
        if (fileData == null)
            errors.add("Brak obrazka");
        Integer value = tryParse(gradeValue);
        if (value == null) {
            errors.add("Ocena nie jest liczbą całkowitą");
        } else {
            if (gradeSchemeService.findByValue(value).map(GradeScheme::getId).map(id -> id != this.id).orElse(false)) {
                errors.add("Ocena z ta wartością już istnieje");
            }
        }
        return errors;
    }

    public Integer tryParse(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    @Override
    protected Control createButtonBar(Composite parent) {
        Control buttonBar = super.createButtonBar(parent);
        List<String> errors = checkConstrains();
        StringJoiner stringJoiner = new StringJoiner(System.lineSeparator());
        errors.forEach(stringJoiner::add);
        getButton(Dialog.OK).setEnabled(errors.isEmpty());
        getButton(Dialog.OK).setToolTipText(stringJoiner.toString());
        buttonBar.addMouseMoveListener(new MouseMoveListener() {
            private DefaultToolTip toolTip = null;

            @Override
            public void mouseMove(MouseEvent e) {
                Button okButton = getButton(Dialog.OK);
                Point location = okButton.getLocation();
                Rectangle size = okButton.getBounds();
                if (e.x >= location.x && e.x <= location.x + size.width && e.y >= location.y && e.y <= location.y + size.height) {
                    if (toolTip == null) {
                        toolTip = new DefaultToolTip(buttonBar, SWT.NONE, true);
                        toolTip.setPopupDelay(0);
                        toolTip.setHideDelay(Integer.MAX_VALUE);
                        toolTip.show(new Point(e.x, e.y));
                        toolTip.setHideOnMouseDown(false);
                        toolTip.setText(okButton.getToolTipText());
                        toolTip.activate();
                    }
                } else if (toolTip != null) {
                    toolTip.hide();
                    toolTip.deactivate();
                    toolTip = null;
                }
            }
        });
        return buttonBar;
    }

    public int getGradeValue() {
        return tryParse(gradeValue);
    }

    public byte[] getImageByte() {
        return fileData;
    }
}

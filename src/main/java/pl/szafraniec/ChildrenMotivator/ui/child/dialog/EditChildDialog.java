package pl.szafraniec.ChildrenMotivator.ui.child.dialog;

import org.apache.commons.validator.routines.EmailValidator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.springframework.beans.factory.annotation.Autowired;
import pl.szafraniec.ChildrenMotivator.model.Child;
import pl.szafraniec.ChildrenMotivator.services.ChildService;

import java.util.function.Consumer;

public class EditChildDialog extends Dialog {

    @Autowired
    private ChildService childService;

    private String shellName;
    private final int id;

    private String name;

    private String surname;

    private String pesel;

    private String parentEmail;

    public EditChildDialog(Shell shell) {
        this(shell, "", "", "", "", "Dodaj dziecko", -1);
    }

    public EditChildDialog(Shell parentShell, String name, String surname, String pesel, String parentEmail, String shellName, int id) {
        super(parentShell);
        this.name = name;
        this.surname = surname;
        this.pesel = pesel;
        this.parentEmail = parentEmail;
        this.shellName = shellName;
        this.id = id;
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(shellName);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite dialogArea = (Composite) super.createDialogArea(parent);
        createChildProperties(dialogArea);
        return dialogArea;
    }

    private void createChildProperties(Composite parent) {
        Composite groupPropertiesComposite = new Composite(parent, SWT.NONE);
        groupPropertiesComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
        groupPropertiesComposite.setLayout(GridLayoutFactory.fillDefaults().numColumns(2).create());
        createChildProperty(groupPropertiesComposite, "Imię: ", name, newValue -> name = newValue);
        createChildProperty(groupPropertiesComposite, "Nazwisko: ", surname, newValue -> surname = newValue);
        createChildProperty(groupPropertiesComposite, "PESEL: ", pesel, newValue -> pesel = newValue);
        createChildProperty(groupPropertiesComposite, "Email rodzica: ", parentEmail, newValue -> parentEmail = newValue);
    }

    private void createChildProperty(Composite parent, String propertyName, String initialValue, Consumer<String> modifyValueConsumer) {
        Label propertyNameLabel = new Label(parent, SWT.NONE);
        propertyNameLabel.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(false, false).create());
        propertyNameLabel.setText(propertyName);
        Text propertyValueLabel = new Text(parent, SWT.BORDER | SWT.RIGHT);
        propertyValueLabel.setText(initialValue);
        propertyValueLabel.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).create());
        propertyValueLabel.addModifyListener(event -> {
            modifyValueConsumer.accept(propertyValueLabel.getText());
            getButton(Dialog.OK).setEnabled(checkConstrains());
        });
    }

    private boolean checkConstrains() {
        return name.trim().length() != 0
                && surname.trim().length() != 0
                && pesel.trim().length() != 0
                && childService.findByPesel(pesel).map(Child::getId).map(id -> id == this.id).orElse(false)
                && (parentEmail.trim().length() == 0 || EmailValidator.getInstance().isValid(parentEmail));
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

    public String getSurname() {
        return surname;
    }

    public String getPesel() {
        return pesel;
    }

    public String getParentEmail() {
        return parentEmail;
    }
}

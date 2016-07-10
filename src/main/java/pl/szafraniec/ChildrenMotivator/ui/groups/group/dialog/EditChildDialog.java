package pl.szafraniec.ChildrenMotivator.ui.groups.group.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class EditChildDialog extends Dialog {
    private String shellName;

    private String name;

    private String surname;

    private String pesel;

    private String parentEmail;

    public EditChildDialog(Shell shell) {
        this(shell, "", "", "", "", "Dodaj dziecko");
    }

    public EditChildDialog(Shell parentShell, String name, String surname, String pesel, String parentEmail, String shellName) {
        super(parentShell);
        this.name = name;
        this.surname = surname;
        this.pesel = pesel;
        this.parentEmail = parentEmail;
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
        createChildProperties(dialogArea);
        return dialogArea;
    }

    private void createChildProperties(Composite parent) {
        Composite groupPropertiesComposite = new Composite(parent, SWT.NONE);
        groupPropertiesComposite.setLayout(GridLayoutFactory.fillDefaults().numColumns(2).create());
        Label labelName = new Label(groupPropertiesComposite, SWT.NONE);
        labelName.setText("Imię: ");
        Text textName = new Text(groupPropertiesComposite, SWT.BORDER);
        textName.setText(name);
        textName.setLayoutData(GridDataFactory.swtDefaults().grab(true, false).create());
        textName.addModifyListener(event -> {
            name = textName.getText();
            getButton(Dialog.OK).setEnabled(checkConstrains());
        });

        Label labelSurname = new Label(groupPropertiesComposite, SWT.NONE);
        labelSurname.setText("Nazwisko: ");
        Text textSurname = new Text(groupPropertiesComposite, SWT.BORDER);
        textSurname.setText(surname);
        textSurname.setLayoutData(GridDataFactory.swtDefaults().grab(true, false).create());
        textSurname.addModifyListener(event -> {
            surname = textSurname.getText();
            getButton(Dialog.OK).setEnabled(checkConstrains());
        });

        Label labelPesel = new Label(groupPropertiesComposite, SWT.NONE);
        labelPesel.setText("PESEL: ");
        Text textPesel = new Text(groupPropertiesComposite, SWT.BORDER);
        textPesel.setText(pesel);
        textPesel.setLayoutData(GridDataFactory.swtDefaults().grab(true, false).create());
        textPesel.addModifyListener(event -> {
            pesel = textPesel.getText();
            getButton(Dialog.OK).setEnabled(checkConstrains());
        });

        Label labelEmail = new Label(groupPropertiesComposite, SWT.NONE);
        labelEmail.setText("Email rodzica: ");
        Text textEmail = new Text(groupPropertiesComposite, SWT.BORDER);
        textEmail.setText(parentEmail);
        textEmail.setLayoutData(GridDataFactory.swtDefaults().grab(true, false).create());
        textEmail.addModifyListener(event -> {
            parentEmail = textEmail.getText();
            getButton(Dialog.OK).setEnabled(checkConstrains());
        });
    }

    private boolean checkConstrains() {
        return name.trim().length() != 0 && surname.trim().length() != 0 && pesel.trim().length() != 0 && parentEmail.trim().length() != 0;
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

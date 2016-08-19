package pl.szafraniec.ChildrenMotivator.ui;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.validator.routines.EmailValidator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import java.util.function.Consumer;

public class ConfigurationDialog extends Dialog {
    private static final int TEST_BUTTON = 1000;

    private boolean successfulSend = false;

    private String fromEmail;

    private String smtpHost;

    private String smtpPort;

    private String mailUser;

    private String mailPassword;

    private boolean sslConnection;

    public ConfigurationDialog(Shell parentShell, String fromEmail, String smtpHost, String smtpPort, String mailUser,
            String mailPassword, boolean sslConnection) {
        super(parentShell);
        this.fromEmail = fromEmail != null ? fromEmail : "";
        this.smtpHost = smtpHost != null ? smtpHost : "";
        this.smtpPort = smtpPort != null ? smtpPort : "";
        this.mailUser = mailUser != null ? mailUser : "";
        this.mailPassword = mailPassword != null ? mailPassword : "";
        this.sslConnection = sslConnection;
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText("Ustawienia");
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
        createProperty(groupPropertiesComposite, "Adres email nadawcy: ", fromEmail, newValue -> fromEmail = newValue);
        createProperty(groupPropertiesComposite, "Adres serwera smtp: ", smtpHost, newValue -> smtpHost = newValue);
        createProperty(groupPropertiesComposite, "Port serwera smtp: ", smtpPort, newValue -> smtpPort = newValue);
        createBooleanProperty(groupPropertiesComposite, "Połączenie SSL: ", sslConnection, newValue -> sslConnection = newValue);
        createProperty(groupPropertiesComposite, "Nazwa użytkownika: ", mailUser, newValue -> mailUser = newValue);
        createProperty(groupPropertiesComposite, "Hasło użytkownika: ", mailPassword, newValue -> mailPassword = newValue, true);
    }

    private void createProperty(Composite parent, String propertyName, String initialValue, Consumer<String> modifyValueConsumer) {
        createProperty(parent, propertyName, initialValue, modifyValueConsumer, false);
    }

    private void createProperty(Composite parent, String propertyName, String initialValue, Consumer<String> modifyValueConsumer,
            boolean secure) {
        Label propertyNameLabel = new Label(parent, SWT.NONE);
        propertyNameLabel.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(false, false).create());
        propertyNameLabel.setText(propertyName);
        int style = SWT.BORDER | SWT.RIGHT;
        if (secure) {
            style |= SWT.PASSWORD;
        }
        Text propertyValueLabel = new Text(parent, style);
        propertyValueLabel.setText(initialValue);
        propertyValueLabel.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).minSize(200, SWT.DEFAULT)
                .hint(200, SWT.DEFAULT).create());
        propertyValueLabel.addModifyListener(event -> {
            modifyValueConsumer.accept(propertyValueLabel.getText());
            getButton(OK).setEnabled(false);
            getButton(TEST_BUTTON).setEnabled(checkConstrains());
        });
    }

    private void createBooleanProperty(Composite parent, String propertyName, boolean initialValue, Consumer<Boolean> modifyValueConsumer) {
        Label propertyNameLabel = new Label(parent, SWT.NONE);
        propertyNameLabel.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(false, false).create());
        if (propertyName != null) {
            propertyNameLabel.setText(propertyName);
        }
        Button propertyValueLabel = new Button(parent, SWT.CHECK);
        propertyValueLabel.setSelection(initialValue);
        propertyValueLabel.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).create());
        propertyValueLabel.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                modifyValueConsumer.accept(propertyValueLabel.getSelection());
                getButton(OK).setEnabled(false);
                getButton(TEST_BUTTON).setEnabled(checkConstrains());
            }
        });
    }

    private boolean checkConstrains() {
        return fromEmail.trim().length() > 0
                && smtpHost.trim().length() > 0
                && smtpPort.trim().length() > 0
                && tryParse(smtpPort) != null
                && mailUser.trim().length() > 0
                && mailPassword.trim().length() > 0
                && EmailValidator.getInstance().isValid(fromEmail);
    }

    public Integer tryParse(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    private void trySendMail() {
        Email email = new HtmlEmail();
        try {
            email.setSubject("Testowy mail z aplikacji ChildrenMotivator");
            email.setMsg("Testowa zawartość z aplikacji ChildrenMotivator");

            email.setAuthentication(mailUser, mailPassword);
            email.setFrom(fromEmail);
            email.addTo(fromEmail);
            if (sslConnection) {
                email.setSslSmtpPort(smtpPort);
                email.setSSLOnConnect(true);
            } else {
                email.setSmtpPort(tryParse(smtpPort));
            }
            email.setHostName(smtpHost);
            email.send();
        } catch (EmailException e) {
            e.printStackTrace();
            getButton(OK).setEnabled(false);
        }
        getButton(OK).setEnabled(true);
    }

    @Override
    protected Control createButtonBar(Composite parent) {
        Control buttonBar = super.createButtonBar(parent);
        getButton(TEST_BUTTON).setEnabled(checkConstrains());
        getButton(OK).setEnabled(false);
        if (checkConstrains()) {
            // trySendMail();
        }
        return buttonBar;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        Button button = createButton(parent, TEST_BUTTON, "Testuj", true);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                trySendMail();
            }
        });
        super.createButtonsForButtonBar(parent);
    }

    public String getFromEmail() {
        return fromEmail;
    }

    public String getSmtpHost() {
        return smtpHost;
    }

    public String getSmtpPort() {
        return smtpPort;
    }

    public String getMailUser() {
        return mailUser;
    }

    public String getMailPassword() {
        return mailPassword;
    }

    public boolean isSslConnection() {
        return sslConnection;
    }
}

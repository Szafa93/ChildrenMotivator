package pl.szafraniec.ChildrenMotivator.ui.groups.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class EditChildrenGroupDialog extends Dialog {
    private String groupName;

    public EditChildrenGroupDialog(Shell shell) {
        this(shell, "");
    }

    public EditChildrenGroupDialog(Shell shell, String groupName) {
        super(shell);
        this.groupName = groupName;
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText("Dodaj grupę");
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite dialogArea = (Composite) super.createDialogArea(parent);
        createGroupProperties(dialogArea);
        return dialogArea;
    }

    private void createGroupProperties(Composite parent) {
        Composite groupPropertiesComposite = new Composite(parent, SWT.NONE);
        groupPropertiesComposite.setLayout(GridLayoutFactory.fillDefaults().numColumns(2).create());
        Label label = new Label(groupPropertiesComposite, SWT.NONE);
        label.setText("Nazwa grupy: ");
        Text text = new Text(groupPropertiesComposite, SWT.BORDER);
        text.setText(groupName);
        text.addModifyListener(event -> groupName = text.getText());
        text.addModifyListener(event -> getButton(Dialog.OK).setEnabled(text.getText().trim().length() != 0));
    }

    @Override
    protected Control createButtonBar(Composite parent) {
        Control buttonBar = super.createButtonBar(parent);
        getButton(Dialog.OK).setEnabled(false);
        return buttonBar;
    }

    public String getGroupName() {
        return groupName;
    }
}

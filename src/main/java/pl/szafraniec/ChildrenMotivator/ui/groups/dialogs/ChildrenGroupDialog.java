package pl.szafraniec.ChildrenMotivator.ui.groups.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ChildrenGroupDialog extends Dialog {
    private String groupName;
    private String shellName;

    public ChildrenGroupDialog(Shell shell, String groupName, String shellName) {
        super(shell);
        this.groupName = groupName;
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
        Label label = new Label(groupPropertiesComposite, SWT.NONE);
        label.setLayoutData(GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).create());
        label.setText("Nazwa grupy: ");
        Text text = new Text(groupPropertiesComposite, SWT.BORDER);
        text.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
        text.setText(groupName);
        text.addModifyListener(event -> {
            groupName = text.getText();
            getButton(Dialog.OK).setEnabled(checkConstrains());
        });

        groupPropertiesComposite.layout(true, true);
    }

    private boolean checkConstrains() {
        return groupName.trim().length() != 0;
    }

    @Override
    protected Control createButtonBar(Composite parent) {
        Control buttonBar = super.createButtonBar(parent);
        getButton(Dialog.OK).setEnabled(checkConstrains());
        return buttonBar;
    }

    public String getGroupName() {
        return groupName;
    }
}

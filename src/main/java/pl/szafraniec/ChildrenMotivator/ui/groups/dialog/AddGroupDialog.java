package pl.szafraniec.ChildrenMotivator.ui.groups.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class AddGroupDialog extends Dialog {
    private String groupName = "";

    public AddGroupDialog(Shell parent) {
        super(parent);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite dialogArea = (Composite) super.createDialogArea(parent);
        Text text = new Text(dialogArea, SWT.BORDER);
        text.addModifyListener(event -> groupName = text.getText());
        return dialogArea;
    }

    public String getGroupName() {
        return groupName;
    }
}

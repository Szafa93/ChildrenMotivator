package pl.szafraniec.ChildrenMotivator.ui.groups.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.springframework.beans.factory.annotation.Autowired;
import pl.szafraniec.ChildrenMotivator.model.ChildrenGroup;
import pl.szafraniec.ChildrenMotivator.services.ChildrenGroupService;

import java.util.Map;
import java.util.stream.Collectors;

public class GroupSelectorDialog extends Dialog {

    @Autowired
    private ChildrenGroupService childrenGroupService;

    private ChildrenGroup childrenGroup;

    public GroupSelectorDialog(Shell shell, ChildrenGroup childrenGroup) {
        super(shell);
        this.childrenGroup = childrenGroup;
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText("Wybierz grupę");
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
        groupPropertiesComposite.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).create());

        Label propertyNameLabel = new Label(parent, SWT.NONE);
        propertyNameLabel.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(false, false).create());
        propertyNameLabel.setText("Grupa:");
        Combo combo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
        Map<String, ChildrenGroup> comboMap = childrenGroupService.findAll().stream().collect(
                Collectors.toMap(ChildrenGroup::getName, g -> g));
        combo.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).create());
        comboMap.keySet().forEach(combo::add);
        combo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                childrenGroup = comboMap.get(combo.getItem(combo.getSelectionIndex()));
                getButton(Dialog.OK).setEnabled(checkConstrains());
            }
        });
        for (int i = 0; i < combo.getItems().length; i++) {
            if (combo.getItem(i).equals(childrenGroup.getName())) {
                combo.select(i);
            }
        }
        groupPropertiesComposite.layout(true, true);
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

    public ChildrenGroup getChildrenGroup() {
        return childrenGroup;
    }
}

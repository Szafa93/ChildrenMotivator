package pl.szafraniec.ChildrenMotivator.ui.groups;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.szafraniec.ChildrenMotivator.model.ChildrenGroup;
import pl.szafraniec.ChildrenMotivator.repository.ChildrenGroupRepository;
import pl.szafraniec.ChildrenMotivator.ui.AbstractMainComposite;
import pl.szafraniec.ChildrenMotivator.ui.groups.dialog.AddGroupDialog;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ChildrenGroupsComposite extends AbstractMainComposite {

    @Autowired
    private ChildrenGroupRepository childrenGroupRepository;

    private Composite childrenGroupComposite;

    private ScrolledComposite scrolledComposite;

    public ChildrenGroupsComposite(Composite parent) {
        super(parent, SWT.NONE);
    }

    @Override
    protected void createTopPart() {
        Composite topPart = new Composite(this, SWT.NONE);
        topPart.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).equalWidth(false).create());
        topPart.setLayoutData(GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).create());

        createLabel(topPart, "Grupy");
        createBackButton(topPart, applicationContext, shell);
    }

    @Override
    protected void createDownPart() {
        createChildrenGroupsComposite(childrenGroupRepository.findAll(), this);
        createControlsButtonsComposite(this);
    }

    private Composite createChildrenGroupsComposite(List<ChildrenGroup> childrenGroups, Composite parent) {

        scrolledComposite = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
        scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        scrolledComposite.setExpandVertical(true);
        scrolledComposite.setExpandHorizontal(true);
        childrenGroupComposite = new Composite(scrolledComposite, SWT.NONE);

        childrenGroupComposite.setLayout(new GridLayout());
        // TODO add layout

        childrenGroups.stream().map(group -> createChildrenGroupButton(group, childrenGroupComposite)).collect(Collectors.toList());
        childrenGroupComposite.setSize(childrenGroupComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        scrolledComposite.setContent(childrenGroupComposite);
        scrolledComposite.setMinSize(childrenGroupComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        return childrenGroupComposite;
    }

    private Control createChildrenGroupButton(ChildrenGroup childrenGroup, Composite parent) {
        Button groupButton = new Button(parent, SWT.NONE);
        groupButton.setText(childrenGroup.getName());
        groupButton.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
        return groupButton;
    }

    private Composite createControlsButtonsComposite(Composite parent) {
        Composite controlsButtonsComposite = new Composite(parent, SWT.NONE);
        controlsButtonsComposite.setLayoutData(GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.TOP).create());
        controlsButtonsComposite.setLayout(new GridLayout());
        // TODO add layout

        Button addGroupButton = new Button(controlsButtonsComposite, SWT.NONE);

        addGroupButton.setText("Dodaj grupę");
        addGroupButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                addGroup();
            }
        });
        return controlsButtonsComposite;
    }

    private ChildrenGroup addChildrenGroup(String groupName) {
        ChildrenGroup childrenGroup = ChildrenGroup.ChildrenGroupFactory.create(groupName);
        return childrenGroupRepository.saveAndFlush(childrenGroup);
    }

    private void addGroup() {
        AddGroupDialog dialog = new AddGroupDialog(Display.getCurrent().getActiveShell());
        if (Window.OK == dialog.open()) {
            ChildrenGroup childrenGroup = addChildrenGroup(dialog.getGroupName());

            // add new button to button list
            createChildrenGroupButton(childrenGroup, childrenGroupComposite);

            scrolledComposite.setMinSize(childrenGroupComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
            scrolledComposite.layout(true, true);
        }
    }

}
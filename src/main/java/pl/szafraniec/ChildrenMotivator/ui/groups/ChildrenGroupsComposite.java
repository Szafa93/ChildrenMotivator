package pl.szafraniec.ChildrenMotivator.ui.groups;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.RowDataFactory;
import org.eclipse.jface.layout.RowLayoutFactory;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
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
import pl.szafraniec.ChildrenMotivator.ui.Fonts;
import pl.szafraniec.ChildrenMotivator.ui.groups.dialog.EditChildrenGroupDialog;
import pl.szafraniec.ChildrenMotivator.ui.groups.group.ChildrenGroupComposite;
import pl.szafraniec.ChildrenMotivator.ui.start.StartComposite;

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
        createControlsButtonsComposite(topPart);
    }

    private Composite createControlsButtonsComposite(Composite parent) {
        Composite controlsButtonsComposite = new Composite(parent, SWT.NONE);
        controlsButtonsComposite.setLayoutData(GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.TOP).create());
        controlsButtonsComposite.setLayout(GridLayoutFactory.swtDefaults().numColumns(1).create());

        createBackButton(controlsButtonsComposite, applicationContext, shell, StartComposite.class);
        createAddGroupButton(controlsButtonsComposite);
        return controlsButtonsComposite;
    }

    private void createAddGroupButton(Composite parent) {
        Button addGroupButton = new Button(parent, SWT.PUSH);
        addGroupButton.setLayoutData(DEFAULT_CONTROL_BUTTON_FACTORY);
        addGroupButton.setText("Dodaj grupę");
        addGroupButton.setFont(FontDescriptor.createFrom(Fonts.DEFAULT_FONT_DATA).createFont(addGroupButton.getDisplay()));
        addGroupButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                addGroup();
            }
        });
    }

    private void addGroup() {
        EditChildrenGroupDialog dialog = new EditChildrenGroupDialog(Display.getCurrent().getActiveShell());
        if (Window.OK == dialog.open()) {
            ChildrenGroup childrenGroup = addChildrenGroup(dialog.getGroupName());

            // add new button to button list
            createChildrenGroupButton(childrenGroup, childrenGroupComposite);

            scrolledComposite.layout(true, true);
        }
    }

    // TODO move to service
    private ChildrenGroup addChildrenGroup(String groupName) {
        ChildrenGroup childrenGroup = ChildrenGroup.ChildrenGroupFactory.create(groupName);
        return childrenGroupRepository.saveAndFlush(childrenGroup);
    }

    @Override
    protected void createDownPart() {
        Composite downPart = new Composite(this, SWT.NONE);
        downPart.setLayout(GridLayoutFactory.swtDefaults().numColumns(1).create());
        downPart.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
        createChildrenGroupsComposite(downPart);
    }

    private Composite createChildrenGroupsComposite(Composite parent) {
        scrolledComposite = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
        scrolledComposite.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
        scrolledComposite.setExpandVertical(true);
        scrolledComposite.setExpandHorizontal(true);

        childrenGroupComposite = new Composite(scrolledComposite, SWT.NONE);
        childrenGroupComposite.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
        childrenGroupComposite.setLayout(RowLayoutFactory.swtDefaults().pack(false).spacing(25).wrap(true).type(SWT.HORIZONTAL).create());

        childrenGroupRepository.findAll().stream().map(group -> createChildrenGroupButton(group, childrenGroupComposite)).collect(
                Collectors.toList());

        childrenGroupComposite.pack(true);
        scrolledComposite.setContent(childrenGroupComposite);
        scrolledComposite.addControlListener(new ControlAdapter() {
            public void controlResized(ControlEvent e) {
                Rectangle r = scrolledComposite.getClientArea();
                scrolledComposite.setMinSize(childrenGroupComposite.computeSize(r.width, SWT.DEFAULT));
            }
        });
        scrolledComposite.layout(true, true);
        return childrenGroupComposite;
    }

    private Control createChildrenGroupButton(ChildrenGroup childrenGroup, Composite parent) {
        Button groupButton = new Button(parent, SWT.WRAP);
        groupButton.setText(childrenGroup.getName());
        groupButton.setFont(FontDescriptor.createFrom(Fonts.DEFAULT_FONT_DATA).createFont(groupButton.getDisplay()));

        groupButton.setLayoutData(RowDataFactory.swtDefaults().hint(150, 150).create());
        groupButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                applicationContext.getBean(ChildrenGroupComposite.class, shell, childrenGroup);
                dispose();
                shell.layout(true, true);
            }
        });
        return groupButton;
    }
}
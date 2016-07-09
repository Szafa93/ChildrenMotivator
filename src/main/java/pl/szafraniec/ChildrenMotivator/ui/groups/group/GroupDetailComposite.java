package pl.szafraniec.ChildrenMotivator.ui.groups.group;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.szafraniec.ChildrenMotivator.model.Child;
import pl.szafraniec.ChildrenMotivator.model.ChildrenGroup;
import pl.szafraniec.ChildrenMotivator.repository.ChildrenGroupRepository;
import pl.szafraniec.ChildrenMotivator.ui.AbstractMainComposite;
import pl.szafraniec.ChildrenMotivator.ui.Fonts;
import pl.szafraniec.ChildrenMotivator.ui.groups.ChildrenGroupsComposite;
import pl.szafraniec.ChildrenMotivator.ui.groups.dialog.EditChildrenGroupDialog;
import pl.szafraniec.ChildrenMotivator.ui.groups.group.children.dialog.EditChildDialog;

import java.util.stream.Collectors;

@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GroupDetailComposite extends AbstractMainComposite {

    @Autowired
    private ChildrenGroupRepository childrenGroupRepository;

    private ChildrenGroup childrenGroup;

    private Composite childrenGroupComposite;

    private ScrolledComposite scrolledComposite;

    private Label label;

    public GroupDetailComposite(Composite parent, ChildrenGroup childrenGroup) {
        super(parent, SWT.NONE);
        this.childrenGroup = childrenGroup;
    }

    @Override
    protected void createTopPart() {
        Composite topPart = new Composite(this, SWT.NONE);
        topPart.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).equalWidth(false).create());
        topPart.setLayoutData(GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).create());

        label = createLabel(topPart, childrenGroup.getName());
        createTopControlsButtonsComposite(topPart);
    }

    private Composite createTopControlsButtonsComposite(Composite parent) {
        Composite controlsButtonsComposite = new Composite(parent, SWT.NONE);
        controlsButtonsComposite.setLayoutData(GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.TOP).create());
        controlsButtonsComposite.setLayout(GridLayoutFactory.swtDefaults().numColumns(1).create());

        createBackButton(controlsButtonsComposite, applicationContext, shell, ChildrenGroupsComposite.class);
        return controlsButtonsComposite;
    }

    @Override
    protected void createDownPart() {
        Composite downPart = new Composite(this, SWT.NONE);
        downPart.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).create());
        downPart.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
        createChildrenComposite(downPart);
        createDownControlsButtonsComposite(downPart);
    }

    private Composite createChildrenComposite(Composite parent) {
        scrolledComposite = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
        scrolledComposite.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
        scrolledComposite.setExpandVertical(true);
        scrolledComposite.setExpandHorizontal(true);

        childrenGroupComposite = new Composite(scrolledComposite, SWT.NONE);
        childrenGroupComposite.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
        childrenGroupComposite.setLayout(GridLayoutFactory.swtDefaults().numColumns(1).create());

        childrenGroup.getChildren().stream().map(child -> createChildButton(child, childrenGroupComposite)).collect(Collectors.toList());

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

    private Control createChildButton(Child child, Composite parent) {
        Button groupButton = new Button(parent, SWT.WRAP);
        groupButton.setText(String.format("%s %s", child.getName(), child.getSurname()));
        groupButton.setFont(FontDescriptor.createFrom(Fonts.DEFAULT_FONT_DATA).createFont(groupButton.getDisplay()));

        groupButton.setLayoutData(GridDataFactory.createFrom(DEFAULT_CONTROL_BUTTON_FACTORY)
                .grab(true, false)
                .align(SWT.FILL, SWT.CENTER)
                .create());
        return groupButton;
    }

    private Composite createDownControlsButtonsComposite(Composite parent) {
        Composite controlsButtonsComposite = new Composite(parent, SWT.NONE);
        controlsButtonsComposite.setLayoutData(GridDataFactory.swtDefaults().grab(false, true).align(SWT.CENTER, SWT.FILL).create());
        controlsButtonsComposite.setLayout(GridLayoutFactory.swtDefaults().numColumns(1).create());

        createBehaviorTableButton(controlsButtonsComposite);
        createAddChildButton(controlsButtonsComposite);
        createEditButton(controlsButtonsComposite);
        createRemoveButton(controlsButtonsComposite, this::removeGroup);
        return controlsButtonsComposite;
    }

    private void createBehaviorTableButton(Composite parent) {
        Button addGroupButton = new Button(parent, SWT.PUSH);
        addGroupButton.setLayoutData(DEFAULT_CONTROL_BUTTON_FACTORY);
        addGroupButton.setText("Tablica zachowań");
        addGroupButton.setFont(FontDescriptor.createFrom(Fonts.DEFAULT_FONT_DATA).createFont(addGroupButton.getDisplay()));
        addGroupButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                showBehaviorTable();
            }
        });
    }

    private void createAddChildButton(Composite parent) {
        Button addGroupButton = new Button(parent, SWT.PUSH);
        addGroupButton.setLayoutData(DEFAULT_CONTROL_BUTTON_FACTORY);
        addGroupButton.setText("Dodaj dziecko");
        addGroupButton.setFont(FontDescriptor.createFrom(Fonts.DEFAULT_FONT_DATA).createFont(addGroupButton.getDisplay()));
        addGroupButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                addChild();
            }
        });
    }

    private void createEditButton(Composite parent) {
        Button addGroupButton = new Button(parent, SWT.PUSH);
        addGroupButton.setLayoutData(DEFAULT_CONTROL_BUTTON_FACTORY);
        addGroupButton.setText("Edytuj");
        addGroupButton.setFont(FontDescriptor.createFrom(Fonts.DEFAULT_FONT_DATA).createFont(addGroupButton.getDisplay()));
        addGroupButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                editGroup();
            }
        });
    }

    private void removeGroup() {
        childrenGroupRepository.delete(childrenGroup);
        childrenGroupRepository.flush();

        applicationContext.getBean(ChildrenGroupsComposite.class, shell);
        dispose();
        shell.layout(true, true);
    }

    private void showBehaviorTable() {
        // TODO
    }

    private void addChild() {
        EditChildDialog dialog = new EditChildDialog(shell);
        if (Window.OK == dialog.open()) {
            Child child = addChild(dialog.getName(), dialog.getSurname(), dialog.getPesel(), dialog.getParentEmail(), childrenGroup);
            createChildButton(child, childrenGroupComposite);
            scrolledComposite.layout(true, true);
        }
    }

    private void editGroup() {
        EditChildrenGroupDialog dialog = new EditChildrenGroupDialog(shell, childrenGroup.getName(), "Edytuj grupę");
        if (Window.OK == dialog.open()) {
            editChildrenGroup(dialog.getGroupName());

            scrolledComposite.layout(true, true);
        }
    }

    private void editChildrenGroup(String groupName) {
        childrenGroup.setName(groupName);
        childrenGroup = childrenGroupRepository.saveAndFlush(childrenGroup);
        label.setText(childrenGroup.getName());
    }

    private Child addChild(String name, String surname, String pesel, String parentMail, ChildrenGroup childrenGroup) {
        Child child = Child.ChildFactory.create(name, surname, pesel, parentMail, childrenGroup);
        this.childrenGroup = childrenGroupRepository.saveAndFlush(childrenGroup);
        return child;
    }

}
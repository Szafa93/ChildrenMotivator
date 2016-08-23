package pl.szafraniec.ChildrenMotivator.ui.groups;

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
import pl.szafraniec.ChildrenMotivator.model.Holder;
import pl.szafraniec.ChildrenMotivator.services.ChildService;
import pl.szafraniec.ChildrenMotivator.services.ChildrenGroupService;
import pl.szafraniec.ChildrenMotivator.services.ReportService;
import pl.szafraniec.ChildrenMotivator.ui.AbstractMainComposite;
import pl.szafraniec.ChildrenMotivator.ui.Fonts;
import pl.szafraniec.ChildrenMotivator.ui.child.ChildComposite;
import pl.szafraniec.ChildrenMotivator.ui.child.dialogs.ChildDialog;
import pl.szafraniec.ChildrenMotivator.ui.groups.dialogs.ChildrenGroupDialog;
import pl.szafraniec.ChildrenMotivator.ui.groups.dialogs.SelectDatesForReportsDialog;
import pl.szafraniec.ChildrenMotivator.ui.services.DialogProvider;

import java.util.stream.Collectors;

@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ChildrenGroupComposite extends AbstractMainComposite {

    @Autowired
    private ChildrenGroupService childrenGroupService;

    @Autowired
    private ChildService childService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private DialogProvider dialogProvider;

    private ChildrenGroup childrenGroup;

    private Composite childrenGroupComposite;

    private ScrolledComposite scrolledComposite;

    private Label label;

    public ChildrenGroupComposite(Composite parent, ChildrenGroup childrenGroup) {
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
    protected Composite createDownPart() {
        Composite downPart = new Composite(this, SWT.NONE);
        downPart.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).create());
        downPart.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
        createChildrenComposite(downPart);
        createDownControlsButtonsComposite(downPart);
        return downPart;
    }

    private Composite createChildrenComposite(Composite parent) {
        scrolledComposite = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
        scrolledComposite.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
        scrolledComposite.setExpandVertical(true);
        scrolledComposite.setExpandHorizontal(true);

        childrenGroupComposite = new Composite(scrolledComposite, SWT.NONE);
        childrenGroupComposite.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
        childrenGroupComposite.setLayout(GridLayoutFactory.swtDefaults().numColumns(1).create());

        fillChildrenGroupComposite();

        childrenGroupComposite.pack(true);
        scrolledComposite.setContent(childrenGroupComposite);
        scrolledComposite.addControlListener(new ControlAdapter() {
            public void controlResized(ControlEvent e) {
                Rectangle r = scrolledComposite.getClientArea();
                scrolledComposite.setMinSize(childrenGroupComposite.computeSize(r.width, SWT.DEFAULT));
            }
        });
        scrolledComposite.layout(true, true);
        return scrolledComposite;
    }

    private void fillChildrenGroupComposite() {
        childrenGroup.getChildren().stream().map(child -> createChildButton(child, childrenGroupComposite)).collect(Collectors.toList());
    }

    private Control createChildButton(Child child, Composite parent) {
        Button groupButton = new Button(parent, SWT.WRAP);
        groupButton.setText(String.format("%s %s", child.getName(), child.getSurname()));
        groupButton.setFont(FontDescriptor.createFrom(Fonts.DEFAULT_FONT_DATA).createFont(groupButton.getDisplay()));

        groupButton.setLayoutData(DEFAULT_CONTROL_BUTTON_FACTORY.copy()
                .grab(true, false)
                .align(SWT.FILL, SWT.CENTER)
                .create());

        groupButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                applicationContext.getBean(ChildComposite.class, shell, Holder.of(child));
                dispose();
                shell.layout(true, true);
            }
        });
        return groupButton;
    }

    private Composite createDownControlsButtonsComposite(Composite parent) {
        Composite controlsButtonsComposite = new Composite(parent, SWT.NONE);
        controlsButtonsComposite.setLayoutData(GridDataFactory.swtDefaults().grab(false, true).align(SWT.CENTER, SWT.FILL).create());
        controlsButtonsComposite.setLayout(GridLayoutFactory.swtDefaults().numColumns(1).create());

        createBehaviorTableButton(controlsButtonsComposite);
        createAddChildButton(controlsButtonsComposite);
        createSendStatisticButton(controlsButtonsComposite);
        createEditButton(controlsButtonsComposite);
        createRemoveButton(controlsButtonsComposite, this::removeGroup,
                "Usunięte zostaną wszystkie dzieci z tej grupy wraz z ich ocenami i tablicami aktywności");
        return controlsButtonsComposite;
    }

    private void createBehaviorTableButton(Composite parent) {
        Button addGroupButton = new Button(parent, SWT.PUSH);
        addGroupButton.setLayoutData(DEFAULT_CONTROL_BUTTON_FACTORY.create());
        addGroupButton.setText("Tablica zachowania");
        addGroupButton.setFont(FontDescriptor.createFrom(Fonts.DEFAULT_FONT_DATA).createFont(addGroupButton.getDisplay()));
        addGroupButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                applicationContext.getBean("BehaviorTableComposite", shell, childrenGroup);
                dispose();
                shell.layout(true, true);
            }
        });
    }

    private void createAddChildButton(Composite parent) {
        Button addGroupButton = new Button(parent, SWT.PUSH);
        addGroupButton.setLayoutData(DEFAULT_CONTROL_BUTTON_FACTORY.create());
        addGroupButton.setText("Dodaj dziecko");
        addGroupButton.setFont(FontDescriptor.createFrom(Fonts.DEFAULT_FONT_DATA).createFont(addGroupButton.getDisplay()));
        addGroupButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                addChild();
            }
        });
    }

    private void createSendStatisticButton(Composite parent) {
        Button sendStatistic = new Button(parent, SWT.PUSH | SWT.WRAP);
        sendStatistic.setLayoutData(DEFAULT_CONTROL_BUTTON_FACTORY.create());
        sendStatistic.setText("Wyślij raport do rodziców");
        sendStatistic.setFont(FontDescriptor.createFrom(Fonts.DEFAULT_FONT_DATA).createFont(sendStatistic.getDisplay()));
        sendStatistic.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                SelectDatesForReportsDialog dialog = new SelectDatesForReportsDialog(shell);
                applicationContext.getAutowireCapableBeanFactory().autowireBean(dialog);
                if (Window.OK == dialog.open()) {
                    reportService.sendReports(childrenGroup, dialog.getStart(), dialog.getEnd());
                }
            }
        });
        sendStatistic.setEnabled(reportService.canSendReports());
    }

    private void createEditButton(Composite parent) {
        Button addGroupButton = new Button(parent, SWT.PUSH);
        addGroupButton.setLayoutData(DEFAULT_CONTROL_BUTTON_FACTORY.create());
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
        childrenGroupService.removeGroup(childrenGroup);

        applicationContext.getBean(ChildrenGroupsComposite.class, shell);
        dispose();
        shell.layout(true, true);
    }

    private void addChild() {
        ChildDialog dialog = dialogProvider.createChildDialog();
        applicationContext.getAutowireCapableBeanFactory().autowireBean(dialog);
        if (Window.OK == dialog.open()) {
            addChild(dialog.getName(), dialog.getSurname(), dialog.getPesel(), dialog.getParentEmail(), childrenGroup);
            for (Control control : childrenGroupComposite.getChildren()) {
                control.dispose();
            }
            fillChildrenGroupComposite();
            scrolledComposite.layout(true, true);
        }
    }

    private void editGroup() {
        ChildrenGroupDialog dialog = new ChildrenGroupDialog(shell, childrenGroup.getName(), "Edytuj grupę");
        if (Window.OK == dialog.open()) {
            editChildrenGroup(dialog.getGroupName());
            scrolledComposite.layout(true, true);
        }
    }

    private void editChildrenGroup(String groupName) {
        childrenGroup = childrenGroupService.edit(childrenGroup, groupName);
        label.setText(childrenGroup.getName());
    }

    private void addChild(String name, String surname, String pesel, String parentMail, ChildrenGroup childrenGroup) {
        this.childrenGroup = childService.create(name, surname, pesel, parentMail, childrenGroup).getChildrenGroup();
    }

}

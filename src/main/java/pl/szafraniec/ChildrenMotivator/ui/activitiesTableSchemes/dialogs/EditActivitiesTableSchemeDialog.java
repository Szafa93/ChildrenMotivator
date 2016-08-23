package pl.szafraniec.ChildrenMotivator.ui.activitiesTableSchemes.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.springframework.beans.factory.annotation.Autowired;
import pl.szafraniec.ChildrenMotivator.model.Activity;
import pl.szafraniec.ChildrenMotivator.repository.ActivityRepository;
import pl.szafraniec.ChildrenMotivator.ui.Fonts;
import pl.szafraniec.ChildrenMotivator.ui.Images;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.stream.Collectors;

public class EditActivitiesTableSchemeDialog extends Dialog {

    @Autowired
    private ActivityRepository activityRepository;

    private String activitiesTableSchemeName;
    private String shellName;
    private List<Activity> activities;

    public EditActivitiesTableSchemeDialog(Shell shell, String activitiesTableSchemeName, List<Activity> activities, String shellName) {
        super(shell);
        this.activitiesTableSchemeName = activitiesTableSchemeName;
        this.activities = activities;
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
        groupPropertiesComposite.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).create());

        Label labelName = new Label(groupPropertiesComposite, SWT.NONE);
        labelName.setLayoutData(GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).create());
        labelName.setText("Nazwa schematu: ");
        Text textName = new Text(groupPropertiesComposite, SWT.BORDER);
        textName.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
        textName.setText(activitiesTableSchemeName);
        textName.addModifyListener(event -> {
            activitiesTableSchemeName = textName.getText();
            getButton(Dialog.OK).setEnabled(checkConstrains());
        });

        Composite activitiesComposite = createActivitiesComposite(groupPropertiesComposite);
        activitiesComposite.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).span(2, 1).align(SWT.FILL, SWT.FILL).create());

        groupPropertiesComposite.layout(true, true);
    }

    private Composite createActivitiesComposite(Composite parent) {
        ScrolledComposite scrolledComposite = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
        scrolledComposite.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
        scrolledComposite.setExpandVertical(true);
        scrolledComposite.setExpandHorizontal(true);

        Composite activitiesComposite = new Composite(scrolledComposite, SWT.NONE);
        activitiesComposite.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
        activitiesComposite.setLayout(GridLayoutFactory.swtDefaults().spacing(25, 25).numColumns(4).create());

        activityRepository.findAll().stream().map(activity -> createActivityButton(activity, activitiesComposite)).collect(
                Collectors.toList());

        activitiesComposite.pack(true);
        scrolledComposite.setContent(activitiesComposite);
        scrolledComposite.addControlListener(new ControlAdapter() {
            public void controlResized(ControlEvent e) {
                Rectangle r = scrolledComposite.getClientArea();
                scrolledComposite.setMinSize(activitiesComposite.computeSize(r.width, SWT.DEFAULT));
            }
        });
        scrolledComposite.layout(true, true);
        return scrolledComposite;
    }

    private Control createActivityButton(Activity activity, Composite parent) {
        Button activityButton = new Button(parent, SWT.CHECK | SWT.BORDER);
        activityButton.setToolTipText(activity.getName());
        activityButton.setFont(FontDescriptor.createFrom(Fonts.DEFAULT_FONT_DATA).createFont(activityButton.getDisplay()));
        // TODO uncommect this if you want image as button
        Image imageData = new Image(getShell().getDisplay(), new ByteArrayInputStream(activity.getImage()));
        imageData = Images.resize(getShell().getDisplay(), imageData);
        activityButton.setBackgroundImage(imageData);
        activityButton.setData(activity);
        activityButton.setLayoutData(GridDataFactory.swtDefaults().hint(Images.IMAGE_WIDTH, Images.IMAGE_HEIGHT).create());
        activityButton.setSelection(activities.contains(activity));

        activityButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (activityButton.getSelection()) {
                    activities.add((Activity) activityButton.getData());
                } else {
                    activities.remove(activityButton.getData());
                }
                getButton(Dialog.OK).setEnabled(checkConstrains());
            }
        });
        return activityButton;
    }

    private boolean checkConstrains() {
        return activitiesTableSchemeName.trim().length() != 0 && activities != null &&
                !activities.isEmpty();
    }

    @Override
    protected Control createButtonBar(Composite parent) {
        Control buttonBar = super.createButtonBar(parent);
        getButton(Dialog.OK).setEnabled(checkConstrains());
        return buttonBar;
    }

    public String getActivitiesTableSchemeName() {
        return activitiesTableSchemeName;
    }

    public List<Activity> getActivities() {
        return activities;
    }
}

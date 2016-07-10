package pl.szafraniec.ChildrenMotivator.ui.activities;

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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.szafraniec.ChildrenMotivator.model.Activity;
import pl.szafraniec.ChildrenMotivator.repository.ActivityRepository;
import pl.szafraniec.ChildrenMotivator.ui.AbstractMainComposite;
import pl.szafraniec.ChildrenMotivator.ui.Fonts;
import pl.szafraniec.ChildrenMotivator.ui.Images;
import pl.szafraniec.ChildrenMotivator.ui.activities.activity.ActivityComposite;
import pl.szafraniec.ChildrenMotivator.ui.activities.dialog.EditActivityDialog;
import pl.szafraniec.ChildrenMotivator.ui.start.StartComposite;

import java.io.ByteArrayInputStream;
import java.util.stream.Collectors;

@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ActivitiesComposite extends AbstractMainComposite {

    @Autowired
    private ActivityRepository activityRepository;

    private Composite activitiesComposite;

    private ScrolledComposite scrolledComposite;

    public ActivitiesComposite(Composite parent) {
        super(parent, SWT.NONE);
    }

    @Override
    protected void createTopPart() {
        Composite topPart = new Composite(this, SWT.NONE);
        topPart.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).equalWidth(false).create());
        topPart.setLayoutData(GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).create());

        createLabel(topPart, "Możliwe aktywności");
        createControlsButtonsComposite(topPart);
    }

    private Composite createControlsButtonsComposite(Composite parent) {
        Composite controlsButtonsComposite = new Composite(parent, SWT.NONE);
        controlsButtonsComposite.setLayoutData(GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.TOP).create());
        controlsButtonsComposite.setLayout(GridLayoutFactory.swtDefaults().numColumns(1).create());

        createBackButton(controlsButtonsComposite, applicationContext, shell, StartComposite.class);
        createAddActivityButton(controlsButtonsComposite);
        return controlsButtonsComposite;
    }

    private void createAddActivityButton(Composite parent) {
        Button addActivityButton = new Button(parent, SWT.PUSH);
        addActivityButton.setLayoutData(DEFAULT_CONTROL_BUTTON_FACTORY);
        addActivityButton.setText("Dodaj aktywność");
        addActivityButton.setFont(FontDescriptor.createFrom(Fonts.DEFAULT_FONT_DATA).createFont(addActivityButton.getDisplay()));
        addActivityButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                addActivity();
            }
        });
    }

    private void addActivity() {
        EditActivityDialog dialog = new EditActivityDialog(Display.getCurrent().getActiveShell());
        if (Window.OK == dialog.open()) {
            Activity activity = addActivity(dialog.getActivityName(), dialog.getImageByte());
            createActivityButton(activity, activitiesComposite);
            scrolledComposite.layout(true, true);
        }
    }

    private Activity addActivity(String activityName, byte[] image) {
        Activity activity = Activity.ActivityFactory.create(activityName, image);
        return activityRepository.saveAndFlush(activity);
    }

    @Override
    protected void createDownPart() {
        Composite downPart = new Composite(this, SWT.NONE);
        downPart.setLayout(GridLayoutFactory.swtDefaults().numColumns(1).create());
        downPart.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
        createActivitiesComposite(downPart);
    }

    private Composite createActivitiesComposite(Composite parent) {
        scrolledComposite = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
        scrolledComposite.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
        scrolledComposite.setExpandVertical(true);
        scrolledComposite.setExpandHorizontal(true);

        activitiesComposite = new Composite(scrolledComposite, SWT.NONE);
        activitiesComposite.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
        activitiesComposite.setLayout(RowLayoutFactory.swtDefaults().pack(false).spacing(25).wrap(true).type(SWT.HORIZONTAL).create());

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
        return activitiesComposite;
    }

    private Control createActivityButton(Activity activity, Composite parent) {
        Label activityButton = new Label(parent, SWT.WRAP | SWT.BORDER);
        activityButton.setText(activity.getName());
        activityButton.setToolTipText(activity.getName());
        activityButton.setFont(FontDescriptor.createFrom(Fonts.DEFAULT_FONT_DATA).createFont(activityButton.getDisplay()));
        // TODO uncommect this if you want image as button
        Image imageData = new Image(getShell().getDisplay(), new ByteArrayInputStream(activity.getImage()));
        imageData = Images.resize(getShell().getDisplay(), imageData);
        activityButton.setImage(imageData);

        activityButton.setLayoutData(RowDataFactory.swtDefaults().hint(Images.IMAGE_WIDTH, Images.IMAGE_HEIGHT).create());
        activityButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                applicationContext.getBean(ActivityComposite.class, shell, activity);
                dispose();
                shell.layout(true, true);
            }
        });
        return activityButton;
    }
}
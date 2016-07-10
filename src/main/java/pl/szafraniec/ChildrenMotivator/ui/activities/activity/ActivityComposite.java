package pl.szafraniec.ChildrenMotivator.ui.activities.activity;

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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
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
import pl.szafraniec.ChildrenMotivator.ui.activities.ActivitiesComposite;
import pl.szafraniec.ChildrenMotivator.ui.activities.dialog.EditActivityDialog;

import java.io.ByteArrayInputStream;

@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ActivityComposite extends AbstractMainComposite {

    @Autowired
    private ActivityRepository activityRepository;

    private Activity activity;

    private Composite activityComposite;

    private ScrolledComposite scrolledComposite;

    private Label label;

    public ActivityComposite(Composite parent, Activity activity) {
        super(parent, SWT.NONE);
        this.activity = activity;
    }

    @Override
    protected void createTopPart() {
        Composite topPart = new Composite(this, SWT.NONE);
        topPart.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).equalWidth(false).create());
        topPart.setLayoutData(GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).create());

        label = createLabel(topPart, activity.getName());
        createTopControlsButtonsComposite(topPart);
    }

    private Composite createTopControlsButtonsComposite(Composite parent) {
        Composite controlsButtonsComposite = new Composite(parent, SWT.NONE);
        controlsButtonsComposite.setLayoutData(GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.TOP).create());
        controlsButtonsComposite.setLayout(GridLayoutFactory.swtDefaults().numColumns(1).create());

        createBackButton(controlsButtonsComposite, applicationContext, shell, ActivitiesComposite.class);
        return controlsButtonsComposite;
    }

    @Override
    protected void createDownPart() {
        Composite downPart = new Composite(this, SWT.NONE);
        downPart.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).create());
        downPart.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
        createActivityComposite(downPart);
        createDownControlsButtonsComposite(downPart);
    }

    private Composite createActivityComposite(Composite parent) {
        scrolledComposite = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
        scrolledComposite.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
        scrolledComposite.setExpandVertical(true);
        scrolledComposite.setExpandHorizontal(true);

        activityComposite = new Composite(scrolledComposite, SWT.NONE);
        activityComposite.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
        activityComposite.setLayout(GridLayoutFactory.swtDefaults().numColumns(1).create());

        Label image = new Label(activityComposite, SWT.WRAP | SWT.BORDER);
        Image imageData = new Image(getShell().getDisplay(), new ByteArrayInputStream(activity.getImage()));
        imageData = Images.resize(getShell().getDisplay(), imageData);
        image.setImage(imageData);
        image.setLayoutData(GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.CENTER).grab(true, true).minSize(Images.IMAGE_WIDTH,
                Images.IMAGE_HEIGHT).hint(Images.IMAGE_WIDTH, Images.IMAGE_HEIGHT).create());
        image.pack(true);

        activityComposite.pack(true);
        scrolledComposite.setContent(activityComposite);
        scrolledComposite.addControlListener(new ControlAdapter() {
            public void controlResized(ControlEvent e) {
                Rectangle r = scrolledComposite.getClientArea();
                scrolledComposite.setMinSize(activityComposite.computeSize(r.width, SWT.DEFAULT));
            }
        });
        scrolledComposite.layout(true, true);
        return activityComposite;
    }

    private Composite createDownControlsButtonsComposite(Composite parent) {
        Composite controlsButtonsComposite = new Composite(parent, SWT.NONE);
        controlsButtonsComposite.setLayoutData(GridDataFactory.swtDefaults().grab(false, true).align(SWT.CENTER, SWT.FILL).create());
        controlsButtonsComposite.setLayout(GridLayoutFactory.swtDefaults().numColumns(1).create());

        createEditButton(controlsButtonsComposite);
        createRemoveButton(controlsButtonsComposite, this::removeActivity);
        return controlsButtonsComposite;
    }

    private void createEditButton(Composite parent) {
        Button addGroupButton = new Button(parent, SWT.PUSH);
        addGroupButton.setLayoutData(DEFAULT_CONTROL_BUTTON_FACTORY);
        addGroupButton.setText("Edytuj");
        addGroupButton.setFont(FontDescriptor.createFrom(Fonts.DEFAULT_FONT_DATA).createFont(addGroupButton.getDisplay()));
        addGroupButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                editActivity();
            }
        });
    }

    private void removeActivity() {
        activityRepository.delete(activity);
        activityRepository.flush();

        applicationContext.getBean(ActivitiesComposite.class, shell);
        dispose();
        shell.layout(true, true);
    }

    private void editActivity() {
        EditActivityDialog dialog = new EditActivityDialog(shell, activity.getName(), activity.getImage(), "Edytuj aktywność");
        if (Window.OK == dialog.open()) {
            editActivity(dialog.getActivityName(), dialog.getImageByte());

            scrolledComposite.layout(true, true);
        }
    }

    private void editActivity(String name, byte[] imageByte) {
        activity.setName(name);
        activity.setImage(imageByte);
        activity = activityRepository.saveAndFlush(activity);
        label.setText(activity.getName());
    }
}
package pl.szafraniec.ChildrenMotivator.ui.activitiesTableSchemes;

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
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.szafraniec.ChildrenMotivator.model.ActivitiesTableScheme;
import pl.szafraniec.ChildrenMotivator.model.Activity;
import pl.szafraniec.ChildrenMotivator.services.ActivitiesTableSchemeService;
import pl.szafraniec.ChildrenMotivator.ui.AbstractMainComposite;
import pl.szafraniec.ChildrenMotivator.ui.Fonts;
import pl.szafraniec.ChildrenMotivator.ui.Images;
import pl.szafraniec.ChildrenMotivator.ui.activitiesTableSchemes.dialogs.ActivitiesTableSchemeDialog;
import pl.szafraniec.ChildrenMotivator.ui.utils.ImageCanvas;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ActivitiesTableSchemeComposite extends AbstractMainComposite {

    @Autowired
    private ActivitiesTableSchemeService activitiesTableSchemeService;

    private ActivitiesTableScheme activitiesTableScheme;

    private Composite activitiesComposite;

    private ScrolledComposite scrolledComposite;

    private Label label;

    private Composite downPart;

    public ActivitiesTableSchemeComposite(Composite parent, ActivitiesTableScheme activitiesTableScheme) {
        super(parent, SWT.NONE);
        this.activitiesTableScheme = activitiesTableScheme;
    }

    @Override
    protected void createTopPart() {
        Composite topPart = new Composite(this, SWT.NONE);
        topPart.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).equalWidth(false).create());
        topPart.setLayoutData(GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).create());

        label = createLabel(topPart, activitiesTableScheme.getName());
        createTopControlsButtonsComposite(topPart);
    }

    private Composite createTopControlsButtonsComposite(Composite parent) {
        Composite controlsButtonsComposite = new Composite(parent, SWT.NONE);
        controlsButtonsComposite.setLayoutData(GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.TOP).create());
        controlsButtonsComposite.setLayout(GridLayoutFactory.swtDefaults().numColumns(1).create());

        createBackButton(controlsButtonsComposite, applicationContext, shell, ActivitiesTableSchemesComposite.class);
        return controlsButtonsComposite;
    }

    @Override
    protected Composite createDownPart() {
        downPart = new Composite(this, SWT.NONE);
        downPart.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).create());
        downPart.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
        createActivitiesComposite(downPart);
        createDownControlsButtonsComposite(downPart);
        return null;
    }

    private Composite createActivitiesComposite(Composite parent) {
        scrolledComposite = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
        scrolledComposite.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
        scrolledComposite.setExpandVertical(true);
        scrolledComposite.setExpandHorizontal(true);

        activitiesComposite = new Composite(scrolledComposite, SWT.NONE);
        activitiesComposite.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
        activitiesComposite.setLayout(RowLayoutFactory.swtDefaults().pack(false).spacing(25).wrap(true).type(SWT.HORIZONTAL).create());

        activitiesTableScheme.getListOfActivities().stream().map(activity -> createActivityButton(activity, activitiesComposite)).collect(
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
        ImageCanvas activityButton = new ImageCanvas(parent, SWT.WRAP | SWT.BORDER);
        activityButton.setText(activity.getName());
        activityButton.setToolTipText(activity.getName());
        activityButton.setFont(FontDescriptor.createFrom(Fonts.DEFAULT_FONT_DATA).createFont(activityButton.getDisplay()));

        activityButton.setImage(activity.getImage());

        activityButton.setLayoutData(RowDataFactory.swtDefaults().hint(Images.IMAGE_WIDTH, Images.IMAGE_HEIGHT).create());
        return activityButton;
    }

    private Composite createDownControlsButtonsComposite(Composite parent) {
        Composite controlsButtonsComposite = new Composite(parent, SWT.NONE);
        controlsButtonsComposite.setLayoutData(GridDataFactory.swtDefaults().grab(false, true).align(SWT.CENTER, SWT.FILL).create());
        controlsButtonsComposite.setLayout(GridLayoutFactory.swtDefaults().numColumns(1).create());

        createEditButton(controlsButtonsComposite);
        createRemoveButton(controlsButtonsComposite, this::removeActivitiesTableScheme,
                "Usunięte zostaną wszystkie tablice aktywności wraz z ocenami ze wszystkich dzieci.");
        return controlsButtonsComposite;
    }

    private void createEditButton(Composite parent) {
        Button addGroupButton = new Button(parent, SWT.PUSH);
        addGroupButton.setLayoutData(DEFAULT_CONTROL_BUTTON_FACTORY.create());
        addGroupButton.setText("Edytuj");
        addGroupButton.setFont(FontDescriptor.createFrom(Fonts.DEFAULT_FONT_DATA).createFont(addGroupButton.getDisplay()));
        addGroupButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                editActivitiesTableScheme();
            }
        });
    }

    private void removeActivitiesTableScheme() {
        activitiesTableSchemeService.remove(activitiesTableScheme);

        applicationContext.getBean(ActivitiesTableSchemesComposite.class, shell);
        dispose();
        shell.layout(true, true);
    }

    private void editActivitiesTableScheme() {
        ActivitiesTableSchemeDialog dialog = new ActivitiesTableSchemeDialog(shell, activitiesTableScheme.getName(),
                activitiesTableScheme.getListOfActivities(), "Edytuj schemat");
        applicationContext.getAutowireCapableBeanFactory().autowireBean(dialog);
        if (Window.OK == dialog.open()) {
            editActivitiesTableScheme(dialog.getActivitiesTableSchemeName(), dialog.getActivities());
            label.setText(activitiesTableScheme.getName());

            downPart.dispose();
            createDownPart();
            layout(true, true);
        }
    }

    private void editActivitiesTableScheme(String name, List<Activity> activities) {
        activitiesTableScheme = activitiesTableSchemeService.edit(activitiesTableScheme, name, activities);
    }
}
package pl.szafraniec.ChildrenMotivator.ui.activities.tableSchemes.tableScheme;

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
import org.eclipse.swt.widgets.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.szafraniec.ChildrenMotivator.model.ActivitiesTableScheme;
import pl.szafraniec.ChildrenMotivator.model.Activity;
import pl.szafraniec.ChildrenMotivator.model.TableCell;
import pl.szafraniec.ChildrenMotivator.repository.ActivitiesTableSchemesRepository;
import pl.szafraniec.ChildrenMotivator.repository.ChildActivitiesTableRepository;
import pl.szafraniec.ChildrenMotivator.ui.AbstractMainComposite;
import pl.szafraniec.ChildrenMotivator.ui.Fonts;
import pl.szafraniec.ChildrenMotivator.ui.Images;
import pl.szafraniec.ChildrenMotivator.ui.activities.tableSchemes.ActivitiesTableSchemesComposite;
import pl.szafraniec.ChildrenMotivator.ui.activities.tableSchemes.dialog.EditActivitiesTableSchemeDialog;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ActivitiesTableSchemeComposite extends AbstractMainComposite {

    @Autowired
    private ActivitiesTableSchemesRepository activitiesTableSchemesRepository;

    @Autowired
    private ChildActivitiesTableRepository childActivitiesTableRepository;

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
    protected void createDownPart() {
        downPart = new Composite(this, SWT.NONE);
        downPart.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).create());
        downPart.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
        createActivitiesComposite(downPart);
        createDownControlsButtonsComposite(downPart);
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
        Label activityButton = new Label(parent, SWT.WRAP | SWT.BORDER);
        activityButton.setText(activity.getName());
        activityButton.setToolTipText(activity.getName());
        activityButton.setFont(FontDescriptor.createFrom(Fonts.DEFAULT_FONT_DATA).createFont(activityButton.getDisplay()));

        Image imageData = new Image(getShell().getDisplay(), new ByteArrayInputStream(activity.getImage()));
        imageData = Images.resize(getShell().getDisplay(), imageData);
        activityButton.setImage(imageData);

        activityButton.setLayoutData(RowDataFactory.swtDefaults().hint(Images.IMAGE_WIDTH, Images.IMAGE_HEIGHT).create());
        return activityButton;
    }

    private Composite createDownControlsButtonsComposite(Composite parent) {
        Composite controlsButtonsComposite = new Composite(parent, SWT.NONE);
        controlsButtonsComposite.setLayoutData(GridDataFactory.swtDefaults().grab(false, true).align(SWT.CENTER, SWT.FILL).create());
        controlsButtonsComposite.setLayout(GridLayoutFactory.swtDefaults().numColumns(1).create());

        createEditButton(controlsButtonsComposite);
        createRemoveButton(controlsButtonsComposite, this::removeActivitiesTableScheme);
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
                editActivitiesTableScheme();
            }
        });
    }

    private void removeActivitiesTableScheme() {
        activitiesTableSchemesRepository.delete(activitiesTableScheme);
        activitiesTableSchemesRepository.flush();

        applicationContext.getBean(ActivitiesTableSchemesComposite.class, shell);
        dispose();
        shell.layout(true, true);
    }

    private void editActivitiesTableScheme() {
        EditActivitiesTableSchemeDialog dialog = new EditActivitiesTableSchemeDialog(shell, activitiesTableScheme.getName(),
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
        activitiesTableScheme.setName(name);
        activitiesTableScheme.setListOfActivities(activities);

        activitiesTableScheme.getChildActivitiesTables().stream().flatMap(table -> table.getDays().stream()).forEach(day -> {
            // remove all removed activities
            day.getGrades().keySet().stream().filter(activity -> !activities.contains(activity)).collect(Collectors.toList()).forEach(
                    activity -> day.getGrades().remove(activity));
            // add all new activities
            activities.stream().filter(activity -> !day.getGrades().keySet().contains(activity)).collect(Collectors.toList()).forEach(
                    activity -> day.getGrades().put(activity, TableCell.TableCellBuilder.create()));
        });

        activitiesTableScheme.setChildActivitiesTables(
                activitiesTableScheme.getChildActivitiesTables().stream().map(childActivitiesTableRepository::save).collect(
                        Collectors.toList()));
        activitiesTableSchemesRepository.saveAndFlush(activitiesTableScheme);
        activitiesTableScheme = activitiesTableSchemesRepository.getOne(activitiesTableScheme.getId());
    }
}
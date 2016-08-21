package pl.szafraniec.ChildrenMotivator.ui.activities.tableSchemes;

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
import org.eclipse.swt.widgets.Display;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.szafraniec.ChildrenMotivator.model.ActivitiesTableScheme;
import pl.szafraniec.ChildrenMotivator.services.ActivitiesTableSchemeService;
import pl.szafraniec.ChildrenMotivator.ui.AbstractMainComposite;
import pl.szafraniec.ChildrenMotivator.ui.Fonts;
import pl.szafraniec.ChildrenMotivator.ui.Images;
import pl.szafraniec.ChildrenMotivator.ui.activities.tableSchemes.dialog.EditActivitiesTableSchemeDialog;
import pl.szafraniec.ChildrenMotivator.ui.activities.tableSchemes.tableScheme.ActivitiesTableSchemeComposite;
import pl.szafraniec.ChildrenMotivator.ui.start.StartComposite;

import java.util.stream.Collectors;

@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ActivitiesTableSchemesComposite extends AbstractMainComposite {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ActivitiesTableSchemeService activitiesTableSchemeService;

    private Composite activitiesTableSchemeComposite;

    private ScrolledComposite scrolledComposite;

    public ActivitiesTableSchemesComposite(Composite parent) {
        super(parent, SWT.NONE);
    }

    @Override
    protected void createTopPart() {
        Composite topPart = new Composite(this, SWT.NONE);
        topPart.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).equalWidth(false).create());
        topPart.setLayoutData(GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).create());

        createLabel(topPart, "Schematy tablic aktywności");
        createControlsButtonsComposite(topPart);
    }

    private Composite createControlsButtonsComposite(Composite parent) {
        Composite controlsButtonsComposite = new Composite(parent, SWT.NONE);
        controlsButtonsComposite.setLayoutData(GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.TOP).create());
        controlsButtonsComposite.setLayout(GridLayoutFactory.swtDefaults().numColumns(1).create());

        createBackButton(controlsButtonsComposite, applicationContext, shell, StartComposite.class);
        createAddActivitiesTableSchemaButton(controlsButtonsComposite);
        return controlsButtonsComposite;
    }

    private void createAddActivitiesTableSchemaButton(Composite parent) {
        Button addActivityButton = new Button(parent, SWT.PUSH);
        addActivityButton.setLayoutData(DEFAULT_CONTROL_BUTTON_FACTORY.create());
        addActivityButton.setText("Dodaj schemat");
        addActivityButton.setFont(FontDescriptor.createFrom(Fonts.DEFAULT_FONT_DATA).createFont(addActivityButton.getDisplay()));
        addActivityButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                EditActivitiesTableSchemeDialog dialog = new EditActivitiesTableSchemeDialog(Display.getCurrent().getActiveShell());
                applicationContext.getAutowireCapableBeanFactory().autowireBean(dialog);
                if (Window.OK == dialog.open()) {
                    ActivitiesTableScheme scheme = activitiesTableSchemeService.addActivitiesTableSchema(dialog.getActivitiesTableSchemeName(),
                            dialog.getActivities());
                    createActivitiesTableSchemeButton(scheme, activitiesTableSchemeComposite);
                    scrolledComposite.layout(true, true);
                    Rectangle r = scrolledComposite.getClientArea();
                    scrolledComposite.setMinSize(activitiesTableSchemeComposite.computeSize(r.width, SWT.DEFAULT));
                }
            }
        });
    }

    @Override
    protected Composite createDownPart() {
        Composite downPart = new Composite(this, SWT.NONE);
        downPart.setLayout(GridLayoutFactory.swtDefaults().numColumns(1).create());
        downPart.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
        createActivitiesTableSchemesComposite(downPart);
        return downPart;
    }

    private Composite createActivitiesTableSchemesComposite(Composite parent) {
        scrolledComposite = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
        scrolledComposite.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
        scrolledComposite.setExpandVertical(true);
        scrolledComposite.setExpandHorizontal(true);

        activitiesTableSchemeComposite = new Composite(scrolledComposite, SWT.NONE);
        activitiesTableSchemeComposite.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
        activitiesTableSchemeComposite.setLayout(RowLayoutFactory.swtDefaults()
                .pack(false)
                .spacing(25)
                .wrap(true)
                .type(SWT.HORIZONTAL)
                .create());

        activitiesTableSchemeService.findAll().stream().map(
                activity -> createActivitiesTableSchemeButton(activity, activitiesTableSchemeComposite)).collect(Collectors.toList());

        activitiesTableSchemeComposite.pack(true);
        scrolledComposite.setContent(activitiesTableSchemeComposite);
        scrolledComposite.addControlListener(new ControlAdapter() {
            public void controlResized(ControlEvent e) {
                Rectangle r = scrolledComposite.getClientArea();
                scrolledComposite.setMinSize(activitiesTableSchemeComposite.computeSize(r.width, SWT.DEFAULT));
            }
        });
        scrolledComposite.layout(true, true);
        return activitiesTableSchemeComposite;
    }

    private Control createActivitiesTableSchemeButton(ActivitiesTableScheme activitiesTableScheme, Composite parent) {
        Button activitiesTableSchemeButton = new Button(parent, SWT.WRAP | SWT.PUSH);
        activitiesTableSchemeButton.setText(activitiesTableScheme.getName());
        activitiesTableSchemeButton.setFont(
                FontDescriptor.createFrom(Fonts.DEFAULT_FONT_DATA).createFont(activitiesTableSchemeButton.getDisplay()));

        activitiesTableSchemeButton.setLayoutData(RowDataFactory.swtDefaults().hint(Images.IMAGE_WIDTH, Images.IMAGE_HEIGHT).create());
        activitiesTableSchemeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                applicationContext.getBean(ActivitiesTableSchemeComposite.class, shell, activitiesTableScheme);
                dispose();
                shell.layout(true, true);
            }
        });
        return activitiesTableSchemeButton;
    }
}
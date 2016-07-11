package pl.szafraniec.ChildrenMotivator.ui.start;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.szafraniec.ChildrenMotivator.ui.AbstractMainComposite;
import pl.szafraniec.ChildrenMotivator.ui.Fonts;
import pl.szafraniec.ChildrenMotivator.ui.activities.ActivitiesComposite;
import pl.szafraniec.ChildrenMotivator.ui.activities.tableSchemes.ActivitiesTableSchemesComposite;
import pl.szafraniec.ChildrenMotivator.ui.gradesSchemes.GradesSchemesComposite;
import pl.szafraniec.ChildrenMotivator.ui.groups.ChildrenGroupsComposite;

@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class StartComposite extends AbstractMainComposite {

    public StartComposite(Composite parent) {
        super(parent, SWT.NONE);
    }

    protected void createTopPart() {
        Composite topPart = new Composite(this, SWT.NONE);
        topPart.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).equalWidth(false).create());
        topPart.setLayoutData(GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).create());

        createLabel(topPart, "Children Motivator");
    }

    protected void createDownPart() {
        Composite downPart = new Composite(this, SWT.NONE);
        downPart.setLayout(GridLayoutFactory.swtDefaults().numColumns(1).create());
        downPart.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.LEFT, SWT.TOP).create());
        createButton(downPart, "Grupy", ChildrenGroupsComposite.class);
        createButton(downPart, "Możliwe oceny", GradesSchemesComposite.class);
        createButton(downPart, "Możliwe aktywności", ActivitiesComposite.class);
        createButton(downPart, "Schematy tablic aktywności", ActivitiesTableSchemesComposite.class);
    }

    private void createButton(Composite parent, String text, Class compositeClass) {
        Button button = new Button(parent, SWT.PUSH);
        button.setLayoutData(GridDataFactory.swtDefaults()
                .align(SWT.LEFT, SWT.CENTER)
                .minSize(500, 35)
                .hint(500, 35)
                .span(SWT.DEFAULT, 10)
                .create());
        button.setText(text);
        button.setFont(FontDescriptor.createFrom(Fonts.DEFAULT_FONT_DATA).createFont(button.getDisplay()));
        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                applicationContext.getBean(compositeClass, shell);
                dispose();
                shell.layout(true, true);
            }
        });
    }
}

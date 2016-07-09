package pl.szafraniec.ChildrenMotivator.ui.activities;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.szafraniec.ChildrenMotivator.ui.AbstractMainComposite;
import pl.szafraniec.ChildrenMotivator.ui.start.StartComposite;

@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ActivitiesComposite extends AbstractMainComposite {

    public ActivitiesComposite(Composite parent) {
        super(parent, SWT.NONE);
    }

    protected void createTopPart() {
        Composite topPart = new Composite(this, SWT.NONE);
        topPart.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).equalWidth(false).create());
        topPart.setLayoutData(GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).create());

        createLabel(topPart, "Możliwe aktywności");
        createBackButton(topPart, applicationContext, shell, StartComposite.class);
    }

    protected void createDownPart() {
        Composite downPart = new Composite(this, SWT.NONE);
        downPart.setLayout(GridLayoutFactory.swtDefaults().numColumns(1).create());
        downPart.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.LEFT, SWT.TOP).create());
    }
}
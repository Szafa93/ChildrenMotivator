package pl.szafraniec.ChildrenMotivator.ui;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import pl.szafraniec.ChildrenMotivator.ui.start.StartComposite;

import javax.annotation.PostConstruct;

public abstract class AbstractMainComposite extends Composite {

    @Autowired
    protected Shell shell;

    @Autowired
    protected ApplicationContext applicationContext;

    public AbstractMainComposite(Composite parent, int style) {
        super(parent, style);
    }

    @PostConstruct
    public void postConstruct() {
        setLayoutData(GridDataFactory.swtDefaults().create());
        setLayout(GridLayoutFactory.swtDefaults().numColumns(1).create());

        createTopPart();
        createDownPart();

        layout(true, true);
    }

    protected abstract void createTopPart();

    protected abstract void createDownPart();

    protected Label createLabel(Composite parent, String text) {
        Label label = new Label(parent, SWT.WRAP);
        label.setText(text);
        label.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
        Font font = Fonts.MAIN_LABEL_FONT_DESCRIPTOR.createFont(label.getDisplay());
        label.setFont(font);
        return label;
    }

    protected Button createBackButton(Composite parent, ApplicationContext applicationContext, Composite composite) {
        Button button = new Button(parent, SWT.PUSH);
        button.setLayoutData(GridDataFactory.swtDefaults().align(SWT.END, SWT.CENTER).minSize(200, 35).hint(200, 35).create());
        button.setText("Wstecz");
        button.setFont(FontDescriptor.createFrom(Fonts.DEFAULT_FONT_DATA).setHeight(16).createFont(button.getDisplay()));
        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                applicationContext.getBean(StartComposite.class, composite);
                dispose();
                composite.layout(true, true);
            }
        });
        return button;
    }
}

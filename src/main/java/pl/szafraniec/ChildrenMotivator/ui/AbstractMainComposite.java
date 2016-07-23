package pl.szafraniec.ChildrenMotivator.ui;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;
import java.util.function.Supplier;

public abstract class AbstractMainComposite extends Composite {
    protected static final GridData DEFAULT_CONTROL_BUTTON_FACTORY = GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).minSize(200,
            35).hint(200, 35).create();
    
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

    protected Button createBackButton(Composite parent, ApplicationContext applicationContext, Composite composite, Class backClass) {
        return createBackButton(parent, applicationContext, composite, backClass, () -> new Object[0]);
    }

    protected Button createBackButton(Composite parent, ApplicationContext applicationContext, Composite composite, Class backClass,
            Supplier<Object[]> argsSupplier) {
        Button button = new Button(parent, SWT.PUSH);
        button.setLayoutData(DEFAULT_CONTROL_BUTTON_FACTORY);
        button.setText("Wstecz");
        button.setFont(FontDescriptor.createFrom(Fonts.DEFAULT_FONT_DATA).createFont(button.getDisplay()));
        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Object[] args = argsSupplier.get();
                Object[] constructorArgs = new Object[args.length + 1];
                constructorArgs[0] = composite;
                System.arraycopy(args, 0, constructorArgs, 1, args.length);
                applicationContext.getBean(backClass, constructorArgs);
                dispose();
                composite.layout(true, true);
            }
        });
        return button;
    }

    protected Button createRemoveButton(Composite parent, Runnable runnable) {
        Button removeButton = new Button(parent, SWT.PUSH);
        removeButton.setLayoutData(GridDataFactory.createFrom(DEFAULT_CONTROL_BUTTON_FACTORY)
                .grab(false, true)
                .align(SWT.FILL, SWT.END)
                .create());
        removeButton.setText("Usuń");
        removeButton.setFont(FontDescriptor.createFrom(Fonts.DEFAULT_FONT_DATA).createFont(removeButton.getDisplay()));
        removeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                if (MessageDialog.openConfirm(shell, "Potwierdzenie usunięcia",
                        "Usunięte zostaną wszystkie dane powiązane. Czy chcesz kontynuować?")) {
                    runnable.run();
                }
            }
        });
        return removeButton;
    }
}

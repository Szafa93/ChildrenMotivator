package pl.szafraniec.ChildrenMotivator;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import pl.szafraniec.ChildrenMotivator.ui.start.StartComposite;

public class Program {
    public static final ApplicationContext CTX = new ClassPathXmlApplicationContext("META-INF/applicationContext.xml");

    public void start() {
        Display display = CTX.getBean(Display.class);
        Shell shell = CTX.getBean(Shell.class);
        shell.setLayout(new FillLayout());
        shell.setSize(1115, 500);
        shell.setMinimumSize(1115, 500);
        CTX.getBean(StartComposite.class, (Composite) shell);
        shell.layout(true, true);
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
        display.dispose();
    }

    public static void main(String[] args) {
        new Program().start();
    }
}
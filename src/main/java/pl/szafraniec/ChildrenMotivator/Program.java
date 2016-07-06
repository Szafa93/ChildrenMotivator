package pl.szafraniec.ChildrenMotivator;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import pl.szafraniec.ChildrenMotivator.ui.groups.ChildrenGroupsComposite;

public class Program {
    private ApplicationContext ctx;

    Program() {
        ctx = new ClassPathXmlApplicationContext("META-INF/applicationContext.xml");
    }

    public void start() {
        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setLayout(new FillLayout());
        shell.setSize(500, 500);
        shell.setMinimumSize(500, 500);
        ctx.getBean(ChildrenGroupsComposite.class, (Composite) shell);
        shell.pack();
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
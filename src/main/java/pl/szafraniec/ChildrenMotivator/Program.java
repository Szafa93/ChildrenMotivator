package pl.szafraniec.ChildrenMotivator;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import pl.szafraniec.ChildrenMotivator.model.Configuration;
import pl.szafraniec.ChildrenMotivator.repository.ConfigurationRepository;
import pl.szafraniec.ChildrenMotivator.ui.start.StartComposite;

public class Program {
    public static final ApplicationContext CTX = new ClassPathXmlApplicationContext("META-INF/applicationContext.xml");

    public void start() {
        ConfigurationRepository configurationRepository = CTX.getBean(ConfigurationRepository.class);
        if (configurationRepository.count() != 1) {
            configurationRepository.saveAndFlush(new Configuration());
        }
        Display display = CTX.getBean(Display.class);
        Shell shell = CTX.getBean(Shell.class);
        shell.setLayout(new FillLayout());
        shell.setSize(750, 500);
        shell.setMinimumSize(750, 500);
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
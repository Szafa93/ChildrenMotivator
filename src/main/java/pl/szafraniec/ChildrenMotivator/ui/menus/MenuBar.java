package pl.szafraniec.ChildrenMotivator.ui.menus;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import pl.szafraniec.ChildrenMotivator.ui.groups.ChildrenGroupsComposite;

@Component
public class MenuBar {

    @Autowired
    private Shell shell;

    @Autowired
    private ApplicationContext ctx;

    private Menu menuBar;

    private Menu fileMenu;

    private Menu editMenu;

    private Menu viewMenu;

    private synchronized void build() {
        menuBar = new Menu(shell, SWT.BAR);
        fileMenu = createFileMenu();
        editMenu = createEditMenu();
        viewMenu = createViewMenu();
    }

    private Menu createFileMenu() {
        MenuItem fileMenuHeader = new MenuItem(menuBar, SWT.CASCADE);
        Menu fileMenu = new Menu(shell, SWT.DROP_DOWN);
        fileMenuHeader.setMenu(fileMenu);
        fileMenuHeader.setText("File");

        MenuItem exitItem = new MenuItem(fileMenu, SWT.PUSH);
        exitItem.setText("Exit");
        exitItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                shell.close();
            }
        });
        return fileMenu;
    }

    private Menu createEditMenu() {
        MenuItem editMenuHeader = new MenuItem(menuBar, SWT.CASCADE);
        Menu editMenu = new Menu(shell, SWT.DROP_DOWN);
        editMenuHeader.setMenu(editMenu);
        editMenuHeader.setText("Edit");

        return editMenu;
    }

    private Menu createViewMenu() {
        MenuItem viewMenuHeader = new MenuItem(menuBar, SWT.CASCADE);
        Menu viewMenu = new Menu(shell, SWT.DROP_DOWN);
        viewMenuHeader.setMenu(viewMenu);
        viewMenuHeader.setText("View");

        MenuItem showChildrenGroupsComposite = new MenuItem(viewMenu, SWT.PUSH);
        showChildrenGroupsComposite.setText("Children Groups");
        showChildrenGroupsComposite.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                for (Control child : shell.getChildren()) {
                    child.dispose();
                }
                ctx.getBean(ChildrenGroupsComposite.class, (Composite) shell);
                shell.layout(true, true);
            }
        });
        return viewMenu;
    }

    public synchronized Menu getMenuBar() {
        if (menuBar == null) {
            build();
        }
        return menuBar;
    }

    public synchronized Menu getFileMenu() {
        if (fileMenu == null) {
            build();
        }
        return fileMenu;
    }

    public synchronized Menu getEditMenu() {
        if (editMenu == null) {
            build();
        }
        return editMenu;
    }

    public synchronized Menu getViewMenu() {
        if (viewMenu == null) {
            build();
        }
        return viewMenu;
    }
}

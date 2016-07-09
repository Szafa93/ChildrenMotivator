package pl.szafraniec.ChildrenMotivator.ui;

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;

public interface Fonts {
    FontData DEFAULT_FONT_DATA = new FontData("Arial", 12, SWT.NORMAL);

    FontDescriptor MAIN_LABEL_FONT_DESCRIPTOR = FontDescriptor.createFrom(DEFAULT_FONT_DATA).setHeight(45);
}

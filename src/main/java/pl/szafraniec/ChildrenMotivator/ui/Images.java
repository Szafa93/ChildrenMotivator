package pl.szafraniec.ChildrenMotivator.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;

public interface Images {
    int IMAGE_WIDTH = 150;
    int IMAGE_HEIGHT = 150;

    static Image resize(Device device, Image image) {
        return resize(device, image, IMAGE_WIDTH, IMAGE_HEIGHT);
    }

    static Image resize(Device device, Image image, int width, int height) {
        Image scaled = new Image(device, width, height);
        GC gc = new GC(scaled);
        gc.setAntialias(SWT.ON);
        gc.setInterpolation(SWT.HIGH);
        gc.drawImage(image, 0, 0, image.getBounds().width, image.getBounds().height, 0, 0, width, height);
        gc.dispose();
        image.dispose(); // don't forget about me!
        return scaled;
    }
}

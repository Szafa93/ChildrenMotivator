package pl.szafraniec.ChildrenMotivator.ui.utils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import pl.szafraniec.ChildrenMotivator.ui.Images;

import java.io.ByteArrayInputStream;

public class ImageCanvas extends Canvas {
    private Image image;
    private String text;

    public ImageCanvas(Composite parent, int style) {
        super(parent, style);
        addPaintListener(e -> {
            e.gc.setAntialias(SWT.ON);
            e.gc.setInterpolation(SWT.HIGH);
            if (image != null) {
                e.gc.drawImage(image, 0, 0, image.getImageData().width, image.getImageData().height, 0, 0, Images.IMAGE_WIDTH, Images.IMAGE_HEIGHT);
            } else if (text != null) {
                Point extent = e.gc.stringExtent(text);
                e.gc.drawText(text, e.width / 2 - extent.x / 2, e.height / 2 - extent.y / 2);
            }
        });
    }

    public ImageCanvas(Composite parent, int style, byte[] bytes) {
        this(parent, style);
        setImage(bytes);
    }

    public void setImage(byte[] bytes) {
        ImageData imageData = new ImageData(new ByteArrayInputStream(bytes));
        this.image = new Image(getShell().getDisplay(), imageData);
        redraw();
    }

    public void setText(String text) {
        this.text = text;
    }
}

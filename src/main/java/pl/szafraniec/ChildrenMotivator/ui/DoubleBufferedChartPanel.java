package pl.szafraniec.ChildrenMotivator.ui;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.image.BufferedImage;

public class DoubleBufferedChartPanel extends ChartPanel {
    public DoubleBufferedChartPanel(JFreeChart chart, boolean useBuffer) {
        super(chart, useBuffer);
        setDoubleBuffered(true);
    }

    @Override
    public void paintComponent(Graphics graphics) {
        Image image = new BufferedImage(graphics.getClip().getBounds().width, graphics.getClip().getBounds().height, BufferedImage
                .TYPE_4BYTE_ABGR);
        super.paintComponent(image.getGraphics());
        Insets insets = getInsets();
        graphics.drawImage(image, insets.left, insets.top, this);
    }
}

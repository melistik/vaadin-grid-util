package org.vaadin.gridutil.client.renderer.indicator;

import com.google.gwt.core.shared.GWT;
import com.vaadin.v7.client.renderers.WidgetRenderer;
import com.vaadin.v7.client.widget.grid.RendererCellReference;

public class VIndicatorRenderer extends WidgetRenderer<Double, VIndicator> {

    private double startGreen = -1, startRed = -1;

    @Override
    public VIndicator createWidget() {
        return GWT.create(VIndicator.class);
    }

    @Override
    public void render(RendererCellReference cell, Double data,
        VIndicator indicator) {
        indicator.setValue(data, startGreen, startRed);
    }

    public void setConfig(double startGreen, double startRed) {
        this.startGreen = startGreen;
        this.startRed = startRed;
    }

}

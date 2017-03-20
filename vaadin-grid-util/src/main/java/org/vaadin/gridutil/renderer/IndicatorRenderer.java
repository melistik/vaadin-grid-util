package org.vaadin.gridutil.renderer;

import org.vaadin.gridutil.client.renderer.indicator.IndicatorRendererState;

import com.vaadin.v7.ui.Grid.AbstractRenderer;

public class IndicatorRenderer extends AbstractRenderer<Double> {

    public IndicatorRenderer(double startGreen, double startRed) {
        super(Double.class);
        getState().startGreen = startGreen;
        getState().startRed = startRed;
    }

    @Override
    protected IndicatorRendererState getState() {
        return (IndicatorRendererState) super.getState();
    }
}

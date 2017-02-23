package org.vaadin.gridutil.renderer;

import com.vaadin.shared.ui.grid.renderers.AbstractRendererState;
import com.vaadin.ui.renderers.AbstractRenderer;
import org.vaadin.gridutil.client.renderer.indicator.IndicatorRendererState;


public class IndicatorRenderer<T> extends AbstractRenderer<T, Double> {

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

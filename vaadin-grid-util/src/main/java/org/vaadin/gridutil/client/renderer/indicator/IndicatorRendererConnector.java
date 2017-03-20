package org.vaadin.gridutil.client.renderer.indicator;

import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.v7.client.connectors.AbstractRendererConnector;
import com.vaadin.shared.ui.Connect;

@Connect(org.vaadin.gridutil.renderer.IndicatorRenderer.class)
public class IndicatorRendererConnector extends AbstractRendererConnector<Double> {

    @Override
    public VIndicatorRenderer getRenderer() {
        return (VIndicatorRenderer) super.getRenderer();
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        getRenderer().setConfig(getState().startGreen, getState().startRed);
    }

    @Override
    public IndicatorRendererState getState() {
        return (IndicatorRendererState) super.getState();
    }
}

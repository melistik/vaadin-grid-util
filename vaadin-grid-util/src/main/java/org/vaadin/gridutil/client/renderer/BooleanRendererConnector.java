package org.vaadin.gridutil.client.renderer;

import com.vaadin.client.connectors.grid.AbstractGridRendererConnector;
import com.vaadin.shared.ui.Connect;

/**
 * connects client and server for BooleanRenderer
 *
 * @author Marten Prieß (http://www.rocketbase.io)
 * @version 1.0
 */
@Connect(org.vaadin.gridutil.renderer.BooleanRenderer.class)
public class BooleanRendererConnector extends AbstractGridRendererConnector<Boolean> {

    @Override
    public VBooleanRenderer getRenderer() {
        return (VBooleanRenderer) super.getRenderer();
    }
}

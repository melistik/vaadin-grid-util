package org.vaadin.gridutil.client.renderer;

import com.vaadin.client.connectors.AbstractRendererConnector;
import com.vaadin.shared.ui.Connect;

@Connect(org.vaadin.gridutil.renderer.BooleanRenderer.class)
public class BooleanRendererConnector extends AbstractRendererConnector<Boolean> {

	@Override
	public VBooleanRenderer getRenderer() {
		return (VBooleanRenderer) super.getRenderer();
	}
}

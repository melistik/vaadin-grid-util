package org.vaadin.gridutil.client.renderer;

import com.vaadin.client.renderers.Renderer;
import com.vaadin.client.widget.grid.RendererCellReference;

public class VBooleanRenderer implements Renderer<Boolean> {

	@Override
	public void render(final RendererCellReference cell, final Boolean data) {
		String output = "<center><span class=\"v-icon v-grid-cell-boolean ";
		if (data != null) {
			output = output + (data ? "boolean-true" : "boolean-false");
		} else {
			output = output + "boolean-null";
		}
		output = output + "\"></span></center>";
		cell.getElement()
				.setInnerHTML(output);
	}
}

package org.vaadin.gridutil.renderer;

import com.vaadin.v7.ui.renderers.ClickableRenderer;

/**
 * Add a view button next to the value (value is rendered as HTML)
 *
 * @author Marten Prieß (http://www.non-rocket-science.com)
 * @version 1.0
 */
public class ViewButtonValueRenderer extends ClickableRenderer<String> {

	/**
	 * "injects" a view button in the cell
	 * 
	 * @param listener
	 *            that get triggered on click on the button
	 */
	public ViewButtonValueRenderer(final RendererClickListener listener) {
		super(String.class);
		addClickListener(listener);
	}

}

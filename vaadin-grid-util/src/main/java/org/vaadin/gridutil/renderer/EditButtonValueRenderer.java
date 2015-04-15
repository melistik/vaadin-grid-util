package org.vaadin.gridutil.renderer;

import com.vaadin.ui.renderers.ClickableRenderer;

/**
 * Add an edit buttons next to the value (value is rendered as HTML)
 *
 * @author Marten Prieß (http://www.non-rocket-science.com)
 * @version 1.0
 */
public class EditButtonValueRenderer extends ClickableRenderer<String> {

	public EditButtonValueRenderer(final RendererClickListener listener) {
		super(String.class);
		addClickListener(listener);
	}

}

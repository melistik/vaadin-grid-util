package org.vaadin.gridutil.renderer;

import com.vaadin.v7.ui.Grid.AbstractRenderer;

/**
 * Renders boolean Values as icons
 *
 * @author Marten Prieß (http://www.non-rocket-science.com)
 * @version 1.0
 */
public class BooleanRenderer extends AbstractRenderer<Boolean> {

	/**
	 * simple boolean renderer that display true/false as icons
	 */
	public BooleanRenderer() {
		super(Boolean.class);
	}

}

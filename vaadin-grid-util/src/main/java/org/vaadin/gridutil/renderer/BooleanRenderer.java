package org.vaadin.gridutil.renderer;


import com.vaadin.ui.renderers.AbstractRenderer;

/**
 * Renders boolean Values as icons
 *
 * @author Marten Prieß (http://www.rocketbase.io)
 * @version 1.0
 */
public class BooleanRenderer<T> extends AbstractRenderer<T, Boolean> {

	/**
	 * simple boolean renderer that display true/false as icons
	 */
	public BooleanRenderer() {
		super(Boolean.class);
	}

}

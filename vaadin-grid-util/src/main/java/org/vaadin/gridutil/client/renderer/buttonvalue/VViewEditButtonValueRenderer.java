package org.vaadin.gridutil.client.renderer.buttonvalue;

/**
 * create the HTML for an view and edit button
 *
 * @author Marten Prieß (http://www.rocketbase.io)
 * @version 1.0
 */
public class VViewEditButtonValueRenderer extends VButtonValueRenderer {

	public VViewEditButtonValueRenderer() {
		super(VButtonValueRenderer.VIEW_BITM | VButtonValueRenderer.EDIT_BITM);
	}

}

package org.vaadin.gridutil.client.renderer.buttonvalue;

/**
 * create the HTML for an edit and delete button
 *
 * @author Marten Prieß (http://www.rocketbase.io)
 * @version 1.0
 */
public class VEditDeleteButtonValueRenderer extends VButtonValueRenderer {

	public VEditDeleteButtonValueRenderer() {
		super(VButtonValueRenderer.EDIT_BITM | VButtonValueRenderer.DELETE_BITM);
	}

}

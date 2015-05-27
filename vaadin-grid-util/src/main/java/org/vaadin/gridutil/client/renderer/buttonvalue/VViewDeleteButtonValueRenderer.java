package org.vaadin.gridutil.client.renderer.buttonvalue;

/**
 * create the HTML for an view and delete button
 *
 * @author Marten Prieß (http://www.non-rocket-science.com)
 * @version 1.0
 */
public class VViewDeleteButtonValueRenderer extends VButtonValueRenderer {

	public VViewDeleteButtonValueRenderer() {
		super(VButtonValueRenderer.VIEW_BITM | VButtonValueRenderer.DELETE_BITM);
	}

}

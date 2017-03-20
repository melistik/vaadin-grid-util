package org.vaadin.gridutil.renderer;

import org.vaadin.gridutil.client.renderer.buttonvalue.VButtonValueRenderer;

import com.vaadin.v7.ui.renderers.ClickableRenderer;

/**
 * Add an edit and delete buttons next to the value (value is rendered as HTML)
 *
 * @author Marten Prieß (http://www.non-rocket-science.com)
 * @version 1.0
 */
public class EditDeleteButtonValueRenderer extends ClickableRenderer<String> {

	/**
	 * specify the {@link RendererClickListener} by a hint which Button is clicked
	 */
	public interface EditDeleteButtonClickListener {

		/**
		 * get fired when editButton is clicked
		 * 
		 * @param event
		 *            clickEvent
		 */
		void onEdit(final RendererClickEvent event);

		/**
		 * get fired when deleteButton is clicked
		 * 
		 * @param event
		 *            clickEvent
		 */
		void onDelete(final RendererClickEvent event);

	}

	private final EditDeleteButtonClickListener listener;

	/**
	 * "injects" a delete button in the cell
	 * 
	 * @param listener
	 *            that get triggered on click on both buttons
	 */
	public EditDeleteButtonValueRenderer(final EditDeleteButtonClickListener listener) {
		super(String.class);
		this.listener = listener;

		addClickListener(new RendererClickListener() {

			@Override
			public void click(final com.vaadin.v7.ui.renderers.ClickableRenderer.RendererClickEvent event) {
				if (event.getRelativeX() == VButtonValueRenderer.EDIT_BITM) {
					listener.onEdit(event);
				} else {
					listener.onDelete(event);
				}
			}
		});
	}

}

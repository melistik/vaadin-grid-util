package org.vaadin.gridutil.renderer;

import org.vaadin.gridutil.client.renderer.buttonvalue.VButtonValueRenderer;

import com.vaadin.v7.ui.renderers.ClickableRenderer;

/**
 * Add view, edit and delete buttons next to the value (value is rendered as HTML)
 *
 * @author Marten Prieß (http://www.non-rocket-science.com)
 * @version 1.0
 */
public class ViewEditButtonValueRenderer extends ClickableRenderer<String> {

	/**
	 * specify the {@link RendererClickListener} by a hint which Button is clicked
	 */
	public interface ViewEditButtonClickListener {

		/**
		 * get fired when viewButton is clicked
		 * 
		 * @param event
		 *            clickEvent
		 */
		void onView(final RendererClickEvent event);

		/**
		 * get fired when editButton is clicked
		 * 
		 * @param event
		 *            clickEvent
		 */
		void onEdit(final RendererClickEvent event);

	}

	private final ViewEditButtonClickListener listener;

	/**
	 * "injects" view, edit and delete buttons in the cell
	 * 
	 * @param listener
	 *            that get triggered on click on both buttons
	 */
	public ViewEditButtonValueRenderer(final ViewEditButtonClickListener listener) {
		super(String.class);
		this.listener = listener;

		addClickListener(new RendererClickListener() {

			@Override
			public void click(final com.vaadin.v7.ui.renderers.ClickableRenderer.RendererClickEvent event) {
				if (event.getRelativeX() == VButtonValueRenderer.VIEW_BITM) {
					listener.onView(event);
				} else {
					listener.onEdit(event);
				}
			}
		});
	}

}

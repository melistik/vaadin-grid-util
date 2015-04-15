package org.vaadin.gridutil.renderer;

import com.vaadin.ui.renderers.ClickableRenderer;

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
		 */
		void onEdit(RendererClickEvent event);

		/**
		 * get fired when deleteButton is clicked
		 * 
		 * @param event
		 */
		void onDelete(RendererClickEvent event);

	}

	private final EditDeleteButtonClickListener listener;

	public EditDeleteButtonValueRenderer(final EditDeleteButtonClickListener listener) {
		super(String.class);
		this.listener = listener;

		addClickListener(new RendererClickListener() {

			@Override
			public void click(final com.vaadin.ui.renderers.ClickableRenderer.RendererClickEvent event) {
				if (event.getRelativeX() < 31) {
					listener.onEdit(event);
				} else {
					listener.onDelete(event);
				}
			}
		});
	}

}

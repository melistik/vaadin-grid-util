package org.vaadin.gridutil.renderer;

import com.vaadin.ui.renderers.ClickableRenderer;

/**
 * Add a delete buttons next to the value (value is rendered as HTML)
 *
 * @author Marten Prieß (http://www.rocketbase.io)
 * @version 1.0
 */
public class DeleteButtonValueRenderer<T> extends ClickableRenderer<T, String> {

    /**
     * "injects" a delete button in the cell
     *
     * @param listener that get triggered on click on the button
     */
    public DeleteButtonValueRenderer(final RendererClickListener<T> listener) {
        super(String.class);
        addClickListener(listener);
    }

}

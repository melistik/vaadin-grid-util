package org.vaadin.gridutil.renderer;

import com.vaadin.ui.renderers.ClickableRenderer;
import org.vaadin.gridutil.client.renderer.buttonvalue.VButtonValueRenderer;

/**
 * Add an edit and delete buttons next to the value (value is rendered as HTML)
 *
 * @author Marten Prieß (http://www.rocketbase.io)
 * @version 1.0
 */
public class EditDeleteButtonValueRenderer<T> extends ClickableRenderer<T, String> {

    public EditDeleteButtonValueRenderer(final RendererClickListener<T> editListener, final RendererClickListener<T> deleteListener) {
        super(String.class);

        addClickListener(event -> {
            if (event.getRelativeX() == VButtonValueRenderer.EDIT_BITM) {
                editListener.click(event);
            } else {
                deleteListener.click(event);
            }
        });
    }

}

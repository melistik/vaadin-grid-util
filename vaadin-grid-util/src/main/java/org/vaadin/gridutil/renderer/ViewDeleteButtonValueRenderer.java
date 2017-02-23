package org.vaadin.gridutil.renderer;

import com.vaadin.ui.renderers.ClickableRenderer;
import org.vaadin.gridutil.client.renderer.buttonvalue.VButtonValueRenderer;

/**
 * Add view, edit and delete buttons next to the value (value is rendered as HTML)
 *
 * @author Marten Prieß (http://www.rocketbase.io)
 * @version 1.0
 */
public class ViewDeleteButtonValueRenderer<T> extends ClickableRenderer<T, String> {

    public ViewDeleteButtonValueRenderer(final RendererClickListener<T> viewListener, final RendererClickListener<T> deleteListener) {
        super(String.class);

        addClickListener(event -> {
            if (event.getRelativeX() == VButtonValueRenderer.VIEW_BITM) {
                viewListener.click(event);
            } else {
                deleteListener.click(event);
            }
        });
    }
}

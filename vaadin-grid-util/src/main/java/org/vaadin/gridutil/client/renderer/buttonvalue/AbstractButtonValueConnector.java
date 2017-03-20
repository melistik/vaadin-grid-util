package org.vaadin.gridutil.client.renderer.buttonvalue;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.vaadin.client.MouseEventDetailsBuilder;
import com.vaadin.client.ServerConnector;
import com.vaadin.v7.client.widgets.Grid;
import com.vaadin.v7.client.connectors.AbstractRendererConnector;
import com.vaadin.v7.client.connectors.ClickableRendererConnector;
import com.vaadin.v7.client.renderers.ClickableRenderer.RendererClickEvent;
import com.vaadin.v7.client.renderers.ClickableRenderer.RendererClickHandler;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.v7.client.connectors.GridConnector;
import com.vaadin.v7.shared.ui.grid.renderers.RendererClickRpc;
import com.vaadin.v7.ui.renderers.ClickableRenderer;

import elemental.json.JsonObject;

/**
 * abstract connector that connects client and server for a ButtonValueRenderer<br>
 * this doesn't extend from {@link ClickableRendererConnector} because of the need for a hack within the onClick
 * 
 * @author Marten Prieß (http://www.non-rocket-science.com)
 * @version 1.0
 */
public abstract class AbstractButtonValueConnector<W extends VButtonValueRenderer> extends AbstractRendererConnector<String> {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	@Override
	public W getRenderer() {
		return (W) super.getRenderer();
	}

	HandlerRegistration clickRegistration;

	/**
	 * dirty hack because of the convertion within {@link ClickableRenderer} of {@link ClickEvent} to {@link NativeEvent} were the
	 * RelativeElement get lost... before rpc-call is fired ask Widget of it's last relativeX value that should be this current onClickEvent
	 */
	@Override
	protected void init() {
		this.clickRegistration = addClickHandler(new RendererClickHandler<JsonObject>() {

			@Override
			public void onClick(final RendererClickEvent<JsonObject> event) {
				MouseEventDetails details = MouseEventDetailsBuilder.buildMouseEventDetails(event.getNativeEvent());
				// get relativeX from Widget itself
				details.setRelativeX(getRenderer().getClickedBITM());

				getRpcProxy(RendererClickRpc.class).click(getRowKey(event.getCell()
						.getRow()), getColumnId(event.getCell()
						.getColumn()), details);
			}
		});
	}

	@Override
	public void onUnregister() {
		this.clickRegistration.removeHandler();
	}

	protected HandlerRegistration addClickHandler(final RendererClickHandler<JsonObject> handler) {
		return getRenderer().addClickHandler(handler);
	}

        // getRowKey and getColumnId are missing in compatibility package
        /**
         * Gets the row key for a row object.
         * <p>
         * In case this renderer wants be able to identify a row in such a way that
         * the server also understands it, the row key is used for that. Rows are
         * identified by unified keys between the client and the server.
         * 
         * @param row
         *            the row object
         * @return the row key for the given row
         */
        protected String getRowKey(JsonObject row) {
            final ServerConnector parent = getParent();
            if (parent instanceof GridConnector) {
                return ((GridConnector) parent).getRowKey(row);
            } else {
                throw new IllegalStateException("Renderers can only be used "
                        + "with a Grid.");
            }
        }

        /**
         * Gets the column id for a column.
         * <p>
         * In case this renderer wants be able to identify a column in such a way
         * that the server also understands it, the column id is used for that.
         * Columns are identified by unified ids between the client and the server.
         * 
         * @param column
         *            the column object
         * @return the column id for the given column
         */
        protected String getColumnId(Grid.Column<?, JsonObject> column) {
            final ServerConnector parent = getParent();
            if (parent instanceof GridConnector) {
                return ((GridConnector) parent).getColumnId(column);
            } else {
                throw new IllegalStateException("Renderers can only be used "
                        + "with a Grid.");
            }
        }
}

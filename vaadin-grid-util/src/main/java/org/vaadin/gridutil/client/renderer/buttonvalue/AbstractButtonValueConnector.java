package org.vaadin.gridutil.client.renderer.buttonvalue;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.vaadin.client.MouseEventDetailsBuilder;
import com.vaadin.client.connectors.ClickableRendererConnector;
import com.vaadin.client.connectors.grid.AbstractGridRendererConnector;
import com.vaadin.client.renderers.ClickableRenderer.RendererClickEvent;
import com.vaadin.client.renderers.ClickableRenderer.RendererClickHandler;
import com.vaadin.shared.MouseEventDetails;
import com.vaadin.shared.ui.grid.renderers.RendererClickRpc;
import com.vaadin.ui.renderers.ClickableRenderer;
import elemental.json.JsonObject;

/**
 * abstract connector that connects client and server for a ButtonValueRenderer<br>
 * this doesn't extend from {@link ClickableRendererConnector} because of the need for a hack within the onClick
 * 
 * @author Marten Prieß (http://www.rocketbase.io)
 * @version 1.0
 */
public abstract class AbstractButtonValueConnector<W extends VButtonValueRenderer> extends AbstractGridRendererConnector<String> {

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

}

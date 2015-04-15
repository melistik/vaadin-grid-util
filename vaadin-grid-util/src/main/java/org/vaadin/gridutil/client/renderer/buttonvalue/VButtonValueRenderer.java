package org.vaadin.gridutil.client.renderer.buttonvalue;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.vaadin.client.renderers.ClickableRenderer;
import com.vaadin.client.widget.grid.RendererCellReference;

/**
 * UpperClass for all ButtonValueVariations: draws a FlowPanel, adds a HTML widget that could handle clicks and add a label for value
 *
 * @author Marten Prieß (http://www.non-rocket-science.com)
 * @version 1.0
 */
public class VButtonValueRenderer extends ClickableRenderer<String, FlowPanel> {

	private static String STYLE_NAME = "v-button-value-cell";
	private int relativeX = -1;
	private final boolean editBtn, deleteBtn;

	public VButtonValueRenderer(final boolean editBtn, final boolean deleteBtn) {
		super();
		this.editBtn = editBtn;
		this.deleteBtn = deleteBtn;
	}

	@Override
	public FlowPanel createWidget() {
		HTML buttonHtml = GWT.create(HTML.class);
		buttonHtml.setStylePrimaryName("v-button-bar");

		String html = "";
		if (this.editBtn) {
			html = html + "<button type=\"button\" class=\"v-nativebutton v-edit\"><span></span></button> ";
		}
		if (this.deleteBtn) {
			html = html + "<button type=\"button\" class=\"v-nativebutton v-delete\"><span></span></button>";
		}

		buttonHtml.setHTML(html);
		buttonHtml.addClickHandler(this);

		FlowPanel panel = GWT.create(FlowPanel.class);
		panel.setStylePrimaryName(STYLE_NAME);
		if (this.editBtn && this.deleteBtn) {
			panel.addStyleName("two-buttons");
		}
		panel.add(buttonHtml);

		HTML valueLabel = GWT.create(HTML.class);
		valueLabel.setStylePrimaryName("v-cell-value");
		panel.add(valueLabel);
		return panel;
	}

	public int getRelativeX() {
		return this.relativeX;
	}

	@Override
	public void render(final RendererCellReference cell, final String text, final FlowPanel panel) {
		((HTML) panel.getWidget(1)).setHTML(text);
	}

	/**
	 * dirty hack - before we fire onClick we keep last relativeX value because of the lost of RelativeElement during convertion from
	 * {@link ClickEvent} to {@link NativeEvent} this value get read from connector
	 */
	@Override
	public void onClick(final ClickEvent event) {
		if (event.getRelativeElement() != null) {
			this.relativeX = event.getRelativeX(event.getRelativeElement());
		}
		super.onClick(event);
	}

}

package org.vaadin.gridutil.client.renderer.buttonvalue;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.vaadin.client.renderers.ClickableRenderer;
import com.vaadin.client.widget.grid.RendererCellReference;

/**
 * UpperClass for all ButtonValueVariations: draws a FlowPanel, adds a HTML widget that could handle clicks and add a label for value
 *
 * @author Marten Prieß (http://www.rocketbase.io)
 * @version 1.0
 */
public class VButtonValueRenderer extends ClickableRenderer<String, FlowPanel> {

	private static String STYLE_NAME = "v-button-value-cell";

	public static final int VIEW_BITM = 4;
	public static final int EDIT_BITM = 16;
	public static final int DELETE_BITM = 32;

	private int clickedBITM = 0;
	private final int buttonBITM;

	public VButtonValueRenderer(final int buttonBITM) {
		super();
		this.buttonBITM = buttonBITM;
	}

	private Button genButton(final int bitm) {
		Button btn = GWT.create(Button.class);
		btn.setStylePrimaryName("v-nativebutton");
		switch (bitm) {
			case VIEW_BITM:
				btn.addStyleName("v-view");
				break;
			case EDIT_BITM:
				btn.addStyleName("v-edit");
				break;
			case DELETE_BITM:
				btn.addStyleName("v-delete");
				break;

		}
		btn.setHTML("<span></span>");
		btn.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				VButtonValueRenderer.this.clickedBITM = bitm;
				VButtonValueRenderer.super.onClick(event);
			}
		});
		return btn;
	}

	/**
	 * dirty hack - before we fire onClick we keep last clicked button because of the lost of RelativeElement during converting and the
	 * issue of different layouts
	 */
	@Override
	public FlowPanel createWidget() {
		FlowPanel buttonBar = GWT.create(FlowPanel.class);
		buttonBar.setStylePrimaryName("v-button-bar");

		int buttonsAdded = 0;
		if ((this.buttonBITM & VIEW_BITM) != 0) {
			buttonBar.add(genButton(VIEW_BITM));
			buttonsAdded++;
		}
		if ((this.buttonBITM & EDIT_BITM) != 0) {
			buttonBar.add(genButton(EDIT_BITM));
			buttonsAdded++;
		}
		if ((this.buttonBITM & DELETE_BITM) != 0) {
			buttonBar.add(genButton(DELETE_BITM));
			buttonsAdded++;
		}

		FlowPanel panel = GWT.create(FlowPanel.class);
		panel.setStylePrimaryName(STYLE_NAME);
		if (buttonsAdded == 3) {
			panel.addStyleName("three-buttons");
		} else if (buttonsAdded == 2) {
			panel.addStyleName("two-buttons");
		} else {
			panel.addStyleName("one-button");
		}
		panel.add(buttonBar);

		HTML valueLabel = GWT.create(HTML.class);
		valueLabel.setStylePrimaryName("v-cell-value");
		panel.add(valueLabel);
		return panel;
	}

	public int getClickedBITM() {
		return this.clickedBITM;
	}

	@Override
	public void render(final RendererCellReference cell, final String text, final FlowPanel panel) {
		((HTML) panel.getWidget(1)).setHTML(text);
	}

}

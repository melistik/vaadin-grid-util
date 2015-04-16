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
 * @author Marten Prieß (http://www.non-rocket-science.com)
 * @version 1.0
 */
public class VButtonValueRenderer extends ClickableRenderer<String, FlowPanel> {

	private static String STYLE_NAME = "v-button-value-cell";
	private boolean firstButtonClicked = true;
	private final boolean editBtn, deleteBtn;

	public VButtonValueRenderer(final boolean editBtn, final boolean deleteBtn) {
		super();
		this.editBtn = editBtn;
		this.deleteBtn = deleteBtn;
	}

	/**
	 * dirty hack - before we fire onClick we keep last clicked button because of the lost of RelativeElement during converting and the
	 * issue of different layouts
	 */
	@Override
	public FlowPanel createWidget() {
		FlowPanel buttonBar = GWT.create(FlowPanel.class);
		buttonBar.setStylePrimaryName("v-button-bar");

		if (this.editBtn) {
			Button editBtn = GWT.create(Button.class);
			editBtn.setStylePrimaryName("v-nativebutton");
			editBtn.addStyleName("v-edit");
			editBtn.setHTML("<span></span>");
			editBtn.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(final ClickEvent event) {
					VButtonValueRenderer.this.firstButtonClicked = true;
					VButtonValueRenderer.super.onClick(event);
				}
			});
			buttonBar.add(editBtn);
		}
		if (this.deleteBtn) {
			Button deleteBtn = GWT.create(Button.class);
			deleteBtn.setStylePrimaryName("v-nativebutton");
			deleteBtn.addStyleName("v-delete");
			deleteBtn.setHTML("<span></span>");
			deleteBtn.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(final ClickEvent event) {
					VButtonValueRenderer.this.firstButtonClicked = false;
					VButtonValueRenderer.super.onClick(event);
				}
			});
			buttonBar.add(deleteBtn);
		}

		FlowPanel panel = GWT.create(FlowPanel.class);
		panel.setStylePrimaryName(STYLE_NAME);
		if (this.editBtn && this.deleteBtn) {
			panel.addStyleName("two-buttons");
		}
		panel.add(buttonBar);

		HTML valueLabel = GWT.create(HTML.class);
		valueLabel.setStylePrimaryName("v-cell-value");
		panel.add(valueLabel);
		return panel;
	}

	public boolean isFirstButtonClicked() {
		return this.firstButtonClicked;
	}

	@Override
	public void render(final RendererCellReference cell, final String text, final FlowPanel panel) {
		((HTML) panel.getWidget(1)).setHTML(text);
	}

}

package org.vaadin.gridutil.demo;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import javax.servlet.annotation.WebServlet;

import org.vaadin.gridutil.GridUtil;
import org.vaadin.gridutil.cell.CellFilterChangedListener;
import org.vaadin.gridutil.cell.GridCellFilter;
import org.vaadin.gridutil.converter.SimpleStringConverter;
import org.vaadin.gridutil.demo.data.Country;
import org.vaadin.gridutil.demo.data.DummyDataGen;
import org.vaadin.gridutil.demo.data.Inhabitants;
import org.vaadin.gridutil.renderer.BooleanRenderer;
import org.vaadin.gridutil.renderer.DeleteButtonValueRenderer;
import org.vaadin.gridutil.renderer.EditDeleteButtonValueRenderer;
import org.vaadin.gridutil.renderer.EditDeleteButtonValueRenderer.EditDeleteButtonClickListener;

import com.google.gwt.i18n.server.testing.Gender;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Container.ItemSetChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.CellReference;
import com.vaadin.ui.Grid.CellStyleGenerator;
import com.vaadin.ui.Grid.FooterRow;
import com.vaadin.ui.Grid.HeaderRow;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.ClickableRenderer.RendererClickEvent;
import com.vaadin.ui.renderers.ClickableRenderer.RendererClickListener;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.themes.ValoTheme;

@Theme("valo")
@Title("GridUtil")
@SuppressWarnings("serial")
public class DemoUI extends UI {

	@WebServlet(
			value = "/*",
			asyncSupported = true)
	@VaadinServletConfiguration(
			productionMode = false,
			ui = DemoUI.class,
			widgetset = "org.vaadin.gridutil.demo.DemoWidgetSet")
	public static class Servlet extends VaadinServlet {
	}

	@Override
	protected void init(final VaadinRequest request) {

		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();
		layout.setMargin(true);
		layout.setSpacing(true);

		Grid grid = genGrid();
		layout.addComponent(grid);
		layout.setExpandRatio(grid, 1);

		setContent(layout);

	}

	private GridCellFilter filter;

	private Grid genGrid() {
		// init Grid
		final Grid grid = new Grid();
		grid.setSizeFull();

		// init Container
		BeanItemContainer<Inhabitants> container = new BeanItemContainer<Inhabitants>(Inhabitants.class, DummyDataGen.genInhabitants(1000));
		grid.setContainerDataSource(container);
		grid.setColumnOrder("id", "gender", "name", "bodySize", "birthday", "onFacebook", "country");

		setColumnRenderes(grid);

		initFilter(grid);
		initFooterRow(grid, container);
		initExtraHeaderRow(grid);

		initColumnAlignments(grid);
		return grid;
	}

	private void setColumnRenderes(final Grid grid) {
		grid.getColumn("id")
				.setRenderer(new EditDeleteButtonValueRenderer(new EditDeleteButtonClickListener() {

					@Override
					public void onEdit(final RendererClickEvent event) {
						Notification.show(event.getItemId()
								.toString() + " want's to get edited", Type.HUMANIZED_MESSAGE);
					}

					@Override
					public void onDelete(final com.vaadin.ui.renderers.ClickableRenderer.RendererClickEvent event) {
						Notification.show(event.getItemId()
								.toString() + " want's to get delete", Type.WARNING_MESSAGE);
					};

				}))
				.setWidth(150);
		grid.getColumn("bodySize")
				.setWidth(150);
		grid.getColumn("birthday")
				.setRenderer(new DateRenderer(DateFormat.getDateInstance()))
				.setWidth(210);
		grid.getColumn("onFacebook")
				.setRenderer(new BooleanRenderer())
				.setWidth(130);

		grid.getColumn("country")
				.setRenderer(new DeleteButtonValueRenderer(new RendererClickListener() {

					@Override
					public void click(final RendererClickEvent event) {
						Notification.show("country shoud get deleted in line: " + ((Inhabitants) event.getItemId()).getId(), Type.ERROR_MESSAGE);
					}
				}), new SimpleStringConverter<Country>(Country.class) {

					@Override
					public String convertToPresentation(final Country value, final Class<? extends String> targetType, final Locale locale)
							throws com.vaadin.data.util.converter.Converter.ConversionException {
						return String.format("%s <i>(%d)</i>", value.getName(), value.getPopulation());
					}
				});
	}

	/**
	 * generates a simple totalCount footer
	 * 
	 * @param grid
	 * @param container
	 */
	private void initFooterRow(final Grid grid, final BeanItemContainer<Inhabitants> container) {
		final FooterRow footerRow = grid.appendFooterRow();
		footerRow.getCell("id")
				.setHtml("total:");
		footerRow.join("gender", "name", "bodySize", "birthday", "onFacebook", "country");
		// inital total count
		footerRow.getCell("gender")
				.setHtml("<b>" + container.getItemIds()
						.size() + "</b>");
		// filter change count recalculate
		container.addItemSetChangeListener(new ItemSetChangeListener() {

			@Override
			public void containerItemSetChange(final ItemSetChangeEvent event) {
				footerRow.getCell("gender")
						.setHtml("<b>" + event.getContainer()
								.getItemIds()
								.size() + "</b>");
			}
		});
	}

	/**
	 * initialize a GridCellFilter
	 * 
	 * @param grid
	 */
	private void initFilter(final Grid grid) {
		this.filter = new GridCellFilter(grid);
		this.filter.setNumberFilter("id");

		// set gender Combo with custom icons
		ComboBox genderCombo = this.filter.setComboBoxFilter("gender", Arrays.asList(Gender.MALE, Gender.FEMALE));
		genderCombo.setItemIcon(Gender.MALE, FontAwesome.MALE);
		genderCombo.setItemIcon(Gender.FEMALE, FontAwesome.FEMALE);

		// simple filters
		this.filter.setTextFilter("name", true, true);
		this.filter.setNumberFilter("bodySize");
		this.filter.setDateFilter("birthday");
		this.filter.setBooleanFilter("onFacebook");

		// set country combo with custom caption
		ComboBox countryCombo = this.filter.setComboBoxFilter("country", DummyDataGen.COUNTRIES);
		countryCombo.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		countryCombo.setItemCaptionPropertyId("name");
	}

	/**
	 * interacts with the GridCellFilter
	 * 
	 * @param grid
	 */
	private void initExtraHeaderRow(final Grid grid) {
		HeaderRow fistHeaderRow = grid.prependHeaderRow();
		fistHeaderRow.join("id", "gender", "name", "bodySize", "birthday");
		fistHeaderRow.getCell("id")
				.setHtml("GridCellFilter simplify the filter settings for a grid");
		fistHeaderRow.join("onFacebook", "country");
		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		fistHeaderRow.getCell("onFacebook")
				.setComponent(buttonLayout);
		Button clearAllFilters = new Button("clearAllFilters", new Button.ClickListener() {

			@Override
			public void buttonClick(final ClickEvent event) {
				DemoUI.this.filter.clearAllFilters();
			}
		});
		clearAllFilters.setIcon(FontAwesome.TIMES);
		clearAllFilters.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
		buttonLayout.addComponent(clearAllFilters);

		final Button changeVisibility = new Button("changeVisibility");
		changeVisibility.addClickListener(new Button.ClickListener() {

			private boolean visibile = true;

			@Override
			public void buttonClick(final ClickEvent event) {
				this.visibile = !this.visibile;
				changeVisibility.setIcon(this.visibile ? FontAwesome.EYE_SLASH : FontAwesome.EYE);
				DemoUI.this.filter.setVisible(this.visibile);
				Notification.show("changed visibility to: " + this.visibile + " it has removed the header row!");
			}
		});
		changeVisibility.setIcon(FontAwesome.EYE_SLASH);
		changeVisibility.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
		buttonLayout.addComponent(changeVisibility);

		// listener's on filter
		this.filter.addCellFilterChangedListener(new CellFilterChangedListener() {

			@Override
			public void changedFilter(final GridCellFilter cellFilter) {
				Notification.show("cellFilter changed " + new Date().toLocaleString(), Type.TRAY_NOTIFICATION);
			}
		});
	}

	/**
	 * uses the inbuild alignments
	 * 
	 * @param grid
	 */
	private void initColumnAlignments(final Grid grid) {
		grid.setCellStyleGenerator(new CellStyleGenerator() {
			@Override
			public String getStyle(final CellReference cellReference) {
				if (cellReference.getPropertyId()
						.equals("id")) {
					return GridUtil.ALIGN_CELL_RIGHT;
				} else if (cellReference.getPropertyId()
						.equals("birthday")) {
					return GridUtil.ALIGN_CELL_CENTER;
				} else {
					return null;
				}
			}
		});
	}

}

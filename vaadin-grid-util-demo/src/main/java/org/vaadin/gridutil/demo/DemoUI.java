package org.vaadin.gridutil.demo;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import javax.servlet.annotation.WebServlet;

import org.vaadin.gridutil.cell.CellFilterChangedListener;
import org.vaadin.gridutil.cell.GridCellFilter;
import org.vaadin.gridutil.converter.SimpleStringConverter;
import org.vaadin.gridutil.demo.data.Country;
import org.vaadin.gridutil.demo.data.DummyDataGen;
import org.vaadin.gridutil.demo.data.Inhabitants;
import org.vaadin.gridutil.renderer.BooleanRenderer;

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
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.renderers.NumberRenderer;
import com.vaadin.ui.themes.ValoTheme;

@Theme("valo")
@Title("MyComponent Add-on Demo")
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

	private Grid genGrid() {
		// init Grid
		Grid grid = new Grid();
		grid.setSizeFull();

		// handle columns
		grid.addColumn("id", Long.class)
				.setRenderer(new NumberRenderer("%d"))
				.setWidth(200);
		grid.addColumn("gender", Enum.class);
		grid.addColumn("name");
		grid.addColumn("bodySize", Double.class);
		grid.addColumn("birthday", Date.class)
				.setRenderer(new DateRenderer(DateFormat.getDateInstance()))
				.setWidth(210);
		grid.addColumn("onFacebook", Boolean.class)
				.setRenderer(new BooleanRenderer());
		grid.addColumn("country", Country.class)
				.setRenderer(new HtmlRenderer(), new SimpleStringConverter<Country>(Country.class) {

					@Override
					public String convertToPresentation(final Country value, final Class<? extends String> targetType, final Locale locale)
							throws com.vaadin.data.util.converter.Converter.ConversionException {
						return String.format("%s <i>(%d)</i>", value.getName(), value.getPopulation());
					}

				});

		// init Container with footer total count
		BeanItemContainer<Inhabitants> container = new BeanItemContainer<Inhabitants>(Inhabitants.class, DummyDataGen.genInhabitants(1000));
		grid.setContainerDataSource(container);

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

		// init filter
		final GridCellFilter filter = new GridCellFilter(grid);
		filter.setNumberFilter("id");

		// set gender Combo with custom icons
		ComboBox genderCombo = filter.setComboBoxFilter("gender", Arrays.asList(Gender.MALE, Gender.FEMALE));
		genderCombo.setItemIcon(Gender.MALE, FontAwesome.MALE);
		genderCombo.setItemIcon(Gender.FEMALE, FontAwesome.FEMALE);

		// simple filters
		filter.setTextFilter("name", true, true);
		filter.setNumberFilter("bodySize");
		filter.setDateFilter("birthday");
		filter.setBooleanFilter("onFacebook");

		// set country combo with custom caption
		ComboBox countryCombo = filter.setComboBoxFilter("country", DummyDataGen.COUNTRIES);
		countryCombo.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		countryCombo.setItemCaptionPropertyId("name");

		// interact with GridCellFilter
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
				filter.clearAllFilters();
			}
		});
		clearAllFilters.setIcon(FontAwesome.TIMES);
		clearAllFilters.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
		buttonLayout.addComponent(clearAllFilters);

		// listener's on filter
		filter.addCellFilterChangedListener(new CellFilterChangedListener() {

			@Override
			public void changedFilter(final GridCellFilter cellFilter) {
				Notification.show("cellFilter changed " + new Date().toLocaleString(), Type.TRAY_NOTIFICATION);
			}
		});

		// mark column for alignments
		grid.setCellStyleGenerator(new CellStyleGenerator() {
			@Override
			public String getStyle(final CellReference cellReference) {
				return cellReference.getPropertyId()
						.equals("id") ? "rightalign" : null;
			}
		});
		return grid;
	}
}

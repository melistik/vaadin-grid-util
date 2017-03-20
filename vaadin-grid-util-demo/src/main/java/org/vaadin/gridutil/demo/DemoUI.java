package org.vaadin.gridutil.demo;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.v7.data.Container.Filter;
import com.vaadin.v7.data.Container.ItemSetChangeEvent;
import com.vaadin.v7.data.Container.ItemSetChangeListener;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.Property.ValueChangeEvent;
import com.vaadin.v7.data.Property.ValueChangeListener;
import com.vaadin.v7.data.util.BeanItemContainer;
import com.vaadin.v7.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.v7.ui.*;
import com.vaadin.v7.ui.Grid.CellReference;
import com.vaadin.v7.ui.Grid.CellStyleGenerator;
import com.vaadin.v7.ui.Grid.FooterRow;
import com.vaadin.v7.ui.Grid.HeaderRow;
import com.vaadin.v7.ui.renderers.ClickableRenderer.RendererClickEvent;
import com.vaadin.v7.ui.renderers.ClickableRenderer.RendererClickListener;
import com.vaadin.v7.ui.renderers.DateRenderer;
import org.vaadin.gridutil.GridUtil;
import org.vaadin.gridutil.cell.CellFilterChangedListener;
import org.vaadin.gridutil.cell.CellFilterComponent;
import org.vaadin.gridutil.cell.GridCellFilter;
import org.vaadin.gridutil.cell.RangeCellFilterComponent;
import org.vaadin.gridutil.converter.SimpleStringConverter;
import org.vaadin.gridutil.demo.data.Country;
import org.vaadin.gridutil.demo.data.Country.Continent;
import org.vaadin.gridutil.demo.data.DummyDataGen;
import org.vaadin.gridutil.demo.data.Inhabitants;
import org.vaadin.gridutil.renderer.BooleanRenderer;
import org.vaadin.gridutil.renderer.EditButtonValueRenderer;
import org.vaadin.gridutil.renderer.EditDeleteButtonValueRenderer;
import org.vaadin.gridutil.renderer.IndicatorRenderer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumSet;
import java.util.Locale;


@SpringUI()
@Widgetset("org.vaadin.gridutil.demo.DemoWidgetSet")
public class DemoUI extends UI {

    private GridCellFilter filter;

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
                .setRenderer(new EditDeleteButtonValueRenderer(new EditDeleteButtonValueRenderer.EditDeleteButtonClickListener() {

                    @Override
                    public void onEdit(final RendererClickEvent event) {
                        Notification.show(event.getItemId()
                                .toString() + " want's to get edited", Type.HUMANIZED_MESSAGE);
                    }

                    @Override
                    public void onDelete(final RendererClickEvent event) {
                        Notification.show(event.getItemId()
                                .toString() + " want's to get deleted", Type.WARNING_MESSAGE);
                    }
                }))
                .setWidth(160);

        grid.getColumn("bodySize")
                .setRenderer(new IndicatorRenderer(1.8, 1.1))
                .setWidth(150);
        grid.getColumn("birthday")
                .setRenderer(new DateRenderer(DateFormat.getDateInstance()))
                .setWidth(210);
        grid.getColumn("onFacebook")
                .setRenderer(new BooleanRenderer())
                .setWidth(130);

		/*
         * the icon of the editButton will get overwritten below by css styling @see DemoUI.initColumnAlignments
		 */
        grid.getColumn("country")
                .setRenderer(new EditButtonValueRenderer(new RendererClickListener() {

                    @Override
                    public void click(final RendererClickEvent event) {
                        Notification.show("Goto Link for " + ((Inhabitants) event.getItemId()).getCountry()
                                .getName(), Type.HUMANIZED_MESSAGE);
                    }
                }), new SimpleStringConverter<Country>(Country.class) {

                    @Override
                    public String convertToPresentation(final Country value, final Class<? extends String> targetType, final Locale locale)
                            throws com.vaadin.v7.data.util.converter.Converter.ConversionException {
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
     * example of a custom FilterComponent
     *
     * @param cellFilter needed to link filter to container
     * @param columnId
     * @return
     */
    private CellFilterComponent<HorizontalLayout> customFilterComponent(final GridCellFilter cellFilter, final String columnId) {
        CellFilterComponent<HorizontalLayout> filter = new CellFilterComponent<HorizontalLayout>() {

            ComboBox comboBox = new ComboBox();

            @Override
            public void triggerUpdate() {
                if (comboBox.getValue() != null) {
                    // this will add filter to container and replace old version if existing
                    cellFilter.replaceFilter(new CustomFilter(columnId, (Continent) comboBox.getValue()), columnId);
                } else {
                    // remove filter by columnId
                    cellFilter.removeFilter(columnId);
                }
            }

            @Override
            public HorizontalLayout layoutComponent() {
                BeanItemContainer<Continent> container = new BeanItemContainer<Continent>(Continent.class, EnumSet.allOf(Continent.class));

                this.comboBox.setNullSelectionAllowed(true);
                this.comboBox.setImmediate(true);
                this.comboBox.setWidth(100, Unit.PERCENTAGE);
                this.comboBox.setContainerDataSource(container);
                this.comboBox.setItemCaptionMode(ItemCaptionMode.PROPERTY);
                this.comboBox.setItemCaptionPropertyId("display");
                this.comboBox.addStyleName(ValoTheme.TEXTFIELD_TINY);
                this.comboBox.addValueChangeListener(new ValueChangeListener() {

                    @Override
                    public void valueChange(final ValueChangeEvent event) {
                        triggerUpdate();
                    }
                });

                HorizontalLayout hLayout = new HorizontalLayout();
                hLayout.addStyleName("filter-header");
                Label label = new Label("Continents: ");
                label.setWidth(100, Unit.PIXELS);
                hLayout.addComponent(label);
                hLayout.addComponent(this.comboBox);
                hLayout.setExpandRatio(this.comboBox, 1);
                return hLayout;
            }

            @Override
            public void clearFilter() {
                this.comboBox.setValue(null);
            }
        };
        return filter;
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
        CellFilterComponent<ComboBox> genderFilter = this.filter.setComboBoxFilter("gender", Arrays.asList(Inhabitants.Gender.MALE, Inhabitants.Gender.FEMALE));
        genderFilter.getComponent()
                .setItemIcon(Inhabitants.Gender.MALE, FontAwesome.MALE);
        genderFilter.getComponent()
                .setItemIcon(Inhabitants.Gender.FEMALE, FontAwesome.FEMALE);

        // simple filters
        this.filter.setTextFilter("name", true, true, "name starts with");
        this.filter.setNumberFilter("bodySize", "smallest", "biggest");

        RangeCellFilterComponent<DateField, HorizontalLayout> dateFilter = this.filter.setDateFilter("birthday", new SimpleDateFormat("yyyy-MMM-dd"), true);
        dateFilter.getSmallestField()
                .setParseErrorMessage("da ist was schief gegangen :)");

        this.filter.setBooleanFilter("onFacebook",
                new GridCellFilter.BooleanRepresentation(FontAwesome.THUMBS_UP, "yes"),
                new GridCellFilter.BooleanRepresentation(FontAwesome.THUMBS_DOWN, "nope"));

        // set country combo with custom caption
        this.filter.setCustomFilter("country", customFilterComponent(this.filter, "country"));
    }

    /**
     * interacts with the GridCellFilter
     *
     * @param grid
     */
    private void initExtraHeaderRow(final Grid grid) {
        HeaderRow fistHeaderRow = grid.prependHeaderRow();
        fistHeaderRow.join("id", "gender", "name", "bodySize");
        fistHeaderRow.getCell("id")
                .setHtml("GridCellFilter simplify the filter settings for a grid");
        fistHeaderRow.join("birthday", "onFacebook", "country");
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        fistHeaderRow.getCell("birthday")
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
                Notification.show("changed visibility to: " + this.visibile + "! Sometimes it's working sometimes not - it's deprecated!", Type.ERROR_MESSAGE);
            }
        });
        changeVisibility.setIcon(FontAwesome.EYE_SLASH);
        changeVisibility.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
        buttonLayout.addComponent(changeVisibility);


        final Button presetFilter = new Button("presetFilter");
        presetFilter.addClickListener(new Button.ClickListener() {


            @Override
            public void buttonClick(final ClickEvent event) {
                CellFilterComponent<TextField> filter = DemoUI.this.filter.getCellFilter("name");
                filter.getComponent()
                        .setValue("eth");
                filter.triggerUpdate();
            }
        });
        presetFilter.setIcon(FontAwesome.PENCIL);
        presetFilter.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
        buttonLayout.addComponent(presetFilter);

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
                } else if (cellReference.getPropertyId()
                        .equals("country")) {
                    /*
                     * example how to change the icon of the buttons
					 *
					 * @formatter:off
					 * .v-grid-cell.link-icon .v-button-bar button.v-edit span:before {
					 *   color: blue; // recolor icon
					 *   content: "\f0c1"; // content-code of FontAwesome that is served by vaadin!
					 * }
					 * @formatter:on
					 */
                    return "link-icon";
                } else {
                    return null;
                }
            }
        });
    }

    /**
     * example of a custom Filter that filter's by a subpropertiy of an object
     */
    private class CustomFilter implements Filter {

        private final Object propertyId;
        private final Continent value;

        public CustomFilter(final Object propertyId, final Continent value) {
            this.propertyId = propertyId;
            this.value = value;
        }

        @Override
        public boolean passesFilter(final Object itemId, final Item item) throws UnsupportedOperationException {
            if (this.value == null) {
                return true;
            }
            final Property<?> p = item.getItemProperty(this.propertyId);
            if (null == p) {
                return false;
            }
            if (p.getValue() instanceof Country) {
                Country itemValue = (Country) p.getValue();
                return this.value.equals(itemValue.getContinent());
            }
            return false;
        }

        @Override
        public boolean appliesToProperty(final Object propertyId) {
            return this.propertyId.equals(propertyId);
        }
    }

}

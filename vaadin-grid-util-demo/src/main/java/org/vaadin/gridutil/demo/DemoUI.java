package org.vaadin.gridutil.demo;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.data.ValueProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.SerializablePredicate;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.components.grid.FooterRow;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.gridutil.GridUtil;
import org.vaadin.gridutil.ShortValueProvider;
import org.vaadin.gridutil.cell.CellFilterChangedListener;
import org.vaadin.gridutil.cell.CellFilterComponent;
import org.vaadin.gridutil.cell.GridCellFilter;
import org.vaadin.gridutil.cell.RangeCellFilterComponent;
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
import java.util.List;


@SpringUI()
@Theme("valo")
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
        final Grid grid = new Grid(Inhabitants.class);
        grid.setSizeFull();

        // init Container
        List<Inhabitants> items = DummyDataGen.genInhabitants(1000);
        grid.setItems(items);

        setColumnRenderes(grid);

        grid.setColumnOrder("id", "gender", "name", "bodySize", "birthday", "onFacebook", "country");

        initFilter(grid);
        initFooterRow(grid, items);
        initExtraHeaderRow(grid);

        initColumnAlignments(grid);
        return grid;
    }

    private void setColumnRenderes(final Grid grid) {
        grid.addColumn(ShortValueProvider.getter(Inhabitants.class, "id"),
                new EditDeleteButtonValueRenderer<Inhabitants>(edit -> {
                    Notification.show(edit.getItem()
                            .toString() + " want's to get edited", Type.HUMANIZED_MESSAGE);
                }, delete -> {
                    Notification.show(delete.getItem()
                            .toString() + " want's to get deleted", Type.WARNING_MESSAGE);

                }))
                .setWidth(160);

        grid.addColumn(ShortValueProvider.getter(Inhabitants.class, "bodySize"), new IndicatorRenderer(1.8, 1.1))
                .setWidth(150);
        grid.addColumn(ShortValueProvider.getter(Inhabitants.class, "birthday"), new DateRenderer(DateFormat.getDateInstance()))
                .setWidth(210);
        grid.addColumn(ShortValueProvider.getter(Inhabitants.class, "onFacebook"), new BooleanRenderer())
                .setWidth(130);

		/*
         * the icon of the editButton will get overwritten below by css styling @see DemoUI.initColumnAlignments
		 */
        grid.addColumn((ValueProvider<Inhabitants, String>) value -> String.format("%s <i>(%d)</i>",
                value.getCountry()
                        .getPopulation(),
                value.getCountry()
                        .getPopulation()), new EditButtonValueRenderer<Inhabitants>(e -> {
            Notification.show("Goto Link for " + e.getItem()
                    .getCountry()
                    .getName(), Type.HUMANIZED_MESSAGE);
        }));
    }

    /**
     * generates a simple totalCount footer
     *
     * @param grid
     * @param items
     */
    private void initFooterRow(final Grid grid, List<Inhabitants> items) {
        final FooterRow footerRow = grid.appendFooterRow();
        footerRow.getCell("id")
                .setHtml("total:");
        footerRow.join("gender", "name", "bodySize", "birthday", "onFacebook", "country");
        // inital total count
        footerRow.getCell("gender")
                .setHtml("<b>" + items.size() + "</b>");

        // filter change count recalculate
        // TODO: need to find a solution for it
        /*
        container.addItemSetChangeListener(new ItemSetChangeListener() {

            @Override
            public void containerItemSetChange(final ItemSetChangeEvent event) {
                footerRow.getCell("gender")
                        .setHtml("<b>" + event.getContainer()
                                .getItemIds()
                                .size() + "</b>");
            }
        });
        */
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

            ComboBox<Continent> comboBox = new ComboBox<>();

            @Override
            public void triggerUpdate() {
                if (comboBox.getValue() != null) {
                    // this will add filter to container and replace old version if existing
                    cellFilter.replaceFilter(new CustomFilter(comboBox.getValue()), columnId);
                } else {
                    // remove filter by columnId
                    cellFilter.removeFilter(columnId);
                }
            }

            @Override
            public HorizontalLayout layoutComponent() {

                this.comboBox.setWidth(100, Unit.PERCENTAGE);
                comboBox.setItemCaptionGenerator(e -> e.getDisplay());
                this.comboBox.addStyleName(ValoTheme.TEXTFIELD_TINY);
                this.comboBox.addValueChangeListener(e -> triggerUpdate());

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
        this.filter = new GridCellFilter(grid, Inhabitants.class);
        this.filter.setNumberFilter("id", Long.class);

        // set gender Combo with custom icons
        CellFilterComponent<ComboBox<Inhabitants.Gender>> genderFilter = this.filter.setComboBoxFilter("gender",
                Inhabitants.Gender.class,
                Arrays.asList(Inhabitants.Gender.MALE, Inhabitants.Gender.FEMALE));
        genderFilter.getComponent()
                .setItemIconGenerator(i -> i.getIcon());

        // simple filters
        this.filter.setTextFilter("name", true, true, "name starts with");
        this.filter.setNumberFilter("bodySize", Double.class, "invalid input", "smallest", "biggest");

        RangeCellFilterComponent<Date, DateField, HorizontalLayout> dateFilter = this.filter.setDateFilter("birthday",
                new SimpleDateFormat("yyyy-MMM-dd"),
                true);
        dateFilter.getSmallestField()
                .setParseErrorMessage("da ist was schief gegangen :)");

        this.filter.setBooleanFilter("onFacebook",
                new GridCellFilter.BooleanRepresentation(true, VaadinIcons.THUMBS_UP, "yes"),
                new GridCellFilter.BooleanRepresentation(false, VaadinIcons.THUMBS_DOWN, "nope"));

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
        grid.getColumn("id")
                .setStyleGenerator(e -> GridUtil.ALIGN_CELL_RIGHT);
        grid.getColumn("birthday")
                .setStyleGenerator(e -> GridUtil.ALIGN_CELL_CENTER);
        grid.getColumn("country")
                .setStyleGenerator(e -> {
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
        });
    }

    /**
     * example of a custom Filter that filter's by a subpropertiy of an object
     */
    private class CustomFilter implements SerializablePredicate<Inhabitants> {

        private final Continent continent;

        public CustomFilter(final Continent continent) {
            this.continent = continent;
        }

        @Override
        public boolean test(Inhabitants value) {
            if (this.continent == null) {
                return true;
            }

            if (value  == null) {
                return false;
            }
            if (value.getCountry() != null) {
                return value.getCountry().equals(continent);
            }
            return false;
        }
    }

}

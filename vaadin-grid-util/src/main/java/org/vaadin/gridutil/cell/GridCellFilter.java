package org.vaadin.gridutil.cell;

import com.vaadin.data.BeanPropertySet;
import com.vaadin.data.PropertySet;
import com.vaadin.data.converter.LocalDateToDateConverter;
import com.vaadin.data.provider.InMemoryDataProviderHelpers;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FontIcon;
import com.vaadin.server.SerializablePredicate;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.*;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.gridutil.cell.filter.BetweenFilter;
import org.vaadin.gridutil.cell.filter.EqualFilter;
import org.vaadin.gridutil.cell.filter.SimpleStringFilter;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.Map.Entry;


/**
 * GridCellFilter helper that has a bunch of different filtering types
 *
 * @author Marten Prieß (http://www.rocketbase.io)
 * @version 1.1
 */
public class GridCellFilter<T> implements Serializable {

    public static String STYLENAME_GRIDCELLFILTER = "gridcellfilter";

    private static Date MIN_DATE_VALUE = new Date(0); // 1970-01-01 00:00:00

    private static Date MAX_DATE_VALUE = new Date(32503676399000L); // 2999-12-31 23:59:59

    private Grid grid;

    private ListDataProvider<T> dataProvider;

    private HeaderRow filterHeaderRow;

    private Map<String, CellFilterComponent> cellFilters;

    private Map<String, SerializablePredicate> assignedFilters;

    private boolean visible = true;

    private List<CellFilterChangedListener> cellFilterChangedListeners;

    private PropertySet<T> propertySet;

    /**
     * keeps link to Grid and added HeaderRow<br>
     * afterwards you need to set filter specification for each row<br>
     * please take care that your Container implements Filterable!
     *
     * @param grid that should get added a HeaderRow that this component will manage
     */
    public GridCellFilter(Grid<T> grid, Class<T> beanType) {
        this.grid = grid;
        filterHeaderRow = grid.appendHeaderRow();
        cellFilters = new HashMap<>();
        assignedFilters = new HashMap<>();
        cellFilterChangedListeners = new ArrayList<>();


        if (!(grid.getDataProvider() instanceof ListDataProvider)) {
            throw new RuntimeException("works only with ListDataProvider");
        } else {
            dataProvider = (ListDataProvider<T>) grid.getDataProvider();
            propertySet = BeanPropertySet.get(beanType);
        }
    }

    /**
     * generated HeaderRow
     *
     * @return added HeaderRow during intialization
     */
    public HeaderRow getFilterRow() {
        return filterHeaderRow;
    }

    /**
     * get list of filtered ColumnIds
     *
     * @return id of all properties that are currently filtered
     */
    public Set<String> filteredColumnIds() {
        return assignedFilters.keySet();
    }

    /**
     * add a listener for filter changes
     *
     * @param listener that should get triggered on changes
     */
    public void addCellFilterChangedListener(CellFilterChangedListener listener) {
        cellFilterChangedListeners.add(listener);
    }

    /**
     * remove a listener for filter changes
     *
     * @param listener that should get removed
     * @return true when found and removed
     */
    public boolean removeCellFilterChangedListener(CellFilterChangedListener listener) {
        return cellFilterChangedListeners.remove(listener);
    }

    /**
     * notify all registered listeners
     */
    protected void notifyCellFilterChanged() {
        for (CellFilterChangedListener listener : cellFilterChangedListeners) {
            listener.changedFilter(this);
        }
    }

    /**
     * will remove or add the filterHeaderRow<br>
     * The grid itself has no feature for changing the visibility of a headerRow
     *
     * @param visibile should get displayed?
     */
    public void setVisible(boolean visibile) {
        if (visible != visibile) {
            if (visibile) {
                filterHeaderRow = grid.appendHeaderRow();

                for (Entry<String, CellFilterComponent> entry : cellFilters.entrySet()) {
                    handleFilterRow(entry.getKey(), entry.getValue());
                }
            } else {
                clearAllFilters();
                for (Entry<String, CellFilterComponent> entry : cellFilters.entrySet()) {
                    if (null != filterHeaderRow.getCell(entry.getKey())) {
                        filterHeaderRow.getCell(entry.getKey())
                                .setText("");
                    }
                }
                grid.removeHeaderRow(filterHeaderRow);
            }
            visible = visibile;
        }
    }

    /**
     * get filter by columnId
     *
     * @param columnId id of property
     * @return CellFilterComponent
     */
    public CellFilterComponent getCellFilter(String columnId) {
        return cellFilters.get(columnId);
    }

    /**
     * removes all filters and clear all inputs
     */
    public void clearAllFilters() {
        for (Entry<String, CellFilterComponent> entry : cellFilters.entrySet()) {
            entry.getValue()
                    .clearFilter();
            removeFilter(entry.getKey(), false);
        }
        notifyCellFilterChanged();
    }

    /**
     * clear's a specific filter by columnId
     *
     * @param columnId id of property
     */
    public void clearFilter(String columnId) {
        cellFilters.get(columnId)
                .clearFilter();
        removeFilter(columnId);
    }

    /**
     * link component to headerRow and take care for styling
     *
     * @param columnId   id of property
     * @param cellFilter component will get added to filterRow
     */
    protected void handleFilterRow(String columnId, CellFilterComponent<?> cellFilter) {
        cellFilters.put(columnId, cellFilter);
        cellFilter.getComponent()
                .setWidth(100, Unit.PERCENTAGE);
        if (null != filterHeaderRow.getCell(columnId)) {
            filterHeaderRow.getCell(columnId)
                    .setComponent(cellFilter.getComponent());
            filterHeaderRow.getCell(columnId)
                    .setStyleName("filter-header");
        }
    }

    /**
     * checks assignedFilters replace already handled one and add new one
     *
     * @param filter   container filter
     * @param columnId id of property
     */
    public void replaceFilter(SerializablePredicate filter, String columnId) {
        assignedFilters.put(columnId, filter);
        refreshFilters();
    }

    private void refreshFilters() {
        dataProvider.clearFilters();
        SerializablePredicate<T> filter = null;
        for (Entry<String, SerializablePredicate> entry :
                assignedFilters.entrySet()) {
            final String columnId = entry.getKey();
            SerializablePredicate<T> singleFilter = InMemoryDataProviderHelpers.createValueProviderFilter(propertySet.getProperty(columnId)
                    .get()
                    .getGetter(), entry.getValue());
            if (filter == null) {
                filter = singleFilter;
            } else {
                SerializablePredicate<T> tempFilter = filter;
                filter = (item -> tempFilter.test(item) && singleFilter.test(item));
            }
        }
        if (filter != null) {
            dataProvider.setFilter(filter);
        }
    }

    /**
     * remove the filter and notify listeners
     *
     * @param columnId id of property
     */
    public void removeFilter(String columnId) {
        removeFilter(columnId, true);
    }

    protected void removeFilter(String columnId, boolean notify) {
        if (assignedFilters.containsKey(columnId)) {
            assignedFilters.remove(columnId);
            refreshFilters();
            if (notify) {
                notifyCellFilterChanged();
            }
        }
    }

    /**
     * allows to add custom FilterComponents to the GridCellFilter
     *
     * @param columnId  id of property
     * @param component that implements the interface
     * @return your created component that is linked with the GridCellFilter
     */
    public CellFilterComponent setCustomFilter(String columnId, CellFilterComponent component) {
        handleFilterRow(columnId, component);
        return component;
    }

    /**
     * assign a <b>SimpleStringFilter</b> to grid for given columnId<br>
     * could also be used for NumberField when you would like to do filter by startWith for example
     *
     * @param columnId        id of property
     * @param ignoreCase      property of SimpleStringFilter
     * @param onlyMatchPrefix property of SimpleStringFilter
     * @return CellFilterComponent that contains TextField
     */
    public CellFilterComponent<TextField> setTextFilter(String columnId, boolean ignoreCase, boolean onlyMatchPrefix) {
        return setTextFilter(columnId, ignoreCase, onlyMatchPrefix, null);
    }

    /**
     * assign a <b>SimpleStringFilter</b> to grid for given columnId<br>
     * could also be used for NumberField when you would like to do filter by startWith for example
     *
     * @param columnId        id of property
     * @param ignoreCase      property of SimpleStringFilter
     * @param onlyMatchPrefix property of SimpleStringFilter
     * @param inputPrompt     hint for user
     * @return CellFilterComponent that contains TextField
     */
    public CellFilterComponent<TextField> setTextFilter(String columnId, boolean ignoreCase, boolean onlyMatchPrefix, String inputPrompt) {
        CellFilterComponent<TextField> filter = new CellFilterComponent<TextField>() {

            TextField textField = new TextField();

            String currentValue = "";

            public void triggerUpdate() {
                if (currentValue == null || currentValue.isEmpty()) {
                    removeFilter(columnId);
                } else {
                    replaceFilter(new SimpleStringFilter(currentValue, ignoreCase, onlyMatchPrefix), columnId);
                }
            }

            @Override
            public TextField layoutComponent() {
                textField.setPlaceholder(inputPrompt);
                textField.addStyleName(STYLENAME_GRIDCELLFILTER);
                textField.addStyleName(ValoTheme.TEXTFIELD_TINY);
                textField.setValueChangeTimeout(200);
                textField.setValueChangeMode(ValueChangeMode.TIMEOUT);
                // used to allow changes from outside
                textField.addValueChangeListener(e -> {
                    currentValue = textField.getValue();
                    triggerUpdate();
                });
                return textField;
            }

            @Override
            public void clearFilter() {
                textField.clear();
            }
        };
        handleFilterRow(columnId, filter);
        return filter;
    }

    /**
     * assign a <b>EqualFilter</b> to grid for given columnId
     *
     * @param columnId id of property
     * @param beanType class of selection
     * @param beans    selection for ComboBox
     * @return CellFilterComponent that contains ComboBox
     */
    public <B> CellFilterComponent<ComboBox<B>> setComboBoxFilter(String columnId, Class<B> beanType, List<B> beans) {
        CellFilterComponent<ComboBox<B>> filter = new CellFilterComponent<ComboBox<B>>() {

            ComboBox<B> comboBox = new ComboBox();

            public void triggerUpdate() {
                if (comboBox.getValue() != null) {
                    replaceFilter(new EqualFilter(comboBox.getValue()), columnId);
                } else {
                    removeFilter(columnId);
                }
            }

            @Override
            public ComboBox<B> layoutComponent() {
                comboBox.setEmptySelectionAllowed(true);
                comboBox.addStyleName(STYLENAME_GRIDCELLFILTER);
                comboBox.addStyleName(ValoTheme.COMBOBOX_TINY);
                comboBox.setItems(beans);
                comboBox.addValueChangeListener(e -> triggerUpdate());
                return comboBox;
            }

            @Override
            public void clearFilter() {
                comboBox.setValue(null);
            }
        };

        handleFilterRow(columnId, filter);
        return filter;
    }

    /**
     * assign a <b>EqualFilter</b> to grid for given columnId
     *
     * @param columnId id of property
     * @return drawn comboBox in order to add some custom styles
     */
    public ComboBox setBooleanFilter(String columnId) {
        return setBooleanFilter(columnId, BooleanRepresentation.TRUE_VALUE, BooleanRepresentation.FALSE_VALUE);
    }

    /**
     * assign a <b>EqualFilter</b> to grid for given columnId
     *
     * @param columnId            id of property
     * @param trueRepresentation  specify caption and icon
     * @param falseRepresentation specify caption and icon
     * @return drawn comboBox in order to add some custom styles
     */
    public ComboBox setBooleanFilter(String columnId, BooleanRepresentation trueRepresentation, BooleanRepresentation falseRepresentation) {
        CellFilterComponent<ComboBox<BooleanRepresentation>> filter = new CellFilterComponent<ComboBox<BooleanRepresentation>>() {

            ComboBox<BooleanRepresentation> comboBox = new ComboBox();

            public void triggerUpdate() {
                if (comboBox.getValue() != null) {
                    replaceFilter(new EqualFilter(comboBox.getValue()
                            .getValue()), columnId);
                } else {
                    removeFilter(columnId);
                }
            }

            @Override
            public ComboBox<BooleanRepresentation> layoutComponent() {

                comboBox.setItemIconGenerator(BooleanRepresentation::getIcon);
                comboBox.setItemCaptionGenerator(BooleanRepresentation::getCaption);
                comboBox.setItems(Arrays.asList(trueRepresentation, falseRepresentation));

                comboBox.setEmptySelectionAllowed(true);
                comboBox.addStyleName(STYLENAME_GRIDCELLFILTER);
                comboBox.addStyleName(ValoTheme.COMBOBOX_TINY);
                comboBox.addValueChangeListener(e -> triggerUpdate());
                return comboBox;
            }

            @Override
            public void clearFilter() {
                comboBox.setValue(null);
            }
        };

        handleFilterRow(columnId, filter);
        return filter.getComponent();
    }

    /**
     * assign a <b>BetweenFilter</b> to grid for given columnId<br>
     * only supports type of <b>Integer, Double, Float, BigInteger and BigDecimal</b>
     *
     * @param columnId id of property
     * @param type     type of the property
     * @return RangeCellFilterComponent that holds both TextFields (smallest and biggest as propertyId) and FilterGroup
     */
    public <T extends Number & Comparable<? super T>> RangeCellFilterComponentTyped<T, TextField, HorizontalLayout> setNumberFilter(String columnId, Class type) {
        return setNumberFilter(columnId, type, String.format("couldn't convert to %s", type.getSimpleName()), null, null);
    }

    /**
     * assign a <b>BetweenFilter</b> to grid for given columnId<br>
     * only supports type of <b>Integer, Double, Float, BigInteger and BigDecimal</b>
     *
     * @param columnId              id of property
     * @param type                  type of the property
     * @param converterErrorMessage message to be displayed in case of a converter error
     * @param smallestInputPrompt   hint for user
     * @param biggestInputPrompt    hint for user
     * @return RangeCellFilterComponent that holds both TextFields (smallest and biggest as propertyId) and FilterGroup
     */
    public <T extends Number & Comparable<? super T>> RangeCellFilterComponentTyped<T, TextField, HorizontalLayout> setNumberFilter(String columnId, Class<T> type, String converterErrorMessage, String smallestInputPrompt, String biggestInputPrompt) {

        RangeCellFilterComponentTyped<T, TextField, HorizontalLayout> filter =
                RangeCellFilterComponentFactory.createForNumberType(
                        columnId,
                        type,
                        converterErrorMessage,
                        smallestInputPrompt,
                        biggestInputPrompt,
                        (serFilter, propertyId) -> this.replaceFilter(serFilter, propertyId),
                        this::removeFilter
                );

        handleFilterRow(columnId, filter);
        return filter;
    }

    /**
     * assign a <b>BetweenFilter</b> to grid for given columnId<br>
     *
     * @param columnId id of property
     * @return RangeCellFilterComponent that holds both DateFields (smallest and biggest as propertyId) and FilterGroup
     */
    public RangeCellFilterComponent<DateField, HorizontalLayout> setDateFilter(String columnId) {
        return setDateFilter(columnId, null, true);
    }

    /**
     * assign a <b>BetweenFilter</b> to grid for given columnId<br>
     *
     * @param columnId        id of property
     * @param dateFormat      the dateFormat to be used for the date fields.
     * @param excludeEndOfDay biggest value until the end of the day (DAY + 23:59:59.999)
     * @return RangeCellFilterComponent that holds both DateFields (smallest and biggest as propertyId) and FilterGroup
     */
    public RangeCellFilterComponent<DateField, HorizontalLayout> setDateFilter(String columnId, java.text.SimpleDateFormat dateFormat, boolean excludeEndOfDay) {
        Class<?> propertyType = propertySet.getProperty(columnId)
                .get()
                .getType();
        if (!Date.class.equals(propertyType)) {
            throw new IllegalArgumentException("columnId " + columnId + " is not of type Date");
        }
        RangeCellFilterComponent<DateField, HorizontalLayout> filter = new RangeCellFilterComponent<DateField, HorizontalLayout>() {

            private final LocalDateToDateConverter ldToDateConverter = new LocalDateToDateConverter();

            private DateField smallest;

            private DateField biggest;

            @Override
            public DateField getSmallestField() {
                if (smallest == null) {
                    smallest = genDateField(SMALLEST, dateFormat);
                }
                return smallest;
            }

            @Override
            public DateField getBiggestField() {
                if (biggest == null) {
                    biggest = genDateField(BIGGEST, dateFormat);
                }
                return biggest;
            }

            private DateField genDateField(final String propertyId, final SimpleDateFormat dateFormat) {
                return FieldFactory.genDateField(getBinder(), propertyId, dateFormat);
            }

            @Override
            public HorizontalLayout layoutComponent() {
                getHLayout().addComponent(getSmallestField());
                getHLayout().addComponent(getBiggestField());
                getHLayout().setExpandRatio(getSmallestField(), 1);
                getHLayout().setExpandRatio(getBiggestField(), 1);

                initBinderValueChangeHandler();

                return getHLayout();
            }

            private Date fixTiming(Date date, boolean excludeEndOfDay) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.set(Calendar.MILLISECOND, excludeEndOfDay ? 0 : 999);
                calendar.set(Calendar.SECOND, excludeEndOfDay ? 0 : 59);
                calendar.set(Calendar.MINUTE, excludeEndOfDay ? 0 : 59);
                calendar.set(Calendar.HOUR, excludeEndOfDay ? 0 : 23);
                return calendar.getTime();
            }

            private void initBinderValueChangeHandler() {
                getBinder().addValueChangeListener(e -> {
                    Object smallest = getBinder().getBean()
                            .getSmallest();
                    Object biggest = getBinder().getBean()
                            .getBiggest();
                    Date smallestDate = checkObject(smallest);
                    Date biggestDate = checkObject(biggest);
                    if (this.smallest != null || biggest != null) {
                        if (this.smallest != null && biggest != null && this.smallest.equals(biggest)) {
                            replaceFilter(new EqualFilter(this.smallest), columnId);
                        } else {
                            replaceFilter(new BetweenFilter(smallestDate != null ? fixTiming(smallestDate, true) : MIN_DATE_VALUE,
                                    biggestDate != null ? fixTiming(biggestDate, excludeEndOfDay) : MAX_DATE_VALUE), columnId);
                        }
                    } else {
                        removeFilter(columnId);
                    }
                });
            }

            private Date checkObject(Object value) {
                if (value instanceof LocalDate) {
                    return ldToDateConverter.convertToModel((LocalDate) value, null)
                            .getOrThrow(msg -> new IllegalArgumentException(msg));
                } else if (value instanceof Date) {
                    return (Date) value;
                }
                return null;
            }

            @Override
            public void clearFilter() {
                getBinder().setBean(new TwoValueObject());
            }
        };

        handleFilterRow(columnId, filter);
        return filter;
    }

    public static class BooleanRepresentation {

        public static BooleanRepresentation TRUE_VALUE = new BooleanRepresentation(true, VaadinIcons.CHECK_SQUARE, Boolean.TRUE.toString());

        public static BooleanRepresentation FALSE_VALUE = new BooleanRepresentation(false, VaadinIcons.CLOSE, Boolean.FALSE.toString());

        private boolean value;

        private FontIcon icon;

        private String caption;

        public BooleanRepresentation(Boolean value, FontIcon icon, String caption) {
            this.value = value;
            this.icon = icon;
            this.caption = caption;
        }

        public FontIcon getIcon() {
            return icon;
        }

        public String getCaption() {
            return caption;
        }

        public boolean getValue() {
            return value;
        }
    }
}

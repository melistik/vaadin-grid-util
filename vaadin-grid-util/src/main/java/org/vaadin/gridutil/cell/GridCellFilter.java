package org.vaadin.gridutil.cell;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Container.Filterable;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitEvent;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitHandler;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.converter.*;
import com.vaadin.data.util.filter.Between;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.FontIcon;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Grid.HeaderRow;
import com.vaadin.ui.themes.ValoTheme;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Calendar;
import java.util.Map.Entry;


/**
 * GridCellFilter helper that has a bunch of different filtering types
 *
 * @author Marten Prieß (http://www.non-rocket-science.com)
 * @version 1.0
 */
public class GridCellFilter implements Serializable {

    private static final String STYLENAME_GRIDCELLFILTER = "gridcellfilter";

    private static final long serialVersionUID = -6449115552660561941L;

    private final Grid grid;
    private HeaderRow filterHeaderRow;
    private final Map<Object, CellFilterComponent<?>> cellFilters;
    private final Map<Object, Filter> assignedFilters;
    private final List<CellFilterChangedListener> cellFilterChangedListeners;
    private boolean visible = true;
    private Map<String, String> customMessages;

    /**
     * keeps link to Grid and added HeaderRow<br>
     * afterwards you need to set filter specification for each row<br>
     * please take care that your Container implements Filterable!
     *
     * @param grid that should get added a HeaderRow that this component will manage
     */
    public GridCellFilter(final Grid grid) {
        this.grid = grid;
        this.filterHeaderRow = grid.appendHeaderRow();
        this.cellFilters = new HashMap<Object, CellFilterComponent<?>>();
        this.assignedFilters = new HashMap<Object, Filter>();
        this.cellFilterChangedListeners = new ArrayList<CellFilterChangedListener>();

        if (!(grid.getContainerDataSource() instanceof Filterable)) {
            throw new RuntimeException("container is not Filterable!");
        }
    }

    public void setCustomMessages(Map<String, String> customMessages) {
    	this.customMessages = customMessages;
    }
    
    /**
     * will remove or add the filterHeaderRow<br>
     * badly the Connectors of the Cell-Components log an error message<br>
     * <i>Widget is still attached to the DOM after the connector ComboBoxConnector has been unregistered. Widget was removed</i><br>
     * that's why it deprecated. The grid itself has no feature for changing the visibility of a headerRow
     *
     * @param visibile should get displayed?
     */
    @Deprecated
    public void setVisible(final boolean visibile) {
        if (this.visible != visibile) {
            if (visibile) {
                this.filterHeaderRow = this.grid.appendHeaderRow();

                for (Entry<Object, CellFilterComponent<?>> entry : this.cellFilters.entrySet()) {
                    handleFilterRow(entry.getKey(), entry.getValue());
                }
            } else {
                clearAllFilters();
                for (Entry<Object, CellFilterComponent<?>> entry : this.cellFilters.entrySet()) {
                    if (null != this.filterHeaderRow.getCell(entry.getKey())) {
                        this.filterHeaderRow.getCell(entry.getKey())
                                .setText("");
                    }
                }
                this.grid.removeHeaderRow(this.filterHeaderRow);
            }
            this.visible = visibile;
        }
    }

    /**
     * generated HeaderRow
     *
     * @return added HeaderRow during intialization
     */
    public HeaderRow getFilterRow() {
        return this.filterHeaderRow;
    }

    /**
     * get list of filtered ColumnIds
     *
     * @return id of all properties that are currently filtered
     */
    public Set<Object> filteredColumnIds() {
        return this.assignedFilters.keySet();
    }

    /**
     * add a listener for filter changes
     *
     * @param listener that should get triggered on changes
     */
    public void addCellFilterChangedListener(final CellFilterChangedListener listener) {
        this.cellFilterChangedListeners.add(listener);
    }

    /**
     * remove a listener for filter changes
     *
     * @param listener that should get removed
     * @return true when found and removed
     */
    public boolean removeCellFilterChangedListener(final CellFilterChangedListener listener) {
        return this.cellFilterChangedListeners.remove(listener);
    }

    /**
     * notify all registered listeners
     */
    protected void notifyCellFilterChanged() {
        for (CellFilterChangedListener listener : this.cellFilterChangedListeners) {
            listener.changedFilter(this);
        }
    }

    /**
     * removes all filters and clear all inputs
     */
    public void clearAllFilters() {
        for (Entry<Object, CellFilterComponent<?>> entry : this.cellFilters.entrySet()) {
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
    public void clearFilter(final Object columnId) {
        this.cellFilters.get(columnId)
                .clearFilter();
        removeFilter(columnId);
    }

    /**
     * link component to headerRow and take care for styling
     *
     * @param columnId   id of property
     * @param cellFilter component will get added to filterRow
     */
    protected void handleFilterRow(final Object columnId, final CellFilterComponent<?> cellFilter) {
        this.cellFilters.put(columnId, cellFilter);
        cellFilter.getComponent()
                .setWidth(100, Unit.PERCENTAGE);
        if (null != this.filterHeaderRow.getCell(columnId)) {
            this.filterHeaderRow.getCell(columnId)
                    .setComponent(cellFilter.getComponent());
            this.filterHeaderRow.getCell(columnId)
                    .setStyleName("filter-header");
        }
    }

    /**
     * checks assignedFilters replace already handled one and add new one
     *
     * @param filter   container filter
     * @param columnId id of property
     */
    public void replaceFilter(final Filter filter, final Object columnId) {
        Filterable f = (Filterable) this.grid.getContainerDataSource();
        if (this.assignedFilters.containsKey(columnId)) {
            f.removeContainerFilter(this.assignedFilters.get(columnId));
        }
        f.addContainerFilter(filter);
        this.assignedFilters.put(columnId, filter);
        this.grid.cancelEditor();
        notifyCellFilterChanged();
    }

    /**
     * remove the filter and notify listeners
     *
     * @param columnId id of property
     */
    public void removeFilter(final Object columnId) {
        removeFilter(columnId, true);
    }

    protected void removeFilter(final Object columnId, final boolean notify) {
        Filterable f = (Filterable) this.grid.getContainerDataSource();
        if (this.assignedFilters.containsKey(columnId)) {
            f.removeContainerFilter(this.assignedFilters.get(columnId));
            this.assignedFilters.remove(columnId);
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
    public CellFilterComponent setCustomFilter(final Object columnId, final CellFilterComponent component) {
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
     * @return generated TextField
     */
    public TextField setTextFilter(final Object columnId, final boolean ignoreCase, final boolean onlyMatchPrefix) {
        return this.setTextFilter(columnId, ignoreCase, onlyMatchPrefix, null);
    }

    /**
     * assign a <b>SimpleStringFilter</b> to grid for given columnId<br>
     * could also be used for NumberField when you would like to do filter by startWith for example
     *
     * @param columnId        id of property
     * @param ignoreCase      property of SimpleStringFilter
     * @param onlyMatchPrefix property of SimpleStringFilter
     * @param inputPrompt     hint for user
     * @return generated TextField
     */
    public TextField setTextFilter(final Object columnId, final boolean ignoreCase, final boolean onlyMatchPrefix, final String inputPrompt) {
        CellFilterComponent<TextField> filter = new CellFilterComponent<TextField>() {

            private static final long serialVersionUID = 1L;
            TextField textField = new TextField();

            @Override
            public TextField layoutComponent() {
                this.textField.setImmediate(true);
                this.textField.setInputPrompt(inputPrompt);
                this.textField.addStyleName(STYLENAME_GRIDCELLFILTER);
                this.textField.addStyleName(ValoTheme.TEXTFIELD_TINY);
                this.textField.addTextChangeListener(new TextChangeListener() {

                    private static final long serialVersionUID = -3567212620627878001L;

                    @Override
                    public void textChange(final TextChangeEvent event) {
                        if (event.getText() != null && event.getText()
                                .length() > 0) {
                            replaceFilter(new SimpleStringFilter(columnId, event.getText(), ignoreCase, onlyMatchPrefix), columnId);
                        } else {
                            removeFilter(columnId);
                        }
                    }
                });
                return this.textField;
            }

            @Override
            public void clearFilter() {
                this.textField.clear();
            }
        };
        handleFilterRow(columnId, filter);
        return filter.getComponent();
    }

    /**
     * assign a <b>EqualFilter</b> to grid for given columnId
     *
     * @param columnId id of property
     * @param list     selection for ComboBox
     * @return drawn comboBox in order to add some custom styles
     */
    public ComboBox setComboBoxFilter(final Object columnId, final List list) {
        CellFilterComponent<ComboBox> filter = new CellFilterComponent<ComboBox>() {

            private static final long serialVersionUID = 1L;
            ComboBox comboBox = new ComboBox();

            @Override
            public ComboBox layoutComponent() {
                BeanItemContainer container = new BeanItemContainer(list.get(0)
                        .getClass(), list);

                this.comboBox.setNullSelectionAllowed(true);
                this.comboBox.setImmediate(true);
                this.comboBox.setContainerDataSource(container);
                this.comboBox.addStyleName(STYLENAME_GRIDCELLFILTER);
                this.comboBox.addStyleName(ValoTheme.COMBOBOX_TINY);
                this.comboBox.addValueChangeListener(new ValueChangeListener() {

                    private static final long serialVersionUID = 4657429154535483528L;

                    @Override
                    public void valueChange(final ValueChangeEvent event) {
                        if (comboBox.getValue() != null) {
                            replaceFilter(new Equal(columnId, comboBox.getValue()), columnId);
                        } else {
                            removeFilter(columnId);
                        }
                    }
                });
                return this.comboBox;
            }

            @Override
            public void clearFilter() {
                this.comboBox.setValue(null);
            }
        };

        handleFilterRow(columnId, filter);
        return filter.getComponent();
    }

    /**
     * assign a <b>EqualFilter</b> to grid for given columnId
     *
     * @param columnId id of property
     * @return drawn comboBox in order to add some custom styles
     */
    public ComboBox setBooleanFilter(final Object columnId) {
        CellFilterComponent<ComboBox> filter = new CellFilterComponent<ComboBox>() {

            private static final long serialVersionUID = 1L;
            ComboBox comboBox = new ComboBox();

            private Item genItem(final Boolean value, String customText) {
                Item item = this.comboBox.getItem(this.comboBox.addItem());
                item.getItemProperty("icon")
                        .setValue(value ? FontAwesome.CHECK_SQUARE : FontAwesome.TIMES);
                item.getItemProperty("value")
                        .setValue(value);
                if (customText == null) {
                	item.getItemProperty("caption")
                            .setValue(value.toString());
                } else {
                	item.getItemProperty("caption")
                            .setValue(customText);
                }
                return item;
            }

            @Override
            public ComboBox layoutComponent() {

                this.comboBox.addContainerProperty("icon", FontIcon.class, null);
                this.comboBox.addContainerProperty("value", Boolean.class, null);
                this.comboBox.addContainerProperty("caption", String.class, null);

                this.comboBox.setItemCaptionMode(ItemCaptionMode.PROPERTY);
                this.comboBox.setItemCaptionPropertyId("caption");
                this.comboBox.setItemIconPropertyId("icon");

                String trueCaption = null;
                String falseCaption = null;
                if (customMessages != null) {
                	trueCaption = customMessages.get("trueCaption");
                	falseCaption = customMessages.get("falseCaption");
                }
                genItem(Boolean.TRUE, trueCaption);
                genItem(Boolean.FALSE, falseCaption);
                
                this.comboBox.setNullSelectionAllowed(true);
                this.comboBox.setImmediate(true);
                this.comboBox.addStyleName(STYLENAME_GRIDCELLFILTER);
                this.comboBox.addStyleName(ValoTheme.COMBOBOX_TINY);
                this.comboBox.addValueChangeListener(new ValueChangeListener() {

                    private static final long serialVersionUID = 75672745825037750L;

                    @Override
                    public void valueChange(final ValueChangeEvent event) {
                        Integer internalId = (Integer) comboBox.getValue();
                        if (internalId != null && internalId > 0) {
                            replaceFilter(new Equal(columnId, internalId.equals(1) ? Boolean.TRUE : Boolean.FALSE), columnId);
                        } else {
                            removeFilter(columnId);
                        }
                    }
                });
                return this.comboBox;
            }

            @Override
            public void clearFilter() {
                this.comboBox.setValue(null);
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
     * @return FieldGroup that holds both TextFields (smallest and biggest as propertyId)
     */
    public FieldGroup setNumberFilter(final Object columnId) {
        return this.setNumberFilter(columnId, null, null);
    }

    /**
     * assign a <b>BetweenFilter</b> to grid for given columnId<br>
     * only supports type of <b>Integer, Double, Float, BigInteger and BigDecimal</b>
     *
     * @param columnId            id of property
     * @param smallestInputPrompt hint for user
     * @param biggestInputPrompt  hint for user
     * @return FieldGroup that holds both TextFields (smallest and biggest as propertyId)
     */
    public FieldGroup setNumberFilter(final Object columnId, final String smallestInputPrompt, final String biggestInputPrompt) {
        final Class type = this.grid.getContainerDataSource()
                .getType(columnId);
        RangeCellFilterComponent<HorizontalLayout> filter = new RangeCellFilterComponent<HorizontalLayout>() {

            private static final long serialVersionUID = 1L;

            private Converter getConverter() {
                if (type == Integer.class) {
                    return new StringToIntegerConverter();
                } else if (type == Double.class) {
                    return new StringToDoubleConverter();
                } else if (type == Float.class) {
                    return new StringToFloatConverter();
                } else if (type == BigInteger.class) {
                    return new StringToBigIntegerConverter();
                } else if (type == BigDecimal.class) {
                    return new StringToBigDecimalConverter();
                } else {
                    return new StringToLongConverter();
                }
            }

            private TextField genNumberField(final String propertyId) {
                final TextField field = new TextField();
                getFieldGroup().bind(field, propertyId);
                field.setWidth("100%");
                field.setImmediate(true);
                field.setInvalidAllowed(false);
                field.setInvalidCommitted(false);
                field.setNullSettingAllowed(true);
                field.setNullRepresentation("");
                field.setConverter(getConverter());
                field.addStyleName(STYLENAME_GRIDCELLFILTER);
                field.addStyleName(ValoTheme.TEXTFIELD_TINY);
                field.addValueChangeListener(new ValueChangeListener() {

                    private static final long serialVersionUID = -8404344833239596320L;

                    @Override
                    public void valueChange(final ValueChangeEvent event) {
                        try {
                            if (field.isValid()) {
                                field.setComponentError(null);
                                getFieldGroup().commit();
                            }
                        } catch (CommitException e) {
                        }
                    }
                });
                return field;
            }

            @Override
            public HorizontalLayout layoutComponent() {
                getFieldGroup().setItemDataSource(genPropertysetItem(type));

                TextField smallest = genNumberField("smallest");
                smallest.setInputPrompt(smallestInputPrompt);
                TextField biggest = genNumberField("biggest");
                biggest.setInputPrompt(biggestInputPrompt);
                getHLayout().addComponent(smallest);
                getHLayout().addComponent(biggest);
                getHLayout().setExpandRatio(smallest, 1);
                getHLayout().setExpandRatio(biggest, 1);

                initCommitHandler();

                return getHLayout();
            }

            // BigInteger and BigDecimal is a bit dirty
            public Number getValue(final boolean max) {
                if (type == Integer.class) {
                    return max ? Integer.MAX_VALUE : Integer.MIN_VALUE;
                } else if (type == Double.class) {
                    return max ? Double.MAX_VALUE : Double.MIN_VALUE;
                } else if (type == Float.class) {
                    return max ? Float.MAX_VALUE : Float.MIN_VALUE;
                } else if (type == BigInteger.class) {
                    return max ? new BigInteger(String.valueOf(Long.MAX_VALUE)) : new BigInteger(String.valueOf(Long.MIN_VALUE));
                } else if (type == BigDecimal.class) {
                    return max ? new BigDecimal(String.valueOf(Long.MAX_VALUE)) : new BigDecimal(String.valueOf(Long.MIN_VALUE));
                } else {
                    return max ? Long.MAX_VALUE : Long.MIN_VALUE;
                }
            }

            private void initCommitHandler() {
                getFieldGroup().addCommitHandler(new CommitHandler() {

                    private static final long serialVersionUID = -2912534421548359666L;

                    @Override
                    public void preCommit(final CommitEvent commitEvent) throws CommitException {
                    }

                    @SuppressWarnings("rawtypes")
                    @Override
                    public void postCommit(final CommitEvent commitEvent) throws CommitException {
                        Object smallestValue = getFieldGroup().getItemDataSource()
                                .getItemProperty("smallest")
                                .getValue();
                        Object biggestValue = getFieldGroup().getItemDataSource()
                                .getItemProperty("biggest")
                                .getValue();
                        if (smallestValue != null || biggestValue != null) {
                            if (smallestValue != null && biggestValue != null && smallestValue.equals(biggestValue)) {
                                replaceFilter(new Equal(columnId, smallestValue), columnId);
                            } else {
                                replaceFilter(new Between(columnId, (Comparable) (smallestValue != null ? smallestValue : getValue(false)),
                                        (Comparable) (biggestValue != null ? biggestValue : getValue(true))), columnId);
                            }
                        } else {
                            removeFilter(columnId);
                        }
                    }
                });
            }

            @Override
            public void clearFilter() {
                getFieldGroup().clear();
            }
        };

        handleFilterRow(columnId, filter);
        return filter.getFieldGroup();
    }

    private final static Date MIN_DATE_VALUE = new Date(0); // 1970-01-01 00:00:00
    private final static Date MAX_DATE_VALUE = new Date(32503676399000L); // 2999-12-31 23:59:59

    /**
     * assign a <b>BetweenFilter</b> to grid for given columnId<br>
     *
     * @param columnId id of property
     * @return FieldGroup that holds both TextFields (smallest and biggest as propertyId)
     */
    public FieldGroup setDateFilter(final Object columnId) {
        return setDateFilter(columnId, new SimpleDateFormat(), true);
    }

    /**
     * assign a <b>BetweenFilter</b> to grid for given columnId<br>
     *
     * @param columnId        id of property
     * @param dateFormat      the dateFormat to be used for the date fields.
     * @param excludeEndOfDay biggest value until the end of the day (DAY + 23:59:59.999)
     * @return FieldGroup that holds both TextFields (smallest and biggest as propertyId)
     */
    public FieldGroup setDateFilter(final Object columnId, final java.text.SimpleDateFormat dateFormat, final boolean excludeEndOfDay) {
        RangeCellFilterComponent<HorizontalLayout> filter = new RangeCellFilterComponent<HorizontalLayout>() {

            /**
             *
             */
            private static final long serialVersionUID = 1L;

            private DateField genDateField(final String propertyId) {
                final DateField dateField = new DateField();
                getFieldGroup().bind(dateField, propertyId);
                dateField.setDateFormat(dateFormat.toPattern());
                dateField.setWidth("100%");
                dateField.setImmediate(true);
                dateField.setInvalidAllowed(false);
                dateField.setInvalidCommitted(false);
                dateField.setResolution(Resolution.DAY);
                dateField.addStyleName(STYLENAME_GRIDCELLFILTER);
                dateField.addStyleName(ValoTheme.DATEFIELD_TINY);
                dateField.addValueChangeListener(new ValueChangeListener() {

                    private static final long serialVersionUID = 9147606627660840906L;

                    @Override
                    public void valueChange(final ValueChangeEvent event) {
                        try {
                            if (dateField.isValid()) {
                                dateField.setComponentError(null);
                                getFieldGroup().commit();
                            }
                        } catch (CommitException e) {
                        }
                    }
                });
                return dateField;
            }

            @Override
            public HorizontalLayout layoutComponent() {
                getFieldGroup().setItemDataSource(genPropertysetItem(Date.class));

                DateField smallest = genDateField("smallest");
                DateField biggest = genDateField("biggest");
                getHLayout().addComponent(smallest);
                getHLayout().addComponent(biggest);
                getHLayout().setExpandRatio(smallest, 1);
                getHLayout().setExpandRatio(biggest, 1);

                initCommitHandler();

                return getHLayout();
            }

            private void initCommitHandler() {
                getFieldGroup().addCommitHandler(new CommitHandler() {

                    private static final long serialVersionUID = 2617591142986829655L;

                    @Override
                    public void preCommit(final CommitEvent commitEvent) throws CommitException {
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

                    @Override
                    public void postCommit(final CommitEvent commitEvent) throws CommitException {
                        Date smallestValue = (Date) getFieldGroup().getItemDataSource()
                                .getItemProperty("smallest")
                                .getValue();
                        Date biggestValue = (Date) getFieldGroup().getItemDataSource()
                                .getItemProperty("biggest")
                                .getValue();
                        if (smallestValue != null || biggestValue != null) {
                            replaceFilter(new Between(columnId, smallestValue != null ? fixTiming(smallestValue, true) : MIN_DATE_VALUE, biggestValue != null ? fixTiming(biggestValue, excludeEndOfDay)
                                    : MAX_DATE_VALUE), columnId);
                        } else {
                            removeFilter(columnId);
                        }
                    }

                });
            }

            @Override
            public void clearFilter() {
                getFieldGroup().clear();
            }
        };

        handleFilterRow(columnId, filter);
        return filter.getFieldGroup();
    }
}

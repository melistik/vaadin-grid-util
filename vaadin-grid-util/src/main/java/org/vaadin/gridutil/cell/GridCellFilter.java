package org.vaadin.gridutil.cell;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.StringToBigDecimalConverter;
import com.vaadin.data.util.converter.StringToBigIntegerConverter;
import com.vaadin.data.util.converter.StringToDoubleConverter;
import com.vaadin.data.util.converter.StringToFloatConverter;
import com.vaadin.data.util.converter.StringToIntegerConverter;
import com.vaadin.data.util.converter.StringToLongConverter;
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
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.HeaderRow;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

/**
 * GridCellFilter helper that has a bunch of different filtering types
 *
 * @author Marten Prieß (http://www.non-rocket-science.com)
 * @version 1.0
 */
public class GridCellFilter {

	private final Grid grid;
	private HeaderRow filterHeaderRow;
	private final Map<String, CellFilterComponent<?>> cellFilters;
	private final Map<String, Filter> assignedFilters;
	private final List<CellFilterChangedListener> cellFilterChangedListeners;
	private boolean visible = true;

	/**
	 * keeps link to Grid and added HeaderRow<br>
	 * afterwards you need to set filter specification for each row<br>
	 * please take care that your Container implements Filterable!
	 * 
	 * @param grid
	 */
	public GridCellFilter(final Grid grid) {
		this.grid = grid;
		this.filterHeaderRow = grid.appendHeaderRow();
		this.cellFilters = new HashMap<String, CellFilterComponent<?>>();
		this.assignedFilters = new HashMap<String, Filter>();
		this.cellFilterChangedListeners = new ArrayList<CellFilterChangedListener>();
	}

	/**
	 * will remove or add the filterHeaderRow<br>
	 * badly the Connectors of the Cell-Components log an error message<br>
	 * <i>Widget is still attached to the DOM after the connector ComboBoxConnector has been unregistered. Widget was removed</i><br>
	 * that's why it deprecated. The grid itself has no feature for changing the visibility of a headerRow
	 * 
	 * @param visibile
	 */
	@Deprecated
	public void setVisible(final boolean visibile) {
		if (this.visible != visibile) {
			if (visibile) {
				this.filterHeaderRow = this.grid.appendHeaderRow();

				for (Entry<String, CellFilterComponent<?>> entry : this.cellFilters.entrySet()) {
					handleFilterRow(entry.getKey(), entry.getValue());
				}
			} else {
				clearAllFilters();
				for (Entry<String, CellFilterComponent<?>> entry : this.cellFilters.entrySet()) {
					this.filterHeaderRow.getCell(entry.getKey())
							.setText("");
				}
				this.grid.removeHeaderRow(this.filterHeaderRow);
			}
			this.visible = visibile;
		}
	}

	/**
	 * generated HeaderRow
	 * 
	 * @return
	 */
	public HeaderRow getFilterRow() {
		return this.filterHeaderRow;
	}

	/**
	 * get list of filtered ColumnIds
	 * 
	 * @return
	 */
	public Set<String> filteredColumnIds() {
		return this.assignedFilters.keySet();
	}

	/**
	 * add a listener for filter changes
	 * 
	 * @param listener
	 */
	public void addCellFilterChangedListener(final CellFilterChangedListener listener) {
		this.cellFilterChangedListeners.add(listener);
	}

	/**
	 * remove a listener for filter changes
	 * 
	 * @param listener
	 * @return
	 */
	public boolean removeCellFilterChangedListener(final CellFilterChangedListener listener) {
		return this.cellFilterChangedListeners.remove(listener);
	}

	protected void notifyCellFilterChanged() {
		for (CellFilterChangedListener listener : this.cellFilterChangedListeners) {
			listener.changedFilter(this);
		}
	}

	/**
	 * removes all filters and clear all inputs
	 */
	public void clearAllFilters() {
		for (Entry<String, CellFilterComponent<?>> entry : this.cellFilters.entrySet()) {
			entry.getValue()
					.clearFilter();
			removeFilter(entry.getKey(), false);
		}
		notifyCellFilterChanged();
	}

	/**
	 * clear's a specific filter by columnId
	 * 
	 * @param columnId
	 */
	public void clearFilter(final String columnId) {
		this.cellFilters.get(columnId)
				.clearFilter();
		removeFilter(columnId);
	}

	/**
	 * link component to headerRow and take care for styling
	 * 
	 * @param columnId
	 * @param cellFilter
	 *            component will get added to filterRow
	 */
	private void handleFilterRow(final String columnId, final CellFilterComponent<?> cellFilter) {
		this.cellFilters.put(columnId, cellFilter);
		cellFilter.getComponent()
				.setWidth(100, Unit.PERCENTAGE);
		this.filterHeaderRow.getCell(columnId)
				.setComponent(cellFilter.getComponent());
		this.filterHeaderRow.getCell(columnId)
				.setStyleName("filter-header");
	}

	/**
	 * checks assignedFilters replace already handled one and add new one
	 * 
	 * @param filter
	 * @param columnId
	 */
	public void replaceFilter(final Filter filter, final String columnId) {
		Filterable f = (Filterable) this.grid.getContainerDataSource();
		if (this.assignedFilters.containsKey(columnId)) {
			f.removeContainerFilter(this.assignedFilters.get(columnId));
		}
		f.addContainerFilter(filter);
		this.assignedFilters.put(columnId, filter);
		this.grid.cancelEditor();
		notifyCellFilterChanged();
	}

	public void removeFilter(final String columnId) {
		removeFilter(columnId, true);
	}

	private void removeFilter(final String columnId, final boolean notify) {
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
	 * assign a <b>SimpleStringFilter</b> to grid for given columnId<br>
	 * could also be used for NumberField when you would like to do filter by startWith for example
	 * 
	 * @param columnId
	 * @param ignoreCase
	 *            property of SimpleStringFilter
	 * @param onlyMatchPrefix
	 *            property of SimpleStringFilter
	 * @return generated TextField
	 */
	public TextField setTextFilter(final String columnId, final boolean ignoreCase, final boolean onlyMatchPrefix) {
		CellFilterComponent<TextField> filter = new CellFilterComponent<TextField>() {

			TextField textField = new TextField();

			@Override
			public TextField layoutComponent() {
				this.textField.setImmediate(true);
				this.textField.addStyleName(ValoTheme.TEXTFIELD_TINY);
				this.textField.addTextChangeListener(new TextChangeListener() {
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
	 * @param columnId
	 * @param list
	 *            selection for ComboBox
	 * @return generated ComboBox
	 */
	public ComboBox setComboBoxFilter(final String columnId, final List list) {
		CellFilterComponent<ComboBox> filter = new CellFilterComponent<ComboBox>() {

			ComboBox comboBox = new ComboBox();

			@Override
			public ComboBox layoutComponent() {
				BeanItemContainer container = new BeanItemContainer(list.get(0)
						.getClass(), list);

				this.comboBox.setNullSelectionAllowed(true);
				this.comboBox.setImmediate(true);
				this.comboBox.setContainerDataSource(container);
				this.comboBox.addStyleName(ValoTheme.TEXTFIELD_TINY);
				this.comboBox.addValueChangeListener(new ValueChangeListener() {

					Equal filter = null;

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
	 * @param columnId
	 * @return
	 */
	public ComboBox setBooleanFilter(final String columnId) {
		CellFilterComponent<ComboBox> filter = new CellFilterComponent<ComboBox>() {

			ComboBox comboBox = new ComboBox();

			private Item genItem(final Boolean value) {
				Item item = this.comboBox.getItem(this.comboBox.addItem());
				item.getItemProperty("icon")
						.setValue(value ? FontAwesome.CHECK_SQUARE : FontAwesome.TIMES);
				item.getItemProperty("value")
						.setValue(value);
				item.getItemProperty("caption")
						.setValue(value.toString());
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

				genItem(Boolean.TRUE);
				genItem(Boolean.FALSE);

				this.comboBox.setNullSelectionAllowed(true);
				this.comboBox.setImmediate(true);
				this.comboBox.addStyleName(ValoTheme.TEXTFIELD_TINY);
				this.comboBox.addValueChangeListener(new ValueChangeListener() {

					Equal filter = null;

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
	 * @param columnId
	 * @return FieldGroup that holds both TextFields (smallest and biggest as propertyId)
	 */
	public FieldGroup setNumberFilter(final String columnId) {
		final Class type = this.grid.getContainerDataSource()
				.getType(columnId);
		RangeCellFilterComponent<HorizontalLayout> filter = new RangeCellFilterComponent<HorizontalLayout>() {

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
				field.addStyleName(ValoTheme.TEXTFIELD_TINY);
				field.addValueChangeListener(new ValueChangeListener() {

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
				TextField biggest = genNumberField("biggest");
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

					@Override
					public void preCommit(final CommitEvent commitEvent) throws CommitException {
					}

					@Override
					public void postCommit(final CommitEvent commitEvent) throws CommitException {
						Object smallestValue = getFieldGroup().getItemDataSource()
								.getItemProperty("smallest")
								.getValue();
						Object biggestValue = getFieldGroup().getItemDataSource()
								.getItemProperty("biggest")
								.getValue();
						if (smallestValue != null || biggestValue != null) {
							replaceFilter(new Between(columnId, (Comparable) (smallestValue != null ? smallestValue : getValue(false)),
									(Comparable) (biggestValue != null ? biggestValue : getValue(true))), columnId);
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
	 * @param columnId
	 * @return FieldGroup that holds both TextFields (smallest and biggest as propertyId)
	 */
	public FieldGroup setDateFilter(final String columnId) {
		RangeCellFilterComponent<HorizontalLayout> filter = new RangeCellFilterComponent<HorizontalLayout>() {

			private DateField genDateField(final String propertyId) {
				final DateField dateField = new DateField();
				getFieldGroup().bind(dateField, propertyId);
				dateField.setWidth("100%");
				dateField.setImmediate(true);
				dateField.setInvalidAllowed(false);
				dateField.setInvalidCommitted(false);
				dateField.setResolution(Resolution.DAY);
				dateField.addStyleName(ValoTheme.TEXTFIELD_TINY);
				dateField.addValueChangeListener(new ValueChangeListener() {

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

					@Override
					public void preCommit(final CommitEvent commitEvent) throws CommitException {
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
							replaceFilter(new Between(columnId, smallestValue != null ? smallestValue : MIN_DATE_VALUE, biggestValue != null ? biggestValue
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

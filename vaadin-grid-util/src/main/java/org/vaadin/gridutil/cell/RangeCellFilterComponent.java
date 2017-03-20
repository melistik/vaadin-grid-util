package org.vaadin.gridutil.cell;

import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.data.util.ObjectProperty;
import com.vaadin.v7.data.util.PropertysetItem;
import com.vaadin.ui.Component;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.HorizontalLayout;

/**
 * extends CellFilterComponent to allow smallest and biggest filter Component
 *
 * @author Marten Prieß (http://www.non-rocket-science.com)
 * @version 1.1
 */
public abstract class RangeCellFilterComponent<F extends Field, C extends Component> extends CellFilterComponent<C> {

	public static final String SMALLEST = "smallest";
	public static final String BIGGEST = "biggest";
	private static final long serialVersionUID = 1L;
	private HorizontalLayout hLayout;
	private FieldGroup fieldGroup;

	public abstract F getSmallestField();

	public abstract F getBiggestField();

	/**
	 * creates the layout when not already done
	 * 
	 * @return a HLayout with already set style
	 */
	public HorizontalLayout getHLayout() {
		if (this.hLayout == null) {
			this.hLayout = new HorizontalLayout();
			this.hLayout.addStyleName("filter-header");
		}
		return this.hLayout;
	}

	/**
	 * create fieldgroup when not already done
	 * 
	 * @return instance of fieldgroup
	 */
	public FieldGroup getFieldGroup() {
		if (this.fieldGroup == null) {
			this.fieldGroup = new FieldGroup();
		}
		return this.fieldGroup;
	}

	@Override
	public void triggerUpdate() {
		try {
			getFieldGroup().commit();
		} catch (FieldGroup.CommitException e) {
		}
	}

	/**
	 * creates an PropertysetItem with two properties (smallest and biggest)
	 *
	 * @param type
	 *            that both properties should have
	 * @return generated PropertysetItem with null as value for both properties
	 */
	public PropertysetItem genPropertysetItem(final Class type) {
		PropertysetItem item = new PropertysetItem();
		item.addItemProperty(SMALLEST, new ObjectProperty(null, type));
		item.addItemProperty(BIGGEST, new ObjectProperty(null, type));
		return item;
	}
}

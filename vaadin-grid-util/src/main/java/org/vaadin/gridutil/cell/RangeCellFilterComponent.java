package org.vaadin.gridutil.cell;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;

/**
 * extends CellFilterComponent to allow smallest and biggest filter Component
 *
 * @author Marten Prieß (http://www.non-rocket-science.com)
 * @version 1.0
 */
public abstract class RangeCellFilterComponent<C extends Component> extends CellFilterComponent<C> {

	private static final long serialVersionUID = 1L;
	private HorizontalLayout hLayout;
	private FieldGroup fieldGroup;

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

	/**
	 * creates an PropertysetItem with two properties (smallest and biggest)
	 * 
	 * @param type
	 *            that both properties should have
	 * @return generated PropertysetItem with null as value for both properties
	 */
	public PropertysetItem genPropertysetItem(final Class type) {
		PropertysetItem item = new PropertysetItem();
		item.addItemProperty("smallest", new ObjectProperty(null, type));
		item.addItemProperty("biggest", new ObjectProperty(null, type));
		return item;
	}
}

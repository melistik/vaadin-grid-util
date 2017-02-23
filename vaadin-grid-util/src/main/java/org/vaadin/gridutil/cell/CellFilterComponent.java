package org.vaadin.gridutil.cell;

import com.vaadin.ui.Component;

import java.io.Serializable;

/**
 * Interface for each CellFilter in order to allow clear values
 *
 * @author Marten Prieß (http://www.rocketbase.io)
 * @version 1.1
 */
public abstract class CellFilterComponent<C extends Component> implements Serializable {

	private static final long serialVersionUID = 1L;
	private C component;

	public C getComponent() {
		if (this.component == null) {
			this.component = layoutComponent();
		}
		return this.component;
	}

	/**
	 * can be used to perform grid filterin<br>
	 * useful when set filter value manually from outside
	 */
	public abstract void triggerUpdate();

	/**
	 * main component that is painted in the filterRow
	 * 
	 * @return to render vaadin component
	 */
	public abstract C layoutComponent();

	/**
	 * implement clearValues
	 */
	public abstract void clearFilter();

}

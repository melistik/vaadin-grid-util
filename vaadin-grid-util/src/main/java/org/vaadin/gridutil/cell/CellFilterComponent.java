package org.vaadin.gridutil.cell;

import com.vaadin.ui.Component;

/**
 * Interface for each CellFilter in order to allow clear values
 *
 * @author Marten Prieß (http://www.non-rocket-science.com)
 * @version 1.0
 */
public abstract class CellFilterComponent<C extends Component> {

	private C component;

	public C getComponent() {
		if (this.component == null) {
			this.component = layoutComponent();
		}
		return this.component;
	}

	/**
	 * main component that is painted in the filterRow
	 * 
	 * @return
	 */
	public abstract C layoutComponent();

	/**
	 * implement clearValues
	 */
	public abstract void clearFilter();

}

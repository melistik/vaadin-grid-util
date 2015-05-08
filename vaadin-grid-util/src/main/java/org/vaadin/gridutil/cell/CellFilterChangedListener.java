package org.vaadin.gridutil.cell;

import java.io.Serializable;

/**
 * Listener for CellFilter changes
 *
 * @author Marten Prieß (http://www.non-rocket-science.com)
 * @version 1.0
 */
public interface CellFilterChangedListener extends Serializable {

	void changedFilter(final GridCellFilter cellFilter);
}

package org.vaadin.gridutil.cell;

import com.vaadin.data.Binder;
import com.vaadin.data.HasValue;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;

/**
 * extends CellFilterComponent to allow smallest and biggest filter Component
 *
 * @author Marten Prieß (http://www.rocketbase.io)
 * @version 1.1
 */
public abstract class RangeCellFilterComponent<V, F extends HasValue, C extends Component> extends CellFilterComponent<C> {

    public static final String SMALLEST = "smallest";
    public static final String BIGGEST = "biggest";
    private static final long serialVersionUID = 1L;
    private HorizontalLayout hLayout;
    private Binder<TwoValueObject> binder;

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
            this.hLayout.setMargin(false);
            this.hLayout.addStyleName("filter-header");
        }
        return this.hLayout;
    }

    /**
     * create binder when not already done
     *
     * @return instance of binder
     */
    public Binder<TwoValueObject> getBinder() {
        if (this.binder == null) {
            this.binder = new Binder(TwoValueObject.class);
            this.binder.setBean(new TwoValueObject());
        }
        return this.binder;
    }

    @Override
    public void triggerUpdate() {
        // trigger value Changed
        getBinder().setBean(getBinder().getBean());
    }

    public class TwoValueObject<V> {
        private V smallest;
        private V biggest;

        public V getSmallest() {
            return smallest;
        }

        public void setSmallest(V smallest) {
            this.smallest = smallest;
        }

        public V getBiggest() {
            return biggest;
        }

        public void setBiggest(V biggest) {
            this.biggest = biggest;
        }
    }

}

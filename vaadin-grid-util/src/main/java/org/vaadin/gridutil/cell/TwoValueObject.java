package org.vaadin.gridutil.cell;

/**
 * Created by marten on 01.03.17.
 */
public class TwoValueObject {
    private Object smallest;
    private Object biggest;

    public TwoValueObject() {

    }

    public TwoValueObject(Object smallest, Object biggest) {
        setSmallest(smallest);
        setBiggest(biggest);
    }

    public Object getSmallest() {
        return smallest;
    }

    public void setSmallest(Object smallest) {
        this.smallest = smallest;
    }

    public Object getBiggest() {
        return biggest;
    }

    public void setBiggest(Object biggest) {
        this.biggest = biggest;
    }
}
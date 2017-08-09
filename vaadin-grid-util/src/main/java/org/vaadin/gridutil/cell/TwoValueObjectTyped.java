package org.vaadin.gridutil.cell;

/**
 * Created by marten on 01.03.17.
 */
public class TwoValueObjectTyped<T> {
    private T smallest;
    private T biggest;

    public TwoValueObjectTyped() {

    }

    public TwoValueObjectTyped(T smallest, T biggest) {
        setSmallest(smallest);
        setBiggest(biggest);
    }

    public T getSmallest() {
        return smallest;
    }

    public void setSmallest(T smallest) {
        this.smallest = smallest;
    }

    public T getBiggest() {
        return biggest;
    }

    public void setBiggest(T biggest) {
        this.biggest = biggest;
    }
}
/* (C)2020 */
package org.anchoranalysis.gui.propertyvalue;

import java.util.ArrayList;
import java.util.Iterator;
import org.anchoranalysis.core.property.IPropertyValueReceivable;
import org.anchoranalysis.core.property.change.PropertyValueChangeListener;

public class PropertyValueChangeListenerList<T>
        implements Iterable<PropertyValueChangeListener<T>> {

    private ArrayList<PropertyValueChangeListener<T>> list = new ArrayList<>();

    @Override
    public Iterator<PropertyValueChangeListener<T>> iterator() {
        return list.iterator();
    }

    public boolean add(PropertyValueChangeListener<T> e) {
        return list.add(e);
    }

    public boolean remove(PropertyValueChangeListener<T> e) {
        return list.remove(e);
    }

    public IPropertyValueReceivable<T> createPropertyValueReceivable() {
        return new IPropertyValueReceivable<T>() {

            @Override
            public void addPropertyValueChangeListener(PropertyValueChangeListener<T> l) {
                list.add(l);
            }

            @Override
            public void removePropertyValueChangeListener(PropertyValueChangeListener<T> l) {
                list.remove(l);
            }
        };
    }

    public int size() {
        return list.size();
    }
}

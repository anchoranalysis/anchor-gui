/*-
 * #%L
 * anchor-gui-frame
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
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

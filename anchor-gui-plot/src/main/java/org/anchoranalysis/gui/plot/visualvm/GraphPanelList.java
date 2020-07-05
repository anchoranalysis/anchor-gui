package org.anchoranalysis.gui.plot.visualvm;

/*
 * #%L
 * anchor-gui
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.anchoranalysis.anchor.mpp.feature.nrg.cfg.CfgWithNRGTotal;
import org.anchoranalysis.gui.videostats.ICfgNRGUpdater;
import org.anchoranalysis.mpp.sgmn.optscheme.feedback.aggregate.Aggregator;

public class GraphPanelList implements ICfgNRGUpdater, Collection<GraphPanel> {

	private ArrayList<GraphPanel> delegate = new ArrayList<>();

	@Override
	public boolean add(GraphPanel e) {
		return delegate.add(e);
	}

	@Override
	public void updateCrnt(int iter, long timeStamp, CfgWithNRGTotal crnt, Aggregator agg) {
		for (GraphPanel gp : delegate) {
			gp.updateCrnt(iter, timeStamp, crnt, agg);
		}
		
	}

	@Override
	public void updateBest(int iter, long timeStamp, CfgWithNRGTotal best) {
		for (GraphPanel gp : delegate) {
			gp.updateBest(iter, timeStamp, best);
		}
	}

	public void add(int index, GraphPanel element) {
		delegate.add(index, element);
	}

	@Override
	public boolean addAll(Collection<? extends GraphPanel> c) {
		return delegate.addAll(c);
	}

	public boolean addAll(int index, Collection<? extends GraphPanel> c) {
		return delegate.addAll(index, c);
	}

	@Override
	public void clear() {
		delegate.clear();
	}

	@Override
	public Object clone() {
		return delegate.clone();
	}

	@Override
	public boolean contains(Object o) {
		return delegate.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> arg0) {
		return delegate.containsAll(arg0);
	}

	public void ensureCapacity(int minCapacity) {
		delegate.ensureCapacity(minCapacity);
	}

	@Override
	public boolean equals(Object arg0) {
		return delegate.equals(arg0);
	}

	public GraphPanel get(int index) {
		return delegate.get(index);
	}

	@Override
	public int hashCode() {
		return delegate.hashCode();
	}

	public int indexOf(Object o) {
		return delegate.indexOf(o);
	}

	@Override
	public boolean isEmpty() {
		return delegate.isEmpty();
	}

	@Override
	public Iterator<GraphPanel> iterator() {
		return delegate.iterator();
	}

	public int lastIndexOf(Object o) {
		return delegate.lastIndexOf(o);
	}

	public ListIterator<GraphPanel> listIterator() {
		return delegate.listIterator();
	}

	public ListIterator<GraphPanel> listIterator(int arg0) {
		return delegate.listIterator(arg0);
	}

	public GraphPanel remove(int index) {
		return delegate.remove(index);
	}

	@Override
	public boolean remove(Object o) {
		return delegate.remove(o);
	}

	@Override
	public boolean removeAll(Collection<?> arg0) {
		return delegate.removeAll(arg0);
	}

	@Override
	public boolean retainAll(Collection<?> arg0) {
		return delegate.retainAll(arg0);
	}

	public GraphPanel set(int index, GraphPanel element) {
		return delegate.set(index, element);
	}

	@Override
	public int size() {
		return delegate.size();
	}

	public List<GraphPanel> subList(int arg0, int arg1) {
		return delegate.subList(arg0, arg1);
	}

	@Override
	public Object[] toArray() {
		return delegate.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return delegate.toArray(a);
	}

	@Override
	public String toString() {
		return delegate.toString();
	}

	public void trimToSize() {
		delegate.trimToSize();
	}
	
	
}

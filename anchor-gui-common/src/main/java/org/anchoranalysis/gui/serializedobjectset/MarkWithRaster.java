/* (C)2020 */
package org.anchoranalysis.gui.serializedobjectset;

import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.core.index.IIndexGetter;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.gui.backgroundset.BackgroundSet;

public class MarkWithRaster implements IIndexGetter, Comparable<IIndexGetter> {

    private int index;
    private Mark mark;
    private NRGStackWithParams nrgStack;
    private BackgroundSet backgroundSet;

    public Mark getMark() {
        return mark;
    }

    public void setMark(Mark mark) {
        this.mark = mark;
    }

    @Override
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public int compareTo(IIndexGetter o) {
        return Integer.valueOf(index).compareTo(o.getIndex());
    }

    public BackgroundSet getBackgroundSet() {
        return backgroundSet;
    }

    public void setBackgroundSet(BackgroundSet backgroundSet) {
        this.backgroundSet = backgroundSet;
    }
    //		public NamedImgStackCollection getRasterCollection() {
    //			return rasterCollection;
    //		}
    //		public void setRasterCollection(NamedImgStackCollection rasterCollection) {
    //			this.rasterCollection = rasterCollection;
    //		}
    public NRGStackWithParams getNRGStack() {
        return nrgStack;
    }

    public void setNRGStack(NRGStackWithParams nrgStack) {
        this.nrgStack = nrgStack;
    }
}

/* (C)2020 */
package org.anchoranalysis.gui.interactivebrowser.openfile.type;

import org.anchoranalysis.anchor.mpp.feature.bean.nrgscheme.NRGSchemeCreator;

public class NrgSchemeCreatorState {

    private static NrgSchemeCreatorState instance;

    private NRGSchemeCreator item;

    private NrgSchemeCreatorState() {}

    public static NrgSchemeCreatorState instance() {
        if (instance == null) {
            instance = new NrgSchemeCreatorState();
        }
        return instance;
    }

    /** Can return NULL */
    public NRGSchemeCreator getItem() {
        return item;
    }

    public void setItem(NRGSchemeCreator item) {
        this.item = item;
    }
}

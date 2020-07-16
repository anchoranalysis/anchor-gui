/* (C)2020 */
package org.anchoranalysis.gui.interactivebrowser.input;

import org.anchoranalysis.anchor.mpp.feature.nrg.scheme.NRGScheme;
import org.anchoranalysis.anchor.mpp.feature.nrg.scheme.NamedNRGSchemeSet;
import org.anchoranalysis.core.error.CreateException;

class RegionMapFinder {

    public static void addFromNrgScheme(NamedNRGSchemeSet nrgElemSet, NRGScheme nrgScheme)
            throws CreateException {
        nrgElemSet.add("elem_ind", nrgScheme);
        nrgElemSet.add("elem_pair", nrgScheme);
    }
}

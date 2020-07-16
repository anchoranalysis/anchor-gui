/* (C)2020 */
package org.anchoranalysis.gui.mark;

import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.core.index.IndicesSelection;

public class CfgUtilities {

    public static Cfg createCfgSubset(Cfg cfg, IndicesSelection indices) {

        Cfg cfgNew = new Cfg();

        // This our current
        for (Mark m : cfg) {
            if (indices.contains(m.getId())) {
                cfgNew.add(m);
            }
        }

        return cfgNew;
    }
}

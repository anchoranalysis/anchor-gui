/* (C)2020 */
package org.anchoranalysis.gui.frame.cfgproposer;

import java.util.EventListener;
import org.anchoranalysis.gui.frame.overlays.ProposedCfg;

@FunctionalInterface
public interface CfgProposedListener extends EventListener {

    void proposed(ProposedCfg proposedCfg);
}

/* (C)2020 */
package org.anchoranalysis.gui.videostats.internalframe.annotator.currentstate;

import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.anchor.mpp.mark.Mark;

public class DualCfg implements IQueryAcceptedRejected {

    private Cfg cfgAccepted = new Cfg();
    private Cfg cfgRejected = new Cfg();

    public DualCfg() {}

    public DualCfg(Cfg cfgAccepted, Cfg cfgRejected) {
        super();
        this.cfgAccepted = cfgAccepted;

        if (cfgRejected != null) {
            this.cfgRejected = cfgRejected;
        } else {
            // Replace null initialisation with an empty set
            // This is handle backwards compatiblility from serialized annotation files
            //   when cfgRejected was not present
            this.cfgRejected = new Cfg();
        }
    }

    public void addAll(boolean accepted, Cfg src) {
        if (accepted) {
            cfgAccepted.addAll(src);
        } else {
            cfgRejected.addAll(src);
        }
    }

    // Remove from either cfgAccepted or cfgRejected depending on where it can be found
    public void removeFromEither(Mark mark) {
        int index = cfgAccepted.indexOf(mark);
        if (index != -1) {
            cfgAccepted.remove(index);
        } else {

            index = cfgRejected.indexOf(mark);
            if (index != -1) {
                cfgRejected.remove(index);
            } else {
                assert false;
            }
        }
    }

    @Override
    public Cfg getCfgAccepted() {
        return cfgAccepted;
    }

    @Override
    public Cfg getCfgRejected() {
        return cfgRejected;
    }

    public DualCfg shallowCopy() {
        DualCfg out = new DualCfg();
        out.cfgAccepted = cfgAccepted.shallowCopy();
        out.cfgRejected = cfgRejected.shallowCopy();
        return out;
    }

    public void addAll(DualCfg in) {
        cfgAccepted.addAll(in.cfgAccepted);
        cfgRejected.addAll(in.cfgRejected);
    }
}

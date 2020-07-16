/* (C)2020 */
package org.anchoranalysis.plugin.gui.bean.createrastergenerator.cfgnrginstantstate;

import org.anchoranalysis.anchor.mpp.feature.nrg.cfg.CfgNRG;
import org.anchoranalysis.bean.annotation.AllowEmpty;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.plugin.gui.bean.createrastergenerator.CreateStringRasterGenerator;

public class CfgSizeStringFromCfgNRGInstantState extends CreateStringRasterGenerator {

    // START BEAN PROPERTIES
    @BeanField @AllowEmpty private String prefix = "";
    // END BEAN PROPERTIES

    @Override
    public String getBeanDscr() {
        return getBeanName();
    }

    @Override
    protected String extractStringFrom(CfgNRG cfgNRG) {
        if (cfgNRG != null) {
            return prefix + String.valueOf(cfgNRG.getCfg().size());
        } else {
            return prefix + "0";
        }
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}

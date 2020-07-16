/* (C)2020 */
package org.anchoranalysis.plugin.gui.bean.createrastergenerator;

import org.anchoranalysis.anchor.mpp.feature.nrg.cfg.CfgNRG;
import org.anchoranalysis.bean.annotation.BeanField;

public class ConstantStringRasterGenerator extends CreateStringRasterGenerator {

    // START BEAN PROPERTIES
    @BeanField private String text;
    // END BEAN PROPERTIES

    @Override
    protected String extractStringFrom(CfgNRG cfgNRG) {
        return text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

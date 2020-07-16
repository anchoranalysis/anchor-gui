/* (C)2020 */
package org.anchoranalysis.plugin.gui.bean.createrastergenerator;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.anchor.mpp.feature.nrg.cfg.CfgNRG;
import org.anchoranalysis.bean.annotation.BeanField;

public class ConstantStringRasterGenerator extends CreateStringRasterGenerator {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private String text;
    // END BEAN PROPERTIES

    @Override
    protected String extractStringFrom(CfgNRG cfgNRG) {
        return text;
    }
}

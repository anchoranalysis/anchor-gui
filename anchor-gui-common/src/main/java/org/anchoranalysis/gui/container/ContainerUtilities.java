/* (C)2020 */
package org.anchoranalysis.gui.container;

import java.util.ArrayList;
import java.util.List;
import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgNRGInstantState;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;

public class ContainerUtilities {

    public static List<BoundedIndexContainer<CfgNRGInstantState>> listCntrs(
            List<ContainerGetter<CfgNRGInstantState>> cntrs) throws GetOperationFailedException {

        List<BoundedIndexContainer<CfgNRGInstantState>> out =
                new ArrayList<BoundedIndexContainer<CfgNRGInstantState>>();

        for (ContainerGetter<CfgNRGInstantState> item : cntrs) {
            out.add(item.getCntr());
        }

        return out;
    }
}

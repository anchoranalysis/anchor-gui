/* (C)2020 */
package org.anchoranalysis.gui.retrieveelements;

import java.util.ArrayList;
import java.util.List;

public class RetrieveElementsList extends RetrieveElements {

    private List<RetrieveElements> list = new ArrayList<>();

    public void add(RetrieveElements re) {
        list.add(re);
    }

    @Override
    public void addToPopUp(AddToExportSubMenu popUp) {

        for (RetrieveElements re : list) {
            re.addToPopUp(popUp);
        }
    }
}

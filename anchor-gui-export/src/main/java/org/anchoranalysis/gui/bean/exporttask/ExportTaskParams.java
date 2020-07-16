/* (C)2020 */
package org.anchoranalysis.gui.bean.exporttask;

import java.util.ArrayList;
import java.util.List;
import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgNRGInstantState;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.gui.container.ContainerGetter;
import org.anchoranalysis.gui.finder.imgstackcollection.FinderImgStackCollection;
import org.anchoranalysis.gui.io.loader.manifest.finder.FinderCSVStats;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;

// Parameters an exportTask can draw itself from
public class ExportTaskParams {

    private ColorIndex colorIndexMarks;
    private FinderImgStackCollection finderImgStackCollection;
    private FinderCSVStats finderCsvStatistics;
    private List<ContainerGetter<CfgNRGInstantState>> listFinderCfgNRGHistory = new ArrayList<>();
    private BoundOutputManagerRouteErrors outputManager;

    public ColorIndex getColorIndexMarks() {
        return colorIndexMarks;
    }

    public void setColorIndexMarks(ColorIndex colorIndexMarks) {
        this.colorIndexMarks = colorIndexMarks;
    }

    public FinderImgStackCollection getFinderImgStackCollection() {
        return finderImgStackCollection;
    }

    public void setFinderImgStackCollection(FinderImgStackCollection finder) {
        this.finderImgStackCollection = finder;
    }

    public FinderCSVStats getFinderCsvStatistics() {
        return finderCsvStatistics;
    }

    public void setFinderCsvStatistics(FinderCSVStats csvStatistics) {
        this.finderCsvStatistics = csvStatistics;
    }

    public ContainerGetter<CfgNRGInstantState> getFinderCfgNRGHistory() {
        return listFinderCfgNRGHistory.get(0);
    }

    public ContainerGetter<CfgNRGInstantState> getFinderCfgNRGHistory(int index) {
        return listFinderCfgNRGHistory.get(index);
    }

    public List<ContainerGetter<CfgNRGInstantState>> getAllFinderCfgNRGHistory() {
        return listFinderCfgNRGHistory;
    }

    public int numCfgNRGHistory() {
        return listFinderCfgNRGHistory.size();
    }

    public void addFinderCfgNRGHistory(ContainerGetter<CfgNRGInstantState> finderCfgNRGHistory) {
        if (finderCfgNRGHistory != null) {
            this.listFinderCfgNRGHistory.add(finderCfgNRGHistory);
        }
    }

    public BoundOutputManagerRouteErrors getOutputManager() {
        return outputManager;
    }

    public void setOutputManager(BoundOutputManagerRouteErrors outputManager) {
        this.outputManager = outputManager;
    }
}

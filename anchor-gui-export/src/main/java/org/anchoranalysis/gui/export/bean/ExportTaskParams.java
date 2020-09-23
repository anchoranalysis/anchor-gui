/*-
 * #%L
 * anchor-gui-export
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

package org.anchoranalysis.gui.export.bean;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.gui.container.ContainerGetter;
import org.anchoranalysis.gui.finder.imgstackcollection.FinderStacks;
import org.anchoranalysis.gui.manifest.FinderCSVStats;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;
import org.anchoranalysis.mpp.feature.energy.IndexableMarksWithEnergy;

// Parameters an exportTask can draw itself from
public class ExportTaskParams {

    @Getter @Setter private ColorIndex colorIndexMarks;
    @Getter @Setter private FinderCSVStats finderCsvStatistics;
    @Getter @Setter private FinderStacks finderStacks;
    @Getter @Setter private BoundOutputManagerRouteErrors outputManager;

    private List<ContainerGetter<IndexableMarksWithEnergy>> listFinderMarksHistory =
            new ArrayList<>();

    public ContainerGetter<IndexableMarksWithEnergy> getFinderMarksHistory() {
        return listFinderMarksHistory.get(0);
    }

    public ContainerGetter<IndexableMarksWithEnergy> getFinderMarksHistory(int index) {
        return listFinderMarksHistory.get(index);
    }

    public List<ContainerGetter<IndexableMarksWithEnergy>> getAllFinderMarksHistory() {
        return listFinderMarksHistory;
    }

    public int numberMarksHistory() {
        return listFinderMarksHistory.size();
    }

    public void addFinderMarksHistory(
            ContainerGetter<IndexableMarksWithEnergy> finderMarksHistory) {
        if (finderMarksHistory != null) {
            this.listFinderMarksHistory.add(finderMarksHistory);
        }
    }
}

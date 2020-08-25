/*-
 * #%L
 * anchor-plugin-gui-export
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

package org.anchoranalysis.plugin.gui.bean.exporttask;

import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.anchor.mpp.feature.energy.IndexableMarksWithEnergy;
import org.anchoranalysis.anchor.mpp.feature.energy.marks.MarksWithEnergyBreakdown;
import org.anchoranalysis.anchor.mpp.feature.energy.marks.MarksWithTotalEnergy;
import org.anchoranalysis.anchor.mpp.feature.energy.scheme.EnergySchemeWithSharedFeatures;
import org.anchoranalysis.anchor.mpp.mark.MarkCollection;
import org.anchoranalysis.anchor.mpp.overlay.OverlayCollectionMarkFactory;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.core.functional.function.CheckedFunction;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;
import org.anchoranalysis.core.index.container.bridge.BoundedIndexContainerBridgeWithoutIndex;
import org.anchoranalysis.gui.bean.exporttask.ExportTaskParams;
import org.anchoranalysis.gui.container.ContainerUtilities;
import org.anchoranalysis.gui.mergebridge.DualStateContainer;
import org.anchoranalysis.gui.mergebridge.MergeMarksBridge;
import org.anchoranalysis.gui.mergebridge.MergedColorIndex;
import org.anchoranalysis.gui.mergebridge.TransformToMarks;
import org.anchoranalysis.overlay.IndexableOverlays;

@RequiredArgsConstructor
class MergedContainerBridge
        implements CheckedFunction<
                ExportTaskParams,
                BoundedIndexContainer<IndexableMarksWithEnergy>,
                OperationFailedException> {

    // START REQUIRED ARGUMENTS
    private final Supplier<RegionMembershipWithFlags> regionMembership;
    
    private final EnergySchemeWithSharedFeatures energyScheme;
    // END REQUIRED ARGUMENTS

    private BoundedIndexContainerBridgeWithoutIndex<
                    IndexableOverlays, IndexableMarksWithEnergy, AnchorImpossibleSituationException>
            retBridge = null;

    @Override
    public BoundedIndexContainer<IndexableMarksWithEnergy> apply(ExportTaskParams sourceObject)
            throws OperationFailedException {

        // TODO fix
        if (retBridge == null) {

            DualStateContainer<MarkCollection> dualHistory =
                    new DualStateContainer<>(
                            ContainerUtilities.listCntrs(sourceObject.getAllFinderMarksHistory()),
                            new TransformToMarks());

            dualHistory.init();

            MergeMarksBridge mergeBridge = new MergeMarksBridge(regionMembership);

            BoundedIndexContainer<IndexableOverlays> container =
                    new BoundedIndexContainerBridgeWithoutIndex<>(dualHistory, mergeBridge);

            // TODO HACK to allow exportparams to work
            sourceObject.setColorIndexMarks(new MergedColorIndex(mergeBridge));

            // TODO this doesn't seem correct
            retBridge =
                    new BoundedIndexContainerBridgeWithoutIndex<>(
                            container,
                            source -> {
                                MarkCollection marks =
                                        OverlayCollectionMarkFactory.marksFromOverlays(
                                                source.getOverlays());
                                return createIndexableMarks(marks, source.getIndex());
                            });
        }
        return retBridge;
    }
    
    private IndexableMarksWithEnergy createIndexableMarks(MarkCollection marks, int index) {
        return new IndexableMarksWithEnergy(
                index,
                new MarksWithEnergyBreakdown(
                        new MarksWithTotalEnergy(marks, energyScheme))
                        
                );
    }
}

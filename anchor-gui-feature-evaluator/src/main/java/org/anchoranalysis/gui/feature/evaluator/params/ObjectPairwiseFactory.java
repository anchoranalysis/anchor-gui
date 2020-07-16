/* (C)2020 */
package org.anchoranalysis.gui.feature.evaluator.params;

import java.util.Optional;
import lombok.AllArgsConstructor;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.VoxelizedMarkMemo;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.feature.object.input.FeatureInputPairObjects;
import org.anchoranalysis.image.object.ObjectMask;

@AllArgsConstructor
public class ObjectPairwiseFactory extends PairwiseFactory {

    private final int regionID;

    @Override
    public FeatureInput create(
            VoxelizedMarkMemo pmm1, VoxelizedMarkMemo pmm2, NRGStackWithParams nrgStack)
            throws CreateException {

        ObjectMask object1 =
                pmm1.getMark()
                        .calcMask(
                                nrgStack.getDimensions(),
                                pmm1.getRegionMap().membershipWithFlagsForIndex(regionID),
                                BinaryValuesByte.getDefault())
                        .getMask();

        ObjectMask object2 =
                pmm1.getMark()
                        .calcMask(
                                nrgStack.getDimensions(),
                                pmm2.getRegionMap().membershipWithFlagsForIndex(regionID),
                                BinaryValuesByte.getDefault())
                        .getMask();

        return new FeatureInputPairObjects(object1, object2, Optional.of(nrgStack));
    }
}

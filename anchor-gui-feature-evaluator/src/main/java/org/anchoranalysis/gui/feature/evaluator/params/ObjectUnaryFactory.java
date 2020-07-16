/* (C)2020 */
package org.anchoranalysis.gui.feature.evaluator.params;

import lombok.AllArgsConstructor;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.VoxelizedMarkMemo;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.feature.object.input.FeatureInputSingleObject;
import org.anchoranalysis.image.object.ObjectMask;

@AllArgsConstructor
public class ObjectUnaryFactory extends UnaryFactory {

    private final int regionID;

    @Override
    public FeatureInput create(VoxelizedMarkMemo pmm, NRGStackWithParams nrgStack)
            throws CreateException {

        ObjectMask object =
                pmm.getMark()
                        .calcMask(
                                nrgStack.getDimensions(),
                                pmm.getRegionMap().membershipWithFlagsForIndex(regionID),
                                BinaryValuesByte.getDefault())
                        .getMask();

        return new FeatureInputSingleObject(object, nrgStack);
    }
}

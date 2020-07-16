/* (C)2020 */
package org.anchoranalysis.gui.feature.evaluator.params;

import java.util.Optional;
import lombok.AllArgsConstructor;
import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.VoxelizedMarkMemo;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.feature.object.input.FeatureInputObjectCollection;
import org.anchoranalysis.image.object.ObjectCollectionFactory;
import org.anchoranalysis.image.object.ObjectMask;

@AllArgsConstructor
public class ObjectCollectionUnaryFactory extends UnaryFactory {

    private final RegionMembershipWithFlags regionMembership;

    @Override
    public FeatureInput create(VoxelizedMarkMemo pmm, NRGStackWithParams nrgStack)
            throws CreateException {

        ObjectMask object =
                pmm.getMark()
                        .calcMask(
                                nrgStack.getDimensions(),
                                regionMembership,
                                BinaryValuesByte.getDefault())
                        .getMask();

        return new FeatureInputObjectCollection(
                ObjectCollectionFactory.from(object), Optional.of(nrgStack));
    }
}

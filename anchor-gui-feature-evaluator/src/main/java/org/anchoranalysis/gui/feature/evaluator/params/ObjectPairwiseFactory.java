/*-
 * #%L
 * anchor-gui-feature-evaluator
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

package org.anchoranalysis.gui.feature.evaluator.params;

import java.util.Optional;
import lombok.AllArgsConstructor;
import org.anchoranalysis.anchor.mpp.mark.voxelized.memo.VoxelizedMarkMemo;
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

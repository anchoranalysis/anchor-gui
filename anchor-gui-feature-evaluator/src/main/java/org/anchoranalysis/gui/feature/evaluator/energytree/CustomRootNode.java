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

package org.anchoranalysis.gui.feature.evaluator.energytree;

import javax.swing.tree.TreeNode;
import org.anchoranalysis.core.exception.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.core.log.error.ErrorReporter;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.bean.list.FeatureListFactory;
import org.anchoranalysis.feature.input.FeatureInput;

class CustomRootNode extends FeatureListNode {

    public CustomRootNode(ErrorReporter errorReporter) {
        super(errorReporter);
        initChildFeatures(FeatureListFactory.empty(), null);
    }

    public void replaceFeatureList(
            FeatureList<FeatureInput> featureList, FeatureInputSource params) {
        getFeatures().clear();
        resetCalcList();
        this.initChildFeatures(featureList, params);
    }

    public void replaceCalcParams(FeatureInputSource params) {
        updateValueSource(params);
    }

    @Override
    public String getValue() {
        return "root";
    }

    @Override
    public TreeNode getParent() {
        return null;
    }

    @Override
    public Feature<FeatureInput> getFeature() {
        return null;
    }

    @Override
    public boolean hasError() {
        return false;
    }

    @Override
    public String getErrorText() {
        return null;
    }

    @Override
    public Throwable getError() {
        throw new AnchorImpossibleSituationException();
    }
}

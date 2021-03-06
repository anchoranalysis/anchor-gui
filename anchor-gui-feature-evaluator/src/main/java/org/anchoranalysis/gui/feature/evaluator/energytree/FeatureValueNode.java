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
import org.anchoranalysis.bean.exception.BeanMisconfiguredException;
import org.anchoranalysis.core.log.error.ErrorReporter;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.bean.list.FeatureListFactory;
import org.anchoranalysis.feature.input.FeatureInput;

// FeatureValue
class FeatureValueNode extends FeatureListNode {

    // The current value defining the feature (to be displayed to the user)
    private String value;

    private Feature<FeatureInput> parentFeature;
    private String errorText = "";
    private Throwable error;

    private TreeNode parentNode;

    private FeatureList<FeatureInput> childFeatures;

    public FeatureValueNode(
            Feature<FeatureInput> parentFeature,
            TreeNode parentNode,
            FeatureInputSource params,
            ErrorReporter errorReporter) {
        super(errorReporter);

        this.parentNode = parentNode;
        this.parentFeature = parentFeature;

        try {
            childFeatures = parentFeature.createListChildFeatures();
        } catch (BeanMisconfiguredException e) {
            errorReporter.recordError(FeatureValueNode.class, e);
            childFeatures = FeatureListFactory.empty();
        }

        // For the children, we embed the createParams that is used for the parent
        //  inside another CreateParams that optionally transforms the params for the child
        // CreateParams createParamsChild = new CreateParamsTransformChild( param, feature );

        initChildFeatures(childFeatures, createChildParam(parentFeature, params, childFeatures));
    }

    private static FeatureInputSource createChildParam(
            Feature<FeatureInput> parentFeature,
            FeatureInputSource parentParams,
            FeatureList<FeatureInput> childFeatures) {
        return parentParams;
    }

    @Override
    protected void updateValueSource(FeatureInputSource paramsSource) {
        FeatureInputSource childParams =
                createChildParam(parentFeature, paramsSource, childFeatures);
        super.updateValueSource(childParams);
    }

    @Override
    public Feature<FeatureInput> getFeature() {
        return parentFeature;
    }

    @Override
    public TreeNode getParent() {
        return parentNode;
    }

    @Override
    public String getValue() {
        return value;
    }

    public void setEnergyValue(double energyValue) {
        this.value = Double.toString(energyValue);
    }

    public void setErrorText(String errorDescription, Throwable cause) {
        // String seperator = System.getProperty("line.separator");
        this.value = "Error";
        errorText = errorDescription;
        error = cause;
    }

    @Override
    public boolean hasError() {
        return errorText != null && !errorText.isEmpty();
    }

    @Override
    public String getErrorText() {
        return errorText;
    }

    @Override
    public Throwable getError() {
        return error;
    }
}

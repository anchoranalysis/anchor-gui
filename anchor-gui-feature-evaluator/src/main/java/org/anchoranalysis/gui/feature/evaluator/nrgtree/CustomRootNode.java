/* (C)2020 */
package org.anchoranalysis.gui.feature.evaluator.nrgtree;

import javax.swing.tree.TreeNode;
import org.anchoranalysis.core.error.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.bean.list.FeatureListFactory;
import org.anchoranalysis.feature.input.FeatureInput;

class CustomRootNode extends FeatureListNode {

    public CustomRootNode(ErrorReporter errorReporter) {
        super(errorReporter);
        initChildFeatures(FeatureListFactory.empty(), null);
    }

    public void replaceFeatureList(FeatureList<FeatureInput> featureList, ParamsSource params) {
        getFeatures().clear();
        resetCalcList();
        this.initChildFeatures(featureList, params);
    }

    public void replaceCalcParams(ParamsSource params) {
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

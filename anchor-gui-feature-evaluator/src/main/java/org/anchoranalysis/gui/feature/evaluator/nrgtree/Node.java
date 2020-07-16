/* (C)2020 */
package org.anchoranalysis.gui.feature.evaluator.nrgtree;

import javax.swing.tree.TreeNode;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.input.FeatureInput;

public abstract class Node implements TreeNode {

    public abstract String getValue();

    public abstract Feature<FeatureInput> getFeature();

    protected abstract void updateValueSource(ParamsSource paramSource);

    public abstract boolean hasError();

    public abstract String getErrorText();

    public abstract Throwable getError();
}

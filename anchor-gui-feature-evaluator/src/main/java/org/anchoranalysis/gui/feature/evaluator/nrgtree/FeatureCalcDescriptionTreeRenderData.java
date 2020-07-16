/* (C)2020 */
package org.anchoranalysis.gui.feature.evaluator.nrgtree;

import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.netbeans.swing.outline.RenderDataProvider;

public class FeatureCalcDescriptionTreeRenderData implements RenderDataProvider {

    private ErrorReporter errorReporter;

    public FeatureCalcDescriptionTreeRenderData(ErrorReporter errorReporter) {
        super();
        this.errorReporter = errorReporter;
    }

    @Override
    public java.awt.Color getBackground(Object o) {
        return null;
    }

    @Override
    public String getDisplayName(Object o) {
        try {
            return ((Node) o).getFeature().getDscrWithCustomName();
        } catch (Exception e) {
            errorReporter.recordError(getClass(), e);
            return "errorCalculatingName";
        }
    }

    @Override
    public java.awt.Color getForeground(Object o) {
        // FeatureCalcDescriptionTreeModel.Node f = (FeatureCalcDescriptionTreeModel.Node) o;
        /*if (!f.isDirectory() && !f.canWrite()) {
            return UIManager.getColor("controlShadow");
        }*/
        return null;
    }

    @Override
    public javax.swing.Icon getIcon(Object o) {
        return null;
    }

    @Override
    public String getTooltipText(Object o) {
        Node f = (Node) o;
        return f.getFeature().getBeanDscr();
    }

    @Override
    public boolean isHtmlDisplayName(Object o) {
        return false;
    }
}

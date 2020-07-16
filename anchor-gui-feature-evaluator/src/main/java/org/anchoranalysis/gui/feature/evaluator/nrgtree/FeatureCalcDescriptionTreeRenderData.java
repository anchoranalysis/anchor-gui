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

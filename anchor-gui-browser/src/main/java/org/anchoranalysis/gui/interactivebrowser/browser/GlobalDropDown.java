/*-
 * #%L
 * anchor-gui-browser
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

package org.anchoranalysis.gui.interactivebrowser.browser;

import javax.swing.JButton;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.gui.feature.evaluator.treetable.FeatureListSrc;
import org.anchoranalysis.gui.videostats.dropdown.BoundVideoStatsModuleDropDown;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.MenuAddException;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.modulecreator.FeatureEvaluatorCreator;

class GlobalDropDown {

    private BoundVideoStatsModuleDropDown delegate;
    private IAddVideoStatsModule adder;

    public GlobalDropDown(IAddVideoStatsModule adder) {

        delegate = new BoundVideoStatsModuleDropDown("Global", "/toolbarIcon/big_circle.png");
        this.adder = adder;
    }

    public void init(FeatureListSrc src, VideoStatsModuleGlobalParams mpg) throws InitException {

        try {
            delegate.addModule(
                    "Selected Mark Properties",
                    progresssReporter -> adder,
                    new FeatureEvaluatorCreator(src, mpg.getLogger()),
                    mpg.getThreadPool(),
                    mpg.getLogger());

        } catch (MenuAddException e) {
            throw new InitException(e);
        }
    }

    public JButton getButton() {
        return delegate.getButton();
    }
}

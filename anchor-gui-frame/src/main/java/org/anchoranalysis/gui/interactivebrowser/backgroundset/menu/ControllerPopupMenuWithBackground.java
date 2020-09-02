/*-
 * #%L
 * anchor-gui-frame
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

package org.anchoranalysis.gui.interactivebrowser.backgroundset.menu;

import javax.swing.JMenu;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.gui.frame.details.ControllerPopupMenu;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.definition.ChangeableBackground;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.definition.ChangeableBackgroundDefinitionSimple;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.definition.ImageStackContainerFromName;
import org.anchoranalysis.gui.videostats.dropdown.BackgroundSetProgressingSupplier;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;

@AllArgsConstructor
public class ControllerPopupMenuWithBackground {

    private ControllerPopupMenu delegate;
    private BackgroundSetter backgroundSetter;

    public void add(
            IGetNames nameGetter,
            ImageStackContainerFromName stackCntrFromName,
            VideoStatsModuleGlobalParams mpg) {
        BackgroundSetMenu menu =
                new BackgroundSetMenu(
                        nameGetter, stackCntrFromName, backgroundSetter, errorReporter(mpg));

        addAdditionalMenu(menu.getMenu());
    }

    public BackgroundUpdater add(
            VideoStatsModuleGlobalParams mpg, BackgroundSetProgressingSupplier backgroundSet) {
        return addDefinition(mpg, new ChangeableBackgroundDefinitionSimple(backgroundSet));
    }

    public BackgroundUpdater addDefinition(
            VideoStatsModuleGlobalParams mpg, ChangeableBackground backgroundDefinition) {
        BackgroundSetMenuWithMap menu =
                new BackgroundSetMenuWithMap(
                        backgroundDefinition, backgroundSetter, errorReporter(mpg));

        addAdditionalMenu(menu.getMenu());

        return menu;
    }

    public void addAdditionalMenu(JMenu menu) {
        delegate.addAdditionalMenu(menu);
    }

    public void setRetrieveElementsInPopupEnabled(boolean retrieveElementsInPopupEnabled) {
        delegate.setRetrieveElementsInPopupEnabled(retrieveElementsInPopupEnabled);
    }

    private ErrorReporter errorReporter(VideoStatsModuleGlobalParams mpg) {
        return mpg.getLogger().errorReporter();
    }
}

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

import lombok.AllArgsConstructor;
import org.anchoranalysis.core.exception.InitException;
import org.anchoranalysis.gui.bean.filecreator.FileCreatorParams;
import org.anchoranalysis.gui.bean.filecreator.MarkCreatorParams;
import org.anchoranalysis.gui.feature.evaluator.treetable.FeatureListSrc;
import org.anchoranalysis.gui.interactivebrowser.FileOpenManager;
import org.anchoranalysis.gui.interactivebrowser.MarkEvaluatorManager;
import org.anchoranalysis.gui.interactivebrowser.SubgrouppedAdder;
import org.anchoranalysis.gui.interactivebrowser.openfile.FileCreatorLoader;
import org.anchoranalysis.gui.interactivebrowser.openfile.importer.ImporterSettings;
import org.anchoranalysis.gui.marks.MarkDisplaySettings;
import org.anchoranalysis.gui.toolbar.VideoStatsToolbar;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.image.io.bean.stack.reader.StackReader;

@AllArgsConstructor
class AdderWithEnergy {
    private VideoStatsModuleGlobalParams moduleParams;
    private FeatureListSrc featureListSrc;
    private SubgrouppedAdder globalSubgroupAdder;

    public void addGlobalSet(VideoStatsToolbar toolbar) throws InitException {
        GlobalDropDown globalDropDown = new GlobalDropDown(globalSubgroupAdder);
        globalDropDown.init(featureListSrc, moduleParams);
        toolbar.add(globalDropDown.getButton());
    }

    public FileCreatorLoader createFileCreatorLoader(
            StackReader stackReader,
            FileOpenManager fileOpenManager,
            MarkEvaluatorManager markEvaluatorManager,
            ImporterSettings importerSettings,
            MarkDisplaySettings markDisplaySettings) {

        FileCreatorParams params = new FileCreatorParams();
        params.setMarkCreatorParams(
                new MarkCreatorParams(moduleParams, markDisplaySettings, markEvaluatorManager));
        params.setStackReader(stackReader);
        params.setImporterSettings(importerSettings);

        return new FileCreatorLoader(params, fileOpenManager, globalSubgroupAdder);
    }
}

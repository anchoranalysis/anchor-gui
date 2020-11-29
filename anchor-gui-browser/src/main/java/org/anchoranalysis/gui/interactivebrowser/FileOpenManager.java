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

package org.anchoranalysis.gui.interactivebrowser;

import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.gui.file.interactive.InteractiveFile;
import org.anchoranalysis.gui.file.opened.OpenedFile;
import org.anchoranalysis.gui.videostats.dropdown.AddVideoStatsModule;
import org.anchoranalysis.gui.videostats.frame.IGetToolbar;
import org.anchoranalysis.gui.videostats.module.VideoStatsModule;
import org.anchoranalysis.io.output.outputter.InputOutputContext;

@RequiredArgsConstructor
public class FileOpenManager implements IOpenFile {

    // START REQUIRED ARGUMENTS

    // The Parameters we need to perform the opening
    private final AddVideoStatsModule globalSubgroupAdder;
    private final IGetToolbar videoStatsFrame;
    private final InputOutputContext inputOutputContext;
    // END REQUIRED ARGUMENTS

    // A map to keep track from currently opened-modules to the appropriate file counter
    private VideoStatsModuleMapToOpenedFileCounter mapModules =
            new VideoStatsModuleMapToOpenedFileCounter();

    // A hash-map we use to keep track of all currently opened-files to the file counter
    private VideoStatsFileMapToOpenedFileCounter mapFile =
            new VideoStatsFileMapToOpenedFileCounter();

    @Override
    public OpenedFile open(InteractiveFile file) throws OperationFailedException {

        OpenedFileCounter openedFileCounter = mapFile.get(file);

        if (openedFileCounter == null) {

            OpenedFileCounter counter =
                    new OpenedFileCounter(mapModules, mapFile, videoStatsFrame.getToolbar());

            AdderGUICountToolbar addToToolbarAdder =
                    new AdderGUICountToolbar(globalSubgroupAdder, counter);
            OpenedFile of = file.open(addToToolbarAdder, inputOutputContext);
            counter.setOpenedFile(of);

            // We don't actually add this to our mapFile, until the first module is added, as
            // sometimes a user right-clicks
            //   and never actually creates a module, so it would be silly to add it to the cache

            return counter.getOpenedFile();
        }

        return openedFileCounter.getOpenedFile();
    }

    public void closeModule(VideoStatsModule module) {
        OpenedFileCounter counter = mapModules.get(module);

        if (counter != null) {
            if (counter.removeVideoStatsModule(module)) {
                // If it returns true, it means we have removed the last module associated with an
                // open file, and therefore we can remove it from our cache
                mapFile.remove(counter.getOpenedFile().getFile());
                mapModules.remove(module);
            }
        } else {
            // NO FILE ASSOCIATED WITH MODULE, we're not sure if this is called in error for now,
            // let's write a message
            System.out.println("No file associated with module");
        }
    }
}

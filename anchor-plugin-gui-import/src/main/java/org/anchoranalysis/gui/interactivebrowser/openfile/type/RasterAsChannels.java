/*-
 * #%L
 * anchor-plugin-gui-import
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

package org.anchoranalysis.gui.interactivebrowser.openfile.type;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import org.anchoranalysis.bean.xml.RegisterBeanFactories;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.gui.bean.filecreator.FileCreator;
import org.anchoranalysis.gui.bean.filecreator.NamedSingleStackCreator;
import org.anchoranalysis.gui.interactivebrowser.openfile.importer.ImporterSettings;
import org.anchoranalysis.image.io.bean.stack.StackReader;
import org.anchoranalysis.io.bean.files.provider.SpecificPathList;
import org.anchoranalysis.io.bean.input.InputManager;
import org.anchoranalysis.io.input.FileInput;
import org.anchoranalysis.plugin.io.bean.channel.map.Autoname;
import org.anchoranalysis.plugin.io.bean.input.channel.NamedChannels;
import org.anchoranalysis.plugin.io.bean.input.file.Files;

public class RasterAsChannels extends OpenFileType {

    @Override
    public String[] getExtensions() {
        return new String[] {"lsm", "zvi", "czi", "ics"};
    }

    @Override
    public String getDescription() {
        return "Raster as Channel Collection";
    }

    @Override
    public List<FileCreator> creatorForFile(List<File> files, ImporterSettings importerSettings)
            throws CreateException {

        SpecificPathList fileList = createFileList(files);

        NamedChannels inputManager = createNamedChannels(new Files(fileList));

        return Arrays.asList(creator(inputManager, files));
    }

    private NamedSingleStackCreator creator(NamedChannels inputManager, List<File> files) {
        NamedSingleStackCreator creator = new NamedSingleStackCreator();
        creator.setCustomName(String.format("raster-set: %s", createName(files)));
        creator.setInput(inputManager);
        return creator;
    }

    private static NamedChannels createNamedChannels(InputManager<FileInput> fileInputManager) {

        StackReader reader = RegisterBeanFactories.getDefaultInstances().get(StackReader.class);

        NamedChannels inputManager = new NamedChannels();
        inputManager.setStackReader(reader);
        inputManager.setFileInput(fileInputManager);
        inputManager.setUseLastSeriesIndexOnly(true);
        inputManager.setChannelMap(new Autoname());
        return inputManager;
    }
}

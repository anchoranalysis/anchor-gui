/* (C)2020 */
package org.anchoranalysis.gui.interactivebrowser.openfile.type;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.anchoranalysis.bean.xml.RegisterBeanFactories;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.gui.bean.filecreator.FileCreator;
import org.anchoranalysis.gui.bean.filecreator.StackCollectionCreator;
import org.anchoranalysis.gui.interactivebrowser.openfile.importer.ImporterSettings;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.io.bean.provider.file.SpecificPathList;
import org.anchoranalysis.plugin.io.bean.input.file.Files;
import org.anchoranalysis.plugin.io.bean.input.stack.Stacks;

public class RasterAsStack extends OpenFileType {

    @Override
    public String[] getExtensions() {
        return new String[] {"tif", "tiff", "jpeg", "jpg", "png", "gif", "bmp"};
    }

    @Override
    public String getDescription() {
        return "Raster as Stack";
    }

    @Override
    public List<FileCreator> creatorForFile(List<File> files, ImporterSettings importerSettings)
            throws CreateException {

        SpecificPathList fileList = createFileList(files);

        Files fileInputManager = new Files();
        fileInputManager.setFileProvider(fileList);

        // Until we eliminate RasterReaderETHMetadata class we use a cast
        RasterReader reader = RegisterBeanFactories.getDefaultInstances().get(RasterReader.class);

        Stacks inputManager = new Stacks();
        inputManager.setRasterReader(reader);
        inputManager.setFileInput(fileInputManager);
        inputManager.setUseLastSeriesIndexOnly(true);

        StackCollectionCreator creator = new StackCollectionCreator();
        creator.setCustomName(String.format("raster-set: %s", createName(files)));
        creator.setInput(inputManager);

        List<FileCreator> out = new ArrayList<>();
        out.add(creator);
        return out;
    }
}

/* (C)2020 */
package org.anchoranalysis.gui.interactivebrowser.openfile.type;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.anchoranalysis.bean.xml.RegisterBeanFactories;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.gui.bean.filecreator.FileCreator;
import org.anchoranalysis.gui.bean.filecreator.NamedSingleStackCreator;
import org.anchoranalysis.gui.interactivebrowser.openfile.importer.ImporterSettings;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.io.bean.provider.file.SpecificPathList;
import org.anchoranalysis.plugin.io.bean.chnl.map.ImgChnlMapAutoname;
import org.anchoranalysis.plugin.io.bean.input.chnl.NamedChnls;
import org.anchoranalysis.plugin.io.bean.input.file.Files;

public class RasterAsChnlCollection extends OpenFileType {

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

        Files fileInputManager = new Files();
        fileInputManager.setFileProvider(fileList);

        RasterReader reader = RegisterBeanFactories.getDefaultInstances().get(RasterReader.class);

        NamedChnls inputManager = new NamedChnls();
        inputManager.setRasterReader(reader);
        inputManager.setFileInput(fileInputManager);
        inputManager.setUseLastSeriesIndexOnly(true);
        inputManager.setImgChnlMapCreator(new ImgChnlMapAutoname());

        NamedSingleStackCreator creator = new NamedSingleStackCreator();
        creator.setCustomName(String.format("raster-set: %s", createName(files)));
        creator.setInput(inputManager);

        List<FileCreator> out = new ArrayList<>();
        out.add(creator);
        return out;
    }
}

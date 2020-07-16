/* (C)2020 */
package org.anchoranalysis.gui.bean.filecreator;

import org.anchoranalysis.annotation.io.bean.input.AnnotationInputManager;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.gui.annotation.AnnotationListInternalFrame;
import org.anchoranalysis.gui.interactivebrowser.IOpenFile;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.module.DefaultModuleState;
import org.anchoranalysis.gui.videostats.module.VideoStatsModule;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleCreateException;
import org.anchoranalysis.image.io.input.ProvidesStackInput;

// A named channel collection derived from a file
public class AnnotationCreator extends FileCreator {

    private int weightWidthDescription;

    public AnnotationCreator(int weightWidthDescription) {
        super();
        this.weightWidthDescription = weightWidthDescription;
    }

    // START BEAN PROPERTIES
    @BeanField private AnnotationInputManager<ProvidesStackInput, ?> input;
    // END BEAN PROPERTIES

    // private static Log log = LogFactory.getLog(NamedChnlCollectionCreator.class);

    @Override
    public VideoStatsModule createModule(
            String name,
            FileCreatorParams params,
            VideoStatsModuleGlobalParams mpg,
            IAddVideoStatsModule adder,
            IOpenFile fileOpenManager,
            ProgressReporter progressReporter)
            throws VideoStatsModuleCreateException {

        AnnotationListInternalFrame listFrame = new AnnotationListInternalFrame(name);

        try {
            listFrame.init(
                    input,
                    adder,
                    fileOpenManager,
                    params.getMarkCreatorParams(),
                    progressReporter,
                    weightWidthDescription);
        } catch (InitException e) {
            throw new VideoStatsModuleCreateException(e);
        }

        return listFrame.moduleCreator().createVideoStatsModule(new DefaultModuleState());
    }

    @Override
    public String suggestName() {

        if (hasCustomName()) {
            return getCustomName();
        }

        return "untitled raster set";
    }

    public AnnotationInputManager<ProvidesStackInput, ?> getInput() {
        return input;
    }

    public void setInput(AnnotationInputManager<ProvidesStackInput, ?> input) {
        this.input = input;
    }
}

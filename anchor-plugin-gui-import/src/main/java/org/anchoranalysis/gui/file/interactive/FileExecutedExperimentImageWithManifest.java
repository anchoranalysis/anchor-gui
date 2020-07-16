/* (C)2020 */
package org.anchoranalysis.gui.file.interactive;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.gui.bean.filecreator.MarkCreatorParams;
import org.anchoranalysis.gui.file.opened.OpenedFile;
import org.anchoranalysis.gui.file.opened.OpenedFileGUI;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.manifest.ManifestDropDown;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;
import org.anchoranalysis.plugin.io.manifest.CoupledManifests;

// A file representing the results applied to an image within an executed experiment
public class FileExecutedExperimentImageWithManifest extends InteractiveFile {

    private ManifestDropDown manifestDropDown;

    private CoupledManifests coupledManifests;
    private RasterReader rasterReader;
    private MarkCreatorParams markCreatorParams;

    public FileExecutedExperimentImageWithManifest(
            CoupledManifests coupledManifests,
            RasterReader rasterReader,
            MarkCreatorParams markCreatorParams) {
        this.coupledManifests = coupledManifests;
        this.rasterReader = rasterReader;
        this.markCreatorParams = markCreatorParams;
    }

    @Override
    public OpenedFile open(
            final IAddVideoStatsModule adder, final BoundOutputManagerRouteErrors outputManager)
            throws OperationFailedException {

        manifestDropDown =
                new ManifestDropDown(coupledManifests, markCreatorParams.getMarkDisplaySettings());

        try {
            manifestDropDown.init(
                    adder,
                    rasterReader,
                    markCreatorParams.getMarkEvaluatorManager(),
                    outputManager,
                    markCreatorParams.getModuleParams());
        } catch (InitException e) {
            throw new OperationFailedException(e);
        }

        return new OpenedFileGUI(this, manifestDropDown.openedFileGUI());
    }

    @Override
    public String identifier() {
        return coupledManifests.descriptiveName();
    }

    @Override
    public Optional<File> associatedFile() {
        return coupledManifests.pathForBinding().map(Path::toFile);
    }

    @Override
    public String type() {
        return "experiment results for image";
    }
}

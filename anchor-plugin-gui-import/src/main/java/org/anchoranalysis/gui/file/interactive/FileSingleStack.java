/* (C)2020 */
package org.anchoranalysis.gui.file.interactive;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.name.store.EagerEvaluationStore;
import org.anchoranalysis.gui.bean.filecreator.MarkCreatorParams;
import org.anchoranalysis.gui.file.opened.OpenedFile;
import org.anchoranalysis.gui.file.opened.OpenedFileGUI;
import org.anchoranalysis.gui.series.OperationCreateTimeSequence;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.multicollection.MultiCollectionDropDown;
import org.anchoranalysis.image.io.input.ProvidesStackInput;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;

public class FileSingleStack extends InteractiveFile {

    private ProvidesStackInput inputObject;
    private MarkCreatorParams params;

    public FileSingleStack(ProvidesStackInput ncc, MarkCreatorParams params) {
        super();
        this.inputObject = ncc;
        this.params = params;
    }

    @Override
    public String identifier() {
        return inputObject.descriptiveName();
    }

    @Override
    public Optional<File> associatedFile() {
        return inputObject.pathForBinding().map(Path::toFile);
    }

    @Override
    public String type() {
        return "raster";
    }

    @Override
    public OpenedFile open(
            IAddVideoStatsModule globalSubgroupAdder, BoundOutputManagerRouteErrors outputManager)
            throws OperationFailedException {

        MultiCollectionDropDown dropDown =
                new MultiCollectionDropDown(
                        new OperationCreateTimeSequence(inputObject, 0),
                        null,
                        null,
                        new EagerEvaluationStore<>(),
                        identifier(),
                        true);

        try {
            dropDown.init(globalSubgroupAdder, outputManager, params);
        } catch (InitException e) {
            throw new OperationFailedException(e);
        }

        return new OpenedFileGUI(this, dropDown.openedFileGUI());
    }
}

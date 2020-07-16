/* (C)2020 */
package org.anchoranalysis.gui.file.interactive;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.name.store.EagerEvaluationStore;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.gui.bean.filecreator.MarkCreatorParams;
import org.anchoranalysis.gui.file.opened.OpenedFile;
import org.anchoranalysis.gui.file.opened.OpenedFileGUI;
import org.anchoranalysis.gui.videostats.dropdown.ExtractTimeSequenceFromInput;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.multicollection.MultiCollectionDropDown;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;
import org.anchoranalysis.plugin.io.bean.input.stack.StackSequenceInput;
import org.apache.commons.io.FilenameUtils;

public class FileStackCollection extends InteractiveFile {

    private StackSequenceInput inputObject;
    private MarkCreatorParams params;

    public FileStackCollection(StackSequenceInput inputObject, MarkCreatorParams params) {
        super();
        this.inputObject = inputObject;
        this.params = params;
    }

    @Override
    public String identifier() {
        return FilenameUtils.removeExtension(new File(inputObject.descriptiveName()).getName());
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
                        new ExtractTimeSequenceFromInput(inputObject),
                        null,
                        null,
                        new EagerEvaluationStore<KeyValueParams>(),
                        identifier(),
                        false);

        try {
            dropDown.init(globalSubgroupAdder, outputManager, params);
        } catch (InitException e) {
            throw new OperationFailedException(e);
        }

        return new OpenedFileGUI(this, dropDown.openedFileGUI());
    }
}

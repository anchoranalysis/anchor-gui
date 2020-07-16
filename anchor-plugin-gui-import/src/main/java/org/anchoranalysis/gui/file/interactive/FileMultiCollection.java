/* (C)2020 */
package org.anchoranalysis.gui.file.interactive;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.name.store.LazyEvaluationStore;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.gui.bean.filecreator.MarkCreatorParams;
import org.anchoranalysis.gui.file.opened.OpenedFile;
import org.anchoranalysis.gui.file.opened.OpenedFileGUI;
import org.anchoranalysis.gui.series.TimeSequenceProvider;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.multicollection.MultiCollectionDropDown;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.stack.TimeSequence;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;
import org.anchoranalysis.mpp.io.input.MultiInput;

public class FileMultiCollection extends InteractiveFile {

    private MultiInput inputObject;
    private MarkCreatorParams markCreatorParams;

    public FileMultiCollection(MultiInput inputObject, MarkCreatorParams markCreatorParams) {
        super();
        this.inputObject = inputObject;
        this.markCreatorParams = markCreatorParams;
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

        LazyEvaluationStore<TimeSequence> stacks =
                new LazyEvaluationStore<>(
                        markCreatorParams.getModuleParams().getLogger(), "stacks");
        inputObject.stack().addToStore(stacks);

        LazyEvaluationStore<Cfg> cfgs =
                new LazyEvaluationStore<>(markCreatorParams.getModuleParams().getLogger(), "cfg");
        inputObject.cfg().addToStore(cfgs);

        Logger logger = markCreatorParams.getModuleParams().getLogger();
        LazyEvaluationStore<KeyValueParams> keyValueParams =
                new LazyEvaluationStore<>(logger, "keyValueParams");
        inputObject.keyValueParams().addToStore(keyValueParams);

        LazyEvaluationStore<ObjectCollection> objects =
                new LazyEvaluationStore<>(logger, "object-collections");
        inputObject.objects().addToStore(objects);

        MultiCollectionDropDown dropDown =
                new MultiCollectionDropDown(
                        progressReporter -> createTimeSequenceProvider(stacks),
                        cfgs,
                        objects,
                        keyValueParams,
                        identifier(),
                        true);

        try {
            dropDown.init(globalSubgroupAdder, outputManager, markCreatorParams);
        } catch (InitException e) {
            throw new OperationFailedException(e);
        }

        return new OpenedFileGUI(this, dropDown.openedFileGUI());
    }

    private TimeSequenceProvider createTimeSequenceProvider(
            LazyEvaluationStore<TimeSequence> stacks) throws CreateException {
        try {
            return new TimeSequenceProvider(stacks, inputObject.numFrames());
        } catch (OperationFailedException e) {
            throw new CreateException(e);
        }
    }
}

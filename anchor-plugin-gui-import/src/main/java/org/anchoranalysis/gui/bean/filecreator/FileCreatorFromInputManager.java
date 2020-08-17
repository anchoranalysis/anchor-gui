package org.anchoranalysis.gui.bean.filecreator;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.gui.file.interactive.InteractiveFile;
import org.anchoranalysis.io.bean.input.InputManager;
import org.anchoranalysis.io.bean.input.InputManagerParams;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.io.input.InputFromManager;

public abstract class FileCreatorFromInputManager<T extends InputFromManager>
        extends FileCreatorGeneralList {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private InputManager<? extends T> input;
    // END BEAN PROPERTIES

    @Override
    protected void addFilesToList(
            List<InteractiveFile> listFiles,
            FileCreatorParams params,
            ProgressReporter progressReporter)
            throws OperationFailedException {

        try {

            List<? extends T> list =
                    input.inputObjects(
                            new InputManagerParams(
                                    params.createInputContext(),
                                    progressReporter,
                                    params.getLogErrorReporter()));

            for (T element : list) {
                listFiles.add(create(element, params.getMarkCreatorParams()));
            }

        } catch (AnchorIOException e) {
            throw new OperationFailedException(e);
        }
    }

    protected abstract InteractiveFile create(
            T providesStackInput, MarkCreatorParams markCreatorParams);

    @Override
    public String suggestName() {

        if (hasCustomName()) {
            return getCustomName();
        }

        return "untitled raster set";
    }
}

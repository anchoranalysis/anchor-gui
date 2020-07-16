/* (C)2020 */
package org.anchoranalysis.gui.bean.filecreator;

import java.util.Iterator;
import java.util.List;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.gui.file.interactive.FileStackCollection;
import org.anchoranalysis.gui.file.interactive.InteractiveFile;
import org.anchoranalysis.io.bean.input.InputManager;
import org.anchoranalysis.io.bean.input.InputManagerParams;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.plugin.io.bean.input.stack.StackSequenceInput;

// A named channel collection derived from a file
public class StackCollectionCreator extends FileCreatorGeneralList {

    // START BEAN PROPERTIES
    @BeanField private InputManager<StackSequenceInput> input;
    // END BEAN PROPERTIES

    @Override
    public void addFilesToList(
            List<InteractiveFile> listFiles,
            FileCreatorParams params,
            ProgressReporter progressReporter)
            throws OperationFailedException {

        try {
            Iterator<StackSequenceInput> itr =
                    input.inputObjects(
                                    new InputManagerParams(
                                            params.createInputContext(),
                                            progressReporter,
                                            params.getLogErrorReporter()))
                            .iterator();

            while (itr.hasNext()) {

                StackSequenceInput obj = itr.next();

                FileStackCollection file =
                        new FileStackCollection(obj, params.getMarkCreatorParams());
                listFiles.add(file);
            }

        } catch (AnchorIOException e) {
            throw new OperationFailedException(e);
        }
    }

    @Override
    public String suggestName() {

        if (hasCustomName()) {
            return getCustomName();
        }

        return "untitled raster set";
    }

    public InputManager<StackSequenceInput> getInput() {
        return input;
    }

    public void setInput(InputManager<StackSequenceInput> input) {
        this.input = input;
    }
}

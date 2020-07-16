/* (C)2020 */
package org.anchoranalysis.gui.bean.filecreator;

import java.util.Iterator;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.gui.file.interactive.FileMultiCollection;
import org.anchoranalysis.gui.file.interactive.InteractiveFile;
import org.anchoranalysis.io.bean.input.InputManager;
import org.anchoranalysis.io.bean.input.InputManagerParams;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.mpp.io.input.MultiInput;

public class NamedMultiCollectionCreator extends FileCreatorGeneralList {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private InputManager<MultiInput> input;
    // END BEAN PROPERTIES

    @Override
    public void addFilesToList(
            List<InteractiveFile> listFiles,
            FileCreatorParams params,
            ProgressReporter progressReporter)
            throws OperationFailedException {

        try {
            Iterator<MultiInput> itr =
                    input.inputObjects(
                                    new InputManagerParams(
                                            params.createInputContext(),
                                            progressReporter,
                                            params.getLogErrorReporter()))
                            .iterator();

            while (itr.hasNext()) {

                MultiInput obj = itr.next();

                FileMultiCollection file =
                        new FileMultiCollection(obj, params.getMarkCreatorParams());
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

        return "untitled stack/cfg collection";
    }
}

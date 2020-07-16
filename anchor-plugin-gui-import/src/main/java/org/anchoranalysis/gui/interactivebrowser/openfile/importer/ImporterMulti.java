/* (C)2020 */
package org.anchoranalysis.gui.interactivebrowser.openfile.importer;

import java.io.File;
import java.util.Optional;
import org.anchoranalysis.gui.bean.filecreator.FileCreator;
import org.anchoranalysis.gui.bean.filecreator.NamedMultiCollectionCreator;
import org.anchoranalysis.io.bean.input.InputManager;
import org.anchoranalysis.mpp.io.bean.input.MultiInputManagerBase;
import org.anchoranalysis.mpp.io.input.MultiInput;

public class ImporterMulti extends ImporterFromBean {

    @Override
    public boolean isApplicable(Object bean) {
        return bean instanceof MultiInputManagerBase;
    }

    @Override
    public Optional<FileCreator> create(Object bean, File file) {
        return Optional.of(createMultiCollection((MultiInputManagerBase) bean, file));
    }

    private static FileCreator createMultiCollection(
            InputManager<MultiInput> inputManager, File f) {
        return CreatorFactory.create(
                new NamedMultiCollectionCreator(),
                inputManager,
                (creator, input) -> creator.setInput(inputManager),
                f,
                "stack-cfg-set");
    }
}

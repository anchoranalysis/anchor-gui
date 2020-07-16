/* (C)2020 */
package org.anchoranalysis.gui.interactivebrowser.openfile.importer;

import java.io.File;
import java.util.Optional;
import org.anchoranalysis.annotation.io.bean.input.AnnotationInputManager;
import org.anchoranalysis.annotation.io.bean.strategy.AnnotatorStrategy;
import org.anchoranalysis.gui.bean.filecreator.AnnotationCreator;
import org.anchoranalysis.gui.bean.filecreator.FileCreator;
import org.anchoranalysis.image.io.input.ProvidesStackInput;

public class ImporterFromAnnotation extends ImporterFromBean {

    @Override
    public boolean isApplicable(Object bean) {
        return bean instanceof AnnotationInputManager;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Optional<FileCreator> create(Object bean, File file) {
        return Optional.of(
                createAnnotation(
                        (AnnotationInputManager<ProvidesStackInput, AnnotatorStrategy>) bean,
                        file));
    }

    // For now we assume we are always dealing with NamedChnlCollectionInputObject
    private static FileCreator createAnnotation(
            AnnotationInputManager<ProvidesStackInput, ?> inputManager, File f) {
        return CreatorFactory.create(
                new AnnotationCreator(inputManager.getAnnotatorStrategy().weightWidthDescription()),
                inputManager,
                (creator, input) -> creator.setInput(inputManager),
                f,
                "annotation");
    }
}

/*-
 * #%L
 * anchor-plugin-gui-annotation
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

package org.anchoranalysis.gui.interactivebrowser.openfile.importer;

import java.io.File;
import java.util.Optional;
import org.anchoranalysis.annotation.io.bean.AnnotationInputManager;
import org.anchoranalysis.annotation.io.bean.AnnotatorStrategy;
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

    // For now we assume we are always dealing with NamedChannelCollectionInputObject
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

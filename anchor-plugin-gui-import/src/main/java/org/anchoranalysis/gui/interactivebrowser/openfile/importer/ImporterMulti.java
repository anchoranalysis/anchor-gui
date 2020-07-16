/*-
 * #%L
 * anchor-plugin-gui-import
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

/*-
 * #%L
 * anchor-gui-frame
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

package org.anchoranalysis.gui.retrieveelements;

import java.util.Optional;
import java.util.function.Supplier;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.outputter.InputOutputContext;

public interface AddToExportSubMenu {

    void addExportItemStackGenerator(String outputName, String label, Supplier<Stack> stack)
            throws OperationFailedException;

    <T> void addExportItem(
            Generator<T> generator,
            T itemToGenerate,
            String outputName,
            String label,
            Optional<ManifestDescription> md,
            int numItems);

    InputOutputContext getInputOutputContext();
}

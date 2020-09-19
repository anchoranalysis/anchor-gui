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

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.io.generator.IterableSingleFileTypeGenerator;
import org.anchoranalysis.io.generator.SingleFileTypeGenerator;
import org.anchoranalysis.io.generator.TwoStageGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

@RequiredArgsConstructor
class OperationGenerator<S, T> extends TwoStageGenerator<Supplier<T>,S> {

    // START REQUIRED ARGUMENTS
    private final IterableSingleFileTypeGenerator<T, S> delegate;
    // END REQUIRED ARGUMENTS

    private Supplier<T> element;

    @Override
    public void setIterableElement(Supplier<T> element) throws SetOperationFailedException {
        this.element = element;
        delegate.setIterableElement(element.get());
    }

    @Override
    public Supplier<T> getIterableElement() {
        return element;
    }

    @Override
    public void start() throws OutputWriteFailedException {
        delegate.start();
    }

    @Override
    public void end() throws OutputWriteFailedException {
        delegate.end();
    }

    @Override
    public SingleFileTypeGenerator<Supplier<T>,S> getGenerator() {
        return this;
    }

    @Override
    public S transform() throws OutputWriteFailedException {
        return delegate.getGenerator().transform();
    }

    @Override
    public void writeToFile(OutputWriteSettings outputWriteSettings, Path filePath)
            throws OutputWriteFailedException {
        delegate.getGenerator().writeToFile(outputWriteSettings, filePath);
    }

    @Override
    public String getFileExtension(OutputWriteSettings outputWriteSettings) throws OperationFailedException {
        return delegate.getGenerator().getFileExtension(outputWriteSettings);
    }

    @Override
    public Optional<ManifestDescription> createManifestDescription() {
        return delegate.getGenerator().createManifestDescription();
    }
}

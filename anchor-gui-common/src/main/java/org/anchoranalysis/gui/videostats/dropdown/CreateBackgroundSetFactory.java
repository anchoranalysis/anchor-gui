/*-
 * #%L
 * anchor-gui-common
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

package org.anchoranalysis.gui.videostats.dropdown;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.identifier.provider.NamedProvider;
import org.anchoranalysis.core.progress.ProgressIgnore;
import org.anchoranalysis.gui.backgroundset.BackgroundSetFactory;
import org.anchoranalysis.gui.container.background.BackgroundStackContainerException;
import org.anchoranalysis.gui.series.TimeSequenceProvider;
import org.anchoranalysis.gui.series.TimeSequenceProviderSupplier;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.core.stack.TimeSequence;
import org.anchoranalysis.image.core.stack.time.WrapStackAsTimeSequence;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateBackgroundSetFactory {

    public static BackgroundSetProgressingSupplier createCached(
            NamedProvider<Stack> namedProvider) {
        return createCached(
                progress ->
                        new TimeSequenceProvider(new WrapStackAsTimeSequence(namedProvider), 1));
    }

    public static BackgroundSetProgressingSupplier createCached(
            TimeSequenceProviderSupplier stacksOverTime) {
        return BackgroundSetProgressingSupplier.cache(
                progress -> {
                    try {
                        NamedProvider<TimeSequence> stacks =
                                stacksOverTime.get(progress).getSequence();

                        return BackgroundSetFactory.createBackgroundSet(
                                stacks, ProgressIgnore.get());
                    } catch (Exception e) {
                        throw new BackgroundStackContainerException(e);
                    }
                });
    }
}

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

package org.anchoranalysis.gui.videostats.dropdown.contextualmodulecreator;

import java.util.Optional;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.gui.videostats.dropdown.AddVideoStatsModuleSupplier;
import org.anchoranalysis.gui.videostats.dropdown.NamedModule;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleCreatorAndAdder;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.modulecreator.VideoStatsModuleCreatorContext;

@AllArgsConstructor
public class SingleContextualModuleCreator extends ContextualModuleCreator {

    private VideoStatsModuleCreatorContext moduleCreator;

    @Override
    public NamedModule[] create(
            String namePrefix,
            AddVideoStatsModuleSupplier adder,
            VideoStatsModuleGlobalParams mpg)
            throws CreateException {

        NamedModule namedModuleSingle = createSingle(namePrefix, adder, mpg);

        if (namedModuleSingle == null) {
            return new NamedModule[] {};
        }

        return new NamedModule[] {namedModuleSingle};
    }

    public NamedModule createSingle(
            String namePrefix,
            AddVideoStatsModuleSupplier adder,
            VideoStatsModuleGlobalParams mpg) {

        if (!moduleCreator.precondition()) {
            return null;
        }

        VideoStatsModuleCreatorAndAdder creatorAndAdder =
                new VideoStatsModuleCreatorAndAdder(adder, moduleCreator.resolve(namePrefix, mpg));

        Optional<String> shortTitle = moduleCreator.shortTitle();
        if (shortTitle.isPresent()) {
            return new NamedModule(moduleCreator.title(), creatorAndAdder, shortTitle.get());
        } else {
            return new NamedModule(moduleCreator.title(), creatorAndAdder);
        }
    }
}

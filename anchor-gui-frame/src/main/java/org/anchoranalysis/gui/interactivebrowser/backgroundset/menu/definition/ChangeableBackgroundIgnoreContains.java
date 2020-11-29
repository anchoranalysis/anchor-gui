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

package org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.definition;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.functional.FunctionalList;
import org.anchoranalysis.core.log.error.ErrorReporter;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.IGetNames;
import org.anchoranalysis.gui.videostats.dropdown.BackgroundSetProgressingSupplier;

@RequiredArgsConstructor
public class ChangeableBackgroundIgnoreContains implements ChangeableBackground {

    // START REQUIRED ARGUMENTS
    private final ChangeableBackground background;
    private final String contains;
    // END REQUIRED ARGUMENTS

    @Override
    public void update(BackgroundSetProgressingSupplier backgroundSet) {
        background.update(backgroundSet);
    }

    @Override
    public ImageStackContainerFromName stackCntrFromName(ErrorReporter errorReporter) {
        return background.stackCntrFromName(errorReporter);
    }

    @Override
    public IGetNames names(ErrorReporter errorReporter) {
        IGetNames namesGet = background.names(errorReporter);
        return () -> filterList(namesGet.names());
    }

    private List<String> filterList(List<String> list) {
        return FunctionalList.filterToList(list, item -> !item.contains(contains));
    }
}

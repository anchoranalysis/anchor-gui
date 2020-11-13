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

import java.util.Map;
import org.anchoranalysis.bean.shared.StringMap;
import org.anchoranalysis.core.functional.checked.CheckedFunction;
import org.anchoranalysis.core.log.error.ErrorReporter;
import org.anchoranalysis.core.progress.ProgressIgnore;
import org.anchoranalysis.gui.container.background.BackgroundStackContainerException;
import org.anchoranalysis.gui.videostats.dropdown.BackgroundSetProgressingSupplier;
import org.anchoranalysis.image.core.stack.DisplayStack;

class StackFromBackgroundSetViaMap implements ImageStackContainerFromName {

    private Map<String, String> map;
    private BackgroundSetProgressingSupplier backgroundSet;
    private ErrorReporter errorReporter;

    public StackFromBackgroundSetViaMap(
            StringMap map,
            BackgroundSetProgressingSupplier backgroundSet,
            ErrorReporter errorReporter) {
        this.backgroundSet = backgroundSet;
        this.map = map.create();
        this.errorReporter = errorReporter;
    }

    @Override
    public CheckedFunction<Integer, DisplayStack, BackgroundStackContainerException> get(
            String name) throws BackgroundStackContainerException {
        try {
            return backgroundSet.get(ProgressIgnore.get()).stackCntr(map.get(name));
        } catch (Exception e) {
            errorReporter.recordError(NamesFromBackgroundSet.class, e);
            return null;
        }
    }
}

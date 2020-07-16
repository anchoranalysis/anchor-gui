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
/* (C)2020 */
package org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.definition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.anchoranalysis.bean.shared.StringMap;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.progress.OperationWithProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.gui.backgroundset.BackgroundSet;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.IGetNames;

class NamesFromMap implements IGetNames {

    private OperationWithProgressReporter<BackgroundSet, ? extends Throwable> backgroundSet;
    private ErrorReporter errorReporter;
    private StringMap map;

    public NamesFromMap(
            StringMap map,
            OperationWithProgressReporter<BackgroundSet, ? extends Throwable> backgroundSet,
            ErrorReporter errorReporter) {
        super();
        this.backgroundSet = backgroundSet;
        this.errorReporter = errorReporter;
        this.map = map;
    }

    @Override
    public List<String> names() {
        try {
            Set<String> backgroundNames =
                    backgroundSet.doOperation(ProgressReporterNull.get()).names();

            Map<String, String> mapping = map.create();

            return new ArrayList<>(createdSortedSet(mapping, backgroundNames));

        } catch (Throwable e) {
            errorReporter.recordError(NamesFromMap.class, e);
            return new ArrayList<>();
        }
    }

    private static Set<String> createdSortedSet(
            Map<String, String> mapping, Set<String> backgroundNames) {
        // We use tree-set to ensure alphabetical order
        TreeSet<String> namesOut = new TreeSet<>();
        for (String s : mapping.keySet()) {

            if (backgroundNames.contains(mapping.get(s))) {
                namesOut.add(s);
            }
        }
        return namesOut;
    }
}

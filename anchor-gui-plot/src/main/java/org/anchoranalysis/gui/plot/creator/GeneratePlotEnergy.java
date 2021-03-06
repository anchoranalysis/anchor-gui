/*-
 * #%L
 * anchor-gui-plot
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

package org.anchoranalysis.gui.plot.creator;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.functional.checked.CheckedFunction;
import org.anchoranalysis.feature.energy.EnergyTotal;
import org.anchoranalysis.gui.plot.EnergyGraphItem;
import org.anchoranalysis.gui.plot.panel.ClickableGraphFactory;
import org.anchoranalysis.gui.plot.panel.ClickablePlotInstance;
import org.anchoranalysis.mpp.feature.energy.EnergyPair;
import org.anchoranalysis.mpp.feature.energy.IndexableMarksWithEnergy;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.plot.AxisLimits;
import org.anchoranalysis.plot.bean.Plot;

@AllArgsConstructor
public class GeneratePlotEnergy
        implements CheckedFunction<
                IndexableMarksWithEnergy, ClickablePlotInstance, OperationFailedException> {

    private final Plot<EnergyGraphItem> definition;
    private final ColorIndex colorIndex;

    @Override
    public ClickablePlotInstance apply(IndexableMarksWithEnergy state)
            throws OperationFailedException {

        if (state.getMarks() != null) {
            List<EnergyGraphItem> list;
            try {
                list = createGraphItems(state, colorIndex);
                return ClickableGraphFactory.create(
                        definition,
                        list.iterator(),
                        Optional.empty(),
                        Optional.of(new AxisLimits(-0.5, 1)));
            } catch (CreateException e) {
                throw new OperationFailedException(e);
            }

        } else {
            return null;
        }
    }

    private static String objectIdentifier(EnergyPair pair) {
        return Integer.toString(pair.getPair().getSource().getIdentifier())
                + "--"
                + Integer.toString(pair.getPair().getDestination().getIdentifier());
    }

    public static List<EnergyGraphItem> createGraphItems(
            IndexableMarksWithEnergy state, ColorIndex colorIndex) {

        ArrayList<EnergyGraphItem> list = new ArrayList<>();

        // Each single energy item
        int i = 0;
        for (EnergyTotal energy : state.getMarks().getIndividual()) {
            Mark mark = state.getMarks().getMarks().get(i++);

            list.add(
                    new EnergyGraphItem(
                            Integer.toString(mark.getIdentifier()),
                            energy.getTotal(),
                            colorIndex.get(mark.getIdentifier()).toAWTColor()));
        }

        // Each double energy item
        for (EnergyPair pair : state.getMarks().getPair().createPairsUnique()) {

            EnergyGraphItem item =
                    new EnergyGraphItem(objectIdentifier(pair), pair.getEnergyTotal().getTotal());

            Color colorSource =
                    colorIndex.get(pair.getPair().getSource().getIdentifier()).toAWTColor();
            Color colorDestination =
                    colorIndex.get(pair.getPair().getDestination().getIdentifier()).toAWTColor();

            item.setPaint(
                    new GradientPaint(
                            new Point2D.Double(0, 0),
                            colorSource,
                            new Point2D.Double(0, 32),
                            colorDestination));
            list.add(item);
        }

        return list;
    }
}

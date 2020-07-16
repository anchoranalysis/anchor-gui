/* (C)2020 */
package org.anchoranalysis.gui.plot.creator;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Optional;
import org.anchoranalysis.anchor.mpp.feature.instantstate.CfgNRGInstantState;
import org.anchoranalysis.anchor.mpp.feature.nrg.NRGPair;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.plot.NRGGraphItem;
import org.anchoranalysis.anchor.plot.AxisLimits;
import org.anchoranalysis.anchor.plot.bean.GraphDefinition;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.function.FunctionWithException;
import org.anchoranalysis.feature.nrg.NRGTotal;
import org.anchoranalysis.gui.plot.panel.ClickableGraphFactory;
import org.anchoranalysis.gui.plot.panel.ClickableGraphInstance;

public class GenerateGraphNRGBreakdownFromInstantState
        implements FunctionWithException<
                CfgNRGInstantState, ClickableGraphInstance, OperationFailedException> {

    private final GraphDefinition<NRGGraphItem> definition;
    private final ColorIndex colorIndex;

    public GenerateGraphNRGBreakdownFromInstantState(
            GraphDefinition<NRGGraphItem> definition, ColorIndex colorIndex) {
        super();
        this.definition = definition;
        this.colorIndex = colorIndex;
    }

    @Override
    public ClickableGraphInstance apply(CfgNRGInstantState state) throws OperationFailedException {

        if (state.getCfgNRG() != null) {
            ArrayList<NRGGraphItem> list;
            try {
                list = createNRGCmp(state, colorIndex);
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

    public static ArrayList<NRGGraphItem> createNRGCmp(
            CfgNRGInstantState state, ColorIndex colorIndex) {

        ArrayList<NRGGraphItem> list = new ArrayList<>();

        // Each single nrg item
        int i = 0;
        for (NRGTotal nrg : state.getCfgNRG().getCalcMarkInd()) {
            Mark m = state.getCfgNRG().getCfg().get(i++);

            NRGGraphItem item = new NRGGraphItem();
            item.setNrg(nrg.getTotal());
            item.setObjectID(Integer.toString(m.getId()));
            item.setPaint(colorIndex.get(m.getId()).toAWTColor());

            list.add(item);
        }

        // Each double nrg item
        for (NRGPair pair : state.getCfgNRG().getCalcMarkPair().createPairsUnique()) {

            NRGGraphItem item = new NRGGraphItem();
            item.setNrg(pair.getNRG().getTotal());
            item.setObjectID(
                    Integer.toString(pair.getPair().getSource().getId())
                            + "--"
                            + Integer.toString(pair.getPair().getDestination().getId()));

            Color colorSource = colorIndex.get(pair.getPair().getSource().getId()).toAWTColor();
            Color colorDestination =
                    colorIndex.get(pair.getPair().getDestination().getId()).toAWTColor();

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

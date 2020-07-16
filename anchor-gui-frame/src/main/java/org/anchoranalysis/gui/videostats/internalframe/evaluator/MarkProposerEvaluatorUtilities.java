/* (C)2020 */
package org.anchoranalysis.gui.videostats.internalframe.evaluator;

import java.awt.Color;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.anchor.mpp.bean.proposer.MarkProposer;
import org.anchoranalysis.anchor.mpp.cfg.ColoredCfg;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.mark.MarkAbstractPosition;
import org.anchoranalysis.anchor.mpp.mark.conic.MarkConicFactory;
import org.anchoranalysis.anchor.mpp.proposer.visualization.CreateProposalVisualization;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.image.extent.ImageDimensions;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MarkProposerEvaluatorUtilities {

    public static Mark createMarkFromPosition(
            Point3d position,
            Mark templateMark,
            final ImageDimensions dimensions,
            final RandomNumberGenerator randomNumberGenerator) {

        final Mark me = templateMark.duplicate();

        if (!(me instanceof MarkAbstractPosition)) {
            throw new IllegalArgumentException("templateMark is not MarkAbstractPosition");
        }

        MarkAbstractPosition meCast = (MarkAbstractPosition) me;
        meCast.setPos(position);

        return me;
    }

    public static ColoredCfg generateCfgFromMark(
            Mark m, Point3d position, MarkProposer markProposer, boolean detailedVisualization) {

        ColoredCfg cfg = new ColoredCfg();

        if (m != null) {
            cfg.addChangeID(m, new RGBColor(Color.BLUE));
            addMaskAtMousePoint(position, cfg, m.numDims() == 3);
        }

        Optional<CreateProposalVisualization> proposalVisualization =
                markProposer.proposalVisualization(detailedVisualization);
        proposalVisualization.ifPresent(pv -> pv.addToCfg(cfg));
        return cfg;
    }

    private static void addMaskAtMousePoint(Point3d position, ColoredCfg cfg, boolean do3D) {
        Mark mousePoint = MarkConicFactory.createMarkFromPoint(position, 1, do3D);
        cfg.addChangeID(mousePoint, new RGBColor(Color.GREEN));
    }
}

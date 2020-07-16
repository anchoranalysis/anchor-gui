/* (C)2020 */
package org.anchoranalysis.gui.videostats.internalframe.evaluator;

import org.anchoranalysis.anchor.mpp.bean.cfg.CfgGen;
import org.anchoranalysis.anchor.mpp.bean.proposer.MarkProposer;
import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.proposer.ProposalAbnormalFailureException;
import org.anchoranalysis.anchor.mpp.proposer.ProposerContext;
import org.anchoranalysis.anchor.mpp.proposer.error.ErrorNode;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.PxlMarkMemoFactory;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.VoxelizedMarkMemo;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.gui.frame.overlays.ProposedCfg;
import org.anchoranalysis.gui.videostats.internalframe.ProposalOperation;
import org.anchoranalysis.image.extent.ImageDimensions;

public class MarkProposerEvaluatorDimensions implements ProposalOperationCreator {

    private MarkProposer markProposer;
    private ImageDimensions dimensions;
    private boolean detailedVisualization;

    public MarkProposerEvaluatorDimensions(
            MarkProposer markProposer, boolean detailedVisualization) {
        super();
        this.markProposer = markProposer;
        assert (markProposer.isInitialized());
        this.detailedVisualization = detailedVisualization;
    }

    @Override
    public ProposalOperation create(
            Cfg cfg, final Point3d position, final ProposerContext context, final CfgGen cfgGen)
            throws OperationFailedException {

        // We actually do the proposal

        // Do proposal
        ProposalOperation doProposal =
                new ProposalOperation() {

                    final Mark m =
                            MarkProposerEvaluatorUtilities.createMarkFromPosition(
                                    position,
                                    cfgGen.getTemplateMark().create(),
                                    context.getDimensions(),
                                    context.getRandomNumberGenerator());

                    @Override
                    public ProposedCfg propose(ErrorNode errorNode)
                            throws ProposalAbnormalFailureException {

                        VoxelizedMarkMemo pmm =
                                PxlMarkMemoFactory.create(m, null, context.getRegionMap());

                        ProposedCfg er = new ProposedCfg();
                        er.setDimensions(dimensions);

                        assert (markProposer.isInitialized());

                        // assumes only called once
                        boolean succ = markProposer.propose(pmm, context);

                        er.setSuccess(succ);

                        if (succ) {
                            er.setColoredCfg(
                                    MarkProposerEvaluatorUtilities.generateCfgFromMark(
                                            pmm.getMark(),
                                            position,
                                            markProposer,
                                            detailedVisualization));
                            er.setSuggestedSliceNum((int) m.centerPoint().getZ());
                            er.setCfgCore(new Cfg(pmm.getMark()));
                        }

                        return er;
                    }
                };
        return doProposal;
    }
}

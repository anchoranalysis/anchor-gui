/* (C)2020 */
package org.anchoranalysis.gui.videostats.internalframe.evaluator;

import org.anchoranalysis.anchor.mpp.bean.cfg.CfgGen;
import org.anchoranalysis.anchor.mpp.bean.proposer.MarkProposer;
import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.proposer.ProposalAbnormalFailureException;
import org.anchoranalysis.anchor.mpp.proposer.ProposerContext;
import org.anchoranalysis.anchor.mpp.proposer.error.ErrorNode;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.VoxelizedMarkMemo;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.gui.frame.overlays.ProposedCfg;
import org.anchoranalysis.gui.videostats.internalframe.ProposalOperation;

public class MarkProposerEvaluatorNRGStack implements ProposalOperationCreator {

    private MarkProposer markProposer;
    private boolean detailedVisualization;

    public MarkProposerEvaluatorNRGStack(MarkProposer markProposer, boolean detailedVisualization) {
        super();
        this.markProposer = markProposer;
        this.detailedVisualization = detailedVisualization;
    }

    @Override
    public ProposalOperation create(
            Cfg cfg, final Point3d position, final ProposerContext context, final CfgGen cfgGen)
            throws OperationFailedException {

        // We actually do the proposal

        // A holder
        // final PxlMarkMemo pmm = new PxlMarkMemo(m,
        // stackCollection.getImage(ImageStackIdentifiers.NRG_STACK) );

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

                        VoxelizedMarkMemo pmm = context.create(m);

                        ProposedCfg er = new ProposedCfg();
                        er.setDimensions(context.getDimensions());

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

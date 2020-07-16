/* (C)2020 */
package org.anchoranalysis.gui.plot.jfreechart.bar;

import org.anchoranalysis.anchor.mpp.plot.execution.KernelExecutionTime;
import org.anchoranalysis.core.error.InitException;

public class GraphDefinitionBarProposalType extends GraphDefinitionBarKernelExecutionTime {

    public static enum ProposalType {
        REJECTED,
        NOT_PROPOSED,
        ACCEPTED
    }

    private static String getNameForProposalType(ProposalType proposalType) {
        switch (proposalType) {
            case REJECTED:
                return "rejected";
            case NOT_PROPOSED:
                return "not proposed";
            case ACCEPTED:
                return "accepted";
            default:
                assert false;
                return "invalid";
        }
    }

    public GraphDefinitionBarProposalType(final String title, final ProposalType proposalType)
            throws InitException {

        super(
                title,
                new String[] {getNameForProposalType(proposalType)},
                (KernelExecutionTime item, int seriesNum) -> {
                    switch (proposalType) {
                        case REJECTED:
                            return (double) item.getRejectedCnt();
                        case NOT_PROPOSED:
                            return (double) item.getNotProposedCnt();
                        case ACCEPTED:
                            return (double) item.getAcceptedCnt();
                        default:
                            assert false;
                            return 0.0;
                    }
                },
                "# Iterations",
                true);
    }

    @Override
    public String getShortTitle() {
        return getTitle();
    }

    @Override
    public int totalIndex() {
        return 3;
    }
}

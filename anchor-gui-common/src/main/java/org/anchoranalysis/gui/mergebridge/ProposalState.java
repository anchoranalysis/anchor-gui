/* (C)2020 */
package org.anchoranalysis.gui.mergebridge;

public enum ProposalState {
    UNUSED,
    UNCHANGED,
    MODIFIED_ORIGINAL,
    MODIFIED_NEW,
    ADDED, // Birth
    REMOVED // Death
}

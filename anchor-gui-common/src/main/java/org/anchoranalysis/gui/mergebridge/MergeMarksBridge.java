/*-
 * #%L
 * anchor-gui-common
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

package org.anchoranalysis.gui.mergebridge;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.error.AnchorNeverOccursException;
import org.anchoranalysis.core.functional.function.CheckedFunction;
import org.anchoranalysis.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.mpp.mark.MarkCollection;
import org.anchoranalysis.mpp.overlay.OverlayCollectionMarkFactory;
import org.anchoranalysis.overlay.IndexableOverlays;
import org.anchoranalysis.overlay.collection.OverlayCollection;

@RequiredArgsConstructor
public class MergeMarksBridge
        implements CheckedFunction<
                IndexableDualState<MarkCollection>, IndexableOverlays, AnchorNeverOccursException> {

    // START REQUIRED ARGUMENTS
    private final Supplier<RegionMembershipWithFlags> regionMembership;
    // END REQUIRED ARGUMENTS

    private List<ProposalState> lastProposalState;

    // From the memory of the last set of proposals, get a particlar indexed items
    public ProposalState getLastProposalStateForIndex(int index) {

        if (lastProposalState == null) {
            assert false;
            return ProposalState.UNCHANGED;
        }

        if (index >= lastProposalState.size()) {
            assert false;
            return ProposalState.UNCHANGED;
        }

        assert (lastProposalState.get(index) != null);

        return lastProposalState.get(index);
    }

    // We copy each mark, and change the ID to reflect the following
    //  1 = UNCHANGED
    //  2 = MODIFIED, ORIGINAL
    //  3 = MODIFIED, NEW
    //  4 = ADDED (BIRTH)
    //  5 = REMOVED (DEATH)
    private static void processMarkId(
            int id,
            Mark selected,
            Mark proposal,
            MarkCollection marksOut,
            List<ProposalState> state) {

        if (selected != null) {
            if (proposal != null) {

                // Normal equality check only considers IDss
                if (selected.equalsDeep(proposal)) {
                    // UNCHANGED
                    Mark outMark = selected.duplicate();
                    state.add(ProposalState.UNCHANGED);
                    marksOut.add(outMark);
                } else {

                    // MODIFIED, ORIGINAL
                    Mark outMarkModifiedOriginal = selected.duplicate();
                    state.add(ProposalState.MODIFIED_ORIGINAL);
                    marksOut.add(outMarkModifiedOriginal);

                    // MODIFIED, NEW
                    Mark outMarkModifiedNew = proposal.duplicate();
                    state.add(ProposalState.MODIFIED_NEW);
                    marksOut.add(outMarkModifiedNew);
                }

            } else {
                // DEATH
                Mark outMark = selected.duplicate();
                state.add(ProposalState.REMOVED);
                marksOut.add(outMark);
            }
        } else {
            // BIRTH
            assert (proposal != null);
            Mark outMark = proposal.duplicate();
            state.add(ProposalState.ADDED);
            marksOut.add(outMark);
        }
        assert (state.get(state.size() - 1) != null);
    }

    private void copyAsUnchanged(
            MarkCollection src, MarkCollection dest, List<ProposalState> state) {

        for (Mark markOld : src) {

            Mark markNew = markOld.duplicate();
            state.add(ProposalState.UNCHANGED);
            dest.add(markNew);
        }
    }

    @Override
    // We combine both marks into one
    public IndexableOverlays apply(IndexableDualState<MarkCollection> sourceObject) {

        MarkCollection mergedMarks = new MarkCollection();

        lastProposalState = new ArrayList<>();

        // If both are valid we do an actual compare
        if (sourceObject.getPrimary() != null && sourceObject.getSecondary() != null) {

            Map<Integer, Mark> selectedHash = sourceObject.getPrimary().createIdHashMap();
            Map<Integer, Mark> proposalHash = sourceObject.getSecondary().createIdHashMap();

            // We now create a HashSet of all the IDs
            HashSet<Integer> markIds = new HashSet<>();
            markIds.addAll(selectedHash.keySet());
            markIds.addAll(proposalHash.keySet());

            // We loop through all ids
            for (int id : markIds) {
                Mark selected = selectedHash.get(id);
                Mark proposal = proposalHash.get(id);
                processMarkId(id, selected, proposal, mergedMarks, lastProposalState);
            }
        }

        // If one is null, and the other is not, we mark them all as unchanged
        if (sourceObject.getPrimary() != null && sourceObject.getSecondary() == null) {
            copyAsUnchanged(sourceObject.getPrimary(), mergedMarks, lastProposalState);
        }

        if (sourceObject.getPrimary() == null && sourceObject.getSecondary() != null) {
            copyAsUnchanged(sourceObject.getSecondary(), mergedMarks, lastProposalState);
        }

        OverlayCollection overlays =
                OverlayCollectionMarkFactory.createWithoutColor(
                        mergedMarks, regionMembership.get());
        return new IndexableOverlays(sourceObject.getIndex(), overlays);
    }

    public int size() {
        return lastProposalState.size();
    }
}

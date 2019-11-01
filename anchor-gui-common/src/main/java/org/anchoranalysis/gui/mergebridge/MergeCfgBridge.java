package org.anchoranalysis.gui.mergebridge;

/*
 * #%L
 * anchor-gui
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Supplier;

import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.core.bridge.IObjectBridge;
import org.anchoranalysis.core.index.GetOperationFailedException;
import overlay.OverlayCollectionMarkFactory;
import ch.ethz.biol.cell.gui.overlay.OverlayCollection;
import ch.ethz.biol.cell.mpp.cfg.Cfg;
import ch.ethz.biol.cell.mpp.instantstate.OverlayedInstantState;
import ch.ethz.biol.cell.mpp.mark.regionmap.RegionMembershipWithFlags;

public class MergeCfgBridge implements IObjectBridge<IndexedDualState<Cfg>, OverlayedInstantState> {
	
	private Supplier<RegionMembershipWithFlags> regionMembership;
	private ArrayList<ProposalState> lastProposalState;
		
	public MergeCfgBridge(Supplier<RegionMembershipWithFlags> regionMembership) {
		super();
		this.regionMembership = regionMembership;
	}

	// From the memory of the last set of proposals, get a particlar indexed items
	public ProposalState getLastProposalStateForIndex( int index ) {
		
		if (lastProposalState==null) {
			assert false;
			return ProposalState.UNCHANGED;
		}
		
		if (index >= lastProposalState.size()) {
			assert false;
			return ProposalState.UNCHANGED;
		}

		assert(lastProposalState.get(index)!=null);
		
		return lastProposalState.get(index);
	}
	
	// We copy each mark, and change the ID to reflect the following
	//  1 = UNCHANGED
	//  2 = MODIFIED, ORIGINAL
	//  3 = MODIFIED, NEW
	//  4 = ADDED (BIRTH)
	//  5 = REMOVED (DEATH)
	private static void processMarkId( int id, Mark selected, Mark proposal, Cfg cfgOut, ArrayList<ProposalState> state ) {
		
		if (selected!=null) {
			if (proposal!=null) {
				
				// Normal equality check only considers IDss
				if (selected.equalsDeep(proposal)) {
					// UNCHANGED						
					Mark outMark = selected.duplicate();
					state.add(ProposalState.UNCHANGED);
					cfgOut.add(outMark);
				} else {
					
					// MODIFIED, ORIGINAL
					Mark outMarkModifiedOriginal = selected.duplicate();
					state.add(ProposalState.MODIFIED_ORIGINAL);
					cfgOut.add(outMarkModifiedOriginal);
					
					// MODIFIED, NEW
					Mark outMarkModifiedNew = proposal.duplicate();
					state.add(ProposalState.MODIFIED_NEW);
					cfgOut.add(outMarkModifiedNew);
				}
				
			} else {
				// DEATH
				Mark outMark = selected.duplicate();
				state.add(ProposalState.REMOVED);
				cfgOut.add(outMark);
			}
		} else {
			// BIRTH
			assert(proposal!=null);
			Mark outMark = proposal.duplicate();
			state.add(ProposalState.ADDED);
			cfgOut.add(outMark);
		}
		assert( state.get(state.size()-1) != null);
	}
	
	private void copyAsUnchanged( Cfg src, Cfg dest, ArrayList<ProposalState> state  ) {
		
		for (Mark markOld : src ) {
			
			Mark markNew = markOld.duplicate();
			state.add(ProposalState.UNCHANGED);
			dest.add( markNew );
		}
	}
	
	@Override
	// We combine both cfg into one
	public OverlayedInstantState bridgeElement(IndexedDualState<Cfg> sourceObject)
			throws GetOperationFailedException {
		
		Cfg mergedCfg = new Cfg();

		lastProposalState = new ArrayList<>();
		
		// If both are valid we do an actual compare
		if (sourceObject.getPrimary() != null && sourceObject.getSecondary() != null) {
		
			HashMap<Integer,Mark> selectedHash = sourceObject.getPrimary().createIdHashMap();
			HashMap<Integer,Mark> proposalHash = sourceObject.getSecondary().createIdHashMap();
			
			// We now create a HashSet of all the IDs
			HashSet<Integer> markIds = new HashSet<>();
			markIds.addAll( selectedHash.keySet() );
			markIds.addAll( proposalHash.keySet() );
			
			// We loop through all ids
			for( int id : markIds ) {
				Mark selected = selectedHash.get(id);
				Mark proposal = proposalHash.get(id);
				processMarkId(id, selected, proposal, mergedCfg, lastProposalState );
			}
		}
		
		// If one is null, and the other is not, we mark them all as unchanged
		if (sourceObject.getPrimary() != null && sourceObject.getSecondary() == null) {
			copyAsUnchanged( sourceObject.getPrimary(), mergedCfg, lastProposalState);
		}
		
		if (sourceObject.getPrimary() == null && sourceObject.getSecondary() != null) {
			copyAsUnchanged( sourceObject.getSecondary(), mergedCfg, lastProposalState);
		}
		
		OverlayCollection oc = OverlayCollectionMarkFactory.createWithoutColor(
			mergedCfg,
			regionMembership.get()
		);
		return new OverlayedInstantState( sourceObject.getIndex(), oc);
	}

	public int size() {
		return lastProposalState.size();
	}
	
}
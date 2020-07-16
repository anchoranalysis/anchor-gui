/*-
 * #%L
 * anchor-plugin-gui-import
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
package org.anchoranalysis.gui.videostats.internalframe.markredraw;

import java.awt.Color;
import java.util.Optional;

import org.anchoranalysis.anchor.mpp.bean.cfg.CfgGen;
import org.anchoranalysis.anchor.mpp.bean.proposer.CfgProposer;
import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.anchor.mpp.cfg.ColoredCfg;
import org.anchoranalysis.anchor.mpp.mark.Mark;



import org.anchoranalysis.anchor.mpp.proposer.ProposerContext;
import org.anchoranalysis.core.color.ColorList;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.gui.frame.overlays.ProposedCfg;
import org.anchoranalysis.gui.videostats.internalframe.ProposalOperation;
import org.anchoranalysis.gui.videostats.internalframe.evaluator.ProposalOperationCreator;

public class CfgProposerEvaluator implements ProposalOperationCreator {

	private CfgProposer proposer;
	
	public CfgProposerEvaluator(CfgProposer cfgProposer) {
		super();
		this.proposer = cfgProposer;
		assert( proposer!=null );
	}

	private static ColoredCfg generateOutputCfg( Optional<Cfg> cfg ) {
		
		if (!cfg.isPresent()) {
			return new ColoredCfg();
		}
		
		Cfg cfgNew = cfg.get().deepCopy();
		
		// We replace all the IDs with 0
		for (Mark m : cfgNew) {
			m.setId(0);
		}
		
		return new ColoredCfg(cfgNew, createDefaultColorList() );
	}

	@Override
	public ProposalOperation create(
		Cfg cfg,
		Point3d position,
		ProposerContext context,
		final CfgGen cfgGen
	) {
		return errorNode -> {
			ProposedCfg er = new ProposedCfg();

			// TODO replace proposer
			Optional<Cfg> cfgProp = proposer.propose( cfgGen, context.replaceError(errorNode) );
			er.setSuccess(cfgProp.isPresent());

			
			if (cfgProp.isPresent()) {
								
				ColoredCfg coloredCfg = generateOutputCfg( cfgProp );
				er.setColoredCfg( coloredCfg );
				er.setCfgToRedraw(
					cfgProp.get().createMerged(coloredCfg.getCfg())
				);
				er.setCfgCore(cfgProp.get());
				
				er.setSuggestedSliceNum( (int) cfgProp.get().get(0).centerPoint().getZ() );
			}
			return er;
		};
	}
	
	private static ColorList createDefaultColorList() {
		ColorList colorList = new ColorList();
		colorList.add( new RGBColor(Color.BLUE) );		//  0 is the mark added
		colorList.add( new RGBColor(Color.RED) );		//  1 is any debug marks
		colorList.add( new RGBColor(Color.GREEN) );		//  2 center point
		colorList.add( new RGBColor(Color.YELLOW) );
		return colorList;
	}
}

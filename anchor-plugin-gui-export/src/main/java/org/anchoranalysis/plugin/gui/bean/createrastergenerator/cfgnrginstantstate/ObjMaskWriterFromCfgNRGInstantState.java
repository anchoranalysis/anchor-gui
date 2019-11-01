package org.anchoranalysis.plugin.gui.bean.createrastergenerator.cfgnrginstantstate;

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


import overlay.OverlayCollectionMarkFactory;

import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.overlay.bean.objmask.writer.ObjMaskWriter;
import org.anchoranalysis.anchor.overlay.collection.ColoredOverlayCollection;
import org.anchoranalysis.anchor.overlay.id.IDGetterOverlayID;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.core.idgetter.IDGetterIter;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.gui.bean.exporttask.ExportTaskParams;
import org.anchoranalysis.image.io.generator.raster.RasterGeneratorFromDisplayStack;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.bean.objmask.writer.RGBOutlineWriter;
import org.anchoranalysis.io.generator.IterableObjectGenerator;
import org.anchoranalysis.io.generator.IterableObjectGeneratorBridge;
import org.anchoranalysis.plugin.gui.bean.createrastergenerator.CreateRasterGenerator;
import org.anchoranalysis.plugin.gui.bean.exporttask.MappedFrom;

import ch.ethz.biol.cell.imageprocessing.io.generator.raster.CachedRGB;
import ch.ethz.biol.cell.imageprocessing.io.generator.raster.CfgCachedGenerator;
import ch.ethz.biol.cell.imageprocessing.io.generator.raster.OverlayedDisplayStackUpdate;
import ch.ethz.biol.cell.imageprocessing.io.idgetter.IDGetterMarkID;
import ch.ethz.biol.cell.mpp.cfg.Cfg;
import ch.ethz.biol.cell.mpp.cfgtoobjmaskwriter.SimpleOverlayWriter;
import ch.ethz.biol.cell.mpp.gui.videostats.internalframe.markredraw.ColoredCfg;
import ch.ethz.biol.cell.mpp.instantstate.CfgNRGInstantState;
import ch.ethz.biol.cell.mpp.mark.GlobalRegionIdentifiers;
import ch.ethz.biol.cell.mpp.mark.regionmap.RegionMapSingleton;
import ch.ethz.biol.cell.mpp.mark.regionmap.RegionMembershipWithFlags;
import ch.ethz.biol.cell.mpp.nrg.CfgNRG;

public class ObjMaskWriterFromCfgNRGInstantState extends CreateRasterGenerator<CfgNRGInstantState> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8570118352758888882L;

	// END BEAN PROPERTIES
	@BeanField
	private ObjMaskWriter objMaskWriter;
	
	@BeanField
	private boolean mip = false;
	
	@BeanField
	private boolean colorFromIter = false;
	
	@BeanField
	private int borderSize = 3;
	
	@BeanField
	private String backgroundStackName = "input_image";
	// END BEAN PROPERTIES

	public ObjMaskWriterFromCfgNRGInstantState() {
		objMaskWriter = new RGBOutlineWriter(borderSize);
	}
	
	private IDGetter<Mark> colorGetter() {
		if (colorFromIter) {
			return new IDGetterIter<>();
		} else {
			return new IDGetterMarkID();
		}
	}
	
	@Override
	public IterableObjectGenerator<MappedFrom<CfgNRGInstantState>, Stack> createGenerator(
			final ExportTaskParams params) throws CreateException {

		final IterableObjectGenerator<OverlayedDisplayStackUpdate,Stack> generator;
		
		// params.getColorIndexMarks()
		if (mip==true) {
			throw new CreateException("The mip flag is no longer supported for this bean");
		} else {
			
			// TODO the defaultImage for the cachedRGB should probably come from elsewhere
			CachedRGB cachedRGB = new CachedRGB(new IDGetterOverlayID() );
			
			CfgCachedGenerator ccGenerator = new CfgCachedGenerator(
				cachedRGB,
				params.getOutputManager().getErrorReporter()
			); 
			
			SimpleOverlayWriter writer = new SimpleOverlayWriter(
				objMaskWriter
			);
			
			ccGenerator.updateMaskWriter(writer);
			
			generator = new RasterGeneratorFromDisplayStack<>(ccGenerator,true);
		}
			
		return new IterableObjectGeneratorBridge<>(
			generator,
			elem -> bridgeElement(elem, params)
		);
	}
	
	private OverlayedDisplayStackUpdate bridgeElement( MappedFrom<CfgNRGInstantState> sourceObject, ExportTaskParams params ) throws GetOperationFailedException {
		try {
			Stack backgroundStackSrc = params.getFinderImgStackCollection().getImgStackCollection().getException(backgroundStackName);
						
			DisplayStack backgroundStack = DisplayStack.create(
				backgroundStackSrc
			);
			
			ColoredCfg coloredCfg = new ColoredCfg(
				extractOrEmpty(sourceObject.getObj().getCfgNRG()),
				params.getColorIndexMarks(),
				colorGetter()
			);
			
			RegionMembershipWithFlags regionMembership = RegionMapSingleton.instance().membershipWithFlagsForIndex(
				GlobalRegionIdentifiers.SUBMARK_INSIDE
			);
		
			ColoredOverlayCollection oc = OverlayCollectionMarkFactory.createColor(
				coloredCfg,
				regionMembership
			);
			return OverlayedDisplayStackUpdate.assignOverlaysAndBackground(oc,backgroundStack);
			
		} catch (CreateException e) {
			throw new GetOperationFailedException(e);
		}
	}

	private static Cfg extractOrEmpty( CfgNRG cfgNRG ) {
		if (cfgNRG!=null) {
			return cfgNRG.getCfg();
		} else {
			return new Cfg();
		}
	}

	@Override
	public boolean hasNecessaryParams(ExportTaskParams params) {
		return params.getFinderImgStackCollection() != null && params.getColorIndexMarks() != null;
	}

	public ObjMaskWriter getObjMaskWriter() {
		return objMaskWriter;
	}

	public void setObjMaskWriter(ObjMaskWriter objMaskWriter) {
		this.objMaskWriter = objMaskWriter;
	}

	public boolean isMip() {
		return mip;
	}

	public void setMip(boolean mip) {
		this.mip = mip;
	}

	@Override
	public String getBeanDscr() {
		return String.format("%s(mip=%d, objMaskWriter=%s)", getBeanName(), mip ? 1 : 0, objMaskWriter.getBeanDscr() );
	}

	public int getBorderSize() {
		return borderSize;
	}

	public void setBorderSize(int borderSize) {
		this.borderSize = borderSize;
	}

	public boolean isColorFromIter() {
		return colorFromIter;
	}

	public void setColorFromIter(boolean colorFromIter) {
		this.colorFromIter = colorFromIter;
	}

	public String getBackgroundStackName() {
		return backgroundStackName;
	}

	public void setBackgroundStackName(String backgroundStackName) {
		this.backgroundStackName = backgroundStackName;
	}
	
}
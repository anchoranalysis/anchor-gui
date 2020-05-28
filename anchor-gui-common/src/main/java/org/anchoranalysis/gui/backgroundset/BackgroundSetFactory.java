package org.anchoranalysis.gui.backgroundset;

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


import java.util.Set;

import org.anchoranalysis.core.cache.CachedOperation;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.Operation;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.container.IBoundedIndexContainer;
import org.anchoranalysis.core.index.container.bridge.BoundedIndexContainerBridgeWithoutIndex;
import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterIncrement;
import org.anchoranalysis.gui.container.background.BackgroundStackCntr;
import org.anchoranalysis.gui.container.background.SingleBackgroundStackCntr;
import org.anchoranalysis.gui.serializedobjectset.MarkWithRaster;
import org.anchoranalysis.image.chnl.factory.ChnlFactory;
import org.anchoranalysis.image.experiment.identifiers.ImgStackIdentifiers;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.TimeSequence;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedByte;
import org.anchoranalysis.io.manifest.deserializer.folder.LoadContainer;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;

public class BackgroundSetFactory {

	public static BackgroundSet createBackgroundSet( NamedProvider<TimeSequence> imageStackCollection, ProgressReporter progressReporter ) throws CreateException {
		BackgroundSet backgroundSet = new BackgroundSet();
		try {
			addFromImgStackCollection(backgroundSet, imageStackCollection, progressReporter );
		} catch (OperationFailedException e) {
			throw new CreateException(e);
		}		
		return backgroundSet;
	}
	
	public static BackgroundSet createBackgroundSetFromExisting( BackgroundSet existing, NamedProvider<TimeSequence> imageStackCollection, OutputWriteSettings outputSettings, ProgressReporter progressReporter ) throws CreateException {
		
		BackgroundSet bsNew = new BackgroundSet();
		bsNew.addAll( existing );
		try {
			BackgroundSetFactory.addFromImgStackCollection(bsNew, imageStackCollection, progressReporter );
		} catch (OperationFailedException e) {
			throw new CreateException(e);
		}

		return bsNew;
	}
	
	public static BackgroundSet createBackgroundSetFromExisting( BackgroundSet existing, NamedProvider<TimeSequence> imageStackCollection, Set<String> keys, OutputWriteSettings outputSettings, ProgressReporter progressReporter ) throws CreateException {
		
		BackgroundSet bsNew = new BackgroundSet();
		bsNew.addAll( existing );
		try {
			BackgroundSetFactory.addFromImgStackCollection(bsNew, imageStackCollection, keys, progressReporter );
		} catch (OperationFailedException e) {
			throw new CreateException(e);
		}

		return bsNew;
	}
	

	public static BackgroundSet createMergedBackgroundSet( LoadContainer<MarkWithRaster> lc ) throws GetOperationFailedException {
		BackgroundSet backgroundSet = new BackgroundSet();
		
		// We assume every LoadContainer contains the same rasters in the BackgroundSet
		///  and use the first one to get the names
		
		BackgroundSet first = lc.getCntr().get( lc.getCntr().getMinimumIndex() ).getBackgroundSet();
		
		for (String name : first.names() ) {
			backgroundSet.addItem(name, new SingleBackgroundStackCntr( rasterBridge(lc, name) ) );
		}
		
		return backgroundSet;
	}
	
	
	private static IBoundedIndexContainer<DisplayStack> rasterBridge( final LoadContainer<MarkWithRaster> cntr, final String name ) {
		
		assert(cntr!=null);
		return new BoundedIndexContainerBridgeWithoutIndex<>(
			cntr.getCntr(),
			sourceObject -> {
				assert(sourceObject!=null);
				try {
					return sourceObject.getBackgroundSet().singleStack(name);
				} catch (GetOperationFailedException e) {
					throw new OperationFailedException(e);
				}
			}
		);		
	}
		
	private static class AddBackgroundSetItem extends CachedOperation<BackgroundStackCntr, OperationFailedException> {

		private NamedProvider<TimeSequence> imageStackCollection;
		private String id;
		
		public AddBackgroundSetItem(
				NamedProvider<TimeSequence> imageStackCollection,
				String id) {
			super();
			this.imageStackCollection = imageStackCollection;
			this.id = id;
		}

		@Override
		protected BackgroundStackCntr execute() throws OperationFailedException {

			try {
				TimeSequence seq = imageStackCollection.getException(id);
				
				if (seq.size()>1) {
					return createBackgroundTimeSeries(seq);
				} else {
					return createBackgroundNotTimeSeries(
						seq.get(0)
					);
				}

			} catch (NamedProviderGetException e) {
				throw new OperationFailedException(e);
			}
		}
	}

	private static BackgroundStackCntr createBackgroundTimeSeries( TimeSequence seq ) throws OperationFailedException {
		return BackgroundStackCntrFactory.convertedSequence(seq);
	}
	
	private static BackgroundStackCntr createBackgroundNotTimeSeries( Stack img ) throws OperationFailedException {
		return BackgroundStackCntrFactory.singleSavedStack(img);
	}
	
	private static void addFromImgStackCollection( BackgroundSet backgroundSet, NamedProvider<TimeSequence> imageStackCollection, ProgressReporter progressReporter ) throws OperationFailedException {
		addFromImgStackCollection(backgroundSet, imageStackCollection, imageStackCollection.keys(), progressReporter );
		
		addEmpty(backgroundSet, imageStackCollection);
	}
	
	/** Adds an empty (all 0 pixels, raster) if at least one other image exists 
	 * @throws OperationFailedException */
	public static void addEmpty( BackgroundSet backgroundSet, NamedProvider<TimeSequence> imageStackCollection ) throws OperationFailedException {
		
		backgroundSet.addItem(
			"blank (all black)",
			() -> {
				ImageDim sd = guessDimensions(imageStackCollection);
				Stack stack = createEmptyStack(sd);
				return BackgroundStackCntrFactory.singleSavedStack(stack);
			}
		);
	}
	
	private static ImageDim guessDimensions( NamedProvider<TimeSequence> imageStackCollection ) throws OperationFailedException {
		try {
			return imageStackCollection.getException( imageStackCollection.keys().iterator().next() ).getDimensions();
		} catch (NamedProviderGetException e) {
			throw new OperationFailedException(e.summarize());
		}
	}
	
	private static Stack createEmptyStack( ImageDim sd ) throws OperationFailedException {
		try {
			Stack stack = new Stack();
			stack.addChnl(
				ChnlFactory.instance().createEmptyInitialised(sd, VoxelDataTypeUnsignedByte.instance )
			);
			return stack;
		} catch (IncorrectImageSizeException e) {
			throw new OperationFailedException(e);
		}
	}
		
	private static void addFromImgStackCollection( BackgroundSet backgroundSet, NamedProvider<TimeSequence> imageStackCollection, Set<String> keys, ProgressReporter progressReporter ) throws OperationFailedException {
	
		boolean hasNrgStack = keys.contains(ImgStackIdentifiers.NRG_STACK);
		
		ProgressReporterIncrement pri = new ProgressReporterIncrement(progressReporter);
		pri.setMin(0);
		pri.setMax( keys.size() + (hasNrgStack?1:0) );
		
		pri.open();
		
		try {
			
			// The way we handle this means we cannot add the (only first three) brackets on the name, as the image has not yet been evaluated
			for (String id : keys) {
				Operation<BackgroundStackCntr, OperationFailedException> operation = new AddBackgroundSetItem(imageStackCollection, id);
				backgroundSet.addItem(id, operation );
				pri.update();
			}
		
			// TODO fix, this will always evaluate the NRG stack
			// We add each part of the NRG Stack separately
		
			if (hasNrgStack) {
				addStackAsSeparateChnl(
					backgroundSet,
					imageStackCollection.getException(ImgStackIdentifiers.NRG_STACK).get(0),	// Only take first
					"nrgStack-chnl"
				);
				pri.update();
			}
			
		} catch (NamedProviderGetException e) {
			throw new OperationFailedException( e.summarize() );
		} finally {
			pri.close();
		}
	}
	
	private static void addStackAsSeparateChnl( BackgroundSet backgroundSet, Stack stack, String prefix ) throws OperationFailedException {
	
		try {
			for (int c=0; c<stack.getNumChnl(); c++) {
				// We create a stack just with this channel
				Stack stackSingle = new Stack();
				stackSingle.addChnl( stack.getChnl(c) );
				
				backgroundSet.addItem( String.format("%s%d", prefix,c), stackSingle );
			}
		} catch (IncorrectImageSizeException e) {
			throw new OperationFailedException(e);
		}
	}
}

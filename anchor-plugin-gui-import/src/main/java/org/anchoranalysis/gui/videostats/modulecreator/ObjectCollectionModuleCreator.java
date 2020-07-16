/*-
 * #%L
 * anchor-plugin-gui-import
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan
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
package org.anchoranalysis.gui.videostats.modulecreator;

import java.util.Optional;

import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;
import org.anchoranalysis.anchor.overlay.collection.OverlayCollectionObjectFactory;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.Operation;
import org.anchoranalysis.core.idgetter.IDGetterIter;
import org.anchoranalysis.gui.image.frame.ISliderState;
import org.anchoranalysis.gui.videostats.dropdown.IAddVideoStatsModule;
import org.anchoranalysis.gui.videostats.dropdown.ModuleAddUtilities;
import org.anchoranalysis.gui.videostats.dropdown.VideoStatsModuleGlobalParams;
import org.anchoranalysis.gui.videostats.dropdown.common.NRGBackground;
import org.anchoranalysis.gui.videostats.internalframe.InternalFrameStaticOverlaySelectable;
import org.anchoranalysis.gui.videostats.module.VideoStatsModuleCreateException;
import org.anchoranalysis.gui.videostats.operation.combine.IVideoStatsOperationCombine;
import org.anchoranalysis.image.object.ObjectCollection;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ObjectCollectionModuleCreator extends VideoStatsModuleCreator {

	private String fileIdentifier;
	private String name;
	private Operation<ObjectCollection,OperationFailedException> opObjects;
	private NRGBackground nrgBackground;
	private VideoStatsModuleGlobalParams mpg;
	
	@Override
	public void createAndAddVideoStatsModule(IAddVideoStatsModule adder) throws VideoStatsModuleCreateException {
		try {
			OverlayCollection overlays = OverlayCollectionObjectFactory.createWithoutColor(
				opObjects.doOperation(),
				new IDGetterIter<>()
			);
			
			InternalFrameStaticOverlaySelectable imageFrame = new InternalFrameStaticOverlaySelectable(
				String.format("%s: %s", fileIdentifier, name),
				false
			);
			
			ISliderState sliderState = imageFrame.init(
				overlays,
				adder.getSubgroup().getDefaultModuleState().getState(),
				mpg
			);
			
			imageFrame.controllerBackgroundMenu(sliderState).add(
				mpg,
				nrgBackground.getBackgroundSet()
			);
			ModuleAddUtilities.add(
				adder,
				imageFrame.moduleCreator(sliderState)
			);

		} catch (InitException | OperationFailedException e) {
			throw new VideoStatsModuleCreateException(e);
		}
	}

	@Override
	public Optional<IVideoStatsOperationCombine> getCombiner() {
		return Optional.of(
			new IVideoStatsOperationCombine() {
				
				@Override
				public Optional<Operation<Cfg,OperationFailedException>> getCfg() {
					return Optional.empty();
				}
		
				@Override
				public String generateName() {
					return fileIdentifier;
				}
	
				@Override
				public Optional<Operation<ObjectCollection, OperationFailedException>> getObjects() {
					return Optional.of(opObjects);
				}
		
				@Override
				public NRGBackground getNrgBackground() {
					return nrgBackground;
				}
			}
		);
	}
}

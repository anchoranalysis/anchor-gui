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

package org.anchoranalysis.gui.videostats.modulecreator;

import java.util.Optional;
import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.anchor.mpp.overlay.OverlayCollectionMarkFactory;
import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.CallableWithException;
import org.anchoranalysis.gui.image.frame.ISliderState;
import org.anchoranalysis.gui.interactivebrowser.backgroundset.menu.definition.ChangeableBackgroundDefinitionSimple;
import org.anchoranalysis.gui.mark.MarkDisplaySettings;
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
public class CfgModuleCreator extends VideoStatsModuleCreator {

    private String fileIdentifier;
    private String name;
    private CallableWithException<Cfg, OperationFailedException> opCfg;
    private NRGBackground nrgBackground;
    private VideoStatsModuleGlobalParams mpg;
    private MarkDisplaySettings markDisplaySettings;

    @Override
    public void createAndAddVideoStatsModule(IAddVideoStatsModule adder)
            throws VideoStatsModuleCreateException {

        try {
            Cfg cfg = opCfg.call();

            OverlayCollection oc =
                    OverlayCollectionMarkFactory.createWithoutColor(
                            cfg, markDisplaySettings.regionMembership());

            String frameName = String.format("%s: %s", fileIdentifier, name);
            InternalFrameStaticOverlaySelectable imageFrame =
                    new InternalFrameStaticOverlaySelectable(frameName, true);
            ISliderState sliderState =
                    imageFrame.init(
                            oc, adder.getSubgroup().getDefaultModuleState().getState(), mpg);

            imageFrame
                    .controllerBackgroundMenu(sliderState)
                    .addDefinition(
                            mpg,
                            new ChangeableBackgroundDefinitionSimple(
                                    nrgBackground.getBackgroundSet()));
            ModuleAddUtilities.add(adder, imageFrame.moduleCreator(sliderState));

        } catch (InitException | OperationFailedException e) {
            throw new VideoStatsModuleCreateException(e);
        }
    }

    @Override
    public Optional<IVideoStatsOperationCombine> getCombiner() {
        return Optional.of(
                new IVideoStatsOperationCombine() {

                    @Override
                    public Optional<CallableWithException<Cfg, OperationFailedException>> getCfg() {
                        return Optional.of(opCfg);
                    }

                    @Override
                    public String generateName() {
                        return fileIdentifier;
                    }

                    @Override
                    public Optional<CallableWithException<ObjectCollection, OperationFailedException>>
                            getObjects() {
                        return Optional.empty();
                    }

                    @Override
                    public NRGBackground getNrgBackground() {
                        return nrgBackground;
                    }
                });
    }
}

/*-
 * #%L
 * anchor-gui-annotation
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

package org.anchoranalysis.gui.annotation.strategy.builder.mark;

import java.nio.file.Path;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.gui.annotation.additional.ShowAdditionalRasters;
import org.anchoranalysis.gui.annotation.additional.ShowComparers;
import org.anchoranalysis.gui.annotation.additional.ShowRaster;
import org.anchoranalysis.gui.annotation.builder.AdditionalFramesContext;
import org.anchoranalysis.plugin.annotation.bean.strategy.MarkProposerStrategy;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
class ShowAdditionalFrames {

    public static void apply(
            InitParamsProposeMarks paramsInit,
            AdditionalFramesContext context,
            Path matchPath,
            MarkProposerStrategy strategy)
            throws OperationFailedException {

        ShowRaster showRaster = new ShowRaster(context.getAdder(), context.getMpg());

        ShowAdditionalRasters showAdditional =
                new ShowAdditionalRasters(
                        showRaster,
                        strategy.getAdditionalBackgrounds(),
                        matchPath,
                        context.getName(),
                        strategy.getStackReader());
        showAdditional.apply();

        ShowComparers showComparers =
                new ShowComparers(
                        showRaster,
                        strategy.getMultipleComparer(),
                        context.getOutputWriteSettings().getDefaultColors(),
                        matchPath,
                        context.getName(),
                        paramsInit.getBackground().getDefaultBackground(),
                        context.getMpg().getModelDirectory(),
                        context.getMpg().getLogger());
        showComparers.apply(paramsInit.getInitAnnotation().getAnnotation());
    }
}

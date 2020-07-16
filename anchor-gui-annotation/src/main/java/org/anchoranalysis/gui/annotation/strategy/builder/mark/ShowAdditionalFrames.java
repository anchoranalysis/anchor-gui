/* (C)2020 */
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
                        strategy.getListDisplayRasters(),
                        matchPath,
                        context.getName(),
                        strategy.getRasterReader());
        showAdditional.apply();

        ShowComparers showComparers =
                new ShowComparers(
                        showRaster,
                        strategy.getMultipleComparer(),
                        context.getOutputWriteSettings().getDefaultColorSetGenerator(),
                        matchPath,
                        context.getName(),
                        paramsInit.getBackground().getDefaultBackground(),
                        context.getMpg().getModelDirectory(),
                        context.getMpg().getLogger());
        showComparers.apply(paramsInit.getInitAnnotation().getAnnotation());
    }
}

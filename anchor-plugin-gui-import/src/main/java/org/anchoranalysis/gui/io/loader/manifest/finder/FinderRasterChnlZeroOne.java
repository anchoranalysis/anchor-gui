/* (C)2020 */
package org.anchoranalysis.gui.io.loader.manifest.finder;

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;
import org.anchoranalysis.core.index.container.SingleContainer;
import org.anchoranalysis.gui.container.background.BackgroundStackCntr;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.image.stack.DisplayStack;

// We can eliminate this finder in a moment
public abstract class FinderRasterChnlZeroOne extends FinderRasterChnl
        implements BackgroundStackCntr {

    public FinderRasterChnlZeroOne(RasterReader rasterReader, ErrorReporter errorReporter) {
        super(rasterReader, true, errorReporter);
    }

    @Override
    public BoundedIndexContainer<DisplayStack> backgroundStackCntr()
            throws GetOperationFailedException {
        return new SingleContainer<>(backgroundStack(), 0, true);
    }

    public DisplayStack backgroundStack() throws GetOperationFailedException {
        Channel background = getFirstChnl();
        try {
            return DisplayStack.create(background);
        } catch (CreateException e) {
            throw new GetOperationFailedException(e);
        }
    }
}

/* (C)2020 */
package org.anchoranalysis.gui.io.loader.manifest.finder;

import java.util.Optional;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;
import org.anchoranalysis.core.index.container.SingleContainer;
import org.anchoranalysis.gui.container.background.BackgroundStackCntr;
import org.anchoranalysis.gui.finder.FinderRasterSingleChnl;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReaderUtilities;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.manifest.file.FileWrite;
import org.anchoranalysis.io.manifest.finder.FinderSingleFile;

public abstract class FinderRasterStack extends FinderSingleFile
        implements FinderRasterSingleChnl, BackgroundStackCntr {

    private Optional<Stack> result;

    private RasterReader rasterReader;

    public FinderRasterStack(RasterReader rasterReader, ErrorReporter errorReporter) {
        super(errorReporter);
        this.rasterReader = rasterReader;
    }

    private Stack createStack(FileWrite fileWrite) throws RasterIOException {
        // Assume single series, single channel
        return RasterReaderUtilities.openStackFromPath(rasterReader, fileWrite.calcPath());
    }

    public Stack get() throws GetOperationFailedException {
        assert (exists());
        if (!result.isPresent()) {
            try {
                result = Optional.of(createStack(getFoundFile()));
            } catch (RasterIOException e) {
                throw new GetOperationFailedException(e);
            }
        }
        return result.get();
    }

    @Override
    public Channel getFirstChnl() throws GetOperationFailedException {
        return get().getChnl(0);
    }

    @Override
    public BoundedIndexContainer<DisplayStack> backgroundStackCntr()
            throws GetOperationFailedException {
        Stack resultNormalized = get().duplicate();

        try {
            DisplayStack bgStack = DisplayStack.create(resultNormalized);
            return new SingleContainer<>(bgStack, 0, true);
        } catch (CreateException e) {
            throw new GetOperationFailedException(e);
        }
    }

    @Override
    public int getNumChnl() throws GetOperationFailedException {
        return get().getNumChnl();
    }
}

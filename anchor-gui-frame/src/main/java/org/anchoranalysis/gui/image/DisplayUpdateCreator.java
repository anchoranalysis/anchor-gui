/* (C)2020 */
package org.anchoranalysis.gui.image;

import org.anchoranalysis.anchor.overlay.Overlay;
import org.anchoranalysis.anchor.overlay.writer.DrawOverlay;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.function.FunctionWithException;
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.gui.frame.display.BoundColoredOverlayCollection;
import org.anchoranalysis.gui.frame.display.DisplayUpdate;
import org.anchoranalysis.gui.frame.display.OverlayedDisplayStackUpdate;
import org.anchoranalysis.gui.frame.display.overlay.OverlayRetriever;
import org.anchoranalysis.image.stack.DisplayStack;

public class DisplayUpdateCreator
        implements FunctionWithException<Integer, DisplayUpdate, OperationFailedException> {

    private FunctionWithException<Integer, OverlayedDisplayStackUpdate, GetOperationFailedException>
            src;
    private DrawOverlay maskWriter;
    private IDGetter<Overlay> idGetter;

    // This keeps track of the current over
    private BoundColoredOverlayCollection boundOverlay = null;

    public DisplayUpdateCreator(
            FunctionWithException<Integer, OverlayedDisplayStackUpdate, GetOperationFailedException>
                    src,
            IDGetter<Overlay> idGetter) {
        super();
        this.src = src;
        this.idGetter = idGetter;
    }

    // Must be called before we can bridge any elements
    public void updateMaskWriter(DrawOverlay maskWriter) throws SetOperationFailedException {
        this.maskWriter = maskWriter;
        if (boundOverlay != null) {
            boundOverlay.updateMaskWriter(maskWriter);
        }
    }

    @Override
    public DisplayUpdate apply(Integer sourceObject) throws OperationFailedException {
        try {
            OverlayedDisplayStackUpdate update = src.apply(sourceObject);

            if (update == null) {
                // No change so we return the existing stack
                return null;
            }

            // System.out.println("OverlayBridge new Element\n");

            // If we haven't a current background yet, then we assume the first update has a
            // BackgroundStack attached
            if (boundOverlay == null) {
                assert (update.getBackgroundStack() != null);
                DisplayStack currentBackground = update.getBackgroundStack();
                boundOverlay =
                        new BoundColoredOverlayCollection(
                                maskWriter, idGetter, currentBackground.getDimensions());
            }

            return update.applyAndCreateDisplayUpdate(boundOverlay);

        } catch (CreateException | GetOperationFailedException e) {
            throw new OperationFailedException(e);
        }
    }

    public OverlayRetriever getOverlayRetriever() {
        return boundOverlay.getPrecalculatedCache();
    }
}

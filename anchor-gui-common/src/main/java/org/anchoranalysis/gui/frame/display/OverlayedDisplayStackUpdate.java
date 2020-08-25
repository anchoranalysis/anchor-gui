/*-
 * #%L
 * anchor-gui-common
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

package org.anchoranalysis.gui.frame.display;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.anchoranalysis.core.color.RGBColor;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.overlay.Overlay;
import org.anchoranalysis.overlay.collection.ColoredOverlayCollection;
import org.anchoranalysis.overlay.collection.OverlayCollection;

public class OverlayedDisplayStackUpdate {

    private ColoredOverlayCollection coloredMarks; // If null, we don't change the current marks
    private DisplayStack backgroundStack; // If null, we don't change the existing background
    private OverlayCollection
            changedMarks; // If non-null, additional marks that should be updated, as they might
    // have
    // changed in some way
    private boolean redrawSpecific =
            false; // If true, then the coloredMarks passed can be considered similar to the
    // existing
    // marks. If false, not

    // Assigns a new configuration completely, throwing out what's already there
    public static OverlayedDisplayStackUpdate assignOverlays(
            ColoredOverlayCollection coloredMarks) {
        assert (coloredMarks != null);
        return new OverlayedDisplayStackUpdate(coloredMarks, null, null, false);
    }

    // Replaces the existing coloredMarks with something similar, but we don't know what's changed
    // exactly
    //  coloredMarks is the entire new Marks
    public static OverlayedDisplayStackUpdate updateOverlaysWithSimilar(
            ColoredOverlayCollection coloredMarks, OverlayCollection changedMarks) {
        assert (coloredMarks != null);
        return new OverlayedDisplayStackUpdate(coloredMarks, null, changedMarks, true);
        // return new ColoredMarksRedrawUpdate(coloredMarks, null, null,false);

    }

    public static OverlayedDisplayStackUpdate updateOverlaysWithSimilar(
            ColoredOverlayCollection coloredMarks) {
        assert (coloredMarks != null);
        return new OverlayedDisplayStackUpdate(coloredMarks, null, null, true);
        // return new ColoredMarksRedrawUpdate(coloredMarks, null, null,false);

    }

    public static OverlayedDisplayStackUpdate assignBackground(DisplayStack backgroundStack) {
        assert (backgroundStack != null);
        return new OverlayedDisplayStackUpdate(null, backgroundStack, null, false);
    }

    public static OverlayedDisplayStackUpdate assignOverlaysAndBackground(
            ColoredOverlayCollection coloredMarks, DisplayStack backgroundStack) {
        assert (coloredMarks != null);
        assert (backgroundStack != null);
        return new OverlayedDisplayStackUpdate(coloredMarks, backgroundStack, null, false);
    }

    public static OverlayedDisplayStackUpdate redrawAll() {
        return new OverlayedDisplayStackUpdate(null, null, null, false);
    }

    public static OverlayedDisplayStackUpdate updateChanged(OverlayCollection changedMarks) {
        assert (changedMarks != null);
        return new OverlayedDisplayStackUpdate(null, null, changedMarks, false);
    }

    private OverlayedDisplayStackUpdate(
            ColoredOverlayCollection coloredMarks,
            DisplayStack backgroundStack,
            OverlayCollection redrawParts,
            boolean similar) {
        super();
        this.coloredMarks = coloredMarks;
        this.backgroundStack = backgroundStack;
        this.changedMarks = redrawParts;
        this.redrawSpecific = similar;
    }

    public OverlayCollection getRedrawParts() {
        return changedMarks;
    }

    public ColoredOverlayCollection getColoredMarks() {
        return coloredMarks;
    }

    public DisplayStack getBackgroundStack() {
        return backgroundStack;
    }

    public void mergeWithNewerUpdate(OverlayedDisplayStackUpdate newer) {

        if (!newer.redrawSpecific) {
            this.redrawSpecific = false;
        }

        if (newer.backgroundStack != null) {
            this.backgroundStack = newer.backgroundStack;
        }

        if (newer.getColoredMarks() != null) {
            this.coloredMarks = newer.getColoredMarks();
            assert (newer.getColoredMarks() != null);
        }

        // Any time the background stack changes, we need to redraw everyything anyway
        if (newer.getRedrawParts() == null || newer.backgroundStack == null) {
            this.changedMarks = null;
        } else {

            // Then we add our new parts to the existing parts
            if (this.changedMarks == null) {
                this.changedMarks = newer.getRedrawParts();
            } else {
                this.changedMarks.addAll(newer.getRedrawParts());
            }
        }
    }

    // Applies this change to a boundoverlay and creates an appropriate DisplayUpdate
    public DisplayUpdate applyAndCreateDisplayUpdate(BoundColoredOverlayCollection boundOverlay)
            throws OperationFailedException {
        synchronized (boundOverlay.getPrecalculatedCache()) {
            try {
                // If the stack has changed, when we have to redraw everything anyways
                // Let's double-check to make sure the stack is diffrent
                if (getBackgroundStack() != null) {

                    // Assign a coloredMarks if it exists
                    if (getColoredMarks() != null) {
                        boundOverlay.setOverlayCollection(getColoredMarks());
                    }

                    BoundOverlayedDisplayStack overlayedDisplayStack =
                            new BoundOverlayedDisplayStack(getBackgroundStack(), boundOverlay);
                    return DisplayUpdate.assignNewStack(overlayedDisplayStack);
                }

                // Otherwise if we've received a new ColoredMarks, then it's time to take action
                // We assume than we receive a new ColoredMarks redraw parts is also not
                // simultaenously set
                if (getColoredMarks() != null) {

                    ColoredOverlayCollection cachedOverlayCollection =
                            boundOverlay.getPrecalculatedCache().getOverlays();

                    //				if (getRedrawParts()!=null) {
                    //					System.out.println("Assuming existing redraw parts knows best");
                    //					boundOverlay.updateColoredMarks( getColoredMarks() );
                    //					return DisplayUpdate.redrawParts(
                    // boundOverlay.boxListForMarks(getRedrawParts()) );
                    //				}
                    // assert( update.getRedrawParts()==null );

                    // Find out the difference between the old marks, and the new marks, and this
                    // becomes our redraw part

                    if (isRedrawSpecific()) {
                        // If the *similar* boolean is set, it means the update is similar to the
                        // previous marks
                        //  so we:
                        //    1. assume no mark has changed internally. So the only differences are
                        // adding/removing marks the marks
                        //    2. find these added/removed marks
                        //	  3. change the boundOverlay accordingly
                        //    4. issue DisplayUpdates only for these locations

                        ColoredOverlayCollection added = new ColoredOverlayCollection();
                        ColoredOverlayCollection removed = new ColoredOverlayCollection();
                        List<Integer> removedIndices = new ArrayList<Integer>();

                        // Find specifily the marks that were added and removed
                        createDiff(
                                cachedOverlayCollection,
                                getColoredMarks(),
                                added,
                                removed,
                                removedIndices);

                        // System.out.printf( "Diff: added=%d, removed=%s\n", added.size(),
                        // removed.size() );

                        List<BoundingBox> boxToRefresh = new ArrayList<BoundingBox>();
                        boxToRefresh.addAll(boundOverlay.boxList(removed));

                        boundOverlay.removeOverlays(removedIndices);
                        boundOverlay.addOverlays(added);

                        boxToRefresh.addAll(boundOverlay.boxList(added));

                        if (getRedrawParts() != null) {

                            // List<IndexExstNew> indices = findIndices( getColoredMarks(),
                            // getRedrawParts() );
                            // boundOverlay.updateColorSpecificIndices( getColoredMarks(), indices
                            // );

                            boxToRefresh.addAll(boundOverlay.boxList(getRedrawParts()));
                        }

                        return DisplayUpdate.redrawParts(boxToRefresh);

                    } else {

                        OverlayCollection marksForUpdate =
                                ColoredOverlayCollection.createIntersectionComplement(
                                        cachedOverlayCollection, getColoredMarks());
                        boundOverlay.setOverlayCollection(getColoredMarks());

                        List<BoundingBox> boxToRefresh = boundOverlay.boxList(marksForUpdate);

                        if (getRedrawParts() != null) {
                            boxToRefresh.addAll(boundOverlay.boxList(getRedrawParts()));
                        }

                        return DisplayUpdate.redrawParts(boxToRefresh);
                    }
                }

                // Otherwise we just have a redraw parts command
                if (isRedrawSpecific()) {
                    return DisplayUpdate.redrawParts(boundOverlay.boxList(getRedrawParts()));
                } else {
                    // Then we update everything
                    return DisplayUpdate.redrawEverything();
                }
            } catch (SetOperationFailedException e) {
                throw new OperationFailedException(e);
            }
        }
    }

    // Compare a new-overlay to a previous one and finds the differences.
    //
    //  Note we compare overlays (via memory refernces)
    //
    //    the added overlays are put in outAdded
    //    the removed overys are put in outRemovedMarks and their indices in indicesRemoved
    private static void createDiff(
            ColoredOverlayCollection marksPrevious,
            ColoredOverlayCollection marksNew,
            ColoredOverlayCollection outAdded,
            ColoredOverlayCollection outRemovedMarks,
            List<Integer> outRemovedIndices) {

        Set<Overlay> setPrevious = marksPrevious.createSet();
        Set<Overlay> setNew = marksNew.createSet();

        for (int i = 0; i < marksPrevious.size(); i++) {

            Overlay ol = marksPrevious.get(i);

            if (!setNew.contains(ol)) {
                // If it's not in the new, for sure we've removed it
                RGBColor col = marksPrevious.getColor(i);
                outRemovedMarks.add(ol, col);
                outRemovedIndices.add(i);
            }
        }

        for (int i = 0; i < marksNew.size(); i++) {

            Overlay ol = marksNew.get(i);

            if (!setPrevious.contains(ol)) {
                // If it's in the New, but not the previous, for sure we've added it
                RGBColor col = marksNew.getColor(i);
                outAdded.add(ol, col);
            }
        }
    }

    // Returns the indices of all the elements of marksToFind found in marksToSearch
    //	private static List<IndexExstNew> findIndices( ColoredMarks marksToSearch, Marks marksToFind
    // ) {
    //
    //		List<IndexExstNew> listOut = new ArrayList<IndexExstNew>();
    //
    //		Map<Mark,Integer> setToFind = marksToFind.createHashMapToId();
    //
    //		for (int i=0; i<marksToSearch.size(); i++ ) {
    //			Mark m = marksToSearch.getMarks().get(i);
    //
    //			Integer id = setToFind.get(m);
    //			if (id!=null) {
    //				listOut.add( new IndexExstNew(i, id) );
    //			}
    //		}
    //
    //		return listOut;
    //	}

    // Do we redraw specific bounding boxes, or look for everything
    public boolean isRedrawSpecific() {
        return redrawSpecific;
    }
}

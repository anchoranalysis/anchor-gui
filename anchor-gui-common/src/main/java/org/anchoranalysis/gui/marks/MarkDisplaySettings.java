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

package org.anchoranalysis.gui.marks;

import java.util.ArrayList;
import java.util.List;
import org.anchoranalysis.image.io.bean.object.draw.BoundingBoxOutline;
import org.anchoranalysis.image.io.bean.object.draw.Combine;
import org.anchoranalysis.image.io.bean.object.draw.Filled;
import org.anchoranalysis.image.io.bean.object.draw.IfElse;
import org.anchoranalysis.image.io.bean.object.draw.Midpoint;
import org.anchoranalysis.image.io.bean.object.draw.Nothing;
import org.anchoranalysis.image.io.bean.object.draw.Orientation;
import org.anchoranalysis.image.io.bean.object.draw.Outline;
import org.anchoranalysis.mpp.bean.regionmap.RegionMapSingleton;
import org.anchoranalysis.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.mpp.io.marks.generator.SimpleOverlayWriter;
import org.anchoranalysis.mpp.mark.GlobalRegionIdentifiers;
import org.anchoranalysis.overlay.bean.DrawObject;
import org.anchoranalysis.overlay.writer.DrawOverlay;

// Contains display settings for a mark
public class MarkDisplaySettings {

    private boolean showBoundingBox = false;

    private boolean showShell = false;

    private boolean showInside = true;

    private boolean showMidpoint = false;

    private boolean showOrientationLine = false;

    private boolean showThickBorder = false;

    private boolean showSolid = false;

    public DrawOverlay createConditionalObjectDrawer(IfElse.Condition conditionSelected) {

        int borderSize = showThickBorder ? 6 : 1;

        List<DrawObject> insideList = new ArrayList<>();
        List<DrawObject> shellList = new ArrayList<>();

        if (showInside) {
            addShowInside(insideList, conditionSelected, borderSize);
        }

        if (showBoundingBox) {
            insideList.add(new BoundingBoxOutline(borderSize));
        }

        if (showShell) {
            addShowShell(insideList, shellList, borderSize);
        }

        return determineWriter(insideList, shellList);
    }

    public MarkDisplaySettings duplicate() {
        MarkDisplaySettings copy = new MarkDisplaySettings();
        copy.showBoundingBox = this.showBoundingBox;
        copy.showShell = this.showShell;
        copy.showInside = this.showInside;
        copy.showMidpoint = this.showMidpoint;
        copy.showOrientationLine = this.showOrientationLine;
        copy.showThickBorder = this.showThickBorder;
        copy.showSolid = this.showSolid;
        return copy;
    }

    private DrawObject createInsideConditionalWriter(
            IfElse.Condition conditionSelected, int borderSize) {

        // true WRITER is for when selected
        DrawObject trueWriter = new Filled();

        // false writer is for when not selected
        Outline falseWriter = new Outline(borderSize, false);

        // Combining both situations gives us a selectable
        return new IfElse(conditionSelected, trueWriter, falseWriter);
    }

    private static DrawObject createWriterFromList(List<DrawObject> writerList) {

        if (writerList.isEmpty()) {
            return null;
        }

        return new Combine(writerList);
    }

    private void addShowInside(
            List<DrawObject> insideList, IfElse.Condition conditionSelected, int borderSize) {
        insideList.add(createInsideConditionalWriter(conditionSelected, borderSize));

        if (showSolid) {
            insideList.add(new Filled());
        } else {

            // We only consider these if we are not considering a solid
            if (showMidpoint) {
                insideList.add(new Midpoint());
            }

            if (showOrientationLine) {
                insideList.add(new Orientation());
            }
        }
    }

    private void addShowShell(
            List<DrawObject> insideList, List<DrawObject> shellList, int borderSize) {

        Outline outlineWriter = new Outline(borderSize, false);
        shellList.add(outlineWriter);

        if (showBoundingBox) {
            shellList.add(new BoundingBoxOutline(borderSize));
        }

        // If showInside is switched off, then we have a second chance to show the midpoint
        if (showMidpoint && !showInside) {
            shellList.add(new Midpoint());
        }

        if (showOrientationLine && !showInside) {
            insideList.add(new Orientation());
        }
    }

    public RegionMembershipWithFlags regionMembership() {
        int identifier =
                showShell
                        ? GlobalRegionIdentifiers.SUBMARK_SHELL
                        : GlobalRegionIdentifiers.SUBMARK_INSIDE;
        return RegionMapSingleton.instance().membershipWithFlagsForIndex(identifier);
    }

    private static SimpleOverlayWriter determineWriter(
            List<DrawObject> insideList, List<DrawObject> shellList) {

        DrawObject insideWriter = createWriterFromList(insideList);
        DrawObject shellWriter = createWriterFromList(shellList);

        if (!shellList.isEmpty()) {
            return new SimpleOverlayWriter(shellWriter);
        } else {

            if (!insideList.isEmpty()) {
                return new SimpleOverlayWriter(insideWriter);
            } else {
                // Then there is no object-mask
                // We should not get here at the moment, as it is impossible to disable showInside
                return new SimpleOverlayWriter(new Nothing());
            }
        }
    }

    public boolean isShowMidpoint() {
        return showMidpoint;
    }

    public void setShowMidpoint(boolean showMidpoint) {
        this.showMidpoint = showMidpoint;
    }

    public boolean isShowOrientationLine() {
        return showOrientationLine;
    }

    public void setShowOrientationLine(boolean showOrientationLine) {
        this.showOrientationLine = showOrientationLine;
    }

    public boolean isShowThickBorder() {
        return showThickBorder;
    }

    public void setShowThickBorder(boolean showThickBorder) {
        this.showThickBorder = showThickBorder;
    }

    public boolean isShowSolid() {
        return showSolid;
    }

    public void setShowSolid(boolean showSolid) {
        this.showSolid = showSolid;
    }

    public boolean isShowInside() {
        return showInside;
    }

    public void setShowInside(boolean showInside) {
        this.showInside = showInside;
    }

    public boolean isShowBoundingBox() {
        return showBoundingBox;
    }

    public void setShowBoundingBox(boolean showBoundingBox) {
        this.showBoundingBox = showBoundingBox;
    }

    public boolean isShowShell() {
        return showShell;
    }

    public void setShowShell(boolean showShell) {
        this.showShell = showShell;
    }
}

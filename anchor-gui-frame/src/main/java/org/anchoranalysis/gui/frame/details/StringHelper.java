/*-
 * #%L
 * anchor-gui-frame
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

package org.anchoranalysis.gui.frame.details;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import org.anchoranalysis.gui.frame.details.canvas.InternalFrameCanvas;
import org.anchoranalysis.gui.image.frame.SliderState;
import org.anchoranalysis.image.extent.Resolution;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;

class StringHelper {

    // optional decimal places 0.##
    private static DecimalFormat resFormat = new DecimalFormat("0");

    private SliderState sliderState;
    private InternalFrameCanvas internalFrameCanvas;
    private List<GenerateExtraDetail> listExtra = new ArrayList<>();

    public StringHelper(InternalFrameCanvas internalFrameCanvas, SliderState sliderState) {
        super();
        this.internalFrameCanvas = internalFrameCanvas;
        this.sliderState = sliderState;
    }

    // Detail generator
    public boolean addAdditionalDetails(GenerateExtraDetail arg0) {
        return listExtra.add(arg0);
    }

    public static String formatUnits(double val) {

        if (val < 1e-6) {
            return String.format("%snm", resFormat.format(val * 1e9));
        } else if (val < 1e-3) {
            // mu symbol
            return String.format("%s%sm", resFormat.format(val * 1e6), "\u00B5");
        } else if (val < 1) {
            return String.format("%smm", resFormat.format(val * 1e3));
        } else {
            return String.format("%sm", resFormat.format(val));
        }
    }

    public String resolution() {
        return internalFrameCanvas.getResolution().map(StringHelper::resolutionAsString).orElse("res=None");
    }
    
    private static String resolutionAsString(Resolution resolution) {
        if (resolution.x() == resolution.y()) {
            return String.format("resXY=%s resZ=%s", formatUnits(resolution.x()), formatUnits(resolution.z()));
        } else {
            return String.format(
                    "resX=%s resY=%s resZ=%s)",
                    formatUnits(resolution.x()), formatUnits(resolution.y()), formatUnits(resolution.z()));
        }
    }

    public String position(int x, int y) {
        return String.format("(%3d,%3d)", x, y);
    }

    public String zoom() {
        int zoomPercentage = internalFrameCanvas.getZoomScale().asPercentage();
        return String.format("Zoom=%4d%s", zoomPercentage, "%");
    }

    public String dataType() {
        return internalFrameCanvas
                .associatedDataType()
                .map(VoxelDataType::toString)
                .orElse("indeterminable");
    }

    public String extraString() {
        StringBuilder sb = new StringBuilder();
        for (GenerateExtraDetail extra : listExtra) {
            sb.append(extra.extraDetail(sliderState.getIndex()));
            sb.append(" ");
        }
        return sb.toString();
    }
}

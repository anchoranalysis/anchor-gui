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

package org.anchoranalysis.gui.videostats.internalframe.markstorgb.markdisplay;

import org.anchoranalysis.gui.marks.MarkDisplaySettings;
import org.anchoranalysis.gui.property.PropertyValueChangeEvent;
import org.anchoranalysis.gui.property.PropertyValueChangeListener;
import org.anchoranalysis.gui.propertyvalue.PropertyValueChangeListenerList;
import org.anchoranalysis.gui.videostats.action.changemarkdisplay.IChangeMarkDisplaySendable;
import org.anchoranalysis.image.core.object.properties.ObjectWithProperties;
import org.anchoranalysis.image.core.stack.RGBStack;
import org.anchoranalysis.image.io.bean.object.draw.IfElse;
import org.anchoranalysis.overlay.writer.DrawOverlay;

public class MarkDisplaySettingsWrapper implements IChangeMarkDisplaySendable {

    private PropertyValueChangeListenerList<MarkDisplaySettings> listListeners =
            new PropertyValueChangeListenerList<>();

    private MarkDisplaySettings markDisplaySettings;

    private IfElse.Condition idMatchCondition;

    // We assume the idMatchCondition can never be obtained
    public MarkDisplaySettingsWrapper(MarkDisplaySettings markDisplaySettings) {
        this(
                markDisplaySettings,
                (ObjectWithProperties object, RGBStack stack, int id) -> false // Always false
                );
    }

    public MarkDisplaySettingsWrapper(
            MarkDisplaySettings markDisplaySettings, IfElse.Condition idMatchCondition) {
        super();
        this.markDisplaySettings = markDisplaySettings;
        this.idMatchCondition = idMatchCondition;
    }

    @Override
    public void setIncludeBoundingBox(boolean b) {

        markDisplaySettings.setShowBoundingBox(b);
        triggerMarkDisplayChanged();
    }

    @Override
    public void setShowShell(boolean showShell) {

        markDisplaySettings.setShowShell(showShell);
        triggerMarkDisplayChanged();
    }

    @Override
    public void setShowMidpoint(boolean show) {
        markDisplaySettings.setShowMidpoint(show);
        triggerMarkDisplayChanged();
    }

    @Override
    public void setShowOrientationLine(boolean show) {
        markDisplaySettings.setShowOrientationLine(show);
        triggerMarkDisplayChanged();
    }

    @Override
    public void setShowThickBorder(boolean show) {
        markDisplaySettings.setShowThickBorder(show);
        triggerMarkDisplayChanged();
    }

    @Override
    public void setShowInside(boolean show) {
        markDisplaySettings.setShowInside(show);
        triggerMarkDisplayChanged();
    }

    @Override
    public void setShowSolid(boolean show) {
        markDisplaySettings.setShowSolid(show);
        triggerMarkDisplayChanged();
    }

    private void triggerMarkDisplayChanged() {

        for (PropertyValueChangeListener<MarkDisplaySettings> listener : listListeners) {
            listener.propertyValueChanged(
                    new PropertyValueChangeEvent<>(this, markDisplaySettings, false));
        }
    }

    public DrawOverlay createObjectDrawer() {
        return markDisplaySettings.createConditionalObjectDrawer(idMatchCondition);
    }

    public void addChangeListener(PropertyValueChangeListener<MarkDisplaySettings> listener) {
        listListeners.add(listener);
    }

    public void removeChangeListener(PropertyValueChangeListener<MarkDisplaySettings> listener) {
        listListeners.remove(listener);
    }

    public MarkDisplaySettings getMarkDisplaySettings() {
        return markDisplaySettings;
    }
}

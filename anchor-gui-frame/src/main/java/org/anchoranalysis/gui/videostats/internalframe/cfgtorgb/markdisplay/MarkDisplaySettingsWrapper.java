/* (C)2020 */
package org.anchoranalysis.gui.videostats.internalframe.cfgtorgb.markdisplay;

import org.anchoranalysis.anchor.overlay.writer.DrawOverlay;
import org.anchoranalysis.core.property.change.PropertyValueChangeEvent;
import org.anchoranalysis.core.property.change.PropertyValueChangeListener;
import org.anchoranalysis.gui.mark.MarkDisplaySettings;
import org.anchoranalysis.gui.propertyvalue.PropertyValueChangeListenerList;
import org.anchoranalysis.gui.videostats.action.changemarkdisplay.IChangeMarkDisplaySendable;
import org.anchoranalysis.image.object.properties.ObjectWithProperties;
import org.anchoranalysis.image.stack.rgb.RGBStack;
import org.anchoranalysis.io.bean.object.writer.IfElse;

public class MarkDisplaySettingsWrapper implements IChangeMarkDisplaySendable {

    private PropertyValueChangeListenerList<MarkDisplaySettings> listListeners =
            new PropertyValueChangeListenerList<>();

    private MarkDisplaySettings markDisplaySettings;

    private IfElse.Condition idMatchCondition;

    // We assume the idMatchCondition can never be obtained
    public MarkDisplaySettingsWrapper(MarkDisplaySettings markDisplaySettings) {
        this(
                markDisplaySettings,
                (ObjectWithProperties mask, RGBStack stack, int id) -> false // Always false
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
        updateMaskWriter();
    }

    @Override
    public void setShowShell(boolean showShell) {

        markDisplaySettings.setShowShell(showShell);
        updateMaskWriter();
    }

    @Override
    public void setShowMidpoint(boolean show) {
        markDisplaySettings.setShowMidpoint(show);
        updateMaskWriter();
    }

    @Override
    public void setShowOrientationLine(boolean show) {
        markDisplaySettings.setShowOrientationLine(show);
        updateMaskWriter();
    }

    @Override
    public void setShowThickBorder(boolean show) {
        markDisplaySettings.setShowThickBorder(show);
        updateMaskWriter();
    }

    @Override
    public void setShowInside(boolean show) {
        markDisplaySettings.setShowInside(show);
        updateMaskWriter();
    }

    @Override
    public void setShowSolid(boolean show) {
        markDisplaySettings.setShowSolid(show);
        updateMaskWriter();
    }

    private void updateMaskWriter() {

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

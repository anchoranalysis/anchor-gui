/* (C)2020 */
package org.anchoranalysis.gui.videostats.action.changemarkdisplay;

public interface IChangeMarkDisplaySendable {

    void setIncludeBoundingBox(boolean includeBoundingBox);

    void setShowShell(boolean showShell);

    void setShowInside(boolean show);

    void setShowMidpoint(boolean show);

    void setShowOrientationLine(boolean show);

    void setShowThickBorder(boolean show);

    void setShowSolid(boolean show);
}

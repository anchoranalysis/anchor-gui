/* (C)2020 */
package org.anchoranalysis.gui.annotation.state;

import java.awt.Color;

public class AnnotationSummary {

    private boolean existsFinished = false;

    /** A short string used to describe the state of the annotation succinctly */
    private String shortDescription = "";

    /** A color associated with the annotation to be displayed in the GUI */
    private Color color;

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public boolean isExistsFinished() {
        return existsFinished;
    }

    public void setExistsFinished(boolean existsFinished) {
        this.existsFinished = existsFinished;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}

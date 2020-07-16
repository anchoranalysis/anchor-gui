/* (C)2020 */
package org.anchoranalysis.gui.videostats.internalframe.annotator;

public class SaveMonitor {

    // If a change has occurred to cfgAccepted since the lastSave
    private boolean changedSinceLastSave = false;

    public boolean isChangedSinceLastSave() {
        return changedSinceLastSave;
    }

    public void markAsChanged() {
        changedSinceLastSave = true;
    }

    public void markAsSaved() {
        changedSinceLastSave = false;
    }
}

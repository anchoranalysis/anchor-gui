/* (C)2020 */
package org.anchoranalysis.gui.videostats.threading;

import javax.swing.SwingWorker;

public abstract class InteractiveWorker<T, V> extends SwingWorker<T, V> {

    public InteractiveWorker() {
        super();
    }

    public void setProgressExt(int val) {
        setProgress(val);
    }
}

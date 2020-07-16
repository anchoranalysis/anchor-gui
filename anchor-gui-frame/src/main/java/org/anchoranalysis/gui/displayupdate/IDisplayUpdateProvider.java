/* (C)2020 */
package org.anchoranalysis.gui.displayupdate;

import javax.swing.event.ChangeListener;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.gui.frame.display.DisplayUpdate;

public interface IDisplayUpdateProvider {

    DisplayUpdate get() throws GetOperationFailedException;

    void addChangeListener(ChangeListener cl);
}

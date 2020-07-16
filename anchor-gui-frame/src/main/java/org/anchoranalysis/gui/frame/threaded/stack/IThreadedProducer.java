/* (C)2020 */
package org.anchoranalysis.gui.frame.threaded.stack;

import org.anchoranalysis.core.index.IIndexGettableSettable;
import org.anchoranalysis.gui.displayupdate.IDisplayUpdateRememberStack;

public interface IThreadedProducer {

    // How it provides stacks to other applications (the output)
    IDisplayUpdateRememberStack getStackProvider();

    // How it is updated with indexes from other classes (the input control mechanism)
    IIndexGettableSettable getIndexGettableSettable();

    void dispose();
}

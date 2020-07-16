/* (C)2020 */
package org.anchoranalysis.gui.container;

import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;

public interface ContainerGetter<T> {

    BoundedIndexContainer<T> getCntr() throws GetOperationFailedException;
}

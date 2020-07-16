/* (C)2020 */
package org.anchoranalysis.gui.videostats.internalframe.cfgtorgb;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.Operation;
import org.anchoranalysis.gui.videostats.dropdown.common.NRGBackground;

public class MultiInput<T> {

    private String name;
    private NRGBackground nrgBackground;
    private Operation<T, OperationFailedException> associatedObjects;

    public MultiInput(
            String name,
            NRGBackground nrgBackground,
            Operation<T, OperationFailedException> associatedObjects) {
        this.name = name;
        this.nrgBackground = nrgBackground;
        this.associatedObjects = associatedObjects;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Operation<T, OperationFailedException> getAssociatedObjects() {
        return associatedObjects;
    }

    public NRGBackground getNrgBackground() {
        return nrgBackground;
    }
}

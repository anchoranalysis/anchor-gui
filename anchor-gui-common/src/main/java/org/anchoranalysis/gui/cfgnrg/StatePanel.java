/* (C)2020 */
package org.anchoranalysis.gui.cfgnrg;

import java.util.Optional;
import javax.swing.JPanel;
import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;
import org.anchoranalysis.core.index.IntArray;
import org.anchoranalysis.core.property.IPropertyValueReceivable;
import org.anchoranalysis.core.property.IPropertyValueSendable;

public abstract class StatePanel<T> {

    public abstract JPanel getPanel();

    public abstract void updateState(T state) throws StatePanelUpdateException;

    public abstract Optional<IPropertyValueSendable<IntArray>> getSelectMarksSendable();

    public abstract Optional<IPropertyValueReceivable<IntArray>> getSelectMarksReceivable();

    public abstract Optional<IPropertyValueReceivable<OverlayCollection>>
            getSelectOverlayCollectionReceivable();

    public abstract Optional<IPropertyValueReceivable<Integer>> getSelectIndexReceivable();
}

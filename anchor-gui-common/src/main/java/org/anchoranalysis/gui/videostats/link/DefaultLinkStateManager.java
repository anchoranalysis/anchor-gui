/* (C)2020 */
package org.anchoranalysis.gui.videostats.link;

import java.util.HashMap;
import org.anchoranalysis.anchor.overlay.collection.OverlayCollection;
import org.anchoranalysis.core.functional.function.FunctionWithException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.index.IntArray;
import org.anchoranalysis.core.property.IPropertyValueSendable;
import org.anchoranalysis.gui.image.OverlayCollectionWithImgStack;
import org.anchoranalysis.image.stack.DisplayStack;

public class DefaultLinkStateManager {

    private DefaultLinkState delegate;

    @SuppressWarnings("rawtypes")
    private HashMap<String, IPropertyValueSendable> mapSendableProperties = new HashMap<>();

    public DefaultLinkStateManager(DefaultLinkState state) {
        this.delegate = state;

        this.<Integer>putMap(
                LinkFramesUniqueID.SLICE_NUM, (value, adjusting) -> delegate.setSliceNum(value));

        this.<Integer>putMap(
                LinkFramesUniqueID.FRAME_INDEX,
                (value, adjusting) -> delegate.setFrameIndex(value));

        this.<IntArray>putMap(
                LinkFramesUniqueID.MARK_INDICES,
                (value, adjusting) -> delegate.setObjectIDs(value.getArr()));

        this.<OverlayCollection>putMap(
                LinkFramesUniqueID.OVERLAYS,
                (value, adjusting) -> delegate.setOverlayCollection(value));

        this.<OverlayCollectionWithImgStack>putMap(
                LinkFramesUniqueID.OVERLAYS_WITH_STACK,
                (value, adjusting) -> delegate.setCfgWithStack(value));
    }

    @SuppressWarnings("unchecked")
    public IPropertyValueSendable<Integer> getSendableSliceNum() {
        return (IPropertyValueSendable<Integer>) getSendable(LinkFramesUniqueID.SLICE_NUM);
    }

    @SuppressWarnings("rawtypes")
    public IPropertyValueSendable getSendable(String key) {
        return mapSendableProperties.get(key);
    }

    public DefaultLinkState getState() {
        return delegate;
    }

    public void setBackground(
            FunctionWithException<Integer, DisplayStack, GetOperationFailedException> background) {
        getState().setBackground(background);
    }

    public void setSliceNum(int sliceNum) {
        getState().setSliceNum(sliceNum);
    }

    /**
     * Provides a copy of the current state (if you change it, it won't affect the global defaults )
     */
    public DefaultLinkState copy() {
        return delegate.duplicate();
    }

    /** Provides a copy of the default module state with a changed background */
    public DefaultLinkState copyChangeBackground(
            FunctionWithException<Integer, DisplayStack, GetOperationFailedException> background) {
        DefaultLinkState dup = delegate.duplicate();
        dup.setBackground(background);
        return dup;
    }

    private <T> void putMap(String id, IPropertyValueSendable<T> sendable) {
        mapSendableProperties.put(id, sendable);
    }
}

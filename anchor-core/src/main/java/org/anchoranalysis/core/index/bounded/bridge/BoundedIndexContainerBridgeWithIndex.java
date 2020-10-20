package org.anchoranalysis.core.index.bounded.bridge;

import org.anchoranalysis.core.index.BridgeElementWithIndex;
import org.anchoranalysis.core.index.bounded.BoundedIndexContainer;

/**
 * Bridges calls from hidden-type to external-type. Uses an IObjectBridgeIndex for the bridging.
 *
 * <p>See {@link
 * org.anchoranalysis.core.index.bounded.bridge.BoundedIndexContainerBridgeWithoutIndex}
 *
 * @author Owen Feehan
 * @param <H> hidden-type (type passed to the delegate)
 * @param <S> external-type (type exposed in an interface from this class)
 * @param <E> exception that can be thrown during briging
 */
public class BoundedIndexContainerBridgeWithIndex<H, S, E extends Exception>
        extends BoundedIndexContainerBridge<H, S, E> {

    private BridgeElementWithIndex<H, S, E> bridge;

    public BoundedIndexContainerBridgeWithIndex(
            BoundedIndexContainer<H> source, BridgeElementWithIndex<H, S, E> bridge) {
        super(source);
        this.bridge = bridge;
    }

    @Override
    protected S bridge(int index, H internalState) throws E {
        return bridge.bridgeElement(index, internalState);
    }
}

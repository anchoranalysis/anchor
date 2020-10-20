package org.anchoranalysis.core.index.bounded.bridge;

import org.anchoranalysis.core.functional.checked.CheckedFunction;
import org.anchoranalysis.core.index.bounded.BoundedIndexContainer;

/**
 * Bridges calls from hidden-type to external-type. Uses an IObjectBridge for the bridging.
 *
 * <p>See {@link
 * org.anchoranalysis.core.index.bounded.bridge.BoundedIndexContainerBridgeWithIndex}
 *
 * @author Owen Feehan
 * @param <H> hidden-type (type passed to the delegate)
 * @param <S> external-type (type exposed in an interface from this class)
 * @param <E> exception that can be thrown during briging
 */
public class BoundedIndexContainerBridgeWithoutIndex<H, S, E extends Exception>
        extends BoundedIndexContainerBridge<H, S, E> {

    private CheckedFunction<H, S, E> bridge;

    public BoundedIndexContainerBridgeWithoutIndex(
            BoundedIndexContainer<H> source, CheckedFunction<H, S, E> bridge) {
        super(source);
        this.bridge = bridge;
    }

    @Override
    protected S bridge(int index, H internalState) throws E {
        return bridge.apply(internalState);
    }
}

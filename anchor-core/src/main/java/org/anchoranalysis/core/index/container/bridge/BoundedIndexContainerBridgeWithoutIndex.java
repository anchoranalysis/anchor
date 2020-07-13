package org.anchoranalysis.core.index.container.bridge;

import org.anchoranalysis.core.functional.function.FunctionWithException;
import org.anchoranalysis.core.index.container.BoundedIndexContainer;

/**
 * Bridges calls from hidden-type to external-type. Uses an IObjectBridge for the bridging.
 * 
 * See {@link org.anchoranalysis.core.index.container.bridge.BoundedIndexContainerBridgeWithIndex}
 * 
 * @author Owen Feehan
 *
 * @param <H> hidden-type (type passed to the delegate)
 * @param <S> external-type (type exposed in an interface from this class)
 * @param <E> exception that can be thrown during briging
 */
public class BoundedIndexContainerBridgeWithoutIndex<H,S,E extends Exception> extends BoundedIndexContainerBridge<H,S,E> {
	
	private FunctionWithException<H,S,E> bridge;
	
	public BoundedIndexContainerBridgeWithoutIndex(
		BoundedIndexContainer<H> source,
		FunctionWithException<H,S,E> bridge
	) {
		super(source);
		this.bridge = bridge;
	}
	
	@Override
	protected S bridge(int index, H internalState) throws E {
		return bridge.apply(internalState);
	}
}

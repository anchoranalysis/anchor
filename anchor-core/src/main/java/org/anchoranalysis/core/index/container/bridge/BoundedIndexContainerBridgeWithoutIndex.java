package org.anchoranalysis.core.index.container.bridge;



import org.anchoranalysis.core.functional.FunctionWithException;
import org.anchoranalysis.core.index.container.IBoundedIndexContainer;

/**
 * Bridges calls from hidden-type to external-type. Uses an IObjectBridge for the bridging.
 * 
 * See {@link org.anchoranalysis.core.index.container.bridge.BoundedIndexContainerBridgeWithIndex}
 * 
 * @author Owen Feehan
 *
 * @param <H> hidden-type (type passed to the delegate)
 * @param <S> external-type (type exposed in an interface from this class)
 */
public class BoundedIndexContainerBridgeWithoutIndex<H,S> extends BoundedIndexContainerBridge<H, S> {
	
	private FunctionWithException<H, S, ? extends Exception> bridge;
	
	public BoundedIndexContainerBridgeWithoutIndex(
		IBoundedIndexContainer<H> source,
		FunctionWithException<H,S, ? extends Exception> bridge
	) {
		super(source);
		this.bridge = bridge;
	}
	
	@Override
	protected S bridge(int index, H internalState) throws Exception {
		return bridge.apply(internalState);
	}
}

package org.anchoranalysis.io.generator;

import org.anchoranalysis.core.functional.function.FunctionWithException;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * Allows us to call an IterableGenerator<ExternalType> as if it was an IterableGenerator<InternalType>
 *   using an interface function to connect the two
 * 
 * @author Owen Feehan
 *
 * @param <S> generator-type
 * @param <T> exposed-iterator type
 * @param <V> hidden-iterator-type
 */
public class IterableObjectGeneratorBridge<S,T,V> implements IterableObjectGenerator<T,S> {

	private T element;
	
	private IterableObjectGenerator<V,S> internalGenerator;
	
	private FunctionWithException<T,V,? extends Throwable> elementBridge;
	
	public IterableObjectGeneratorBridge(IterableObjectGenerator<V,S> internalGenerator, FunctionWithException<T,V,? extends Throwable> elementBridge) {
		super();
		this.internalGenerator = internalGenerator;
		this.elementBridge = elementBridge;
	}

	@Override
	public T getIterableElement() {
		return this.element;
	}

	@Override
	public void setIterableElement(T element) throws SetOperationFailedException {
		this.element = element;
		try {
			V bridgedElement = elementBridge.apply(element); 
			internalGenerator.setIterableElement( bridgedElement );
		} catch (Exception e) {
			throw new SetOperationFailedException(e);
		}
	}

	@Override
	public ObjectGenerator<S> getGenerator() {
		return internalGenerator.getGenerator();
	}

	@Override
	public void start() throws OutputWriteFailedException {
		internalGenerator.start();
	}


	@Override
	public void end() throws OutputWriteFailedException {
		internalGenerator.end();
	}

}

package org.anchoranalysis.io.generator;



import java.util.Optional;

import org.anchoranalysis.core.functional.FunctionWithException;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.io.manifest.file.FileType;
import org.anchoranalysis.io.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.namestyle.OutputNameStyle;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.bound.BoundOutputManager;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

 

/**
 * Allows us to call an IterableGenerator<S> as if it was an IterableGenerator<T>
 
 * @author Owen Feehan
 *
 * @param <S> source-type
 * @param <T> destination-type
 */
public class IterableGeneratorBridge<S,T> extends Generator implements IterableGenerator<S> {

	private S element;
	
	private IterableGenerator<T> delegate;
	
	private FunctionWithException<S,T,?> bridge;
	
	public IterableGeneratorBridge(IterableGenerator<T> delegate, FunctionWithException<S,T,?> bridge) {
		super();
		this.delegate = delegate;
		this.bridge = bridge;
	}

	@Override
	public S getIterableElement() {
		return this.element;
	}

	@Override
	public void setIterableElement(S element) throws SetOperationFailedException {
		this.element = element;
		try {
			delegate.setIterableElement( bridge.apply(element) );
		} catch (Throwable e) {
			throw new SetOperationFailedException(e);
		}
	}

	@Override
	public Generator getGenerator() {
		return delegate.getGenerator();
	}

	@Override
	public void start() throws OutputWriteFailedException {
		delegate.start();
	}


	@Override
	public void end() throws OutputWriteFailedException {
		delegate.end();
	}

	@Override
	public void write(OutputNameStyle outputNameStyle, BoundOutputManager outputManager)
			throws OutputWriteFailedException {
		delegate.getGenerator().write(outputNameStyle, outputManager);
	}

	@Override
	public int write(IndexableOutputNameStyle outputNameStyle, String index, BoundOutputManager outputManager)
			throws OutputWriteFailedException {
		return delegate.getGenerator().write(outputNameStyle, index, outputManager );
	}

	@Override
	public Optional<FileType[]> getFileTypes(OutputWriteSettings outputWriteSettings) {
		return delegate.getGenerator().getFileTypes(outputWriteSettings);
	}
}

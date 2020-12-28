package org.anchoranalysis.io.input;

import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.core.log.error.ErrorReporter;
import lombok.AllArgsConstructor;

/**
 * A base class for {@link InputFromManager}-implementing classes that delegate to another.
 * 
 * @author Owen Feehan
 *
 */
@AllArgsConstructor
public abstract class InputFromManagerDelegate<T extends InputFromManager> implements InputFromManager {

    private final T delegate;
    
    @Override
    public String name() {
        return delegate.name();
    }

    @Override
    public Optional<Path> pathForBinding() {
        return delegate.pathForBinding();
    }

    @Override
    public void close(ErrorReporter errorReporter) {
        delegate.close(errorReporter);
    }

    protected T getDelegate() {
        return delegate;
    }
}

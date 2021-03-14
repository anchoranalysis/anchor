/*-
 * #%L
 * anchor-io-input
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
package org.anchoranalysis.io.input;

import java.nio.file.Path;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.log.error.ErrorReporter;

/**
 * A base class for {@link InputFromManager}-implementing classes that delegate to another.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public abstract class InputFromManagerDelegate<T extends InputFromManager>
        implements InputFromManager {

    private final T delegate;

    @Override
    public String identifier() {
        return delegate.identifier();
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

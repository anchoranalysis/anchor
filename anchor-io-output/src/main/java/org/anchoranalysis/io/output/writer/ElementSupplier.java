package org.anchoranalysis.io.output.writer;

import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * Creates or gets a element to output files together with a {@link ElementWriter}.
 * 
 * <p>This exists primarily facilitate lazy creation of an element
 * (only if an output is enabled by the rules).
 *
 * @author Owen Feehan
 * @param <T> the type of element to be written
 */
@FunctionalInterface
public interface ElementSupplier<T> {

    /**
     * Gets/creates the element to be written.
     *
     * @return the element to be written
     * @throws OutputWriteFailedException
     */
    T get() throws OutputWriteFailedException;
}

package org.anchoranalysis.io.output.writer;

import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * Creates a writable-item to be outputted
 *
 * @author Owen Feehan
 * @param <T> writable-item
 */
@FunctionalInterface
public interface GenerateWritableItem<T extends WritableItem> {

    T generate() throws OutputWriteFailedException;
}

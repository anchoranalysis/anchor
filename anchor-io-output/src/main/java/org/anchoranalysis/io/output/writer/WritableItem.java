/* (C)2020 */
package org.anchoranalysis.io.output.writer;

import org.anchoranalysis.io.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.namestyle.OutputNameStyle;
import org.anchoranalysis.io.output.bound.BoundOutputManager;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * An item that can be outputted via a write() method
 *
 * @author Owen Feehan
 */
public interface WritableItem {

    /**
     * Writes a non-indexable output (an output that isn't part of a collection of other similar
     * items)
     *
     * @param outputNameStyle
     * @param outputManager
     * @throws OutputWriteFailedException
     */
    public abstract void write(OutputNameStyle outputNameStyle, BoundOutputManager outputManager)
            throws OutputWriteFailedException;

    /**
     * Writes an indexable output (many outputs of the same type, uniquely identified by an index)
     *
     * @param outputNameStyle
     * @param index
     * @param outputManager
     * @return
     * @throws OutputWriteFailedException
     */
    public abstract int write(
            IndexableOutputNameStyle outputNameStyle,
            String index,
            BoundOutputManager outputManager)
            throws OutputWriteFailedException;
}

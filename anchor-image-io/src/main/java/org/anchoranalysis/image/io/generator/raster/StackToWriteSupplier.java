package org.anchoranalysis.image.io.generator.raster;

import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * Supplies an image-stack to be written in a generator
 * 
 * @author Owen Feehan
 *
 */
@FunctionalInterface
public interface StackToWriteSupplier {

    Stack get() throws OutputWriteFailedException;
}

/*-
 * #%L
 * anchor-image
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
package org.anchoranalysis.image.voxel.iterator.predicate;

import java.util.function.Predicate;
import org.anchoranalysis.image.convert.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.iterator.predicate.buffer.PredicateBufferBinary;
import org.anchoranalysis.image.voxel.iterator.process.buffer.ProcessBufferBinary;

/**
 * Like {@link Predicate} but takes two bytes as argument.
 *
 * @author Owen Feehan
 */
@FunctionalInterface
public interface PredicateTwoBytes {

    /**
     * Do the combination of two arguments fulfill the predicate?
     *
     * @param first first argument
     * @param second second argument
     * @return true if the arguments pass the predicate
     */
    boolean test(byte first, byte second);

    /**
     * Derives a predicate with a different interface for operating on {@link UnsignedByteBuffer}.
     *
     * @return the derived predicate.
     */
    default PredicateBufferBinary<UnsignedByteBuffer> deriveUnsignedBytePredicate() {
        return (point, buffer1, buffer2, offset1, offset2) ->
                test(buffer1.getRaw(offset1), buffer2.getRaw(offset2));
    }

    /**
     * Derives a processor with a different interface for operating on {@link UnsignedByteBuffer}.
     *
     * @return the derived processor.
     */
    default ProcessBufferBinary<UnsignedByteBuffer,UnsignedByteBuffer> deriveUnsignedByteProcessor() {
        return (point, buffer1, buffer2, offset1, offset2) ->
                test(buffer1.getRaw(offset1), buffer2.getRaw(offset2));
    }
}

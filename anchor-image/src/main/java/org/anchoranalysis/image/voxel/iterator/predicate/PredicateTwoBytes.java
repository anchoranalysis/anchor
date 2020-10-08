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
    default ProcessBufferBinary<UnsignedByteBuffer> deriveUnsignedByteProcessor() {
        return (point, buffer1, buffer2, offset1, offset2) ->
                test(buffer1.getRaw(offset1), buffer2.getRaw(offset2));
    }
}

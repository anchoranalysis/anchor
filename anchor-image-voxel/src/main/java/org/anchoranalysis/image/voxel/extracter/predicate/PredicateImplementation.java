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
package org.anchoranalysis.image.voxel.extracter.predicate;

import java.util.function.BiConsumer;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.arithmetic.Counter;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.extent.box.BoundingBox;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.iterator.IterateVoxelsAll;
import org.anchoranalysis.image.voxel.iterator.IterateVoxelsBoundingBox;
import org.anchoranalysis.image.voxel.iterator.IterateVoxelsObjectMask;
import org.anchoranalysis.image.voxel.iterator.process.voxelbuffer.ProcessVoxelBufferUnary;

/**
 * Implementation of {@link VoxelsPredicate} for a particular {@link Voxels}.
 *
 * @author Owen Feehan
 * @param <T> buffer-type
 */
@RequiredArgsConstructor
public class PredicateImplementation<T> implements VoxelsPredicate {

    // START REQUIRED ARGUMENTS
    /** The voxels on which the predicate is based. */
    private final Voxels<T> voxels;
    // END REQUIRED ARGUMENTS

    // START REQUIRED ARGUMENTS
    /** Checks if the current value of a buffer matches a predicate */
    private final Predicate<T> predicate;
    // END REQUIRED ARGUMENTS

    @Override
    public boolean anyExists() {
        return IterateVoxelsAll.anyPredicateMatch(voxels, predicate);
    }

    @Override
    public int count() {
        return countPredicate(IterateVoxelsAll::withVoxelBuffer);
    }

    @Override
    public int countForObject(ObjectMask object) {
        return countPredicate( (voxelsToProcess,processor) ->
            IterateVoxelsObjectMask.withVoxelBuffer(object,voxelsToProcess,processor) );
    }

    @Override
    public boolean higherCountExistsThan(int threshold) {
        return predicateMatchWithCounter(threshold+1);
    }

    @Override
    public boolean lowerCountExistsThan(int threshold) {
        // The return value is the complement of whether we exited early or not
        return !predicateMatchWithCounter(threshold);
    }

    @Override
    public ObjectMask deriveObject(BoundingBox box) {
        
        ObjectMask object = new ObjectMask(box);

        byte outOn = object.binaryValuesByte().getOnByte();

        ReadableTuple3i shiftForMask = Point3i.immutableScale(object.boundingBox().cornerMin(), -1);
        IterateVoxelsBoundingBox.withTwoMixedBuffers(box, shiftForMask, voxels, object.voxels(), (point, buffer1, buffer2, offset1, offset2) -> {
            buffer1.position(offset1);

            if (predicate.test(buffer1.buffer())) {
                buffer2.putRaw(offset2, outOn);
            }
        });
        
        return object;
    }

    
    private boolean predicateMatchWithCounter(int threshold) {
        
        Counter counter = new Counter();
        
        return IterateVoxelsAll.anyPredicateMatch(voxels, buffer -> {
            if (predicate.test(buffer)) {
                counter.increment();
                // We've reached the threshold, positive outcome
                return counter.getCount()==threshold;
            } else {
                return false;
            }
        });
    }
    
    private int countPredicate(BiConsumer<Voxels<T>, ProcessVoxelBufferUnary<T>> consumer) {

        // We use an object on the heap, as the lambda cannot reference a variable on the stack.
        Counter counter = new Counter();
        
        consumer.accept(voxels, (buffer, offset) -> {
            buffer.position(offset);
            if (predicate.test(buffer.buffer())) {
                counter.increment();
            }            
        });
        
        return counter.getCount();
    }
}

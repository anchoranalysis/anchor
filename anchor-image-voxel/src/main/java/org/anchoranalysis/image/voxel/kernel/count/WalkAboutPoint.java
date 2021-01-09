package org.anchoranalysis.image.voxel.kernel.count;

import java.util.Optional;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.kernel.KernelApplicationParameters;
import org.anchoranalysis.image.voxel.kernel.KernelPointCursor;
import org.anchoranalysis.spatial.point.Point3i;
import lombok.AllArgsConstructor;

/**
 * Walks in X, Y and Z directions from a point, executing a {@link Runnable} if the neighbour satisfies conditions.
 * 
 * <p>The conditions are:
 * <ul>
 * <li>An associated buffer for the point must have an OFF value, <i>or</i> if its outside the scene, the {@link KernelApplicationParameters} parameters must indicate off.
 * <li>An additional predicate around the point. 
 * </ul>
 * 
 * @author Owen Feehan
 *
 */
@AllArgsConstructor class WalkAboutPoint {
    
    /** The cursor used for iterating through points. */
    private final KernelPointCursor point;
    
    /** An additional predicate that must also be satisfied, as the <b>second</b> condition (see class description). */
    private final Predicate<Point3i> additionalPredicate;
            
    /** Executed whenever an neighbour satisfied the conditions. */
    private final Runnable executeWhenSatisfied;
    
    /**
     * Walks in X and Y direction, and Z direction if enabled.
     * 
     * @param buffer the buffer associated with the current slice
     * @param bufferRetriever a means of retrieving buffers for other slices, accepting a relative shift compared to current slice (e.g. -1, +1) etc.
     */
    public void walkAllDirections(UnsignedByteBuffer buffer, IntFunction<Optional<UnsignedByteBuffer>> bufferRetriever) {
        walkX(buffer);
        
        walkY(buffer);
        
        if (point.isUseZ()) {
            walkZ(bufferRetriever);
        }
    }
    
    /** Walk in the x-dimension seeing if neighbors qualify. */
    private void walkX(UnsignedByteBuffer buffer) {
        point.decrementX();
        
        increaseCounterIf(point.nonNegativeX(), () -> buffer);

        point.incrementXTwice();
        
        increaseCounterIf(point.lessThanMaxX(), () -> buffer);
        
        point.decrementX();        
    }
    
    /** Walk in the y-dimension seeing if neighbors qualify. */
    private void walkY(UnsignedByteBuffer buffer) {
        
        point.decrementY();

        increaseCounterIf(point.nonNegativeY(), () -> buffer);
        
        point.incrementYTwice();

        increaseCounterIf(point.lessThanMaxY(), () -> buffer);
        
        point.decrementY();    
    }
    
    /** Walk in the z-dimension seeing if neighbors qualify. */
    private void walkZ(IntFunction<Optional<UnsignedByteBuffer>> bufferRetriever) {
        point.decrementZ();
        increaseCounterIfSpecificZ(bufferRetriever, -1);
        point.incrementZTwice();
        increaseCounterIfSpecificZ(bufferRetriever, +1);
        point.decrementZ();
    }
    
    /** Does a neighbor voxel in <b>a specific Z-direction</b> qualify the voxel? */
    private void increaseCounterIfSpecificZ(IntFunction<Optional<UnsignedByteBuffer>> bufferRetriever, int zShift) {
        Optional<UnsignedByteBuffer> buffer = bufferRetriever.apply(zShift);
        increaseCounterIf(buffer.isPresent(), buffer::get);
    }
    
    private void increaseCounterIf(boolean condition, Supplier<UnsignedByteBuffer> buffer) {
        if (condition) {
            if (point.isBufferOff(buffer.get()) && additionalPredicate.test(point.getPoint())) {
                executeWhenSatisfied.run();
            }
        } else {
            if (point.isOutsideOffUnignored() && additionalPredicate.test(point.getPoint())) {
                executeWhenSatisfied.run();
            }
        } 
    }
}
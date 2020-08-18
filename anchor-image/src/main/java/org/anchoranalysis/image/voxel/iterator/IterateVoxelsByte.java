package org.anchoranalysis.image.voxel.iterator;

import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.function.Consumer;
import org.anchoranalysis.core.arithmetic.RunningSum;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.core.geometry.consumer.PointThreeDimensionalConsumer;
import org.anchoranalysis.image.binary.mask.Mask;
import org.anchoranalysis.image.convert.ByteConverter;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.voxel.BoundedVoxels;
import org.anchoranalysis.image.voxel.Voxels;
import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class IterateVoxelsByte {

    /**
     * Iterates through all points with a specific voxel-value reusing the {@link Point3i} in each iteration.
     * <p>
     * This means that the same {@link Point3i} instance is passed to the consumer each time
     * and is <b>not</b> newly created for each matching voxel.
     * 
     * @param voxels voxels to iterate through
     * @param equalToValue voxels match if they are equal to this value
     * @param consumer called for every matching voxel
     */
    public static void iterateEqualValuesReusePoint( Voxels<ByteBuffer> voxels, byte equalToValue, Consumer<Point3i> consumer) {
        IterateVoxels.callEachPoint(voxels, (point, buffer, offset) -> {
            if (buffer.get(offset) == equalToValue) {
                consumer.accept(point);
            }
        });
    }
    
    /**
     * Iterates through all points with a specific voxel-value
     * 
     * @param voxels voxels to iterate through
     * @param equalToValue voxels match if they are equal to this value
     * @param consumer called for every matching voxel
     */
    public static void iterateEqualValues( Voxels<ByteBuffer> voxels, byte equalToValue, PointThreeDimensionalConsumer consumer) {
        Extent extent = voxels.extent();
        extent.iterateOverZ( z->iterateEqualValuesSlice(voxels, z, equalToValue, consumer) );
    }
    
    /**
     * Iterates through all points on a slice with a specific voxel-value
     * 
     * @param voxels voxels to iterate through
     * @param sliceIndex which slice to iterate over (z coordinate)
     * @param equalToValue voxels match if they are equal to this value
     * @param consumer called for every matching voxel
     */
    public static void iterateEqualValuesSlice( Voxels<ByteBuffer> voxels, int sliceIndex, byte equalToValue, PointThreeDimensionalConsumer consumer) {
        ByteBuffer buffer = voxels.sliceBuffer(sliceIndex);

        voxels.extent().iterateOverXY( (x,y,offset) -> {
            if (buffer.get() == equalToValue) {
                consumer.accept(x, y, sliceIndex);
            }
        });
    }
    
    /**
     * Calls each voxel that is equal to a specific value until a point is found
     *
     * @param voxels the voxels to iterate over
     * @param equalToValue voxels match if they are equal to this value
     * @return the first point found in global-coordinates (newly created), or empty() if no points are equal-to.
     */
    public static Optional<Point3i> iterateUntilFirstEqual(BoundedVoxels<ByteBuffer> voxels, byte equalToValue) {

        Extent extentMask = voxels.extent();
        ReadableTuple3i corner = voxels.boundingBox().cornerMin();

        for (int z = 0; z < extentMask.z(); z++) {

            ByteBuffer bufferMask = voxels.sliceBufferLocal(z);

            for (int y = 0; y < extentMask.y(); y++) {

                for (int x = 0; x < extentMask.x(); x++) {

                    if (bufferMask.get() == equalToValue) {
                        return Optional.of(new Point3i(corner.x() + x, corner.y(), corner.z() + z));
                    }
                }
            }
        }
        return Optional.empty();
    }
    
    /**
     * Calculates the sum and count across voxels that correspond to ON voxels on a <i>mask</i>
     * <p>
     * The {@code mask} must have equal extent to {@code voxelsIntensity} 
     * 
     * @param voxelsIntensity the voxels whose intensity we wish to find the mean of (subject to {@code mask}
     * @param mask only voxels who correspond to an ON voxels in the mask are included
     * @return the running-sum
     */
    public static RunningSum calculateSumAndCount( Voxels<ByteBuffer> voxelsIntensity, Mask mask) {
        Preconditions.checkArgument( voxelsIntensity.extent().equals(mask.extent()));
        
        RunningSum running = new RunningSum();
        
        IterateVoxels.callEachPoint(voxelsIntensity, mask, (point,buffer,offset) ->
            addFromBufferToRunning(buffer, offset, running)
        );

        return running;
    }
    
    /**
     * Calculates the sum and count across voxels that correspond to ON voxels on an <i>object-mask</i>
     * 
     * @param voxelsIntensity the voxels whose intensity we wish to find the mean of (subject to {@code mask}
     * @param mask only voxels who correspond to an ON voxels in the object-mask are included
     * @return the running-sum
     */
    public static RunningSum calculateSumAndCount( Voxels<ByteBuffer> voxelsIntensity, ObjectMask object) {
        
        RunningSum running = new RunningSum();
        
        IterateVoxels.callEachPoint(voxelsIntensity, object, (point,buffer,offset) ->
            addFromBufferToRunning(buffer, offset, running)
        );

        return running;
    }
    
    private static void addFromBufferToRunning(ByteBuffer buffer, int offset, RunningSum running) {
        int intensity = ByteConverter.unsignedByteToInt(buffer.get(offset));
        running.increment(intensity, 1);
    }
}

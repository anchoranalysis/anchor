package org.anchoranalysis.image.voxel.iterator;

import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.function.Consumer;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.core.geometry.consumer.PointThreeDimensionalConsumer;
import org.anchoranalysis.image.convert.ByteConverter;
import org.anchoranalysis.image.extent.Extent;
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
        Extent extent = voxels.extent();
        
        Point3i point = new Point3i(0, 0, 0);
        for (point.setZ(0); point.z() < extent.z(); point.incrementZ()) {

            ByteBuffer buf = voxels.sliceBuffer(point.z());

            for (point.setY(0); point.y() < extent.y(); point.incrementY()) {
                for (point.setX(0); point.x() < extent.x(); point.incrementX()) {

                    int offset = extent.offsetSlice(point);
                    if (buf.get(offset) == equalToValue) {
                        consumer.accept(point);
                    }
                }
            }
        }
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
        Extent extent = voxels.extent();
        ByteBuffer buffer = voxels.sliceBuffer(sliceIndex);

        for (int y = 0; y < extent.y(); y++) {
            for (int x = 0; x < extent.x(); x++) {

                if (buffer.get() == equalToValue) {
                    consumer.accept(x, y, sliceIndex);
                }
            }
        }
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
     * Calculates the mean-value across voxels that correspond to ON voxels on an object-mask
     * <p>
     * The {@code mask} must have equal extent to {@code voxelsIntensity} 
     * 
     * @param voxelsIntensity the voxels whose intensity we wish to find the mean of (subject to {@code mask}
     * @param mask only voxels who correspond to an ON voxels in the mask are included
     * @param maskOnValue what constitutes ON voxel in the mask
     * @param meanIfZeroCount what to return if no voxels are included i.e. the mask is empty
     * @return the mean voxel-intensity or {@code meanIfZeroCount) if count is zero.
     */
    public static double calculateMean( Voxels<ByteBuffer> voxelsIntensity, Voxels<ByteBuffer> mask, byte maskOnValue, double meanIfZeroCount ) {
        Preconditions.checkArgument( voxelsIntensity.extent().equals(mask.extent()));
        
        Extent extent = voxelsIntensity.extent();
        
        double sum = 0.0;
        double count = 0;

        for (int z = 0; z < extent.z(); z++) {

            ByteBuffer bufferMask = mask.sliceBuffer(z);
            ByteBuffer bufferIntensity = voxelsIntensity.sliceBuffer(z);

            int offset = 0;
            for (int y = 0; y < extent.y(); y++) {
                for (int x = 0; x < extent.x(); x++) {

                    if (bufferMask.get(offset) == maskOnValue) {
                        int intensity = ByteConverter.unsignedByteToInt(bufferIntensity.get(offset));
                        sum += intensity;
                        count++;
                    }

                    offset++;
                }
            }
        }

        if (count == 0) {
            return meanIfZeroCount;
        }

        return sum / count;
    }
}

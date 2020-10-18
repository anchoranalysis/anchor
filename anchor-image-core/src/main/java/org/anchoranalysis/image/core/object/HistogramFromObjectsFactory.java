package org.anchoranalysis.image.core.object;

import java.util.Optional;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.core.mask.Mask;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.VoxelsWrapper;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.datatype.IncorrectVoxelTypeException;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedShortVoxelType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.object.ObjectCollection;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.image.voxel.object.factory.ObjectCollectionFactory;
import org.anchoranalysis.image.voxel.statistics.HistogramFactory;
import org.anchoranalysis.math.histogram.Histogram;
import org.anchoranalysis.spatial.extent.Extent;
import org.anchoranalysis.spatial.point.ReadableTuple3i;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class HistogramFromObjectsFactory {

    public static Histogram create(VoxelsWrapper inputBuffer, Optional<ObjectMask> object) {

        if (!isDataTypeSupported(inputBuffer.getVoxelDataType())) {
            throw new IncorrectVoxelTypeException(
                    String.format("Data type %s is not supported", inputBuffer.getVoxelDataType()));
        }

        if (object.isPresent()) {
            return createWithMask(inputBuffer.any(), object.get());
        } else {
            return HistogramFactory.create(inputBuffer);
        }
    }
    
    public static Histogram createHistogramIgnoreZero(
            Channel channel, ObjectMask object, boolean ignoreZero) {
        Histogram histogram = create(channel, object);
        if (ignoreZero) {
            histogram.zeroValue(0);
        }
        return histogram;
    }
    
    public static Histogram create(Channel channel) throws CreateException {

        try {
            return HistogramFactory.create(channel.voxels());
        } catch (IncorrectVoxelTypeException e) {
            throw new CreateException("Cannot create histogram from channel", e);
        }
    }

    public static Histogram create(Channel channel, Mask mask) throws CreateException {

        if (!channel.extent().equals(mask.extent())) {
            throw new CreateException("Size of channel and mask do not match");
        }

        Histogram total = new Histogram((int) channel.getVoxelDataType().maxValue());

        Histogram histogramForObject =
                createWithMask(channel.voxels().any(), new ObjectMask(mask.binaryVoxels()));
        try {
            total.addHistogram(histogramForObject);
        } catch (OperationFailedException e) {
            assert false;
        }

        return total;
    }

    public static Histogram create(Channel channel, ObjectMask object) {
        return create(channel, ObjectCollectionFactory.of(object));
    }

    public static Histogram create(Channel channel, ObjectCollection objects) {
        return createWithMasks(channel.voxels(), objects);
    }
    
    private static Histogram createWithMask(Voxels<?> inputBuffer, ObjectMask object) {

        Histogram histogram = new Histogram((int) inputBuffer.dataType().maxValue());

        Extent extent = inputBuffer.extent();

        ReadableTuple3i cornerMin = object.boundingBox().cornerMin();
        ReadableTuple3i cornerMax = object.boundingBox().calculateCornerMax();

        byte matchValue = object.binaryValuesByte().getOnByte();

        for (int z = cornerMin.z(); z <= cornerMax.z(); z++) {

            VoxelBuffer<?> buffer = inputBuffer.slice(z);
            UnsignedByteBuffer bufferMask = object.sliceBufferGlobal(z);

            for (int y = cornerMin.y(); y <= cornerMax.y(); y++) {
                for (int x = cornerMin.x(); x <= cornerMax.x(); x++) {

                    int offset = extent.offset(x, y);
                    int offsetMask = object.offsetGlobal(x, y);

                    byte valueOnMask = bufferMask.getRaw(offsetMask);

                    if (valueOnMask == matchValue) {
                        int val = buffer.getInt(offset);
                        histogram.incrementValue(val);
                    }
                }
            }
        }
        return histogram;
    }

    private static Histogram createWithMasks(VoxelsWrapper voxels, ObjectCollection objects) {

        Histogram total = new Histogram((int) voxels.getVoxelDataType().maxValue());

        try {
            for (ObjectMask objectMask : objects) {
                Histogram histogram = createWithMask(voxels.any(), objectMask);
                total.addHistogram(histogram);
            }

        } catch (OperationFailedException e) {
            assert false;
        }

        return total;
    }
    
    private static boolean isDataTypeSupported(VoxelDataType dataType) {
        return dataType.equals(UnsignedByteVoxelType.INSTANCE)
                || dataType.equals(UnsignedShortVoxelType.INSTANCE);
    }
}

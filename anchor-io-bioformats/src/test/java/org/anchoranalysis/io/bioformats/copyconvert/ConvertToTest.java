package org.anchoranalysis.io.bioformats.copyconvert;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.core.channel.factory.ChannelFactory;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.dimensions.OrientationChange;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.datatype.FloatVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedIntVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedShortVoxelType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.io.bioformats.copyconvert.tobyte.UnsignedByteFromFloat;
import org.anchoranalysis.io.bioformats.copyconvert.tobyte.UnsignedByteFromUnsignedByteInterleaving;
import org.anchoranalysis.io.bioformats.copyconvert.tobyte.UnsignedByteFromUnsignedByteNoInterleaving;
import org.anchoranalysis.io.bioformats.copyconvert.tobyte.UnsignedByteFromUnsignedInt;
import org.anchoranalysis.io.bioformats.copyconvert.tobyte.UnsignedByteFromUnsignedShort;
import org.anchoranalysis.io.bioformats.copyconvert.tofloat.FloatFromUnsignedByte;
import org.anchoranalysis.io.bioformats.copyconvert.tofloat.FloatFromUnsignedInt;
import org.anchoranalysis.io.bioformats.copyconvert.toint.UnsignedIntFromUnsignedInt;
import org.anchoranalysis.io.bioformats.copyconvert.toshort.UnsignedShortFromUnsignedShort;
import org.anchoranalysis.spatial.box.Extent;
import org.junit.jupiter.api.Test;

/**
 * Tests various implementations of {@link ConvertTo}.
 *
 * <p>Note that it does not test any conversions involving <i>signed short</i> as voxel buffers do
 * not support this.
 *
 * <p>Each test creates voxels with values sequentially in the range 243 to 255 in the input-type.
 * These voxels are converted, and checked to be identical to the input sequence (subject to a
 * scaling factor).
 *
 * @author Owen Feehan
 */
class ConvertToTest {

    private static final Dimensions DIMENSIONS = new Dimensions(new Extent(3, 4, 1));

    /**
     * Added to the index value, to form the sequential intensity values that are tested, giving a
     * range from 243 to 255.
     */
    private static final int INTENSITY_SHIFT = 243;

    @Test
    void testUnsignedByte_FromUnsignedByteInterleaving() throws IOException {
        testSequential(
                new UnsignedByteFromUnsignedByteInterleaving(),
                UnsignedByteVoxelType.INSTANCE,
                UnsignedByteVoxelType.INSTANCE,
                1.0f,
                true);
    }

    @Test
    void testUnsignedByte_FromUnsignedByteNoInterleaving() throws IOException {
        testSequential(
                new UnsignedByteFromUnsignedByteNoInterleaving(),
                UnsignedByteVoxelType.INSTANCE,
                UnsignedByteVoxelType.INSTANCE,
                1.0f,
                true);
    }

    @Test
    void testUnsignedByte_FromUnsignedShort() throws IOException {
        testSequential(
                new UnsignedByteFromUnsignedShort(8),
                UnsignedShortVoxelType.INSTANCE,
                UnsignedByteVoxelType.INSTANCE,
                1.0f,
                true);
        testSequential(
                new UnsignedByteFromUnsignedShort(10),
                UnsignedShortVoxelType.INSTANCE,
                UnsignedByteVoxelType.INSTANCE,
                0.25f,
                true);
    }

    @Test
    void testUnsignedByte_FromUnsignedInt() throws IOException {
        testSequential(
                new UnsignedByteFromUnsignedInt(8),
                UnsignedIntVoxelType.INSTANCE,
                UnsignedByteVoxelType.INSTANCE,
                1.0f,
                true);
        testSequential(
                new UnsignedByteFromUnsignedInt(9),
                UnsignedIntVoxelType.INSTANCE,
                UnsignedByteVoxelType.INSTANCE,
                0.5f,
                true);
    }

    @Test
    void testUnsignedByte_FromFloat() throws IOException {
        testSequential(
                new UnsignedByteFromFloat(),
                FloatVoxelType.INSTANCE,
                UnsignedByteVoxelType.INSTANCE,
                1.0f,
                true);
    }

    @Test
    void testUnsignedShort_FromUnsignedShort() throws IOException {
        testSequential(
                new UnsignedShortFromUnsignedShort(),
                UnsignedShortVoxelType.INSTANCE,
                UnsignedShortVoxelType.INSTANCE,
                1.0f,
                true);
    }

    @Test
    void testUnsignedInt_FromUnsignedInt() throws IOException {
        testSequential(
                new UnsignedIntFromUnsignedInt(),
                UnsignedIntVoxelType.INSTANCE,
                UnsignedIntVoxelType.INSTANCE,
                1.0f,
                true);
    }

    @Test
    void testFloat_FromUnsignedByte() throws IOException {
        testSequential(
                new FloatFromUnsignedByte(),
                UnsignedByteVoxelType.INSTANCE,
                FloatVoxelType.INSTANCE,
                1.0f,
                true);
    }

    @Test
    void testFloat_FromUnsignedInt() throws IOException {
        testSequential(
                new FloatFromUnsignedInt(),
                UnsignedIntVoxelType.INSTANCE,
                FloatVoxelType.INSTANCE,
                1.0f,
                false);
    }

    /**
     * Creates voxels with values sequentially in the range 243 to 255 in the input-type.
     *
     * <p>These voxels are converted, and checked to be identical to the input sequence (subject to
     * a scaling factor).
     */
    private static <S, T> void testSequential(
            ConvertTo<T> converter,
            VoxelDataType inputDataType,
            VoxelDataType outputDataType,
            float expectedOutputScaleRatio,
            boolean checkOrientationChange)
            throws IOException {

        // With no orientation-change.
        testSequential(
                converter,
                inputDataType,
                outputDataType,
                createSequentialBuffer(
                        inputDataType, DIMENSIONS, 1.0f, OrientationChange.KEEP_UNCHANGED),
                createSequentialBuffer(
                        outputDataType,
                        DIMENSIONS,
                        expectedOutputScaleRatio,
                        OrientationChange.KEEP_UNCHANGED),
                OrientationChange.KEEP_UNCHANGED);

        if (checkOrientationChange) {
            // With 90 degrees clockwise orientation-change.
            testSequential(
                    converter,
                    inputDataType,
                    outputDataType,
                    createSequentialBuffer(
                            inputDataType, DIMENSIONS, 1.0f, OrientationChange.KEEP_UNCHANGED),
                    createSequentialBuffer(
                            outputDataType,
                            DIMENSIONS,
                            expectedOutputScaleRatio,
                            OrientationChange.ROTATE_90_ANTICLOCKWISE),
                    OrientationChange.ROTATE_90_ANTICLOCKWISE);
        }
    }

    private static <S, T> void testSequential(
            ConvertTo<T> converter,
            VoxelDataType inputDataType,
            VoxelDataType outputDataType,
            VoxelBuffer<S> inputBuffer,
            VoxelBuffer<T> expectedOutputBuffer,
            OrientationChange orientationChange)
            throws IOException {
        ByteBuffer underlyingBytes = ByteBuffer.wrap(inputBuffer.underlyingBytes());
        Channel channel = ChannelFactory.instance().create(DIMENSIONS, outputDataType);
        ImageFileEncoding encoding = new ImageFileEncoding(false, false, 1);
        converter.copyAllChannels(
                DIMENSIONS, underlyingBytes, index -> channel, 0, encoding, orientationChange);
        VoxelBuffer<T> outputSlice = channel.voxels().slice(0);

        // Check the arrays have exactly the same bytes.
        assertTrue(
                Arrays.equals(
                        expectedOutputBuffer.underlyingBytes(), outputSlice.underlyingBytes()));
    }

    /**
     * Creates a buffer with values sequentially increasing in the range range from {@code (243 to
     * 255) * scaleRatio}.
     */
    private static <T> VoxelBuffer<T> createSequentialBuffer(
            VoxelDataType dataType,
            Dimensions dimensions,
            float scaleRatio,
            OrientationChange orientationChange) {

        Extent extent = dimensions.extent();

        Dimensions dimensionsReoriented = maybeReorientDimensions(dimensions, orientationChange);

        Channel channel = ChannelFactory.instance().create(dimensionsReoriented, dataType);

        VoxelBuffer<T> buffer = channel.voxels().slice(0);
        for (int y = 0; y < extent.y(); y++) {
            for (int x = 0; x < extent.x(); x++) {
                int valueUnscaled = extent.offset(x, y) + INTENSITY_SHIFT;
                int valueScaled = (int) (scaleRatio * valueUnscaled);
                int indexReoriented = orientationChange.index(x, y, extent);
                buffer.putInt(indexReoriented, valueScaled);
            }
        }
        return buffer;
    }

    /** Creates a new {@link Dimensions} with changed orientation. The resolution is ignored. */
    private static Dimensions maybeReorientDimensions(
            Dimensions dimensions, OrientationChange orientationChange) {
        return new Dimensions(orientationChange.extent(dimensions.extent()));
    }
}

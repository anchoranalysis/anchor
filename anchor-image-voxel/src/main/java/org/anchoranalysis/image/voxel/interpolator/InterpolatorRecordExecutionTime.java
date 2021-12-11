package org.anchoranalysis.image.voxel.interpolator;

import java.nio.FloatBuffer;
import org.anchoranalysis.core.time.ExecutionTimeRecorder;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedShortBuffer;
import org.anchoranalysis.spatial.box.Extent;

/**
 * Calls another {@link Interpolator} recording each interpolation call via a {@link
 * ExecutionTimeRecorder}.
 *
 * @author Owen Feehan
 */
public class InterpolatorRecordExecutionTime extends Interpolator {

    // START: REQUIRED ARGUMENTS
    /** The interpolator whose activity will be recorded. */
    private final Interpolator interpolator;

    /** The recorder of the execution times. */
    private final ExecutionTimeRecorder executionTimeRecorder;
    // END: REQUIRED ARGUMENTS

    // START: The identifiers used for recording time.
    private final String identifierByte;
    private final String identifierShort;
    private final String identifierFloat;
    // END: The identifiers used for recording time.

    /**
     * Creates with an {@link Interpolator} and {@link ExecutionTimeRecorder}.
     *
     * @param interpolator the interpolator whose activity will be recorded.
     * @param executionTimeRecorder the recorder of the execution times.
     * @param operationIdentifierPrefix a prefix that will be prepended to the identifiers used to
     *     record execution time (to help make them unique to a given context).
     */
    public InterpolatorRecordExecutionTime(
            Interpolator interpolator,
            ExecutionTimeRecorder executionTimeRecorder,
            String operationIdentifierPrefix) {
        this.interpolator = interpolator;
        this.executionTimeRecorder = executionTimeRecorder;
        this.identifierByte = buildIdentifier("byte", operationIdentifierPrefix);
        this.identifierShort = buildIdentifier("short", operationIdentifierPrefix);
        this.identifierFloat = buildIdentifier("float", operationIdentifierPrefix);
    }

    @Override
    public boolean canValueRangeChange() {
        return interpolator.canValueRangeChange();
    }

    @Override
    protected VoxelBuffer<UnsignedByteBuffer> interpolateByte(
            VoxelBuffer<UnsignedByteBuffer> voxelsSource,
            VoxelBuffer<UnsignedByteBuffer> voxelsDestination,
            Extent extentSource,
            Extent extentDestination) {
        return executionTimeRecorder.recordExecutionTime(
                identifierByte,
                () ->
                        interpolator.interpolateByte(
                                voxelsSource, voxelsDestination, extentSource, extentDestination));
    }

    @Override
    protected VoxelBuffer<UnsignedShortBuffer> interpolateShort(
            VoxelBuffer<UnsignedShortBuffer> voxelsSource,
            VoxelBuffer<UnsignedShortBuffer> voxelsDestination,
            Extent extentSource,
            Extent extentDestination) {
        return executionTimeRecorder.recordExecutionTime(
                identifierShort,
                () ->
                        interpolator.interpolateShort(
                                voxelsSource, voxelsDestination, extentSource, extentDestination));
    }

    @Override
    protected VoxelBuffer<FloatBuffer> interpolateFloat(
            VoxelBuffer<FloatBuffer> voxelsSource,
            VoxelBuffer<FloatBuffer> voxelsDestination,
            Extent extentSource,
            Extent extentDestination) {
        return executionTimeRecorder.recordExecutionTime(
                identifierFloat,
                () ->
                        interpolator.interpolateFloat(
                                voxelsSource, voxelsDestination, extentSource, extentDestination));
    }

    /** Builds an identifier for a particular data-type, and with a particular prefix. */
    private static String buildIdentifier(String dataType, String operationIdentifierPrefix) {
        return String.format("%s - interpolating %s voxels.", operationIdentifierPrefix, dataType);
    }
}

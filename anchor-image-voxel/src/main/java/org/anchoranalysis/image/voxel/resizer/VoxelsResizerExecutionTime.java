/*-
 * #%L
 * anchor-image-voxel
 * %%
 * Copyright (C) 2010 - 2021 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.image.voxel.resizer;

import java.nio.FloatBuffer;
import org.anchoranalysis.core.time.ExecutionTimeRecorder;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedShortBuffer;
import org.anchoranalysis.spatial.box.Extent;

/**
 * Calls another {@link VoxelsResizer} recording each resize call via a {@link
 * ExecutionTimeRecorder}.
 *
 * @author Owen Feehan
 */
public class VoxelsResizerExecutionTime extends VoxelsResizer {

    // START: REQUIRED ARGUMENTS
    /** The interpolator whose activity will be recorded. */
    private final VoxelsResizer resizer;

    /** The recorder of the execution times. */
    private final ExecutionTimeRecorder executionTimeRecorder;
    // END: REQUIRED ARGUMENTS

    // START: The identifiers used for recording time.
    private final String identifierByte;
    private final String identifierShort;
    private final String identifierFloat;
    // END: The identifiers used for recording time.

    /**
     * Creates with an {@link VoxelsResizer} and {@link ExecutionTimeRecorder}.
     *
     * @param interpolator the interpolator whose activity will be recorded.
     * @param executionTimeRecorder the recorder of the execution times.
     * @param operationIdentifierPrefix a prefix that will be prepended to the identifiers used to
     *     record execution time (to help make them unique to a given context).
     */
    public VoxelsResizerExecutionTime(
            VoxelsResizer interpolator,
            ExecutionTimeRecorder executionTimeRecorder,
            String operationIdentifierPrefix) {
        this.resizer = interpolator;
        this.executionTimeRecorder = executionTimeRecorder;
        this.identifierByte = buildIdentifier("byte", operationIdentifierPrefix);
        this.identifierShort = buildIdentifier("short", operationIdentifierPrefix);
        this.identifierFloat = buildIdentifier("float", operationIdentifierPrefix);
    }

    @Override
    public boolean canValueRangeChange() {
        return resizer.canValueRangeChange();
    }

    @Override
    protected VoxelBuffer<UnsignedByteBuffer> resizeByte(
            VoxelBuffer<UnsignedByteBuffer> voxelsSource,
            VoxelBuffer<UnsignedByteBuffer> voxelsDestination,
            Extent extentSource,
            Extent extentDestination) {
        return executionTimeRecorder.recordExecutionTime(
                identifierByte,
                () ->
                        resizer.resizeByte(
                                voxelsSource, voxelsDestination, extentSource, extentDestination));
    }

    @Override
    protected VoxelBuffer<UnsignedShortBuffer> resizeShort(
            VoxelBuffer<UnsignedShortBuffer> voxelsSource,
            VoxelBuffer<UnsignedShortBuffer> voxelsDestination,
            Extent extentSource,
            Extent extentDestination) {
        return executionTimeRecorder.recordExecutionTime(
                identifierShort,
                () ->
                        resizer.resizeShort(
                                voxelsSource, voxelsDestination, extentSource, extentDestination));
    }

    @Override
    protected VoxelBuffer<FloatBuffer> resizeFloat(
            VoxelBuffer<FloatBuffer> voxelsSource,
            VoxelBuffer<FloatBuffer> voxelsDestination,
            Extent extentSource,
            Extent extentDestination) {
        return executionTimeRecorder.recordExecutionTime(
                identifierFloat,
                () ->
                        resizer.resizeFloat(
                                voxelsSource, voxelsDestination, extentSource, extentDestination));
    }

    /** Builds an identifier for a particular data-type, and with a particular prefix. */
    private static String buildIdentifier(String dataType, String operationIdentifierPrefix) {
        return String.format("%s - resizing %s voxels", operationIdentifierPrefix, dataType);
    }
}

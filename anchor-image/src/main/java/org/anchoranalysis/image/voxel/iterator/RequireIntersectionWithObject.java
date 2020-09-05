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

package org.anchoranalysis.image.voxel.iterator;

import org.anchoranalysis.image.convert.UnsignedByteBuffer;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.object.ObjectMask;

/**
 * Only processes a point if it lines on the region of an object-mask
 *
 * <p>Any points lying outside the object-mask are never processed.
 *
 * @author Owen Feehan
 */
final class RequireIntersectionWithObject implements ProcessVoxel {

    private final ProcessVoxel process;

    private final ObjectMask objectMask;
    private final byte byteOn;

    private UnsignedByteBuffer bufferObject;

    /**
     * Constructor
     *
     * @param process the processor to call on the region of the object-mask
     * @param objectMask the object-mask that defines the "on" region which is processed only.
     */
    public RequireIntersectionWithObject(ProcessVoxel process, ObjectMask objectMask) {
        super();
        this.process = process;
        this.objectMask = objectMask;
        this.byteOn = objectMask.binaryValuesByte().getOnByte();
    }

    @Override
    public void notifyChangeSlice(int z) {
        process.notifyChangeSlice(z);
        bufferObject = objectMask.sliceBufferGlobal(z);
    }

    @Override
    public void notifyChangeY(int y) {
        process.notifyChangeY(y);
    }

    @Override
    public void process(Point3i point) {
        // We skip if our containing object-mask doesn't include it
        if (isPointOnObject(point)) {
            process.process(point);
        }
    }

    private boolean isPointOnObject(Point3i point) {
        int offsetMask = objectMask.offsetGlobal(point.x(), point.y());

        // We skip if our containing object-mask doesn't include it
        return (bufferObject.getRaw(offsetMask) == byteOn);
    }
}

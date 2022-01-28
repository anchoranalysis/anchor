/*-
 * #%L
 * anchor-plugin-image-task
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

package org.anchoranalysis.image.bean.nonbean;

import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.exception.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;

/**
 * Checks that all {@link Channel}s have the same voxel-data-type.
 *
 * @author Owen Feehan
 */
public class ConsistentChannelChecker {

    /** The voxel-data-type that is consistent across all channels. */
    private VoxelDataType voxelDataType;

    /**
     * Checks that a channel has the same type (max value) as the others.
     *
     * @param channel the {@link Channel} to check.
     * @throws OperationFailedException if {@code voxelDataType} is inconsistent with any existing
     *     type, if one exists.
     */
    public void checkChannelType(Channel channel) throws OperationFailedException {
        if (this.voxelDataType == null) {
            this.voxelDataType = channel.getVoxelDataType();
        } else {
            if (!channel.getVoxelDataType().equals(this.voxelDataType)) {
                throw new OperationFailedException(
                        String.format(
                                "All images must have identical voxel-data-types, but they are not consistent: %s versus %s",
                                this.voxelDataType, channel.getVoxelDataType()));
            }
        }
    }

    /**
     * The voxel-data-type that is consistent across all channels.
     *
     * @return the voxel-data-type.
     */
    public VoxelDataType getVoxelDataType() {
        if (voxelDataType != null) {
            return voxelDataType;
        } else {
            throw new AnchorFriendlyRuntimeException(
                    "There have been no calls to checkChannelType, so no consistent voxelDataType has been established");
        }
    }
}

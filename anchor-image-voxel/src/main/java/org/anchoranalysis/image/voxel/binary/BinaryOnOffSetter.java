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

package org.anchoranalysis.image.voxel.binary;

import org.anchoranalysis.spatial.point.Point3i;

/**
 * Getters and setters for reading and assigning binary state to voxels.
 *
 * @author Owen Feehan
 */
public interface BinaryOnOffSetter {

    /**
     * Does the voxel at this point have an <i>on</i> state?
     *
     * @param x the x-component of the point.
     * @param y the y-component of the point.
     * @param z the z-component of the point.
     * @return true if the voxel has an <i>on</i> state.
     */
    boolean isOn(int x, int y, int z);

    /**
     * Does the voxel at this point have an <i>off</i> state?
     *
     * @param x the x-component of the point.
     * @param y the y-component of the point.
     * @param z the z-component of the point.
     * @return true if the voxel has an <i>off</i> state.
     */
    boolean isOff(int x, int y, int z);

    /**
     * Assigns an <i>on</i> state to a voxel at a particular point.
     *
     * @param x the x-component of the point.
     * @param y the y-component of the point.
     * @param z the z-component of the point.
     */
    void setOn(int x, int y, int z);

    /**
     * Assigns an <i>off</i> state to a voxel at a particular point.
     *
     * @param x the x-component of the point.
     * @param y the y-component of the point.
     * @param z the z-component of the point.
     */
    void setOff(int x, int y, int z);

    /**
     * Assigns an <i>on</i> state to a voxel at a particular point.
     *
     * @param point the point to assign an <i>on</i> state to.
     */
    default void setOn(Point3i point) {
        setOn(point.x(), point.y(), point.z());
    }

    /**
     * Assigns an <i>off</i> state to a voxel at a particular point.
     *
     * @param point the point to assign an <i>off</i> state to.
     */
    default void setOff(Point3i point) {
        setOff(point.x(), point.y(), point.z());
    }

    /**
     * Does the voxel at this point have an <i>on</i> state?
     *
     * @param point the point to assign an <i>on</i> state to.
     * @return true if the voxel has an <i>on</i> state.
     */
    default boolean isOn(Point3i point) {
        return isOn(point.x(), point.y(), point.z());
    }

    /**
     * Does the voxel at this point have an <i>off</i> state?
     *
     * @param point the point to assign an <i>off</i> state to.
     * @return true if the voxel has an <i>off</i> state.
     */
    default boolean isOff(Point3i point) {
        return isOff(point.x(), point.y(), point.z());
    }
}

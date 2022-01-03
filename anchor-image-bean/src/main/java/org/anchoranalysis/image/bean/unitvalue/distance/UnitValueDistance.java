/*-
 * #%L
 * anchor-image-bean
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

package org.anchoranalysis.image.bean.unitvalue.distance;

import java.io.Serializable;
import java.util.Optional;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.core.dimensions.UnitConverter;
import org.anchoranalysis.spatial.axis.Axis;
import org.anchoranalysis.spatial.orientation.DirectionVector;
import org.anchoranalysis.spatial.point.Point3d;
import org.anchoranalysis.spatial.point.Point3i;

/**
 * Base class for methods to specify a distance along a vector in an image.
 *
 * @author Owen Feehan
 */
public abstract class UnitValueDistance extends AnchorBean<UnitValueDistance>
        implements Serializable {

    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Resolves the distance using the direction between two points of type {@link Point3d}.
     *
     * <p>The magnitude of the distance between the two points is ignored, only the direction.
     *
     * <p>See {@link #resolve(Optional, Point3i, Point3i)} for a {@link Point3i} equivalent.
     *
     * @param unitConverter converts to/from voxels to physical units.
     * @param point1 the first point for the direction.
     * @param point2 the second point for the direction.
     * @return the distance in units of voxels.
     * @throws OperationFailedException if the resolution cannot successfully complete.
     */
    public double resolve(Optional<UnitConverter> unitConverter, Point3d point1, Point3d point2)
            throws OperationFailedException {
        return resolve(unitConverter, DirectionVector.createBetweenTwoPoints(point1, point2));
    }

    /**
     * Resolves the distance using the direction between two points of type {@link Point3i}.
     *
     * <p>The magnitude of the distance between the two points is ignored, only the direction.
     *
     * <p>See {@link #resolve(Optional, Point3d, Point3d)} for a {@link Point3d} equivalent.
     *
     * @param unitConverter converts to/from voxels to physical units.
     * @param point1 the first point for the direction.
     * @param point2 the second point for the direction.
     * @return the distance in units of voxels.
     * @throws OperationFailedException if the resolution cannot successfully complete.
     */
    public double resolve(Optional<UnitConverter> unitConverter, Point3i point1, Point3i point2)
            throws OperationFailedException {
        return resolve(unitConverter, DirectionVector.createBetweenTwoPoints(point1, point2));
    }

    /**
     * Resolves the distance using a {@link DirectionVector}.
     *
     * <p>The magnitude of the vector is ignored, using only the direction.
     *
     * @param unitConverter converts to/from voxels to physical units.
     * @param direction the direction-vector.
     * @return the distance in units of voxels.
     * @throws OperationFailedException if the resolution cannot successfully complete.
     */
    public abstract double resolve(Optional<UnitConverter> unitConverter, DirectionVector direction)
            throws OperationFailedException;

    /**
     * Resolves the distance in a direction aligned to a particular axis.
     *
     * @param unitConverter converts to/from voxels to physical units.
     * @param axis axis to indicate direction.
     * @return the distance in the direction of the axis in units of voxels.
     * @throws OperationFailedException if the resolution cannot successfully complete.
     */
    public double resolveForAxis(Optional<UnitConverter> unitConverter, Axis axis)
            throws OperationFailedException {
        return resolve(unitConverter, new DirectionVector(axis));
    }
}

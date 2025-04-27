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

package org.anchoranalysis.spatial.orientation;

import org.anchoranalysis.core.exception.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.core.exception.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.spatial.axis.Axis;
import org.anchoranalysis.spatial.point.Point3d;
import org.anchoranalysis.spatial.point.Point3i;
import org.anchoranalysis.spatial.point.Tuple3d;
import org.anchoranalysis.spatial.point.Vector3d;

/**
 * A vector indicating a direction in space.
 *
 * @author Owen Feehan
 */
public final class DirectionVector {

    private final Tuple3d vector;

    /** Creates with each dimension's value set to {@code 0}. */
    public DirectionVector() {
        this.vector = new Vector3d();
    }

    /**
     * Creates reusing {@link Tuple3d} internally for the dimension's values.
     *
     * @param vector the vector to reuse.
     */
    public DirectionVector(Tuple3d vector) {
        this.vector = vector;
    }

    /**
     * Creates for a particular value in each dimension.
     *
     * @param x the value in the x-dimension.
     * @param y the value in the y-dimension.
     * @param z the value in the z-dimension.
     */
    public DirectionVector(double x, double y, double z) {
        this.vector = new Vector3d(x, y, z);
    }

    /**
     * Creates a direction aligned to a particular axis.
     *
     * @param axis the axis to align to.
     */
    public DirectionVector(Axis axis) {
        this.vector =
                switch (axis) {
                    case X -> new Point3d(1, 0, 0);
                    case Y -> new Point3d(0, 1, 0);
                    case Z -> new Point3d(0, 0, 1);
                    default -> throw new AnchorImpossibleSituationException();
                };
    }

    /**
     * Creates a {@link DirectionVector} representing the direction between two points of type
     * {@link Point3d}.
     *
     * @param point1 the first-point.
     * @param point2 the second-point.
     * @return a vector with the distance between the two points, normalized to have magnitude of
     *     {@code 1}.
     */
    public static DirectionVector createBetweenTwoPoints(Point3d point1, Point3d point2) {

        double sx = point2.x() - point1.x();
        double sy = point2.y() - point1.y();
        double sz = point2.z() - point1.z();
        return createNormed(sx, sy, sz);
    }

    /**
     * Creates a {@link DirectionVector} representing the direction between two points of type
     * {@link Point3i}.
     *
     * @param point1 the first-point.
     * @param point2 the second-point.
     * @return a vector with the distance between the two points, normalized to have magnitude of
     *     {@code 1}.
     */
    public static DirectionVector createBetweenTwoPoints(Point3i point1, Point3i point2) {

        double sx = (double) point2.x() - point1.x();
        double sy = (double) point2.y() - point1.y();
        double sz = (double) point2.z() - point1.z();
        return createNormed(sx, sy, sz);
    }

    /**
     * The x-component of the vector.
     *
     * @return the component's value.
     */
    public double x() {
        return vector.x();
    }

    /**
     * The y-component of the vector.
     *
     * @return the component's value.
     */
    public double y() {
        return vector.y();
    }

    /**
     * The z-component of the vector.
     *
     * @return the component's value.
     */
    public double z() {
        return vector.z();
    }

    /**
     * Sets an element of the vector by the index of its position.
     *
     * @param index the index to change in the vector where {@code index=0} is the X-element, {@code
     *     index=1} is the Y-element, and {@code index=2} is the Z-element.
     * @param valueToAssign the value to assign.
     */
    public void setIndex(int index, double valueToAssign) {
        switch (index) {
            case 0 -> vector.setX(valueToAssign);
            case 1 -> vector.setY(valueToAssign);
            case 2 -> vector.setZ(valueToAssign);
            default -> throw new AnchorFriendlyRuntimeException("Index must be >= 0 and < 3");
        }
    }

    /**
     * Converts to a {@link Vector3d} representation.
     *
     * @return a newly-created {@link Vector3d} with identical component values as the current
     *     object.
     */
    public Vector3d asVector3d() {
        return new Vector3d(vector);
    }

    /**
     * Creates a {@link DirectionVector} from x, y, z components, and then normalizes them to have a
     * magnitude of {@code 1}.
     */
    private static DirectionVector createNormed(double x, double y, double z) {
        double norm = Math.sqrt(Math.pow(x, 2.0) + Math.pow(y, 2.0) + Math.pow(z, 2.0));
        return new DirectionVector(x / norm, y / norm, z / norm);
    }
}

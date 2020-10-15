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

package org.anchoranalysis.image.dimensions;

import lombok.AllArgsConstructor;
import org.anchoranalysis.core.axis.AxisType;
import org.anchoranalysis.image.orientation.DirectionVector;

/**
 * Converts from voxelized units to different physical measurements of area / volume / distance.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class UnitConverter {

    /** The image-resolution used to resolve physical measurements in different directions. */
    private final Resolution resolution;

    /**
     * Converts an area from square voxels to square meters.
     *
     * @param value the area in square voxels
     * @return the area converted into square meters.
     */
    public double toPhysicalArea(double value) {
        return value * resolution.unitArea();
    }

    /**
     * Converts an area from square voxels to physical units.
     *
     * @param value the area in square voxels
     * @param unitType unit-type to convert to ala {@link SpatialUnits}.
     * @return the area converted into physical units (of type {@code unitType}).
     */
    public double toPhysicalArea(double value, String unitType) {
        return SpatialUnits.convertToUnits(toPhysicalArea(value), unitType);
    }

    /**
     * Converts a volume from cubic voxels to cubic meters.
     *
     * @param value the volume in cubic voxels
     * @return the volume converted into cubic meters.
     */
    public double toPhysicalVolume(double value) {
        return value * resolution.unitVolume();
    }

    /**
     * Converts a volume from cubic voxels to physical units.
     *
     * @param value the volume in cubic voxels
     * @param unitType unit-type to convert to ala {@link SpatialUnits}.
     * @return the volume converted into physical units (of type {@code unitType}).
     */
    public double toPhysicalVolume(double value, String unitType) {
        return SpatialUnits.convertToUnits(toPhysicalVolume(value), unitType);
    }

    /**
     * Converts a distance from voxels to meters.
     *
     * @param value the distance in voxels
     * @param direction the direction in which the distance is measured
     * @return the distance converted into meters.
     */
    public double toPhysicalDistance(double value, DirectionVector direction) {
        return value * unitDistanceForDirection(direction);
    }

    /**
     * Converts a distance from voxels to physical units.
     *
     * @param value the distance in voxels
     * @param direction the direction in which the distance is measured
     * @param unitType unit-type to convert to ala {@link SpatialUnits}.
     * @return the distance converted into physical units (of type {@code unitType}).
     */
    public double toPhysicalDistance(double value, DirectionVector direction, String unitType) {
        return SpatialUnits.convertToUnits(toPhysicalDistance(value, direction), unitType);
    }

    /**
     * Converts from physical-volume to voxels.
     *
     * <p>The physical unit-type is assumed to be cubic-meters.
     *
     * @param value the value in physical units
     * @return the area converted into voxels.
     */
    public double fromPhysicalVolume(double value) {
        return value / resolution.unitVolume();
    }

    /**
     * Converts from physical-volume to voxels.
     *
     * @param value the value in physical units
     * @param unitType unit-type of value ala {@link SpatialUnits}
     * @return the area converted into voxels.
     */
    public double fromPhysicalVolume(double value, String unitType) {
        double valueAsBase = SpatialUnits.convertFromUnits(value, unitType);
        return fromPhysicalVolume(valueAsBase);
    }

    /**
     * Converts from physical-area to pixels.
     *
     * <p>The physical unit-type is assumed to be square-meters.
     *
     * @param value the value in physical units
     * @return the area converted into pixels.
     */
    public double fromPhysicalArea(double value) {
        return value / resolution.unitArea();
    }

    /**
     * Converts from physical-area to pixels.
     *
     * @param value the value in physical units
     * @param unitType physical-unit type ala {@link SpatialUnits}
     * @return the area converted into pixels.
     */
    public double fromPhysicalArea(double value, String unitType) {
        double valueAsBase = SpatialUnits.convertFromUnits(value, unitType);
        return fromPhysicalArea(valueAsBase);
    }

    /**
     * Converts from physical-distance to voxels.
     *
     * <p>The physical unit-type is assumed to be meters.
     *
     * @param value the value in physical units
     * @param direction the direction in which the distance is measured
     * @return the distance converted into voxels.
     */
    public double fromPhysicalDistance(double value, DirectionVector direction) {
        return value / unitDistanceForDirection(direction);
    }

    /**
     * Converts from physical-distance to voxels.
     *
     * <p>The physical unit-type is assumed to be meters, and the direction of the distance is
     * assumed to be along the X axis.
     *
     * @param value the value in physical units
     * @return the distance converted into voxels.
     */
    public double fromPhysicalDistance(double value) {
        DirectionVector unitX = new DirectionVector(AxisType.X);
        return fromPhysicalDistance(value, unitX);
    }

    private double unitDistanceForDirection(DirectionVector direction) {
        double x = direction.x() * resolution.x();
        double y = direction.y() * resolution.y();
        double z = direction.z() * resolution.z();
        return Math.sqrt(Math.pow(x, 2.0) + Math.pow(y, 2.0) + Math.pow(z, 2.0));
    }
}

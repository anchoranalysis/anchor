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

package org.anchoranalysis.image.core.dimensions;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.box.Extent;
import org.anchoranalysis.spatial.point.Point3d;
import org.anchoranalysis.spatial.point.Point3i;
import org.anchoranalysis.spatial.point.ReadableTuple3i;
import org.anchoranalysis.spatial.scale.RelativeScaleCalculator;
import org.anchoranalysis.spatial.scale.ScaleFactor;

/**
 * The size of an image (in voxels), together with the image resolution.
 *
 * <p>The <i>image resolution</i> is the <i>physical size</i> of each voxel, if known.
 *
 * <p>This class is <b>immutable</b>.
 */
@EqualsAndHashCode
@Accessors(fluent = true)
@AllArgsConstructor
public final class Dimensions {

    /**
     * The width and height and depth of the image.
     *
     * <p>i.e. the size of each of the three possible dimensions.
     */
    @Getter private final Extent extent;

    /**
     * Resolution of voxels to physical measurements.
     *
     * <p>e.g. physical size of each voxel in a particular dimension.
     */
    @Getter private final Optional<Resolution> resolution;

    /**
     * Construct with an explicit extent and no resolution.
     *
     * @param x the size of the <i>X</i>-dimension in voxels.
     * @param y the size of the <i>Y</i>-dimension in voxels.
     * @param z the size of the <i>Z</i>-dimension in voxels.
     */
    public Dimensions(int x, int y, int z) {
        this(new Extent(x, y, z));
    }

    /**
     * Construct with an explicit extent and no resolution.
     *
     * @param extent the size of the image in voxels, for respectively the <i>X</i>, <i>Y</i> and
     *     <i>Z</i> dimensions.
     */
    public Dimensions(ReadableTuple3i extent) {
        this(new Extent(extent.x(), extent.y(), extent.z()));
    }

    /**
     * Construct with an explicit extent and no resolution.
     *
     * @param extent the size of the image in voxels.
     */
    public Dimensions(Extent extent) {
        this(extent, Optional.empty());
    }

    /**
     * Resizes the dimensions to have new sizes in the X and Y dimension.
     *
     * <p>The z-dimension remains unchanged.
     *
     * <p>The resolution is also scaled accordingly, so the image refers to the same physical size,
     * before and after scaling..
     *
     * @param x the new size to assign in the <i>X</i>-dimension.
     * @param y the new size to assign in the <i>Y</i>-dimension.
     * @return a newly created {@link Channel} containing a resized version of the current.
     */
    public Dimensions resizeXY(int x, int y) {
        Extent extentScaled = new Extent(x, y, extent.z());
        ScaleFactor scaleFactor = RelativeScaleCalculator.relativeScale(extent, extentScaled);
        return new Dimensions(extentScaled, scaledResolution(scaleFactor));
    }

    /**
     * Scales the X- and Y- dimensions by a scaling-factor.
     *
     * <p>The resolution is also scaled, so the image refers to the same physical size, before and
     * after scaling..
     *
     * @param scaleFactor the scaling-factor to multiply the respective X and Y dimension values by.
     * @return a new {@link Extent} whose X and Y values are scaled versions of the current values,
     *     and Z value is unchanged.
     */
    public Dimensions scaleXYBy(ScaleFactor scaleFactor) {
        return new Dimensions(extent.scaleXYBy(scaleFactor), scaledResolution(scaleFactor));
    }

    /**
     * Deep-copies the current object, but assigns a different {@link Extent}.
     *
     * @param extentToAssign the extent to assign.
     * @return a copy, but with the assigned extent.
     */
    public Dimensions duplicateChangeExtent(Extent extentToAssign) {
        return new Dimensions(extentToAssign, resolution);
    }

    /**
     * Deep-copies the current object, but assigns a different size for the Z-dimension.
     *
     * @param z the size in the Z-dimension.
     * @return a copy, but with the assigned extent.
     */
    public Dimensions duplicateChangeZ(int z) {
        return new Dimensions(extent.duplicateChangeZ(z), resolution);
    }

    /**
     * Deep-copies the current object, but assigns a different {@link Resolution}.
     *
     * @param resolutionToAssign the resolution to assign.
     * @return a copy, but with the assigned resolution.
     */
    public Dimensions duplicateChangeResolution(Optional<Resolution> resolutionToAssign) {
        return new Dimensions(extent, resolutionToAssign);
    }

    /**
     * Calculates the volume of the {@link Extent} when considered as a box.
     *
     * <p>This is is the size in the X, Y and Z dimensions multiplied together.
     *
     * @return the volume in voxels.
     */
    public long calculateVolume() {
        return extent.calculateVolume();
    }

    /**
     * Size in X multiplied by size in Y.
     *
     * <p>This may be convenient for calculating offsets and for iterations.
     *
     * @return the area (in square voxels).
     */
    public int areaXY() {
        return extent.areaXY();
    }

    /**
     * The size in the X dimension.
     *
     * @return the size.
     */
    public int x() {
        return extent.x();
    }

    /**
     * The size in the Y dimension.
     *
     * @return the size.
     */
    public int y() {
        return extent.y();
    }

    /**
     * The size in the Z dimension.
     *
     * @return the size.
     */
    public int z() {
        return extent.z();
    }

    /**
     * Calculates a XY-offset of a point in a buffer whose dimensions are this extent.
     *
     * @param x the value in the X-dimension for the point.
     * @param y the value in the Y-dimension for the point.
     * @return the offset, pertaining only to all dimensions.
     */
    public int offset(int x, int y) {
        return extent.offset(x, y);
    }

    /**
     * Is a point of type {@link Point3d} contained within the extent?
     *
     * @param point the point to check.
     * @return true iff the point exists within the extent, considering all axes.
     */
    public boolean contains(Point3d point) {
        return extent.contains(point);
    }

    /**
     * Is a point of type {@link ReadableTuple3i} contained within the {@link Extent}?
     *
     * @param point the point to check.
     * @return true iff the point exists within the {@link Extent}, considering all axes.
     */
    public boolean contains(Point3i point) {
        return extent.contains(point);
    }

    /**
     * Calculates a XYZ-offset of a point in a buffer whose dimensions are this extent.
     *
     * @param point the point to calculate an offset for.
     * @return the offset, pertaining only to all dimensions.
     */
    public final int offset(Point3i point) {
        return extent.offset(point);
    }

    /**
     * Calculates a XY-offset of a point in a buffer whose dimensions are this extent.
     *
     * @param point the point to calculate an offset for.
     * @return the offset, pertaining only to the X and Y dimensions.
     */
    public final int offsetSlice(Point3i point) {
        return extent.offsetSlice(point);
    }

    /**
     * Is {@code box} entirely contained within the extent?
     *
     * @param box the bounding-box to check.
     * @return true iff {@code} only describes space contained in the current extent.
     */
    public boolean contains(BoundingBox box) {
        return extent.contains(box);
    }

    @Override
    public String toString() {
        return extent.toString();
    }

    /**
     * Converts voxel-scaled measurements to/from physical units.
     *
     * @return a converter that will perform conversions using current resolution.
     */
    public Optional<UnitConverter> unitConvert() {
        return resolution.map(Resolution::unitConvert);
    }

    /**
     * Checks equality between this object and another {@link Dimensions}, but possibly skipping a
     * comparison of image-resolution in the check.
     *
     * @param other the object to compare with.
     * @param compareResolution if true image resolution is compared, otherwise it is not
     *     considered.
     * @return true iff the two objects are equal by the above criteria.
     */
    public boolean equals(Dimensions other, boolean compareResolution) {
        if (compareResolution) {
            return equals(other);
        } else {
            return extent.equals(other.extent);
        }
    }

    /** Apply a scaling-factor to the resolution, if it exists. */
    private Optional<Resolution> scaledResolution(ScaleFactor scaleFactor) {
        return resolution.map(res -> res.scaleXY(scaleFactor));
    }
}

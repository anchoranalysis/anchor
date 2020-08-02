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

package org.anchoranalysis.image.extent;

import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.scale.ScaleFactor;
import org.anchoranalysis.image.scale.ScaleFactorUtilities;

/**
 * The dimensions of an image (in voxels), together with the image resolution
 *
 * <p>This class is IMMUTABLE.
 */
@EqualsAndHashCode
public final class ImageDimensions implements Serializable {

    /** */
    private static final long serialVersionUID = 1L;

    private final ImageResolution res;

    @Getter private final Extent extent;

    /** Construct with an explicit extent and default resolution (1.0 for each dimension) */
    public ImageDimensions(int x, int y, int z) {
        this(new Extent(x, y, z));
    }

    /** Construct with an explicit extent and default resolution (1.0 for each dimension) */
    public ImageDimensions(Extent extent) {
        this(extent, new ImageResolution());
    }

    /** Construct with an explicit extent and resolution */
    public ImageDimensions(Extent extent, ImageResolution res) {
        this.extent = extent;
        this.res = res;
    }

    public ImageDimensions scaleXYTo(int x, int y) {
        Extent extentScaled = new Extent(x, y, extent.getZ());
        ScaleFactor sf = ScaleFactorUtilities.calcRelativeScale(extent, extentScaled);
        return new ImageDimensions(extentScaled, res.scaleXY(sf));
    }

    public ImageDimensions scaleXYBy(ScaleFactor sf) {
        return new ImageDimensions(extent.scaleXYBy(sf), res.scaleXY(sf));
    }

    public ImageDimensions duplicateChangeZ(int z) {
        return new ImageDimensions(extent.duplicateChangeZ(z), res);
    }

    public ImageDimensions duplicateChangeRes(ImageResolution resToAssign) {
        return new ImageDimensions(extent, resToAssign);
    }

    public long getVolume() {
        return extent.getVolume();
    }

    public int getVolumeXY() {
        return extent.getVolumeXY();
    }

    public int getX() {
        return extent.getX();
    }

    public int getY() {
        return extent.getY();
    }

    public int getZ() {
        return extent.getZ();
    }

    public int offset(int x, int y) {
        return extent.offset(x, y);
    }

    public int offset(int x, int y, int z) {
        return extent.offset(x, y, z);
    }

    public boolean contains(Point3d point) {
        return extent.contains(point);
    }

    public boolean contains(Point3i point) {
        return extent.contains(point);
    }

    public final int offset(Point3i point) {
        return extent.offset(point);
    }

    public final int offsetSlice(Point3i point) {
        return extent.offsetSlice(point);
    }

    public ImageResolution getRes() {
        return res;
    }

    public boolean contains(BoundingBox bbox) {
        return extent.contains(bbox);
    }

    @Override
    public String toString() {
        return extent.toString();
    }
}

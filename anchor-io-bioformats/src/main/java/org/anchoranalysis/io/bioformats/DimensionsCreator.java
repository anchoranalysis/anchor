/*-
 * #%L
 * anchor-io-bioformats
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

package org.anchoranalysis.io.bioformats;

import com.google.common.base.Preconditions;
import java.util.Optional;
import java.util.function.DoubleConsumer;
import java.util.function.Function;
import loci.formats.IFormatReader;
import loci.formats.meta.IMetadata;
import lombok.AllArgsConstructor;
import ome.units.UNITS;
import ome.units.quantity.Length;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.dimensions.Resolution;
import org.anchoranalysis.io.bioformats.bean.options.ReadOptions;
import org.anchoranalysis.spatial.box.Extent;
import org.anchoranalysis.spatial.point.Point3d;

@AllArgsConstructor
public class DimensionsCreator {

    private final IMetadata lociMetadata;

    public Dimensions apply(IFormatReader reader, ReadOptions readOptions, int seriesIndex)
            throws CreateException {
        Preconditions.checkArgument(lociMetadata != null);

        Extent extent = new Extent(reader.getSizeX(), reader.getSizeY(), readOptions.sizeZ(reader));

        return new Dimensions(extent, maybeConstructResolution(seriesIndex));
    }

    /**
     * Reads a resolution of the metadata but only if at least X and Y dimensions are defined. If z
     * is undefined its Double.NaN.
     *
     * @throws CreateException
     */
    private Optional<Resolution> maybeConstructResolution(int seriesIndex) throws CreateException {

        // By default the resolution is 1 in all dimensions
        Point3d resolution = new Point3d(Double.NaN, Double.NaN, Double.NaN);

        boolean xUpdated =
                maybeUpdateDimension(
                        metadata -> metadata.getPixelsPhysicalSizeX(seriesIndex), resolution::setX);

        boolean yUpdated =
                maybeUpdateDimension(
                        metadata -> metadata.getPixelsPhysicalSizeY(seriesIndex), resolution::setY);

        maybeUpdateDimension(
                metadata -> metadata.getPixelsPhysicalSizeZ(seriesIndex), resolution::setZ);

        if (xUpdated && yUpdated) {
            return Optional.of(new Resolution(resolution));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Maybe update a particular dimension with resolution-information from metadata
     *
     * @param dimensionFromMetadata gets metadata for a particular dimension
     * @param assigner assigns this dimension's metadata to the {@link Point3d}.
     * @return true if the dimension was assigned, otherwise false.
     */
    private boolean maybeUpdateDimension(
            Function<IMetadata, Length> dimensionFromMetadata, DoubleConsumer assigner) {
        Length length = dimensionFromMetadata.apply(lociMetadata);
        if (length != null) {
            Number converted = length.value(UNITS.METER);

            // A null implies that len can not be converted to meters as units, so we abandon
            if (converted != null) {
                assigner.accept(converted.doubleValue());
                return true;
            }
        }
        return false;
    }
}

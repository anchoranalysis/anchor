/*-
 * #%L
 * anchor-plugin-io
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

package org.anchoranalysis.io.bioformats.bean.writer;

import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.FormatTools;
import loci.formats.meta.IMetadata;
import loci.formats.services.OMEXMLService;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ome.units.UNITS;
import ome.units.quantity.Length;
import ome.xml.model.enums.DimensionOrder;
import ome.xml.model.enums.PixelType;
import ome.xml.model.primitives.PositiveInteger;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.dimensions.Resolution;
import org.anchoranalysis.spatial.Extent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class MetadataUtilities {

    // NOTE: Tidy up exceptions later
    public static IMetadata createMetadata(
            Dimensions dimensions,
            int numberChannels,
            PixelType pixelType,
            boolean makeRGB,
            boolean pretendSeries)
            throws ServiceException, DependencyException {

        ServiceFactory factory = new ServiceFactory();
        OMEXMLService service = factory.getInstance(OMEXMLService.class);
        IMetadata meta = service.createOMEXMLMetadata();

        meta.createRoot();

        int seriesIndex = 0;
        meta.setImageID(String.format("Image:%d", seriesIndex), seriesIndex);
        meta.setPixelsID(String.format("Pixels:%d", seriesIndex), seriesIndex);
        meta.setPixelsBigEndian(Boolean.TRUE, seriesIndex);
        meta.setPixelsBinDataBigEndian(Boolean.TRUE, seriesIndex, 0);
        meta.setPixelsDimensionOrder(DimensionOrder.XYCZT, seriesIndex);
        meta.setPixelsType(pixelType, seriesIndex);

        int effectiveNumberChannels = calculateNumberChannels(makeRGB, numberChannels);
        int effectiveSamplePerPixels = calculateSamplesPerPixel(makeRGB, numberChannels);

        meta.setPixelsSizeC(new PositiveInteger(numberChannels), seriesIndex);

        meta.setPixelsSizeX(new PositiveInteger(dimensions.x()), seriesIndex);
        meta.setPixelsSizeY(new PositiveInteger(dimensions.y()), seriesIndex);

        // We pretend Z-stacks are Time frames as it makes it easier to
        //   view in other software if they are a series
        if (pretendSeries) {
            meta.setPixelsSizeT(new PositiveInteger(dimensions.z()), seriesIndex);
            meta.setPixelsSizeZ(new PositiveInteger(1), seriesIndex);
        } else {
            meta.setPixelsSizeT(new PositiveInteger(1), seriesIndex);
            meta.setPixelsSizeZ(new PositiveInteger(dimensions.z()), seriesIndex);
        }

        if (dimensions.resolution().isPresent()) {
            assignResolution(meta, dimensions.resolution().get(), dimensions.extent()); // NOSONAR
        }

        addChannels(meta, effectiveNumberChannels, effectiveSamplePerPixels, seriesIndex);

        return meta;
    }

    private static void assignResolution(IMetadata meta, Resolution resolution, Extent extent) {
        meta.setPixelsPhysicalSizeX(createLength(resolution.x() * extent.x()), 0);
        meta.setPixelsPhysicalSizeY(createLength(resolution.y() * extent.y()), 0);
        meta.setPixelsPhysicalSizeZ(createLength(resolution.z() * extent.z()), 0);
    }

    private static int calculateNumberChannels(boolean makeRGB, int numberChannels) {
        return makeRGB ? 1 : numberChannels;
    }

    private static int calculateSamplesPerPixel(boolean makeRGB, int numberChannels) {
        // We do the opposite of calculateNumChannels
        return calculateNumberChannels(!makeRGB, numberChannels);
    }

    private static void addChannels(
            IMetadata meta, int numberChannels, int samplesPerPixel, int seriesIndex) {
        for (int i = 0; i < numberChannels; i++) {
            meta.setChannelID(String.format("Channel:%d:%d", seriesIndex, i), seriesIndex, i);
            meta.setChannelSamplesPerPixel(new PositiveInteger(samplesPerPixel), seriesIndex, i);
        }
    }

    private static Length createLength(double valMeters) {
        return FormatTools.createLength(valMeters, UNITS.METER);
    }
}

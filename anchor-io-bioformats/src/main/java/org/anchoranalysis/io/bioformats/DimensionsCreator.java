/* (C)2020 */
package org.anchoranalysis.io.bioformats;

import java.util.function.DoubleConsumer;
import java.util.function.Function;
import loci.formats.IFormatReader;
import loci.formats.meta.IMetadata;
import ome.units.UNITS;
import ome.units.quantity.Length;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.extent.ImageResolution;
import org.anchoranalysis.io.bioformats.bean.options.ReadOptions;

public class DimensionsCreator {

    private IMetadata lociMetadata;

    public DimensionsCreator(IMetadata lociMetadata) {
        super();
        this.lociMetadata = lociMetadata;
    }

    public ImageDimensions apply(IFormatReader reader, ReadOptions readOptions, int seriesIndex) {

        assert (lociMetadata != null);

        Point3d res = new Point3d();

        metadataDim(metadata -> metadata.getPixelsPhysicalSizeX(seriesIndex), res::setX);

        metadataDim(metadata -> metadata.getPixelsPhysicalSizeY(seriesIndex), res::setY);

        metadataDim(metadata -> metadata.getPixelsPhysicalSizeZ(seriesIndex), res::setZ);

        return new ImageDimensions(
                new Extent(reader.getSizeX(), reader.getSizeY(), readOptions.sizeZ(reader)),
                new ImageResolution(res));
    }

    private void metadataDim(Function<IMetadata, Length> funcDimRes, DoubleConsumer setter) {
        Length len = funcDimRes.apply(lociMetadata);
        if (len != null) {
            Double dbl = len.value(UNITS.METER).doubleValue();
            setter.accept(dbl);
        }
    }
}

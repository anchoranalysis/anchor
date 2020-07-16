/* (C)2020 */
package org.anchoranalysis.image.io.generator.raster.series;

import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.io.bean.rasterwriter.RasterWriter;
import org.anchoranalysis.image.io.generator.raster.RasterWriterUtilities;
import org.anchoranalysis.io.generator.SingleFileTypeGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

public class RGBTimeSeriesGenerator extends SingleFileTypeGenerator {

    private ImgStackSeries stackSeries;

    public RGBTimeSeriesGenerator(ImgStackSeries stackSeries) {
        super();
        this.stackSeries = stackSeries;
    }

    @Override
    public void writeToFile(OutputWriteSettings outputWriteSettings, Path filePath)
            throws OutputWriteFailedException {

        try {
            RasterWriter rasterWriter =
                    RasterWriterUtilities.getDefaultRasterWriter(outputWriteSettings);
            rasterWriter.writeTimeSeriesStackByte(stackSeries, filePath, true);
        } catch (RasterIOException e) {
            throw new OutputWriteFailedException(e);
        }
    }

    @Override
    public String getFileExtension(OutputWriteSettings outputWriteSettings) {
        return RasterWriterUtilities.getDefaultRasterFileExtension(outputWriteSettings);
    }

    @Override
    public Optional<ManifestDescription> createManifestDescription() {
        return Optional.of(new ManifestDescription("raster", "rgbTimeSeries"));
    }
}

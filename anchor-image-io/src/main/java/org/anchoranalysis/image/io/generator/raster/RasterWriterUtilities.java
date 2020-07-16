/* (C)2020 */
package org.anchoranalysis.image.io.generator.raster;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.io.bean.rasterwriter.RasterWriter;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RasterWriterUtilities {

    /**
     * Gets the default raster-writer associated with outputWriteSettings
     *
     * @param outputWriteSettings
     * @return a writer (always non-null)
     * @throws RasterIOException if a writer doesn't exist
     */
    public static RasterWriter getDefaultRasterWriter(OutputWriteSettings outputWriteSettings)
            throws RasterIOException {
        RasterWriter defaultWriter =
                (RasterWriter) outputWriteSettings.getWriterInstance(RasterWriter.class);
        if (defaultWriter == null) {
            throw new RasterIOException("No default rasterWriter has been set");
        }
        return defaultWriter;
    }

    public static String getDefaultRasterFileExtension(OutputWriteSettings outputWriteSettings) {
        RasterWriter defaultWriter =
                (RasterWriter) outputWriteSettings.getWriterInstance(RasterWriter.class);
        if (defaultWriter == null) {
            return ".unknown";
        }
        return defaultWriter.dfltExt();
    }
}

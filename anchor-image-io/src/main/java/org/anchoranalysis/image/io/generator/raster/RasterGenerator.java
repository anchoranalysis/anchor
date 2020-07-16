/* (C)2020 */
package org.anchoranalysis.image.io.generator.raster;

import java.nio.file.Path;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.io.bean.rasterwriter.RasterWriter;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.generator.ObjectGenerator;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

public abstract class RasterGenerator extends ObjectGenerator<Stack> {

    public abstract boolean isRGB() throws OutputWriteFailedException;

    @Override
    public void writeToFile(OutputWriteSettings outputWriteSettings, Path filePath)
            throws OutputWriteFailedException {

        Stack stack = generate();
        writeToFile(stack, outputWriteSettings, filePath, isRGB());
    }

    public static void writeToFile(
            Stack stack, OutputWriteSettings outputWriteSettings, Path filePath, boolean rgb)
            throws OutputWriteFailedException {

        try {
            RasterWriter rasterWriter =
                    RasterWriterUtilities.getDefaultRasterWriter(outputWriteSettings);
            rasterWriter.writeStack(stack, filePath, rgb);
        } catch (RasterIOException e) {
            throw new OutputWriteFailedException(e);
        }
    }

    @Override
    public String getFileExtension(OutputWriteSettings outputWriteSettings) {
        return RasterWriterUtilities.getDefaultRasterFileExtension(outputWriteSettings);
    }
}

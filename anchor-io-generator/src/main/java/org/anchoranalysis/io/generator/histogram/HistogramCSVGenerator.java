/* (C)2020 */
package org.anchoranalysis.io.generator.histogram;

import java.nio.file.Path;
import org.anchoranalysis.image.histogram.Histogram;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.generator.IterableGenerator;
import org.anchoranalysis.io.generator.csv.CSVGenerator;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

public class HistogramCSVGenerator extends CSVGenerator implements IterableGenerator<Histogram> {

    public HistogramCSVGenerator() {
        super("histogram");
    }

    private Histogram histogram;
    private boolean ignoreZeros = false;

    @Override
    public Histogram getIterableElement() {
        return histogram;
    }

    @Override
    public void setIterableElement(Histogram element) {
        this.histogram = element;
    }

    @Override
    public Generator getGenerator() {
        return this;
    }

    @Override
    public void writeToFile(OutputWriteSettings outputWriteSettings, Path filePath)
            throws OutputWriteFailedException {
        try {
            HistogramCSVWriter.writeHistogramToFile(histogram, filePath, ignoreZeros);
        } catch (AnchorIOException e) {
            throw new OutputWriteFailedException(e);
        }
    }

    public boolean isIgnoreZeros() {
        return ignoreZeros;
    }

    public void setIgnoreZeros(boolean ignoreZeros) {
        this.ignoreZeros = ignoreZeros;
    }
}

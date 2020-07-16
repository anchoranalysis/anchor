/* (C)2020 */
package org.anchoranalysis.feature.io.csv.writer;

import java.nio.file.Path;
import java.util.List;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.generator.IterableGenerator;
import org.anchoranalysis.io.generator.csv.CSVGenerator;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.csv.CSVWriter;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * Generates a CSV file like a table
 *
 * @author Owen Feehan
 * @param <T> rows-object type
 */
public abstract class TableCSVGenerator<T> extends CSVGenerator implements IterableGenerator<T> {

    private List<String> headerNames;

    private T element;

    public TableCSVGenerator(String manifestFunction, List<String> headerNames) {
        super(manifestFunction);
        this.headerNames = headerNames;
    }

    @Override
    public T getIterableElement() {
        return element;
    }

    @Override
    public void setIterableElement(T element) {
        this.element = element;
    }

    @Override
    public Generator getGenerator() {
        return this;
    }

    @Override
    public void writeToFile(OutputWriteSettings outputWriteSettings, Path filePath)
            throws OutputWriteFailedException {

        try (CSVWriter writer = CSVWriter.create(filePath)) {
            writeRowsAndColumns(writer, element, headerNames);
        } catch (AnchorIOException e) {
            throw new OutputWriteFailedException(e);
        }
    }

    protected abstract void writeRowsAndColumns(CSVWriter writer, T rows, List<String> headerNames)
            throws OutputWriteFailedException;
}

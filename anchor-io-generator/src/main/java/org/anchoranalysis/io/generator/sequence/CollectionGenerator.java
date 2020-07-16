/* (C)2020 */
package org.anchoranalysis.io.generator.sequence;

import java.util.Collection;
import java.util.Optional;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.generator.IterableGenerator;
import org.anchoranalysis.io.manifest.file.FileType;
import org.anchoranalysis.io.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.namestyle.OutputNameStyle;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.bound.BoundOutputManager;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

public class CollectionGenerator<T> implements Generator, IterableGenerator<Collection<T>> {

    private Collection<T> collection;
    private IterableGenerator<T> generator;
    private BoundOutputManager outputManager;
    private int numDigits;
    private boolean checkIfAllowed;
    private String subfolderName;

    public CollectionGenerator(
            String subfolderName,
            IterableGenerator<T> generator,
            BoundOutputManager outputManager,
            int numDigits,
            boolean checkIfAllowed) {
        super();
        this.generator = generator;
        this.outputManager = outputManager;
        this.numDigits = numDigits;
        this.checkIfAllowed = checkIfAllowed;
        this.subfolderName = subfolderName;
    }

    public CollectionGenerator(
            Collection<T> collection,
            String subfolderName,
            IterableGenerator<T> generator,
            BoundOutputManager outputManager,
            int numDigits,
            boolean checkIfAllowed) {
        super();
        this.collection = collection;
        this.generator = generator;
        this.outputManager = outputManager;
        this.numDigits = numDigits;
        this.checkIfAllowed = checkIfAllowed;
        this.subfolderName = subfolderName;
    }

    @Override
    public void write(OutputNameStyle outputNameStyle, BoundOutputManager outputManager)
            throws OutputWriteFailedException {

        writeCollection(subfolderName, outputNameStyle.deriveIndexableStyle(numDigits), 0);
    }

    @Override
    public int write(
            IndexableOutputNameStyle outputNameStyle,
            String index,
            BoundOutputManager outputManager)
            throws OutputWriteFailedException {

        // In this context, we take the index as an indication of the first id to use - and assume
        // the String index is a number
        int indexInt = Integer.parseInt(index);
        return writeCollection(subfolderName, outputNameStyle, indexInt);
    }

    private int writeCollection(
            String subfolderName, IndexableOutputNameStyle outputNameStyle, int startIndex)
            throws OutputWriteFailedException {

        assert (collection != null);

        // We start with id with 0
        GeneratorSequenceIncrementalWriter<T> sequenceWriter =
                new GeneratorSequenceIncrementalWriter<>(
                        outputManager,
                        subfolderName,
                        outputNameStyle,
                        generator,
                        startIndex,
                        checkIfAllowed);

        int numWritten = 0;

        sequenceWriter.start();
        for (T element : collection) {
            sequenceWriter.add(element);
            numWritten++;
        }
        sequenceWriter.end();

        return numWritten;
    }

    @Override
    public Optional<FileType[]> getFileTypes(OutputWriteSettings outputWriteSettings) {
        return generator.getGenerator().getFileTypes(outputWriteSettings);
    }

    @Override
    public Collection<T> getIterableElement() {
        return collection;
    }

    @Override
    public void setIterableElement(Collection<T> element) throws SetOperationFailedException {
        this.collection = element;
    }

    @Override
    public void start() throws OutputWriteFailedException {
        generator.start();
    }

    @Override
    public void end() throws OutputWriteFailedException {
        generator.end();
    }

    @Override
    public Generator getGenerator() {
        return this;
    }
}

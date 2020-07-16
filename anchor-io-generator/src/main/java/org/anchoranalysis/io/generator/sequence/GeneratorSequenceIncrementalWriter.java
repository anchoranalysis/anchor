/* (C)2020 */
package org.anchoranalysis.io.generator.sequence;

import java.util.Optional;
import org.anchoranalysis.io.generator.IterableGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.sequencetype.IncrementalSequenceType;
import org.anchoranalysis.io.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.output.bound.BoundOutputManager;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

public class GeneratorSequenceIncrementalWriter<T> implements GeneratorSequenceIncremental<T> {

    private GeneratorSequenceNonIncrementalWriter<T> delegate;

    private int iter = 0;
    private int startIndex = 0;

    // Automatically create a ManifestDescription for the folder from the Generator
    public GeneratorSequenceIncrementalWriter(
            BoundOutputManager outputManager,
            String subfolderName,
            IndexableOutputNameStyle outputNameStyle,
            IterableGenerator<T> iterableGenerator,
            int startIndex,
            boolean checkIfAllowed) {
        delegate =
                new GeneratorSequenceNonIncrementalWriter<>(
                        outputManager,
                        subfolderName,
                        outputNameStyle,
                        iterableGenerator,
                        checkIfAllowed);
        this.iter = startIndex;
        this.startIndex = startIndex;
    }

    // User-specified ManifestDescription for the folder
    public GeneratorSequenceIncrementalWriter(
            BoundOutputManager outputManager,
            String subfolderName,
            IndexableOutputNameStyle outputNameStyle,
            IterableGenerator<T> iterableGenerator,
            ManifestDescription folderManifestDescription,
            int startIndex,
            boolean checkIfAllowed) {
        delegate =
                new GeneratorSequenceNonIncrementalWriter<>(
                        outputManager,
                        subfolderName,
                        outputNameStyle,
                        iterableGenerator,
                        checkIfAllowed,
                        folderManifestDescription);
        this.startIndex = startIndex;
    }

    @Override
    public void start() throws OutputWriteFailedException {
        delegate.start(new IncrementalSequenceType(startIndex), -1);
    }

    @Override
    public void add(T element) throws OutputWriteFailedException {
        delegate.add(element, String.valueOf(iter));
        iter++;
    }

    @Override
    public void end() throws OutputWriteFailedException {
        delegate.end();
    }

    public boolean isOn() {
        return delegate.isOn();
    }

    public Optional<BoundOutputManager> getSubFolderOutputManager() {
        return delegate.getSubFolderOutputManager();
    }
}

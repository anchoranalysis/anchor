/* (C)2020 */
package org.anchoranalysis.io.generator.sequence;

import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.io.generator.IterableGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.file.FileType;
import org.anchoranalysis.io.manifest.sequencetype.SequenceType;
import org.anchoranalysis.io.manifest.sequencetype.SequenceTypeException;
import org.anchoranalysis.io.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.output.bound.BoundOutputManager;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

public class GeneratorSequenceNonIncrementalWriter<T>
        implements GeneratorSequenceNonIncremental<T> {

    private BoundOutputManager parentOutputManager = null;

    private IterableGenerator<T> iterableGenerator;

    private SequenceType sequenceType;

    private SequenceWriter sequenceWriter;

    private boolean firstAdd = true;

    @Getter @Setter private boolean suppressSubfolder;

    // Automatically create a ManifestDescription for the folder from the Generator
    public GeneratorSequenceNonIncrementalWriter(
            BoundOutputManager outputManager,
            String subfolderName,
            IndexableOutputNameStyle outputNameStyle,
            IterableGenerator<T> iterableGenerator,
            boolean checkIfAllowed) {
        this(
                outputManager,
                subfolderName,
                outputNameStyle,
                iterableGenerator,
                checkIfAllowed,
                null);
    }

    // User-specified ManifestDescription for the folder
    public GeneratorSequenceNonIncrementalWriter(
            BoundOutputManager outputManager,
            String subfolderName,
            IndexableOutputNameStyle outputNameStyle,
            IterableGenerator<T> iterableGenerator,
            boolean checkIfAllowed,
            ManifestDescription folderManifestDescription) {

        if (!outputManager.getOutputWriteSettings().hasBeenInit()) {
            throw new AnchorFriendlyRuntimeException("outputManager has not yet been initialized");
        }

        this.sequenceWriter =
                new SubfolderWriter(
                        outputManager,
                        subfolderName,
                        outputNameStyle,
                        folderManifestDescription,
                        checkIfAllowed);
        this.parentOutputManager = outputManager;
        this.iterableGenerator = iterableGenerator;
    }

    public boolean isOn() {
        return sequenceWriter.isOn();
    }

    private void initOnFirstAdd() throws InitException {

        // For now we only take the first FileType from the generator, we will have to modify this
        // in future
        FileType[] fileTypes =
                iterableGenerator
                        .getGenerator()
                        .getFileTypes(this.parentOutputManager.getOutputWriteSettings())
                        .orElseThrow(
                                () ->
                                        new InitException(
                                                "This operation requires file-types to be defined by the generator"));

        this.sequenceWriter.init(fileTypes, this.sequenceType, this.suppressSubfolder);
    }

    @Override
    public void add(T element, String index) throws OutputWriteFailedException {

        try {
            iterableGenerator.setIterableElement(element);

            // We delay the initialisation of subFolder until the first iteration and we have a
            // valid generator
            if (firstAdd) {

                initOnFirstAdd();
                firstAdd = false;
            }

            // Then output isn't allowed and we should just exit
            if (!sequenceWriter.isOn()) {
                return;
            }

            sequenceType.update(index);
            this.sequenceWriter.write(
                    () -> iterableGenerator.getGenerator(), String.valueOf(index));
        } catch (InitException | SequenceTypeException | SetOperationFailedException e) {
            throw new OutputWriteFailedException(e);
        }
    }

    @Override
    public void start(SequenceType sequenceType, int totalNumAdd)
            throws OutputWriteFailedException {
        iterableGenerator.start();
        this.sequenceType = sequenceType;
    }

    @Override
    public void end() throws OutputWriteFailedException {
        iterableGenerator.end();
    }

    public Optional<BoundOutputManager> getSubFolderOutputManager() {
        return sequenceWriter.getOutputManagerForFiles();
    }
}

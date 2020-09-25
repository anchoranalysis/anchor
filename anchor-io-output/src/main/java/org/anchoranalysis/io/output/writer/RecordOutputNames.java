package org.anchoranalysis.io.output.writer;

import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.ManifestFolderDescription;
import org.anchoranalysis.io.manifest.folder.FolderWriteWithPath;
import org.anchoranalysis.io.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.output.bound.BoundOutputManager;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import lombok.RequiredArgsConstructor;

/**
 * Delegates to another writer while recording output-names passed to functions.
 * 
 * <p>Outputs are recorded differently based upon if they were or were not allowed.
 *  
 * @author Owen Feehan
 *
 */
@RequiredArgsConstructor
public class RecordOutputNames implements Writer {

    // START REQUIRED ARGUMENTS
    /** The writer to delegate to. */
    private final Writer writer;
    
    /** What all outputs that this write processes are added to. */
    private final RecordedOutputs recordedOutputs;
    
    /** Whether to record the output-names used in indexable outputs (usually the contents of sub-directories)/ */
    private final boolean includeIndexableOutputs;
    // END REQUIRED ARGUMENTS

    @Override
    public Optional<BoundOutputManager> createSubdirectory(String outputName,
            ManifestFolderDescription manifestDescription, Optional<FolderWriteWithPath> manifestFolder)
            throws OutputWriteFailedException {
        Optional<BoundOutputManager> outputManager = writer.createSubdirectory(outputName, manifestDescription, manifestFolder);
        recordedOutputs.add(outputName, outputManager.isPresent());
        return outputManager;
    }

    @Override
    public boolean writeSubdirectoryWithGenerator(String outputName, GenerateWritableItem<?> collectionGenerator)
            throws OutputWriteFailedException {
        boolean allowed = writer.writeSubdirectoryWithGenerator(outputName, collectionGenerator);
        recordedOutputs.add(outputName, allowed);
        return allowed;
    }

    @Override
    public int write(IndexableOutputNameStyle outputNameStyle, GenerateWritableItem<?> generator,
            String index) throws OutputWriteFailedException {
        int numberElements = writer.write(outputNameStyle, generator, index);
        if (includeIndexableOutputs) {
            recordedOutputs.add(outputNameStyle.getOutputName(), numberElements!=CheckIfAllowed.NUMBER_ELEMENTS_WRITTEN_NOT_ALLOWED);
        }
        return numberElements;
    }

    @Override
    public boolean write(String outputName, GenerateWritableItem<?> generator)
            throws OutputWriteFailedException {
        boolean allowed = writer.write(outputName, generator);
        recordedOutputs.add(outputName, allowed);
        return allowed;
    }

    @Override
    public Optional<Path> writeGenerateFilename(String outputName, String extension,
            Optional<ManifestDescription> manifestDescription) {
        Optional<Path> filename = writer.writeGenerateFilename(outputName, extension, manifestDescription);
        recordedOutputs.add(outputName, filename.isPresent());
        return filename;
    }
}

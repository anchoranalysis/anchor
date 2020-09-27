package org.anchoranalysis.io.generator;

import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.file.FileType;
import org.anchoranalysis.io.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.namestyle.OutputNameStyle;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.bound.OutputterChecked;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * A {@link Generator} that eventually writes only a single file to the filesystem.
 * 
 * @author Owen Feehan
 *
 * @param <T> iteration-type
 * @param <S> type after any necessary preprocessing 
 */
public abstract class SingleFileTypeGenerator<T,S> implements Generator<T> {

    /**
     * Assigns a new element, and then calls {@link #transform()}.
     * 
     * @param element element to be assigned and then transformed
     * @return the transformed element after necessary preprocessing.
     * @throws OutputWriteFailedException
     */
    public S transform(T element) throws OutputWriteFailedException {
        try {
            assignElement(element);
        } catch (SetOperationFailedException e) {
            throw new OutputWriteFailedException(e);
        }
        return transform();
    }
    
    /**
     * Applies any necessary preprocessing, to create an element suitable for writing to the filesystem.
     * 
     * @return the transformed element after necessary preprocessing.
     * 
     * @throws OutputWriteFailedException if anything goes wrong
     */
    public abstract S transform() throws OutputWriteFailedException;
    
    public abstract void writeToFile(OutputWriteSettings outputWriteSettings, Path filePath)
            throws OutputWriteFailedException;

    public abstract String getFileExtension(OutputWriteSettings outputWriteSettings) throws OperationFailedException;

    public abstract Optional<ManifestDescription> createManifestDescription();
    
    // We delegate to a much simpler method, for single file generators
    @Override
    public void write(OutputNameStyle outputNameStyle, OutputterChecked outputter)
            throws OutputWriteFailedException {
        writeInternal(
                outputNameStyle.getPhysicalName(),
                outputNameStyle.getOutputName(),
                "",
                outputter);
    }

    /**
     * As only a single-file is involved, this methods delegates to a simpler virtual method.
     */
    @Override
    public int write(
            IndexableOutputNameStyle outputNameStyle,
            String index,
            OutputterChecked outputter)
            throws OutputWriteFailedException {

        writeInternal(
                outputNameStyle.getPhysicalName(index),
                outputNameStyle.getOutputName(),
                index,
                outputter);

        return 1;
    }

    // We create a single file type
    @Override
    public Optional<FileType[]> getFileTypes(OutputWriteSettings outputWriteSettings) throws OperationFailedException {
        Optional<ManifestDescription> manifestDescription = createManifestDescription();
        return OptionalUtilities.map( manifestDescription,
                md -> new FileType[] {new FileType(md, getFileExtension(outputWriteSettings))});
    }

    private void writeInternal(
            String filePhysicalNameWithoutExtension,
            String outputName,
            String index,
            OutputterChecked outputter)
            throws OutputWriteFailedException {

        try {
            Path outFilePath =
                    outputter.outFilePath(
                            filePhysicalNameWithoutExtension
                                    + "."
                                    + getFileExtension(outputter.getSettings()));
    
            // First write to the file system, and then write to the operation-recorder. Thi
            writeToFile(outputter.getSettings(), outFilePath);
    
            Optional<ManifestDescription> manifestDescription = createManifestDescription();
            manifestDescription.ifPresent(
                    md ->
                            outputter.writeFileToOperationRecorder(
                                    outputName, outFilePath, md, index));
        } catch (OperationFailedException e) {
            throw new OutputWriteFailedException(e);
        }
    }
}

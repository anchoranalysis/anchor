/* (C)2020 */
package org.anchoranalysis.io.output.writer;

import java.nio.file.Path;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.functional.Operation;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.ManifestFolderDescription;
import org.anchoranalysis.io.manifest.folder.FolderWriteWithPath;
import org.anchoranalysis.io.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.namestyle.OutputNameStyle;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.bound.BoundOutputManager;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

@RequiredArgsConstructor
public class CheckIfAllowed implements Writer {

    // START REQUIRED ARGUMENTS
    /** The associated output-manager */
    private final BoundOutputManager outputManager;

    /** Execute before every operation */
    private final WriterExecuteBeforeEveryOperation preop;

    private final Writer writer;
    // END REQUIRED ARGUMENTS

    @Override
    public Optional<BoundOutputManager> bindAsSubdirectory(
            String outputName,
            ManifestFolderDescription manifestDescription,
            Optional<FolderWriteWithPath> folder)
            throws OutputWriteFailedException {

        if (!outputManager.isOutputAllowed(outputName)) {
            return Optional.empty();
        }

        preop.exec();

        return writer.bindAsSubdirectory(outputName, manifestDescription, folder);
    }

    @Override
    public void writeSubfolder(
            String outputName,
            Operation<? extends WritableItem, OutputWriteFailedException> collectionGenerator)
            throws OutputWriteFailedException {

        if (!outputManager.isOutputAllowed(outputName)) {
            return;
        }

        preop.exec();

        writer.writeSubfolder(outputName, collectionGenerator);
    }

    @Override
    public int write(
            IndexableOutputNameStyle outputNameStyle,
            Operation<? extends WritableItem, OutputWriteFailedException> generator,
            String index)
            throws OutputWriteFailedException {

        if (!outputManager.isOutputAllowed(outputNameStyle.getOutputName())) {
            return -1;
        }

        preop.exec();

        return writer.write(outputNameStyle, generator, index);
    }

    @Override
    public void write(
            OutputNameStyle outputNameStyle,
            Operation<? extends WritableItem, OutputWriteFailedException> generator)
            throws OutputWriteFailedException {

        if (!outputManager.isOutputAllowed(outputNameStyle.getOutputName())) return;

        preop.exec();

        writer.write(outputNameStyle, generator);
    }

    @Override
    public Optional<Path> writeGenerateFilename(
            String outputName,
            String extension,
            Optional<ManifestDescription> manifestDescription,
            String outputNamePrefix,
            String outputNameSuffix,
            String index) {

        if (!outputManager.isOutputAllowed(outputName)) {
            return Optional.empty();
        }

        preop.exec();

        return writer.writeGenerateFilename(
                outputName,
                extension,
                manifestDescription,
                outputNamePrefix,
                outputNameSuffix,
                index);
    }

    @Override
    public OutputWriteSettings getOutputWriteSettings() {
        return outputManager.getOutputWriteSettings();
    }
}

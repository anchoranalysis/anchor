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
import org.anchoranalysis.io.namestyle.IntegerSuffixOutputNameStyle;
import org.anchoranalysis.io.namestyle.OutputNameStyle;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.bound.BoundOutputManager;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

@RequiredArgsConstructor
public class AlwaysAllowed implements Writer {

    // START REQUIRED ARGUMENTS
    /** Bound output manager */
    private final BoundOutputManager bom;

    /** Execute before every operation */
    private final WriterExecuteBeforeEveryOperation preop;
    // END REQUIRED ARGUMENTS

    @Override
    public Optional<BoundOutputManager> bindAsSubdirectory(
            String outputName,
            ManifestFolderDescription manifestDescription,
            Optional<FolderWriteWithPath> folder)
            throws OutputWriteFailedException {

        preop.exec();

        return Optional.of(bom.deriveSubdirectory(outputName, manifestDescription, folder));
    }

    @Override
    public void writeSubfolder(
            String outputName,
            Operation<? extends WritableItem, OutputWriteFailedException> collectionGenerator)
            throws OutputWriteFailedException {

        preop.exec();

        collectionGenerator
                .doOperation()
                .write(new IntegerSuffixOutputNameStyle(outputName, 3), bom);
    }

    @Override
    public int write(
            IndexableOutputNameStyle outputNameStyle,
            Operation<? extends WritableItem, OutputWriteFailedException> generator,
            String index)
            throws OutputWriteFailedException {

        preop.exec();
        return generator.doOperation().write(outputNameStyle, index, bom);
    }

    // Write a file without checking if the outputName is allowed
    @Override
    public void write(
            OutputNameStyle outputNameStyle,
            Operation<? extends WritableItem, OutputWriteFailedException> generator)
            throws OutputWriteFailedException {

        preop.exec();
        generator.doOperation().write(outputNameStyle, bom);
    }

    // A non-generator way of creating outputs, that are still included in the manifest
    // Returns null if output is not allowed
    @Override
    public Optional<Path> writeGenerateFilename(
            String outputName,
            String extension,
            Optional<ManifestDescription> manifestDescription,
            String outputNamePrefix,
            String outputNameSuffix,
            String index) {

        preop.exec();

        Path outPath =
                bom.outFilePath(outputNamePrefix + outputName + outputNameSuffix + "." + extension);

        manifestDescription.ifPresent(
                md -> bom.writeFileToOperationRecorder(outputName, outPath, md, index));
        return Optional.of(outPath);
    }

    @Override
    public OutputWriteSettings getOutputWriteSettings() {
        return bom.getOutputWriteSettings();
    }
}

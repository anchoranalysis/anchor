/* (C)2020 */
package org.anchoranalysis.io.output.bound;

import java.nio.file.Path;
import java.util.Optional;
import lombok.Getter;
import org.anchoranalysis.io.bean.filepath.prefixer.PathWithDescription;
import org.anchoranalysis.io.error.FilePathPrefixerException;
import org.anchoranalysis.io.filepath.prefixer.FilePathPrefix;
import org.anchoranalysis.io.filepath.prefixer.FilePathPrefixerParams;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.ManifestFolderDescription;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.manifest.folder.FolderWriteWithPath;
import org.anchoranalysis.io.manifest.operationrecorder.DualWriterOperationRecorder;
import org.anchoranalysis.io.manifest.operationrecorder.NullWriteOperationRecorder;
import org.anchoranalysis.io.manifest.operationrecorder.WriteOperationRecorder;
import org.anchoranalysis.io.output.bean.OutputManager;
import org.anchoranalysis.io.output.bean.OutputManagerPermissive;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.bean.allowed.OutputAllowed;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.writer.AlwaysAllowed;
import org.anchoranalysis.io.output.writer.CheckIfAllowed;
import org.anchoranalysis.io.output.writer.Writer;
import org.anchoranalysis.io.output.writer.WriterExecuteBeforeEveryOperation;

public class BoundOutputManager {

    @Getter private final OutputManager outputManager;

    @Getter private final FilePathPrefix boundFilePathPrefix;

    @Getter private final OutputWriteSettings outputWriteSettings;

    private WriteOperationRecorder writeOperationRecorder;

    @Getter private LazyDirectoryFactory lazyDirectoryFactory;

    @Getter private final Writer writerAlwaysAllowed;

    @Getter private final Writer writerCheckIfAllowed;

    private WriterExecuteBeforeEveryOperation initIfNeeded;

    /**
     * Constructor - defaulting to a permissive output-manager in a directory and otherwise default
     * settings
     *
     * @param destination directory to associate with output-amanger
     * @param deleteExistingFolder if true this directory if it already exists is deleted before
     *     executing the experiment, otherwise an exception is thrown if it exists.
     */
    public BoundOutputManager(Path destination, boolean deleteExistingFolder) {
        this(
                new OutputManagerPermissive(),
                new FilePathPrefix(destination),
                new OutputWriteSettings(),
                new NullWriteOperationRecorder(),
                deleteExistingFolder);
    }

    /**
     * Constructor
     *
     * @param outputManager output-manager to determine what is included or not
     * @param prefix
     * @param outputWriteSettings
     * @param deleteExistingFolder if true this directory if it already exists is deleted before
     *     executing the experiment, otherwise an exception is thrown if it exists.
     * @param parentInit iff defined, parent initializer to call, before our own initializer is
     *     called.
     */
    public BoundOutputManager(
            OutputManager outputManager,
            FilePathPrefix prefix,
            OutputWriteSettings outputWriteSettings,
            WriteOperationRecorder writeOperationRecorder,
            boolean deleteExistingFolder) {
        this(
                outputManager,
                prefix,
                outputWriteSettings,
                writeOperationRecorder,
                new LazyDirectoryFactory(deleteExistingFolder),
                Optional.empty());
    }

    /**
     * Constructor
     *
     * @param outputManager
     * @param prefix
     * @param outputWriteSettings
     * @param writeOperationRecorder
     * @param parentInit iff defined, parent initializer to call, before our own initializer is
     *     called.
     */
    private BoundOutputManager(
            OutputManager outputManager,
            FilePathPrefix prefix,
            OutputWriteSettings outputWriteSettings,
            WriteOperationRecorder writeOperationRecorder,
            LazyDirectoryFactory lazyDirectoryFactory,
            Optional<WriterExecuteBeforeEveryOperation> parentInit) {

        this.boundFilePathPrefix = prefix;
        this.outputManager = outputManager;
        this.outputWriteSettings = outputWriteSettings;
        this.writeOperationRecorder = writeOperationRecorder;
        this.lazyDirectoryFactory = lazyDirectoryFactory;

        initIfNeeded = lazyDirectoryFactory.createOrReuse(prefix.getFolderPath(), parentInit);
        writerAlwaysAllowed = new AlwaysAllowed(this, initIfNeeded);
        writerCheckIfAllowed = new CheckIfAllowed(this, initIfNeeded, writerAlwaysAllowed);
    }

    /** Adds an additional operation recorder alongside any existing recorders */
    public void addOperationRecorder(WriteOperationRecorder toAdd) {
        this.writeOperationRecorder =
                new DualWriterOperationRecorder(writeOperationRecorder, toAdd);
    }

    /** Derives a BoundOutputManager from a file that is somehow relative to the root directory */
    public BoundOutputManager deriveFromInput(
            PathWithDescription input,
            String experimentIdentifier,
            Optional<ManifestRecorder> manifestRecorder,
            Optional<ManifestRecorder> experimentalManifestRecorder,
            FilePathPrefixerParams context)
            throws BindFailedException {
        try {
            FilePathPrefix fpp =
                    outputManager.prefixForFile(
                            input,
                            experimentIdentifier,
                            manifestRecorder,
                            experimentalManifestRecorder,
                            context);
            return new BoundOutputManager(
                    outputManager,
                    fpp,
                    outputWriteSettings,
                    writeRecorder(manifestRecorder),
                    lazyDirectoryFactory,
                    Optional.of(initIfNeeded));
        } catch (FilePathPrefixerException e) {
            throw new BindFailedException(e);
        }
    }

    /**
     * Derives a bound-output-manager for a (possibly newly created) subdirectory of the existing
     * manager
     *
     * @param subdirectoryName what determines the subdirectory-name
     * @param manifestDescription manifest-description
     * @param manifestFolder manifest-folder
     * @return a bound-output-manager for the subdirectory
     * @throws OutputWriteFailedException
     */
    public BoundOutputManager deriveSubdirectory(
            String subdirectoryName,
            ManifestFolderDescription manifestDescription,
            Optional<FolderWriteWithPath> manifestFolder) {

        // Construct a sub-folder for the desired outputName
        Path folderOut = outFilePath(subdirectoryName);

        // Only change the writeOperationRecorder if we actually pass a folder
        WriteOperationRecorder recorderNew =
                writeFolderToOperationRecorder(folderOut, manifestDescription, manifestFolder);

        return new BoundOutputManager(
                outputManager,
                new FilePathPrefix(folderOut),
                outputWriteSettings,
                recorderNew,
                lazyDirectoryFactory,
                Optional.of(initIfNeeded));
    }

    /**
     * Writes a file-entry to the operation recorder
     *
     * @param outputName output-name
     * @param path path of the file
     * @param manifestDescription the manifest description
     * @param index an index associated with this item (there may be other items with the same name,
     *     but not the same index)
     */
    public void writeFileToOperationRecorder(
            String outputName, Path path, ManifestDescription manifestDescription, String index) {
        writeOperationRecorder.write(
                outputName, manifestDescription, boundFilePathPrefix.relativePath(path), index);
    }

    /**
     * Writes a folder-entry to the operation-recorder (if it exists)
     *
     * @param path path of the folder that is to be written
     * @param manifestDescription the manifest-description
     * @param manifestFolder the associated folder in the manifest
     * @return a write recorder for the sub folder (if it exists) or otherwise the write recorder
     *     associated with the output manager
     */
    private WriteOperationRecorder writeFolderToOperationRecorder(
            Path path,
            ManifestFolderDescription manifestDescription,
            Optional<FolderWriteWithPath> manifestFolder) {
        if (manifestFolder.isPresent()) {
            // Assume the folder are writing to has no path
            return writeOperationRecorder.writeFolder(
                    boundFilePathPrefix.relativePath(path),
                    manifestDescription,
                    manifestFolder.get());
        } else {
            return writeOperationRecorder;
        }
    }

    private static WriteOperationRecorder writeRecorder(
            Optional<ManifestRecorder> manifestRecorder) {
        Optional<WriteOperationRecorder> opt =
                manifestRecorder.map(ManifestRecorder::getRootFolder);
        return opt.orElse(new NullWriteOperationRecorder());
    }

    public boolean isOutputAllowed(String outputName) {
        return outputManager.isOutputAllowed(outputName);
    }

    public OutputAllowed outputAllowedSecondLevel(String key) {
        return outputManager.outputAllowedSecondLevel(key);
    }

    public Path getOutputFolderPath() {
        return boundFilePathPrefix.getFolderPath();
    }

    public Path outFilePath(String filePathRelative) {
        return boundFilePathPrefix.outFilePath(filePathRelative);
    }
}

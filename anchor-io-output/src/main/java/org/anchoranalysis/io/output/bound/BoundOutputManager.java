/*-
 * #%L
 * anchor-io-output
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

package org.anchoranalysis.io.output.bound;

import java.nio.file.Path;
import java.util.Optional;
import lombok.Getter;
import org.anchoranalysis.core.error.friendly.AnchorImpossibleSituationException;
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
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.bean.allowed.OutputAllowed;
import org.anchoranalysis.io.output.bound.directory.BoundDirectory;
import org.anchoranalysis.io.output.writer.RecordedOutputs;
import org.anchoranalysis.io.output.writer.RecordingWriters;

/**
 * An {@link OutputManager} after it has been associated with a particular directory on the filesystem.
 * 
 * @author Owen Feehan
 *
 */
public class BoundOutputManager {

    @Getter private final FilePathPrefix boundFilePathPrefix;

    private WriteOperationRecorder writeOperationRecorder;

    /** The writers associated with this output-manager. */
    @Getter private RecordingWriters writers;

    /** The directory to which the output-manager is bound to. */
    private BoundDirectory directory;

    /** Some variables shared among {@link BoundOutputManager} across successive subdirectories. */
    private final BoundOutputContext context;

    /**
     * Creates, defaulting to a permissive output-manager in a directory, and otherwise default
     * settings.
     *
     * @param destination directory to associate with output-amanger
     * @param deleteExistingFolder if true this directory if it already exists is deleted before
     *     executing the experiment, otherwise an exception is thrown if it exists.
     * @throws BindFailedException
     */
    public static BoundOutputManager createPermissive(
            Path destination, boolean deleteExistingFolder) throws BindFailedException {
        return createExistingWithPrefix(
                new OutputManager(),
                new FilePathPrefix(destination),
                new OutputWriteSettings(),
                new NullWriteOperationRecorder(),
                deleteExistingFolder);
    }

    /**
     * Creates a bound output-manager from an existing {@link OutputManager} with a prefix.
     *
     * @param outputManager output-manager to determine what is included or not
     * @param prefix the prefix
     * @param outputWriteSettings associated settings
     * @param deleteExistingFolder if true this directory if it already exists is deleted before
     *     executing the experiment, otherwise an exception is thrown if it exists.
     * @throws BindFailedException if a directory cannot be created at the intended path.
     */
    public static BoundOutputManager createExistingWithPrefix(
            OutputManager outputManager,
            FilePathPrefix prefix,
            OutputWriteSettings outputWriteSettings,
            WriteOperationRecorder writeOperationRecorder,
            boolean deleteExistingFolder)
            throws BindFailedException {
        return new BoundOutputManager(
            prefix,
            writeOperationRecorder,
            new BoundDirectory(prefix.getFolderPath(), deleteExistingFolder),
            new BoundOutputContext(outputManager, outputWriteSettings)
        );
    }

    /**
     * Constructor
     *
     * @param prefix
     * @param writeOperationRecorder
     * @param parentDirectory the parent-directory in which this {@link BoundOutputManager} will create a new directory.
     * @param context
     */
    private BoundOutputManager(
            FilePathPrefix prefix,
            WriteOperationRecorder writeOperationRecorder,
            BoundDirectory parentDirectory,
            BoundOutputContext context)
            throws BindFailedException {

        this.boundFilePathPrefix = prefix;
        this.writeOperationRecorder = writeOperationRecorder;
        this.context = context;
        
        this.directory = parentDirectory.bindToDirectory(prefix.getFolderPath());
        
        // An operation will always exist in this case, after binding to the path
        this.writers = new RecordingWriters(this, directory.getParentDirectoryCreator().get(), context.getRecordedOutputs());  // NOSONAR
    }
    
    /** 
     * Adds an additional operation recorder alongside any existing recorders.
     */
    public void addOperationRecorder(WriteOperationRecorder toAdd) {
        this.writeOperationRecorder =
                new DualWriterOperationRecorder(writeOperationRecorder, toAdd);
    }

    /** 
     * Derives a {@link BoundOutputManager} from a file that is somehow relative to the root directory.
     */
    public BoundOutputManager deriveFromInput(
            PathWithDescription input,
            String experimentIdentifier,
            Optional<ManifestRecorder> manifestRecorder,
            Optional<ManifestRecorder> experimentalManifestRecorder,
            FilePathPrefixerParams prefixerParams)
            throws BindFailedException {

        try {
            FilePathPrefix prefix =
                    context.getOutputManager().prefixForFile(
                            input,
                            experimentIdentifier,
                            manifestRecorder,
                            experimentalManifestRecorder,
                            prefixerParams);
            return new BoundOutputManager(
                    prefix,
                    writeRecorder(manifestRecorder),
                    directory,
                    context
            );
        } catch (FilePathPrefixerException e) {
            throw new BindFailedException(e);
        }
    }

    /**
     * Derives a bound-output-manager for a (possibly newly created) subdirectory of the existing
     * manager
     *
     * @param subdirectoryName the subdirectory-name
     * @param manifestDescription manifest-description
     * @param manifestFolder manifest-folder
     * @return a bound-output-manager for the subdirectory
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

        try {
            return new BoundOutputManager(
                    new FilePathPrefix(folderOut),
                    recorderNew,
                    directory,
                    context);
        } catch (BindFailedException e) {
            // This exception can only be thrown if the prefix-path doesn't reside within the
            // rootDirectory
            // of the directoryCreator, which should never occur in this class.
            throw new AnchorImpossibleSituationException();
        }
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

    public boolean isOutputAllowed(String outputName) {
        return context.getOutputManager().getOutputsEnabled().isOutputAllowed(outputName);
    }

    public OutputAllowed outputAllowedSecondLevel(String key) {
        return context.getOutputManager().getOutputsEnabled().outputAllowedSecondLevel(key);
    }

    public Path getOutputFolderPath() {
        return boundFilePathPrefix.getFolderPath();
    }

    public Path outFilePath(String filePathRelative) {
        return boundFilePathPrefix.outFilePath(filePathRelative);
    }
    
    public RecordedOutputs recordedOutputs() {
        return writers.recordedOutputs();
    }

    public OutputManager getOutputManager() {
        return context.getOutputManager();
    }

    public OutputWriteSettings getOutputWriteSettings() {
        return context.getSettings();
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
}

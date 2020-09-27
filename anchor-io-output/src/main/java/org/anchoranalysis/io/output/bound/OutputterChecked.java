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
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.io.bean.filepath.prefixer.FilePathPrefixer;
import org.anchoranalysis.io.bean.filepath.prefixer.NamedPath;
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
import org.anchoranalysis.io.output.bean.rules.OutputEnabledRules;
import org.anchoranalysis.io.output.bean.rules.Permissive;
import org.anchoranalysis.io.output.bound.directory.BoundDirectory;
import org.anchoranalysis.io.output.writer.RecordedOutputs;
import org.anchoranalysis.io.output.writer.RecordingWriters;

/**
 * A particular directory on the filesystem in which outputting can occur.
 * 
 * <p>Unlike {@link Outputter}, exceptions are thrown when operations fail.
 * 
 * <p>Further {@link OutputterChecked}s can be derived in sub-directories or with different
 * prefixes.
 * 
 * <p>A prefix can be associated with the outputter, which places a prefix (containing possibly
 * both sub-directory and file-name parts) on output file locations.
 *
 * @author Owen Feehan
 *
 */
public class OutputterChecked {

    @Getter private final FilePathPrefix boundFilePathPrefix;

    private WriteOperationRecorder writeOperationRecorder;

    /** The writers associated with this output-manager. */
    @Getter private RecordingWriters writers;

    /** The directory to which the output-manager is bound to. */
    private BoundDirectory directory;

    /** Which outputs are enabled or not enabled. */
    @Getter private OutputEnabledRules outputsEnabled;
    
    /** The prefixer associated with this context. */
    private Optional<FilePathPrefixer> prefixer;
    
    /** Records the names of all outputs written to, if defined. */
    private Optional<RecordedOutputs> recordedOutputs;
    
    /** Some variables shared among {@link OutputterChecked} across successive subdirectories. */
    private final BoundOutputContext context;

    /**
     * Creates, defaulting to a permissive output-manager in a a particular directory.
     * 
     * <p>Outputs are not recorded.
     *
     * @param pathDirectory directory to associate with output-amanger
     * @param deleteExistingDirectory if true this directory if it already exists is deleted before
     *     executing the experiment, otherwise an exception is thrown if it exists.
     * @throws BindFailedException
     */
    public static OutputterChecked createForDirectoryPermissive(
            Path pathDirectory, boolean deleteExistingDirectory) throws BindFailedException {
        return createFromOutputManager(
                new OutputManager(),
                new FilePathPrefix(pathDirectory),
                new OutputWriteSettings(),
                new NullWriteOperationRecorder(),
                Optional.empty(),
                deleteExistingDirectory);
    }

    /**
     * Creates a bound output-manager from an existing {@link OutputManager} with a prefix.
     *
     * @param outputManager output-manager to determine what is included or not
     * @param prefix the prefix
     * @param outputWriteSettings associated settings
     * @param deleteExistingDirectory if true this directory if it already exists is deleted before
     *     executing the experiment, otherwise an exception is thrown if it exists.
     * @param recordedOutputs if defined, records output-names that are written / not-written in {@link OutputterChecked} (but not any sub-directories thereof)
     * @throws BindFailedException if a directory cannot be created at the intended path.
     */
    public static OutputterChecked createFromOutputManager(
            OutputManager outputManager,
            FilePathPrefix prefix,
            OutputWriteSettings outputWriteSettings,
            WriteOperationRecorder writeOperationRecorder,
            Optional<RecordedOutputs> recordedOutputs,
            boolean deleteExistingDirectory)
            throws BindFailedException {
        return new OutputterChecked(
            prefix,
            writeOperationRecorder,
            new BoundDirectory(prefix.getFolderPath(), deleteExistingDirectory),
            outputManager.getOutputsEnabled(),
            Optional.of(outputManager.getFilePathPrefixer()),
            recordedOutputs,
            new BoundOutputContext(outputWriteSettings)
        );
    }

    /**
     * Constructor
     *
     * @param prefix
     * @param writeOperationRecorder
     * @param parentDirectory the parent-directory in which this {@link OutputterChecked} will create a new directory.
     * @param prefixer the prefixer associated with this output-manager, if one is defined. otherwise the {@link #deriveFromInput} method will not operate.
     * @param recordedOutputs records the names of all outputs written to, if defined.
     * @param context
     */
    private OutputterChecked(
            FilePathPrefix prefix,
            WriteOperationRecorder writeOperationRecorder,
            BoundDirectory parentDirectory,
            OutputEnabledRules outputsEnabled,
            Optional<FilePathPrefixer> prefixer,
            Optional<RecordedOutputs> recordedOutputs,
            BoundOutputContext context)
            throws BindFailedException {

        this.boundFilePathPrefix = prefix;
        this.writeOperationRecorder = writeOperationRecorder;
        this.context = context;
        this.outputsEnabled = outputsEnabled;
        this.prefixer = prefixer;
        this.recordedOutputs = recordedOutputs;
        
        this.directory = parentDirectory.bindToDirectory(prefix.getFolderPath());
        
        // An operation will always exist in this case, after binding to the path
        this.writers = new RecordingWriters(this, directory.getParentDirectoryCreator().get(), recordedOutputs);  // NOSONAR
    }
    
    /** 
     * Adds an additional operation recorder alongside any existing recorders.
     */
    public void addOperationRecorder(WriteOperationRecorder toAdd) {
        this.writeOperationRecorder =
                new DualWriterOperationRecorder(writeOperationRecorder, toAdd);
    }

    /** 
     * Derives a {@link OutputterChecked} from a file that is somehow relative to the root directory.
     * 
     * @param path the full path of an input with an associated-name
     */
    public OutputterChecked deriveFromInput(
            NamedPath path,
            String experimentIdentifier,
            Optional<ManifestRecorder> manifestRecorder,
            Optional<ManifestRecorder> experimentalManifestRecorder,
            FilePathPrefixerParams prefixerParams)
            throws BindFailedException {

        FilePathPrefixer prefixerSelected = prefixer.orElseThrow( () ->
            new BindFailedException( new OperationFailedException("The deriveFromInput operation is not supported on this BoundOutputManager, only on the manager associated with the root directory.")) );
        
        try {
            FilePathPrefix prefix = new PrefixForInput(prefixerSelected, prefixerParams).prefixForFile(
                            path,
                            experimentIdentifier,
                            manifestRecorder,
                            experimentalManifestRecorder
                            );
            return new OutputterChecked(
                    prefix,
                    writeRecorder(manifestRecorder),
                    directory,
                    outputsEnabled,
                    Optional.empty(),   // The prefixer is no longer defined on sub-directories
                    recordedOutputs,
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
    public OutputterChecked deriveSubdirectory(
            String subdirectoryName,
            ManifestFolderDescription manifestDescription,
            Optional<FolderWriteWithPath> manifestFolder) {

        // Construct a sub-directory for the desired outputName
        Path pathSubdirectory = outFilePath(subdirectoryName);

        try {
            return new OutputterChecked(
                    new FilePathPrefix(pathSubdirectory),
                    writeFolderToOperationRecorder(pathSubdirectory, manifestDescription, manifestFolder),
                    directory,
                    new Permissive(), // Allow all outputs in the sub-directory
                    Optional.empty(),   // The prefixer is no longer defined on sub-directories
                    Optional.empty(),   // Output-names are no longer recorded on sub-directories
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

    public Path getOutputFolderPath() {
        return boundFilePathPrefix.getFolderPath();
    }

    public Path outFilePath(String filePathRelative) {
        return boundFilePathPrefix.outFilePath(filePathRelative);
    }
    
    public OutputWriteSettings getSettings() {
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

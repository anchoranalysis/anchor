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

package org.anchoranalysis.io.output.outputter;

import java.nio.file.Path;
import java.util.Optional;
import lombok.Getter;
import org.anchoranalysis.core.error.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.ManifestDirectoryDescription;
import org.anchoranalysis.io.manifest.directory.SubdirectoryBase;
import org.anchoranalysis.io.manifest.operationrecorder.DualWriterOperationRecorder;
import org.anchoranalysis.io.manifest.operationrecorder.WriteOperationRecorder;
import org.anchoranalysis.io.output.bean.OutputManager;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.bean.rules.Permissive;
import org.anchoranalysis.io.output.enabled.multi.MultiLevelOutputEnabled;
import org.anchoranalysis.io.output.outputter.directory.OutputterTarget;
import org.anchoranalysis.io.output.path.DirectoryWithPrefix;
import org.anchoranalysis.io.output.recorded.MultiLevelRecordedOutputs;
import org.anchoranalysis.io.output.recorded.RecordingWriters;

/**
 * A particular directory on the filesystem in which outputting can occur.
 *
 * <p>Unlike {@link Outputter}, exceptions are thrown when operations fail.
 *
 * <p>Further {@link OutputterChecked}s can be derived in sub-directories or with different
 * prefixes.
 *
 * <p>A prefix can be associated with the outputter, which prepends a path (containing possibly both
 * subdirectory and file-name parts) on all outputs.
 *
 * @author Owen Feehan
 */
public class OutputterChecked {

    /** The directory and prefix the output writes to */
    private final OutputterTarget target;

    /** Records a log of outputs written to the filesystem. */
    private Optional<WriteOperationRecorder> writeOperationRecorder;

    /** The writers associated with this output-manager. */
    @Getter private RecordingWriters writers;

    /** Which outputs are enabled or not enabled. */
    @Getter private MultiLevelOutputEnabled outputsEnabled;

    /** Records the names of all outputs written to, if defined. */
    private Optional<MultiLevelRecordedOutputs> recordedOutputs;

    /** General settings for writing outputs. */
    @Getter private OutputWriteSettings settings;

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
        return createWithPrefix(
                new DirectoryWithPrefix(pathDirectory),
                Permissive.INSTANCE,
                new OutputWriteSettings(),
                Optional.empty(),
                Optional.empty(),
                deleteExistingDirectory);
    }

    /**
     * Creates a bound output-manager from an existing {@link OutputManager} with a prefix.
     *
     * @param outputEnabled which outputs are enabled or not
     * @param prefix the prefix
     * @param settings associated settings
     * @param deleteExistingDirectory if true this directory if it already exists is deleted before
     *     executing the experiment, otherwise an exception is thrown if it exists.
     * @param recordedOutputs if defined, records output-names that are written / not-written in
     *     {@link OutputterChecked} (but not any sub-directories thereof)
     * @throws BindFailedException if a directory cannot be created at the intended path.
     */
    public static OutputterChecked createWithPrefix(
            DirectoryWithPrefix prefix,
            MultiLevelOutputEnabled outputEnabled,
            OutputWriteSettings settings,
            Optional<WriteOperationRecorder> writeOperationRecorder,
            Optional<MultiLevelRecordedOutputs> recordedOutputs,
            boolean deleteExistingDirectory)
            throws BindFailedException {
        return new OutputterChecked(
                new OutputterTarget(prefix, deleteExistingDirectory),
                writeOperationRecorder,
                outputEnabled,
                recordedOutputs,
                settings);
    }

    /**
     * Creates using a target.
     *
     * @param target the target-directory and prefix this outputter writes to
     * @param writeOperationRecorder an entry for every outputted filr is written here
     * @param recordedOutputs records the names of all outputs written to, if defined.
     * @param settings general settings for writing outputs.
     */
    private OutputterChecked(
            OutputterTarget target,
            Optional<WriteOperationRecorder> writeOperationRecorder,
            MultiLevelOutputEnabled outputsEnabled,
            Optional<MultiLevelRecordedOutputs> recordedOutputs,
            OutputWriteSettings settings) {
        this.target = target;
        this.writeOperationRecorder = writeOperationRecorder;
        this.settings = settings;
        this.recordedOutputs = recordedOutputs;
        this.outputsEnabled = maybeRecordOutputNames(outputsEnabled);

        this.writers =
                new RecordingWriters(
                        this, target.getParentDirectoryCreator(), recordedOutputs); // NOSONAR
    }

    /** Adds an additional operation recorder alongside any existing recorders. */
    public void addOperationRecorder(WriteOperationRecorder toAdd) {
        if (writeOperationRecorder.isPresent()) {
            this.writeOperationRecorder =
                    Optional.of(
                            new DualWriterOperationRecorder(writeOperationRecorder.get(), toAdd));
        } else {
            this.writeOperationRecorder = Optional.of(toAdd);
        }
    }

    /**
     * Creates a {@link OutputterChecked} with a changed prefix and {@link WriteOperationRecorder}.
     *
     * <p>All other attributes are identical to the {@link OutputterChecked} on which this method is
     * called.
     *
     * @param prefixToAssign prefix to assign
     * @param writeOperationRecorderToAssign writer operator to use for the newly created {@link
     *     OutputterChecked}.
     * @return a newly created identical {@link OutputterChecked} apart from the two aforementioned
     *     changes.
     * @throws BindFailedException
     */
    public OutputterChecked changePrefix(
            DirectoryWithPrefix prefixToAssign,
            Optional<WriteOperationRecorder> writeOperationRecorderToAssign)
            throws BindFailedException {
        return new OutputterChecked(
                target.changePrefix(prefixToAssign),
                writeOperationRecorderToAssign,
                outputsEnabled,
                recordedOutputs,
                settings);
    }

    /**
     * Derives a bound-output-manager for a (possibly newly created) subdirectory of the existing
     * manager
     *
     * @param subdirectoryName the subdirectory-name
     * @param manifestDescription manifest-description
     * @param manifestFolder manifest-folder
     * @param inheritOutputRulesAndRecording if true, the output rules and recording are inherited
     *     from the parent directory. if false, they are not, and all outputs are allowed and are
     *     unrecorded.
     * @return a bound-output-manager for the subdirectory
     */
    public OutputterChecked deriveSubdirectory(
            String subdirectoryName,
            ManifestDirectoryDescription manifestDescription,
            Optional<SubdirectoryBase> manifestFolder,
            boolean inheritOutputRulesAndRecording) {

        // Construct a subdirectory for the desired outputName
        Path pathSubdirectory = makeOutputPath(subdirectoryName);

        try {
            return new OutputterChecked(
                    target.changePrefix(new DirectoryWithPrefix(pathSubdirectory)),
                    writeFolderToOperationRecorder(
                            pathSubdirectory, manifestDescription, manifestFolder),
                    inheritOutputRulesAndRecording
                            ? outputsEnabled
                            : Permissive.INSTANCE, // Allow all outputs in the subdirectory
                    inheritOutputRulesAndRecording
                            ? recordedOutputs
                            : Optional.empty(), // Output-names are no longer recorded on
                    // sub-directories
                    settings);
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
     * @param pathSuffix the final part of the path of the file
     * @param manifestDescription the manifest description
     * @param index an index associated with this item (there may be other items with the same name,
     *     but not the same index)
     */
    public void writeFileToOperationRecorder(
            String outputName,
            Path pathSuffix,
            ManifestDescription manifestDescription,
            String index) {
        writeOperationRecorder.ifPresent(
                writer ->
                        writer.recordWrittenFile(
                                outputName, manifestDescription, relativePath(pathSuffix), index));
    }

    public Path getOutputFolderPath() {
        return target.getFolderPath();
    }

    /**
     * Creates a full absolute path that completes the part of the path present in the outputter
     * with an additional suffix.
     *
     * @param suffix the suffix for the path
     * @return a newly created absolute path, combining directory, prefix (if it exists) and suffix.
     */
    public Path makeOutputPath(String suffix) {
        return target.pathCreator().makePathAbsolute(suffix);
    }

    /**
     * Like {@link #makeOutputPath(String)} but additionally adds an extension.
     *
     * @param suffixWithoutExtension the suffix for the path (without any extension).
     * @return a newly created absolute path, combining directory, prefix (if it exists), suffix and
     *     extension.
     */
    public Path makeOutputPath(String suffixWithoutExtension, String extension) {
        return makeOutputPath(suffixWithoutExtension + "." + extension);
    }

    public DirectoryWithPrefix getPrefix() {
        return target.getPrefix();
    }

    /**
     * Writes a folder-entry to the operation-recorder (if it exists)
     *
     * @param pathSuffix the final part of the path of the file
     * @param manifestDescription the manifest-description
     * @param manifestFolder the associated folder in the manifest
     * @return a write recorder for the sub folder (if it exists) or otherwise the write recorder
     *     associated with the output manager
     */
    private Optional<WriteOperationRecorder> writeFolderToOperationRecorder(
            Path pathSuffix,
            ManifestDirectoryDescription manifestDescription,
            Optional<SubdirectoryBase> manifestFolder) {
        if (manifestFolder.isPresent() && writeOperationRecorder.isPresent()) {
            // Assume the folder are writing to has no path
            return Optional.of(
                    writeOperationRecorder
                            .get()
                            .recordSubdirectoryCreated(
                                    relativePath(pathSuffix),
                                    manifestDescription,
                                    manifestFolder.get()));
        } else {
            return writeOperationRecorder;
        }
    }

    private MultiLevelOutputEnabled maybeRecordOutputNames(MultiLevelOutputEnabled outputsEnabled) {
        if (recordedOutputs.isPresent()) {
            return new RecordOutputNamesMultiLevel(outputsEnabled, recordedOutputs.get());
        } else {
            return outputsEnabled;
        }
    }

    private Path relativePath(Path pathSuffix) {
        return target.pathCreator().makePathRelative(pathSuffix);
    }
}

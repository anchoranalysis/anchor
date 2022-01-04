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
import org.anchoranalysis.core.exception.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.io.output.bean.OutputManager;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.bean.rules.Permissive;
import org.anchoranalysis.io.output.enabled.multi.MultiLevelOutputEnabled;
import org.anchoranalysis.io.output.outputter.directory.OutputterTarget;
import org.anchoranalysis.io.output.path.prefixer.DirectoryWithPrefix;
import org.anchoranalysis.io.output.recorded.MultiLevelRecordedOutputs;
import org.anchoranalysis.io.output.recorded.RecordingWriters;
import org.anchoranalysis.io.output.writer.ElementOutputter;

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

    /** The writers associated with this output-manager. */
    @Getter private RecordingWriters writers;

    /** Which outputs are enabled or not enabled. */
    @Getter private MultiLevelOutputEnabled outputsEnabled;

    /** Records the names of all outputs written to, if defined. */
    private Optional<MultiLevelRecordedOutputs> recordedOutputs;

    /** General settings for writing outputs. */
    @Getter private OutputWriteContext context;

    private Optional<Logger> logger;

    /**
     * Creates, defaulting to a permissive output-manager in a particular directory.
     *
     * <p>Outputs are not recorded.
     *
     * @param pathDirectory directory to associate with output-manager
     * @param deleteExistingDirectory if true this directory if it already exists is deleted before
     *     executing the experiment, otherwise an exception is thrown if it exists.
     * @param logger logger for warning for information messages when outputting
     * @throws BindFailedException
     */
    public static OutputterChecked createForDirectoryPermissive(
            Path pathDirectory, boolean deleteExistingDirectory, Optional<Logger> logger)
            throws BindFailedException {
        return createWithPrefix(
                new DirectoryWithPrefix(pathDirectory),
                Permissive.INSTANCE,
                new OutputWriteContext(),
                Optional.empty(),
                deleteExistingDirectory,
                logger);
    }

    /**
     * Creates a bound output-manager from an existing {@link OutputManager} with a prefix.
     *
     * @param outputEnabled which outputs are enabled or not
     * @param prefix the prefix
     * @param context associated output context
     * @param deleteExistingDirectory if true this directory if it already exists is deleted before
     *     executing the experiment, otherwise an exception is thrown if it exists.
     * @param recordedOutputs if defined, records output-names that are written / not-written in
     *     {@link OutputterChecked} (but not any sub-directories thereof)
     * @param logger logger for warning for information messages when outputting
     * @throws BindFailedException if a directory cannot be created at the intended path.
     */
    public static OutputterChecked createWithPrefix(
            DirectoryWithPrefix prefix,
            MultiLevelOutputEnabled outputEnabled,
            OutputWriteContext context,
            Optional<MultiLevelRecordedOutputs> recordedOutputs,
            boolean deleteExistingDirectory,
            Optional<Logger> logger)
            throws BindFailedException {
        return new OutputterChecked(
                new OutputterTarget(prefix, deleteExistingDirectory),
                outputEnabled,
                recordedOutputs,
                context,
                logger);
    }

    /**
     * Creates using a target.
     *
     * @param target the target-directory and prefix this outputter writes to.
     * @param recordedOutputs records the names of all outputs written to, if defined.
     * @param context settings and user-supplied parameters for writing outputs.
     * @param logger logger for warning for information messages when outputting.
     */
    private OutputterChecked(
            OutputterTarget target,
            MultiLevelOutputEnabled outputsEnabled,
            Optional<MultiLevelRecordedOutputs> recordedOutputs,
            OutputWriteContext context,
            Optional<Logger> logger) {
        this.target = target;
        this.context = context;
        this.recordedOutputs = recordedOutputs;
        this.outputsEnabled = maybeRecordOutputNames(outputsEnabled);
        this.logger = logger;

        // As the logger may change, we supply a lambda to it, instead of the object directly.
        ElementOutputter outputter =
                new ElementOutputter(this, context.getExecutionTimeRecorder(), () -> this.logger);
        this.writers =
                new RecordingWriters(
                        outputter, target.getParentDirectoryCreator(), recordedOutputs); // NOSONAR
    }

    /**
     * Creates a {@link OutputterChecked} with a changed prefix.
     *
     * <p>All other attributes are identical to the {@link OutputterChecked} on which this method is
     * called.
     *
     * @param prefixToAssign prefix to assign.
     * @return a newly created identical {@link OutputterChecked} apart from the two aforementioned
     *     changes.
     * @throws BindFailedException
     */
    public OutputterChecked changePrefix(DirectoryWithPrefix prefixToAssign)
            throws BindFailedException {
        return new OutputterChecked(
                target.changePrefix(prefixToAssign),
                outputsEnabled,
                recordedOutputs,
                context,
                Optional.empty());
    }

    /**
     * Derives a bound-output-manager for a (possibly newly created) subdirectory of the existing
     * manager
     *
     * @param subdirectoryName the subdirectory-name.
     * @param inheritOutputRulesAndRecording if true, the output rules and recording are inherited
     *     from the parent directory. if false, they are not, and all outputs are allowed and are
     *     unrecorded.
     * @return a bound-output-manager for the subdirectory.
     */
    public OutputterChecked deriveSubdirectory(
            String subdirectoryName, boolean inheritOutputRulesAndRecording) {

        // Construct a subdirectory for the desired outputName
        Path pathSubdirectory = makeOutputPath(subdirectoryName);

        try {
            return new OutputterChecked(
                    target.changePrefix(new DirectoryWithPrefix(pathSubdirectory)),
                    inheritOutputRulesAndRecording
                            ? outputsEnabled
                            : Permissive.INSTANCE, // Allow all outputs in the subdirectory
                    inheritOutputRulesAndRecording
                            ? recordedOutputs
                            : Optional.empty(), // Output-names are no longer recorded on
                    // sub-directories
                    context,
                    logger);
        } catch (BindFailedException e) {
            // This exception can only be thrown if the prefix-path doesn't reside within the
            // rootDirectory
            // of the directoryCreator, which should never occur in this class.
            throw new AnchorImpossibleSituationException();
        }
    }

    /**
     * General settings for outputting.
     *
     * @return the settings.
     */
    public OutputWriteSettings getSettings() {
        return context.getSettings();
    }

    public Path getOutputDirectory() {
        return target.getDirectory();
    }

    /**
     * Creates a full absolute path that completes the part of the path present in the outputter
     * with an additional suffix.
     *
     * @param suffix the suffix for the path
     * @return a newly created absolute path, combining directory, prefix (if it exists) and suffix.
     */
    public Path makeOutputPath(String suffix) {
        return target.pathCreator().makePathAbsolute(Optional.of(suffix), Optional.empty(), suffix);
    }

    /**
     * Like {@link #makeOutputPath(String)} but additionally adds an extension.
     *
     * @param suffixWithoutExtension the suffix for the path (without any extension).
     * @param fallbackSuffix if neither a {@code prefix} is defined nor a {@code suffix}, then this
     *     provides a suffix to use so a file isn't only an extension.
     * @return a newly created absolute path, combining directory, prefix (if it exists), suffix and
     *     extension.
     */
    public Path makeOutputPath(
            Optional<String> suffixWithoutExtension, String extension, String fallbackSuffix) {
        return target.pathCreator()
                .makePathAbsolute(suffixWithoutExtension, Optional.of(extension), fallbackSuffix);
    }

    /**
     * Assigns a logger to the outputter, which is used to output warnings or messages when
     * outputting.
     *
     * @param logger the logger to assign
     */
    public void assignLogger(Logger logger) {
        this.logger = Optional.of(logger);
    }

    public DirectoryWithPrefix getPrefix() {
        return target.getPrefix();
    }

    private MultiLevelOutputEnabled maybeRecordOutputNames(MultiLevelOutputEnabled outputsEnabled) {
        if (recordedOutputs.isPresent()) {
            return new RecordOutputNamesMultiLevel(outputsEnabled, recordedOutputs.get());
        } else {
            return outputsEnabled;
        }
    }
}

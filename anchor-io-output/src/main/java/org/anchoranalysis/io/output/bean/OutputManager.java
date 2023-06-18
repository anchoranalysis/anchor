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

package org.anchoranalysis.io.output.bean;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.core.format.ImageFileFormat;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.time.ExecutionTimeRecorder;
import org.anchoranalysis.io.output.bean.path.prefixer.PathPrefixer;
import org.anchoranalysis.io.output.bean.rules.OutputEnabledRules;
import org.anchoranalysis.io.output.enabled.multi.MultiLevelOutputEnabled;
import org.anchoranalysis.io.output.outputter.BindFailedException;
import org.anchoranalysis.io.output.outputter.DirectoryCreationParameters;
import org.anchoranalysis.io.output.outputter.OutputWriteContext;
import org.anchoranalysis.io.output.outputter.OutputterChecked;
import org.anchoranalysis.io.output.path.prefixer.DirectoryWithPrefix;
import org.anchoranalysis.io.output.path.prefixer.PathPrefixerContext;
import org.anchoranalysis.io.output.path.prefixer.PathPrefixerException;
import org.anchoranalysis.io.output.recorded.MultiLevelRecordedOutputs;
import org.anchoranalysis.io.output.recorded.RecordedOutputsWithRules;

/**
 * Responsible for making decisions on where output goes and what form it takes.
 *
 * <p>An output prefix (with directory and/or file-path components) is calculated for each input.
 *
 * <p>Rules can be specified in {@code outputsEnabled} as to which outputs occur or do not occur
 * (from the available outputs in an experiment) when an experiment is run.
 */
public class OutputManager extends AnchorBean<OutputManager> {

    // BEAN PROPERTIES
    /**
     * Determines a prefix to use when outputting a file based upon an input-path.
     *
     * <p>This method is called with a binding path from the input to determine a output prefix for
     * each input to an experiment.
     */
    @BeanField @Getter @Setter private PathPrefixer prefixer;

    /**
     * Whether to silently first delete any existing output at the intended path, or rather throw an
     * error.
     *
     * <p>If true, if an existing output folder (at intended path) is deleted If false, an error is
     * thrown if the folder already exists
     */
    @BeanField @Getter @Setter private boolean silentlyDeleteExisting = false;

    /** General settings (default file extensions, colors etc.) for outputting files. */
    @BeanField @Getter @Setter
    private OutputWriteSettings outputWriteSettings = new OutputWriteSettings();

    /** Which outputs are enabled or not enabled. If null, default rules are used instead. */
    @BeanField @OptionalBean @Getter @Setter private OutputEnabledRules outputsEnabled;
    // END BEAN PROPERTIES

    /**
     * Determines which outputs are enabled or not.
     *
     * @param recordedOutputs where output-names are recorded as used/tested.
     * @return a {@link MultiLevelOutputEnabled} which indicates which outputs are enabled or not.
     */
    public MultiLevelOutputEnabled determineEnabledOutputs(
            RecordedOutputsWithRules recordedOutputs) {
        return recordedOutputs.selectOutputEnabled(Optional.ofNullable(outputsEnabled));
    }

    /**
     * Creates the {@link OutputWriteContext} needed for writing files.
     *
     * @param suggestedFormatToWrite a suggestion on what file-format to write.
     * @param executionTimeRecorder for recording the execution-times of operations.
     * @return newly created context.
     */
    public OutputWriteContext createContextForWriting(
            Optional<ImageFileFormat> suggestedFormatToWrite,
            ExecutionTimeRecorder executionTimeRecorder) {
        return new OutputWriteContext(
                getOutputWriteSettings(), suggestedFormatToWrite, executionTimeRecorder);
    }

    /**
     * Creates an outputter for the experiment in general.
     *
     * <p>i.e. this is not an outputter for a specific job.
     *
     * @param experimentIdentifier if defined, an identifier for the experiment, to be included in
     *     the directory root.
     * @param outputsEnabled which outputs are enabled. This is typically provided via a call to
     *     {@link #determineEnabledOutputs(RecordedOutputsWithRules)}.
     * @param writeContext context needed for writing. This is typically provided via a call to
     *     {@link #createContextForWriting(Optional, ExecutionTimeRecorder)}.
     * @param prefixerContext parameters for the file-path prefixer.
     * @param callUponDirectoryCreation when defined, this {@code consumer} is called (with the
     *     directory path) when the directory is first created, as it is created lazily only when
     *     first needed.
     * @param logger logger for warning for information messages when outputting.
     * @return a newly created outputter.
     * @throws BindFailedException when an outputter cannot be successfully bound to an output
     *     directory.
     */
    public OutputterChecked createExperimentOutputter(
            Optional<String> experimentIdentifier,
            MultiLevelOutputEnabled outputsEnabled,
            Optional<MultiLevelRecordedOutputs> recordedOutputs,
            OutputWriteContext writeContext,
            PathPrefixerContext prefixerContext,
            Optional<Consumer<Path>> callUponDirectoryCreation,
            Optional<Logger> logger)
            throws BindFailedException {

        try {
            DirectoryWithPrefix prefix =
                    prefixer.rootDirectoryPrefix(experimentIdentifier, prefixerContext);

            return OutputterChecked.createWithPrefix(
                    prefix,
                    outputsEnabled,
                    writeContext,
                    recordedOutputs,
                    new DirectoryCreationParameters(
                            silentlyDeleteExisting, callUponDirectoryCreation),
                    logger);

        } catch (PathPrefixerException e) {
            throw new BindFailedException(e);
        }
    }
}

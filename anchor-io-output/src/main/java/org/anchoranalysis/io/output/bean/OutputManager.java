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

import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.core.format.ImageFileFormat;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.system.ExecutionTimeRecorder;
import org.anchoranalysis.io.manifest.Manifest;
import org.anchoranalysis.io.output.bean.path.prefixer.PathPrefixer;
import org.anchoranalysis.io.output.bean.rules.OutputEnabledRules;
import org.anchoranalysis.io.output.outputter.BindFailedException;
import org.anchoranalysis.io.output.outputter.OutputWriteContext;
import org.anchoranalysis.io.output.outputter.OutputterChecked;
import org.anchoranalysis.io.output.path.prefixer.DirectoryWithPrefix;
import org.anchoranalysis.io.output.path.prefixer.PathPrefixerContext;
import org.anchoranalysis.io.output.path.prefixer.PathPrefixerException;
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

    /** General settings (default file extensions, colors etc.) for outtping files. */
    @BeanField @Getter @Setter
    private OutputWriteSettings outputWriteSettings = new OutputWriteSettings();

    /** Which outputs are enabled or not enabled. If null, default rules are used instead. */
    @BeanField @OptionalBean @Getter @Setter private OutputEnabledRules outputsEnabled;
    // END BEAN PROPERTIES

    /**
     * Creates an outputter for the experiment in general.
     *
     * <p>i.e. this is not an outputter for a specific job.
     *
     * @param experimentIdentifier if defined, an identifier for the experiment, to be included in
     *     the directory root.
     * @param manifest where output files are store
     * @param recordedOutputs where output-names are recorded as used/tested
     * @param suggestedFormatToWrite a suggestion on what file-format to write
     * @param prefixerContext parameters for the file-path prefixer
     * @param executionTimeRecorder records execution-times of write operations
     * @param logger logger for warning for information messages when outputting
     * @return a newly created outputter
     * @throws BindFailedException
     */
    public OutputterChecked createExperimentOutputter(
            Optional<String> experimentIdentifier,
            Manifest manifest,
            RecordedOutputsWithRules recordedOutputs,
            Optional<ImageFileFormat> suggestedFormatToWrite,
            PathPrefixerContext prefixerContext,
            ExecutionTimeRecorder executionTimeRecorder,
            Optional<Logger> logger)
            throws BindFailedException {

        try {
            DirectoryWithPrefix prefix =
                    prefixer.rootDirectoryPrefix(experimentIdentifier, prefixerContext);
            manifest.initialize(prefix.getDirectory());
            return OutputterChecked.createWithPrefix(
                    prefix,
                    recordedOutputs.selectOutputEnabled(Optional.ofNullable(outputsEnabled)),
                    new OutputWriteContext(
                            getOutputWriteSettings(),
                            suggestedFormatToWrite,
                            executionTimeRecorder),
                    Optional.of(manifest.getRootDirectory()),
                    recordedOutputs.getRecordedOutputs(),
                    silentlyDeleteExisting,
                    logger);

        } catch (PathPrefixerException e) {
            throw new BindFailedException(e);
        }
    }
}

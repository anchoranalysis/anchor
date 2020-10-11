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
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.output.bean.rules.OutputEnabledRules;
import org.anchoranalysis.io.output.outputter.BindFailedException;
import org.anchoranalysis.io.output.outputter.OutputterChecked;
import org.anchoranalysis.io.output.path.DerivePathException;
import org.anchoranalysis.io.output.path.DirectoryWithPrefix;
import org.anchoranalysis.io.output.path.FilePathPrefixerContext;
import org.anchoranalysis.io.output.path.PathPrefixer;
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
     * <p>This method is called with a binding path from the input to determine a
     * output prefix for each input to an experiment.
     */
    @BeanField @Getter @Setter private PathPrefixer filePathPrefixer;

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
     * @param experimentIdentifier an identifier for the experiment
     * @param manifestRecorder where manifest operations are recorded
     * @param recordedOutputs where output-names are recorded as used/tested
     * @param prefixerContext parameters for the file-path prefixer
     * @return a newly created outputter
     * @throws BindFailedException
     */
    public OutputterChecked createExperimentOutputter(
            String experimentIdentifier,
            ManifestRecorder manifestRecorder,
            RecordedOutputsWithRules recordedOutputs,
            FilePathPrefixerContext prefixerContext)
            throws BindFailedException {

        try {
            DirectoryWithPrefix prefix =
                    filePathPrefixer.rootFolderPrefix(experimentIdentifier, prefixerContext);
            manifestRecorder.init(prefix.getDirectory());

            return OutputterChecked.createWithPrefix(
                    prefix,
                    recordedOutputs.selectOutputEnabled(Optional.ofNullable(outputsEnabled)),
                    getOutputWriteSettings(),
                    manifestRecorder.getRootFolder(),
                    recordedOutputs.getRecordedOutputs(),
                    silentlyDeleteExisting);

        } catch (DerivePathException e) {
            throw new BindFailedException(e);
        }
    }
}

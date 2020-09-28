/*-
 * #%L
 * anchor-experiment
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

package org.anchoranalysis.experiment.bean.io;

import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.experiment.bean.Experiment;
import org.anchoranalysis.experiment.bean.identifier.ExperimentIdentifier;
import org.anchoranalysis.experiment.bean.log.LoggingDestination;
import org.anchoranalysis.experiment.bean.log.ToConsole;
import org.anchoranalysis.experiment.log.StatefulMessageLogger;
import org.anchoranalysis.experiment.task.ParametersExperiment;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.io.error.FilePathPrefixerException;
import org.anchoranalysis.io.generator.text.StringGenerator;
import org.anchoranalysis.io.generator.xml.XMLConfigurationWrapperGenerator;
import org.anchoranalysis.io.manifest.ManifestRecorder;
import org.anchoranalysis.io.output.bean.OutputManager;
import org.anchoranalysis.io.output.outputter.BindFailedException;
import org.anchoranalysis.io.output.outputter.Outputter;
import org.anchoranalysis.io.output.outputter.OutputterChecked;
import org.anchoranalysis.io.output.writer.RecordedOutputs;
import org.apache.commons.lang.time.StopWatch;
import com.google.common.base.Preconditions;

/**
 * An experiment that uses a {@link OutputManager} to control outputting of results (which results
 * are outputted, location etc.)
 *
 * @author Owen Feehan
 */
public abstract class OutputExperiment extends Experiment {

    private static final String OUTPUT_NAME_CONFIG_COPY = "config";
    private static final String OUTPUT_NAME_EXECUTION_TIME = "executionTime";

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private OutputManager output;

    @BeanField @Getter @Setter private LoggingDestination logExperiment = new ToConsole();

    @BeanField @Getter @Setter private ExperimentIdentifier experimentIdentifier;

    /**
     * Whether more detailed log-messages around each experiment occur.
     *
     * <p>if true, then detailed messages around each experiment (name, time, start-stop events
     * etc.) are ALWAYS displayed if false, these will sometimes be hidden (e.g. if the execution of
     * each file is very quick)
     */
    @BeanField @Getter @Setter private boolean forceDetailedLogging = false;
    // END BEAN PROPERTIES

    /* If defined, records output-names that are written / not-written in {@link BoundOutputManager} (but not any sub-directories thereof. */
    private RecordedOutputs recordedOutputs = new RecordedOutputs();
    
    // Runs the experiment on all files
    public void doExperiment(ExperimentExecutionArguments arguments)
            throws ExperimentExecutionException {

        try {
            doExperimentWithParams(createParams(arguments));

        } catch (AnchorIOException e) {
            throw new ExperimentExecutionException(e);
        }
    }

    @Override
    public boolean useDetailedLogging() {
        return forceDetailedLogging;
    }

    private void doExperimentWithParams(ParametersExperiment params)
            throws ExperimentExecutionException {
        try {
            StopWatch stopWatchExperiment = new StopWatch();
            stopWatchExperiment.start();

            initBeforeExec(params);
            execExperiment(params);
            tidyUpAfterExec(params, stopWatchExperiment);
        } finally {
            // An experiment is considered always successful
            params.getLoggerExperiment().close(true);
        }
    }

    protected abstract void execExperiment(ParametersExperiment params)
            throws ExperimentExecutionException;

    private ParametersExperiment createParams(ExperimentExecutionArguments expArgs)
            throws AnchorIOException {

        ManifestRecorder experimentalManifest = new ManifestRecorder();

        String experimentId = experimentIdentifier.identifier(expArgs.getTaskName());

        try {
            OutputterChecked rootOutputter =
                    getOutput()
                            .bindRootFolder(
                                    experimentId,
                                    experimentalManifest,
                                    Optional.of(recordedOutputs),
                                    expArgs.createParamsContext());

            Preconditions.checkArgument(rootOutputter.getSettings().hasBeenInit());

            // Important we bind to a root folder before any log messages go out, as certain log
            //  appenders require the OutputManager to be set before outputting to the correct
            // location
            //  and this only occurs after the call to bindRootFolder()
            return new ParametersExperiment(
                    expArgs,
                    experimentId,
                    Optional.of(experimentalManifest),
                    rootOutputter,
                    getOutput().getFilePathPrefixer(),
                    createLogger(rootOutputter, expArgs),
                    useDetailedLogging());
        } catch (FilePathPrefixerException e) {
            throw new AnchorIOException("Cannot create params-context", e);
        } catch (BindFailedException e) {
            throw new AnchorIOException("Bind failed", e);
        }
    }

    private StatefulMessageLogger createLogger(
            OutputterChecked rootOutputter, ExperimentExecutionArguments expArgs) {
        return logExperiment.createWithConsoleFallback(
                rootOutputter, expArgs, useDetailedLogging());
    }

    private void initBeforeExec(ParametersExperiment params) throws ExperimentExecutionException {
        params.getLoggerExperiment().start();
        OutputExperimentLogHelper.maybeLogStart(params);

        writeConfigCopy(params.getOutputter());

        if (!params.getOutputter().getChecked().getSettings().hasBeenInit()) {
            throw new ExperimentExecutionException("Experiment has not been initialized");
        }
    }

    private void tidyUpAfterExec(ParametersExperiment params, StopWatch stopWatchExperiment) {
        writeExecutionTime(params.getOutputter(), stopWatchExperiment);

        // Outputs after processing
        stopWatchExperiment.stop();

        OutputExperimentLogHelper.maybeLogCompleted(recordedOutputs, params, stopWatchExperiment);
    }

    /** Maybe writes a copy of a configuration */
    private void writeConfigCopy(Outputter rootOutputter) {
        rootOutputter
                .writerSelective()
                .write(
                        OUTPUT_NAME_CONFIG_COPY,
                        () -> new XMLConfigurationWrapperGenerator(getXMLConfiguration()));
    }

    /** Maybe writes the execution time to the filesystem */
    private void writeExecutionTime(
            Outputter rootOutputter, StopWatch stopWatchExperiment) {
        rootOutputter
                .writerSelective()
                .write(
                        OUTPUT_NAME_EXECUTION_TIME,
                        () -> new StringGenerator(Long.toString(stopWatchExperiment.getTime())));
    }
}

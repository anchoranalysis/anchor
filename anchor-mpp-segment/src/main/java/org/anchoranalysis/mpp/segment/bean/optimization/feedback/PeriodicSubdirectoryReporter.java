/*-
 * #%L
 * anchor-mpp-sgmn
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

package org.anchoranalysis.mpp.segment.bean.optimization.feedback;

import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.generator.sequence.OutputSequenceFactory;
import org.anchoranalysis.io.generator.sequence.OutputSequenceIndexed;
import org.anchoranalysis.io.generator.sequence.pattern.OutputPatternIntegerSuffix;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.outputter.OutputterChecked;
import org.anchoranalysis.mpp.feature.energy.marks.VoxelizedMarksWithEnergy;
import org.anchoranalysis.mpp.segment.optimization.feedback.FeedbackBeginParameters;
import org.anchoranalysis.mpp.segment.optimization.feedback.FeedbackEndParameters;
import org.anchoranalysis.mpp.segment.optimization.feedback.ReporterException;
import org.anchoranalysis.mpp.segment.optimization.feedback.period.PeriodReceiver;
import org.anchoranalysis.mpp.segment.optimization.feedback.period.PeriodReceiverException;
import org.anchoranalysis.mpp.segment.optimization.step.Reporting;

public abstract class PeriodicSubdirectoryReporter<T>
        extends ReporterInterval<VoxelizedMarksWithEnergy> {

    protected static final int NUMBER_DIGITS_IN_OUTPUT = 10;

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private String outputName;
    // END BEAN PROPER

    private OutputSequenceIndexed<T, Integer> sequenceWriter;

    private OutputterChecked parentOutputter;

    /** Handles period-receiver updates */
    private class AddToWriter implements PeriodReceiver<VoxelizedMarksWithEnergy> {

        private Optional<T> runningElement;

        @Override
        public void periodStart(Reporting<VoxelizedMarksWithEnergy> reporting)
                throws PeriodReceiverException {

            try {
                Optional<T> element = generateIterableElement(reporting);

                if (element.isPresent()) {
                    runningElement = element;
                } else {
                    if (!runningElement.isPresent()) {
                        throw new PeriodReceiverException(
                                "The first iterable item must be non-empty, but it is empty");
                    }
                }

                sequenceWriter.add(runningElement.get(), reporting.getIteration());

            } catch (OutputWriteFailedException | ReporterException e) {
                throw new PeriodReceiverException(e);
            }
        }

        @Override
        public void periodEnd(Reporting<VoxelizedMarksWithEnergy> reporting) {
            // NOTHING TO DO
        }
    }

    // We setup the manifest from a Generator
    protected void init(Generator<T> generator) // NOSONAR
            throws OutputWriteFailedException {

        OutputPatternIntegerSuffix pattern =
                new OutputPatternIntegerSuffix(
                        outputName, NUMBER_DIGITS_IN_OUTPUT, true, Optional.empty());

        this.sequenceWriter =
                new OutputSequenceFactory<>(generator, getParentOutputter())
                        .incrementingIntegers(pattern, 0, getAggInterval());
    }

    @Override
    public void reportBegin(FeedbackBeginParameters<VoxelizedMarksWithEnergy> optInit)
            throws ReporterException {

        this.parentOutputter = optInit.getInitContext().getOutputter().getChecked();

        // Let's only do this if the output is allowed
        if (parentOutputter.getOutputsEnabled().isOutputEnabled(outputName)) {
            optInit.getPeriodTriggerBank().obtain(getAggInterval(), new AddToWriter());
        }
    }

    @Override
    public void reportEnd(FeedbackEndParameters<VoxelizedMarksWithEnergy> params)
            throws ReporterException {}

    protected abstract Optional<T> generateIterableElement(
            Reporting<VoxelizedMarksWithEnergy> reporting) throws ReporterException;

    protected OutputterChecked getParentOutputter() {
        return parentOutputter;
    }
}

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

package org.anchoranalysis.mpp.sgmn.bean.optscheme.feedback;

import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.anchor.mpp.feature.energy.marks.VoxelizedMarksWithEnergy;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.io.generator.IterableGenerator;
import org.anchoranalysis.io.generator.sequence.GeneratorSequenceNonIncrementalWriter;
import org.anchoranalysis.io.manifest.sequencetype.IncrementalSequenceType;
import org.anchoranalysis.io.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.namestyle.IntegerSuffixOutputNameStyle;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.mpp.sgmn.optscheme.feedback.OptimizationFeedbackEndParams;
import org.anchoranalysis.mpp.sgmn.optscheme.feedback.OptimizationFeedbackInitParams;
import org.anchoranalysis.mpp.sgmn.optscheme.feedback.ReporterException;
import org.anchoranalysis.mpp.sgmn.optscheme.feedback.period.PeriodReceiver;
import org.anchoranalysis.mpp.sgmn.optscheme.feedback.period.PeriodReceiverException;
import org.anchoranalysis.mpp.sgmn.optscheme.step.Reporting;

public abstract class PeriodicSubfolderReporter<T> extends ReporterInterval<VoxelizedMarksWithEnergy> {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private String outputName;
    // END BEAN PROPER

    private GeneratorSequenceNonIncrementalWriter<T> sequenceWriter;

    private BoundOutputManagerRouteErrors parentOutputManager;

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

                sequenceWriter.add(runningElement.get(), String.valueOf(reporting.getIter()));

            } catch (OutputWriteFailedException | ReporterException e) {
                throw new PeriodReceiverException(e);
            }
        }

        @Override
        public void periodEnd(Reporting<VoxelizedMarksWithEnergy> reporting) {
            // NOTHING TO DO
        }
    }

    // We generate an OutputName class from the outputName string
    protected IndexableOutputNameStyle generateOutputNameStyle() {
        return new IntegerSuffixOutputNameStyle(outputName, 10);
    }

    // We setup the manifest from an IterableGenerator
    protected IncrementalSequenceType init(IterableGenerator<T> iterableGenerator)
            throws OutputWriteFailedException {

        IncrementalSequenceType sequenceType = new IncrementalSequenceType();
        sequenceType.setIncrementSize(getAggInterval());
        sequenceType.setStart(0);

        IndexableOutputNameStyle outputStyle = generateOutputNameStyle();
        this.sequenceWriter =
                new GeneratorSequenceNonIncrementalWriter<>(
                        getParentOutputManager().getDelegate(),
                        outputStyle.getOutputName(),
                        outputStyle,
                        iterableGenerator,
                        true);

        this.sequenceWriter.start(sequenceType, -1);

        return sequenceType;
    }

    @Override
    public void reportBegin(OptimizationFeedbackInitParams<VoxelizedMarksWithEnergy> optInit)
            throws ReporterException {

        this.parentOutputManager = optInit.getInitContext().getOutputManager();

        // Let's only do this if the output is allowed
        if (!getParentOutputManager().isOutputAllowed(outputName)) {
            return;
        }

        optInit.getPeriodTriggerBank().obtain(getAggInterval(), new AddToWriter());
    }

    protected abstract Optional<T> generateIterableElement(Reporting<VoxelizedMarksWithEnergy> reporting)
            throws ReporterException;

    protected BoundOutputManagerRouteErrors getParentOutputManager() {
        return parentOutputManager;
    }

    @Override
    public void reportEnd(OptimizationFeedbackEndParams<VoxelizedMarksWithEnergy> optStep)
            throws ReporterException {

        try {
            this.sequenceWriter.end();
        } catch (OutputWriteFailedException e) {
            throw new ReporterException(e);
        }
    }
}

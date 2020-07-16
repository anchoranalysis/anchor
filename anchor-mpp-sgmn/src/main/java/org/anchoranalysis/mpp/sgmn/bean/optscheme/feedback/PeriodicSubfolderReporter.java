/* (C)2020 */
package org.anchoranalysis.mpp.sgmn.bean.optscheme.feedback;

import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.anchor.mpp.feature.nrg.cfg.CfgNRGPixelized;
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

public abstract class PeriodicSubfolderReporter<T> extends ReporterInterval<CfgNRGPixelized> {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private String outputName;
    // END BEAN PROPER

    private GeneratorSequenceNonIncrementalWriter<T> sequenceWriter;

    private BoundOutputManagerRouteErrors parentOutputManager;

    /** Handles period-receiver updates */
    private class AddToWriter implements PeriodReceiver<CfgNRGPixelized> {

        private Optional<T> runningElement;

        @Override
        public void periodStart(Reporting<CfgNRGPixelized> reporting)
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
        public void periodEnd(Reporting<CfgNRGPixelized> reporting) {
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
    public void reportBegin(OptimizationFeedbackInitParams<CfgNRGPixelized> optInit)
            throws ReporterException {

        this.parentOutputManager = optInit.getInitContext().getOutputManager();

        // Let's only do this if the output is allowed
        if (!getParentOutputManager().isOutputAllowed(outputName)) {
            return;
        }

        optInit.getPeriodTriggerBank().obtain(getAggInterval(), new AddToWriter());
    }

    protected abstract Optional<T> generateIterableElement(Reporting<CfgNRGPixelized> reporting)
            throws ReporterException;

    protected BoundOutputManagerRouteErrors getParentOutputManager() {
        return parentOutputManager;
    }

    @Override
    public void reportEnd(OptimizationFeedbackEndParams<CfgNRGPixelized> optStep)
            throws ReporterException {

        try {
            this.sequenceWriter.end();
        } catch (OutputWriteFailedException e) {
            throw new ReporterException(e);
        }
    }
}

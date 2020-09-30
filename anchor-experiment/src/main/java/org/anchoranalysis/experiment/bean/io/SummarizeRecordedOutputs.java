package org.anchoranalysis.experiment.bean.io;

import java.util.Map.Entry;
import org.anchoranalysis.io.output.writer.MultiLevelRecordedOutputs;
import org.anchoranalysis.io.output.writer.RecordedOutputs;
import lombok.AllArgsConstructor;

/**
 * Generates a multiline string summarizing the contents of a {@link MultiLevelRecordedOutputs}.
 * 
 * @author Owen Feehan
 *
 */
@AllArgsConstructor
class SummarizeRecordedOutputs {

    private MultiLevelRecordedOutputs recordedOutputs;
    
    /**
     * A string (one or two lines) summarizing what outputs were written or not-written but
     * possible.
     *
     * @return the string
     */
    public String summarize() {
        if (recordedOutputs.first().isEmpty()) {
            return "No outputs were written as no possible outputs exist.";
        }

        if (recordedOutputs.first().numberEnabled() > 0) {
            return summarizeWritten(recordedOutputs.first());
        } else {
            return String.format(
                    "No outputs were written.%nPossible outputs are: %s.",
                    recordedOutputs.first().summarizeDisabled());
        }
    }
    
    /** Summarizes a situation where at least one output was written. */
    private String summarizeWritten(RecordedOutputs first) {
        StringBuilder builder = new StringBuilder();
        if (first.numberDisabled() > 0) {
            addLineSummary("Enabled", true, builder);
            builder.append( System.lineSeparator() );
            addLineSummary("Disabled", false, builder);
        } else {
            addLineSummary("All possible outputs were written", true, builder);
        }
        return builder.toString();
    }
    
    private void addLineSummary(String prefix, boolean written, StringBuilder builder) {
        builder.append(prefix);
        builder.append(":\t");
        builder.append(recordedOutputs.first().summarizeMultiplex(written));
        addSecondLevelLines(builder, written);
    }
    
    /** 
     * Adds lines describing any second-level outputs, if they exist.
     * 
     * @param builder what to add the lines to
     * @param written if true, only show outputs that were written, otherwise only shown those not written.
     */
    private void addSecondLevelLines(StringBuilder builder, boolean written) {
        for( Entry<String, RecordedOutputs> entry : recordedOutputs.secondEntries() ) {
            int number = entry.getValue().numberMultiplex(written); 
            if (number > 0) {
                builder.append( System.lineSeparator() );
                builder.append("|- ");
                builder.append(entry.getKey());
                builder.append("\t");
                builder.append(entry.getValue().summarizeMultiplex(written));
            }
        }
    }
}

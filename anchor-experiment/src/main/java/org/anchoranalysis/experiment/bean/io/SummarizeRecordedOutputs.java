package org.anchoranalysis.experiment.bean.io;

import java.util.Map.Entry;
import lombok.AllArgsConstructor;
import org.anchoranalysis.io.output.recorded.MultiLevelRecordedOutputs;
import org.anchoranalysis.io.output.recorded.RecordedOutputs;

/**
 * Generates a multiline string summarizing the contents of a {@link MultiLevelRecordedOutputs}.
 *
 * @author Owen Feehan
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

        if (recordedOutputs.first().hasAtLeastOneEnabled()) {
            return summarizeWritten();
        } else {
            return String.format(
                    "No outputs were written.%nPossible outputs are: %s.",
                    recordedOutputs.first().summarizeDisabled());
        }
    }

    /** Summarizes a situation where at least one output was written. */
    private String summarizeWritten() {
        StringBuilder builder = new StringBuilder();
        if (recordedOutputs.hasAtLeastOneDisabled()) {
            addLineSummary("Enabled", true, builder);
            builder.append(System.lineSeparator());
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
     * @param written if true, only show outputs that were written, otherwise only shown those not
     *     written.
     */
    private void addSecondLevelLines(StringBuilder builder, boolean written) {
        for (Entry<String, RecordedOutputs> entry : recordedOutputs.secondEntries()) {
            if (entry.getValue().hasAtLeastOneMultiplex(written)) {
                builder.append(System.lineSeparator());
                builder.append("|- ");
                builder.append(entry.getKey());
                builder.append("\t");
                builder.append(entry.getValue().summarizeMultiplex(written));
            }
        }
    }
}

package org.anchoranalysis.io.output.writer;

import java.util.Optional;
import org.anchoranalysis.io.output.outputter.OutputterChecked;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Two writers that record any output-names that are passed as arguments.
 * 
 * <p>The two writers are:
 * <ul>
 * <li>A {@link CheckIfAllowed} that selectively outputs certain output-names.
 * <li>A {@link AlwaysAllowed} that allows all output-names.
 * </ul>
 * 
 * @author Owen Feehan
 */
@Accessors(fluent=true)
public class RecordingWriters {

    /** A writer that allows all output-names */
    @Getter private final Writer permissive;

    /** A writer that allows only certain selected output-names */
    @Getter private final Writer selective;
    
    /** Id defined, all output-names that are passed as arguments to both writers are recorded here. */
    @Getter private final Optional<RecordedOutputs> recordedOutputs;
    
    /**
     * Creates the two writers.
     * 
     * @param outputter the output-manager with which the writers are associated.
     * @param preop an operation executed before creation of every directory.
     * @param recordedOutputs all output-names that are passed as arguments to both writers are recorded here.
     */
    public RecordingWriters(OutputterChecked outputter, WriterExecuteBeforeEveryOperation preop, Optional<RecordedOutputs> recordedOutputs) {
        this.recordedOutputs = recordedOutputs;
        this.permissive = record(new AlwaysAllowed(outputter, preop));
        this.selective = record(new CheckIfAllowed(outputter.getOutputsEnabled(), preop, permissive));
    }
    
    /**
     * Multiplexes between the {@code selective} and {@code permissive} writers based on a flag.
     * 
     * @param selectSelective if true, {@code selective} is returned, otherwise {@code permissive}.
     * @return the chosen writer
     */
    public Writer multiplex(boolean selectSelective) {
        if (selectSelective) {
            return selective;
        } else {
            return permissive;
        }
    }
    
    private Writer record( Writer writer ) {
        // Indexable outputs are ignored, as it is assumed that the outputName
        // used for the containing directory is the relevant identifier to
        // show the user
        if (recordedOutputs.isPresent()) {
            return new RecordOutputNames(writer, recordedOutputs.get(), false);
        } else {
            return writer;
        }
    }
}

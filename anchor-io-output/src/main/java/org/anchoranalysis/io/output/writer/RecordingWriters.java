package org.anchoranalysis.io.output.writer;

import org.anchoranalysis.io.output.bound.BoundOutputManager;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Two writers that record any output-names that are passed as arguments.
 * 
 * <p>The two writers are:
 * <ul>
 * <li>A {@link CheckIfAllowed} that selectively outputs certain output-names.
 * <li>A {@link AlwaysAllowed} that allows all output-names.
 * 
 * @author Owen Feehan
 */
@Accessors(fluent=true)
public class RecordingWriters {

    /** A writer that allows all output-names */
    @Getter private final Writer alwaysAllowed;

    /** A writer that checks if output-names are allowed. */
    @Getter private final Writer checkIfAllowed;
    
    /** All output-names that are passed as arguments to both writers are recorded here. */
    @Getter private RecordedOutputs recordedOutputs = new RecordedOutputs();
    
    /**
     * Creates the two writers.
     * 
     * @param outputManager the output-manager with which the writers are associated.
     * @param preop an operation executed before creation of every directory.
     */
    public RecordingWriters(BoundOutputManager outputManager, WriterExecuteBeforeEveryOperation preop) {
        alwaysAllowed = record(new AlwaysAllowed(outputManager, preop));
        checkIfAllowed = record(new CheckIfAllowed(outputManager, preop, alwaysAllowed));
    }
    
    private Writer record( Writer writer ) {
        return new RecordOutputNames(writer, recordedOutputs);
    }
}

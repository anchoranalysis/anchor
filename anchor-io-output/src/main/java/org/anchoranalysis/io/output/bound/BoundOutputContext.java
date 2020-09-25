package org.anchoranalysis.io.output.bound;

import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.bean.manager.OutputManager;
import org.anchoranalysis.io.output.writer.RecordedOutputs;
import lombok.RequiredArgsConstructor;
import lombok.Value;

/**
 * Some variables shared among {@link BoundOutputManager} across successive subdirectories.
 * 
 * @author Owen Feehan
 *
 */
@Value @RequiredArgsConstructor
public class BoundOutputContext {
    
    // START REQUIRED ARGUMENTS
    /** The output-manager associated with this context. */
    private OutputManager outputManager;
    
    /** General settings for how to perform outputting. */
    private OutputWriteSettings settings;
    // END REQUIRED ARGUMENTS
    
    /** A single recorded of outputs is shared across all {@link BoundOutputManager} that use this context. */
    private RecordedOutputs recordedOutputs = new RecordedOutputs();
}

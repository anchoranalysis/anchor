package org.anchoranalysis.io.output.bound;

import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import lombok.RequiredArgsConstructor;
import lombok.Value;

/**
 * Some variables shared among {@link OutputterChecked} across successive subdirectories.
 * 
 * @author Owen Feehan
 *
 */
@Value @RequiredArgsConstructor
public class BoundOutputContext {
    
    // START REQUIRED ARGUMENTS
    /** General settings for how to perform outputting. */
    private OutputWriteSettings settings;
    // END REQUIRED ARGUMENTS
}

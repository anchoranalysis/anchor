package org.anchoranalysis.io.generator.sequence;

import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.output.outputter.OutputterChecked;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * Parameters needed for creating an output-sequence.
 * 
 * @author Owen Feehan
 *
 * @param <T> element-type for generator
 */
@AllArgsConstructor @Value
class OutputSequenceParameters<T> {

    private OutputterChecked outputter;
    
    private OutputSequenceDirectory directory;
    
    private Generator<T> generator;
}

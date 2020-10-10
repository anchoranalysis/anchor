package org.anchoranalysis.io.generator.sequence;

import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.generator.sequence.pattern.OutputPattern;
import org.anchoranalysis.io.output.outputter.OutputterChecked;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * An outputter bound together with {@link OutputPattern} and a generator.
 * 
 * @author Owen Feehan
 *
 * @param <T> element-type for generator
 */
@AllArgsConstructor @Value
public class BoundOutputter<T> {

    /** The outputter to be used for the sequence. */
    private OutputterChecked outputter;
    
    private OutputPattern outputPattern;
    
    /** The generator to be (repeatedly) used to write elements in the sequence. */
    private Generator<T> generator;
}

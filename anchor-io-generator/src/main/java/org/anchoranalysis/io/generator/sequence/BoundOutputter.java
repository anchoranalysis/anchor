/*-
 * #%L
 * anchor-io-generator
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

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
package org.anchoranalysis.io.generator.sequence.pattern;

import java.util.Optional;
import org.anchoranalysis.io.output.namestyle.StringSuffixOutputNameStyle;

/**
 * A {@link OutputPattern} that outputs a file-name with a trailing string index.
 * 
 * @author Owen Feehan
 *
 */
public class OutputPatternStringSuffix extends OutputPattern {

    /**
     * Creates <i>without</i> a prefix.
     * 
     * @param outputName the output-name to use, which also determines the subdirectory name if it is not suppressed.
     * @param suppressSubdirectory if true, a separate subdirectory is not created, and rather the outputs occur in the parent directory.
     */
    public OutputPatternStringSuffix(String outputName, boolean suppressSubdirectory) {
        this(outputName, suppressSubdirectory, Optional.empty());
    }

    /**
     * Creates <i>with</i> a prefix.
     * 
     * @param outputName the output-name to use, which also determines the subdirectory name if it is not suppressed.
     * @param suppressSubdirectory if true, a separate subdirectory is not created, and rather the outputs occur in the parent directory.
     * @param prefix a string that appears before the index in each outputted filename
     */
    public OutputPatternStringSuffix(String outputName, boolean suppressSubdirectory, String prefix) {
        this(outputName, suppressSubdirectory, Optional.of(prefix));
    }
    
    private OutputPatternStringSuffix(String outputName, boolean suppressSubdirectory, Optional<String> prefix) {
        super(
           OutputPatternUtilities.maybeSubdirectory(outputName, suppressSubdirectory),
           new StringSuffixOutputNameStyle(outputName, prefix),
           true,
           Optional.empty()
        );
    }
}

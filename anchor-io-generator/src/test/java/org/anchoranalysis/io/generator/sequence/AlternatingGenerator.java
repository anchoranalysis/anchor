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

import lombok.RequiredArgsConstructor;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.manifest.file.FileType;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.output.namestyle.OutputNameStyle;
import org.anchoranalysis.io.output.outputter.OutputterChecked;

@RequiredArgsConstructor
class AlternatingGenerator<T> implements Generator<T> {

    // START REQUIRED ARGUMENTS
    private final Generator<T> first;
    private final Generator<T> second;
    // END REQUIRED ARGUMENTS

    private int counter = 0;

    @Override
    public FileType[] write(T element, OutputNameStyle outputNameStyle, OutputterChecked outputter)
            throws OutputWriteFailedException {
        return selectAndIncrement().write(element, outputNameStyle, outputter);
    }

    @Override
    public FileType[] writeWithIndex(
            T element,
            String index,
            IndexableOutputNameStyle outputNameStyle,
            OutputterChecked outputter)
            throws OutputWriteFailedException {
        return selectAndIncrement().writeWithIndex(element, index, outputNameStyle, outputter);
    }

    private Generator<T> selectAndIncrement() {
        if ((counter++ % 2) == 0) {
            return first;
        } else {
            return second;
        }
    }
}

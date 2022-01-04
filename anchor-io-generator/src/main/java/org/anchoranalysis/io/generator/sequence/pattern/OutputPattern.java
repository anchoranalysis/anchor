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
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.anchoranalysis.io.output.namestyle.IndexableOutputNameStyle;

/**
 * A particular naming pattern files follow when outputted.
 *
 * <p>This involves several aspects:
 *
 * <ul>
 *   <li>An optional subdirectory where the output-sequence is written to, or else the current
 *       output-directory (both relative to the input-output-context).
 *   <li>A naming style (possibly including an index in the filename, and a prefix.suffix) for each
 *       file created in that directory.
 *   <li>An associated output-name that is used as an identifer in forming rules on what outputs are
 *       enabled or not.
 *   <li>Whether to enforce these rules or not, on what outputs are enabled.
 * </ul>
 *
 * along with associated rules for writing.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public abstract class OutputPattern {

    /**
     * The name of a subdirectory in the current context to write to, or if empty, files are written
     * into the current directory context.
     */
    @Getter private final Optional<String> subdirectoryName;

    @Getter private final IndexableOutputNameStyle outputNameStyle;

    /** Whether the output should be checked to see if it is allowed or not. */
    @Getter private final boolean selective;
}

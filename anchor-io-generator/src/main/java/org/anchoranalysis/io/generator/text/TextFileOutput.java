/*-
 * #%L
 * anchor-io-output
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

package org.anchoranalysis.io.generator.text;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * Utility for writing to a text-file at a particular path, but only if it is enabled.
 *
 * <p>If a file is disabled, the methods can be called as normal, but no text-file is creates and no
 * content is written.
 *
 * <p>To write contents, a sequence should be followed:
 *
 * <ol>
 *   <li>Call {@link #start}.
 *   <li>Call methods on {@link #getWriter()} to write content.
 *   <li>Call {@link #end}.
 * </ol>
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
public class TextFileOutput {

    // START REQUIRED ARGUMENTS
    /** The path to which the text-file is written. */
    private final String filePath;
    // END REQUIRED ARGUMENTS

    /** The {@link PrintWriter} used for writing the text file. */
    @Getter private Optional<PrintWriter> writer;

    /**
     * This should be called <i>once</i> before subsequently writing content with {@link
     * #getWriter()}
     *
     * @throws OutputWriteFailedException if unable to write content to {@code filePath}.
     */
    public void start() throws OutputWriteFailedException {
        try {
            this.writer = Optional.of(new PrintWriter(new FileWriter(filePath))); // NOSONAR
        } catch (IOException e) {
            throw new OutputWriteFailedException(e);
        }
    }

    /**
     * Whether the writer is enabled to write to the file-system.
     *
     * @return true iff the writer is enabled, otherwise it is disabled and no content is written.
     */
    public boolean isEnabled() {
        return writer.isPresent();
    }

    /** This should be called <i>once</i> after writing all content with {@link #getWriter()} */
    public void end() {
        writer.ifPresent(PrintWriter::close);
    }
}

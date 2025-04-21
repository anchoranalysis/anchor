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

import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.anchoranalysis.core.exception.InitializeException;
import org.anchoranalysis.io.generator.sequence.pattern.OutputPattern;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.outputter.OutputterChecked;
import org.anchoranalysis.io.output.recorded.RecordingWriters;
import org.anchoranalysis.io.output.writer.ElementSupplier;
import org.anchoranalysis.io.output.writer.ElementWriterSupplier;
import org.anchoranalysis.io.output.writer.Writer;

/**
 * Like {@link RecordingWriters} but for a sequence of items, maybe in a subfolder.
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
@Accessors(fluent = true)
class SequenceWriters {

    // START: REQUIRED ARGUMENTS
    private final RecordingWriters parentWriters;

    private final OutputPattern pattern;

    // END: REQUIRED ARGUMENTS

    /** The associated writers, if they exist. */
    @Getter private Optional<RecordingWriters> writers = Optional.empty();

    public void initialize() throws InitializeException {

        try {
            this.writers = selectWritersMaybeCreateSubdirectory();
        } catch (OutputWriteFailedException e) {
            throw new InitializeException(e);
        }
    }

    public <T> boolean write(
            ElementWriterSupplier<T> generator, ElementSupplier<T> element, String index)
            throws OutputWriteFailedException {

        if (isOn()) {
            return multiplexSelective()
                    .writeWithIndex(pattern.getOutputNameStyle(), generator, element, index);
        } else {
            return false;
        }
    }

    /**
     * Writes an element to the sequence, with neither an name nor an index.
     *
     * <p>This should only be called once for a sequence, as otherwise an existing element will be
     * overwritten.
     *
     * @param <T> the type of element to be written
     * @param writer the writer.
     * @param element the element.
     * @throws OutputWriteFailedException if any error occurs writing the element.
     */
    public <T> void writeWithoutName(ElementWriterSupplier<T> writer, ElementSupplier<T> element)
            throws OutputWriteFailedException {

        if (isOn()) {
            multiplexSelective()
                    .writeWithoutName(
                            pattern.getOutputNameStyle().getOutputName(), writer, element);
        }
    }

    /**
     * Whether the writer is enabled to be used.
     *
     * @return true if the writer is enabled.
     */
    public boolean isOn() {
        return writers.isPresent();
    }

    private Writer multiplexSelective() {
        return this.writers // NOSONAR
                .get()
                .multiplex(pattern.isSelective());
    }

    private Optional<RecordingWriters> selectWritersMaybeCreateSubdirectory()
            throws OutputWriteFailedException {
        if (pattern.getSubdirectoryName().isPresent()) {
            return parentWriters
                    .multiplex(pattern.isSelective())
                    .createSubdirectory(
                            pattern.getSubdirectoryName().get(), // NOSONAR
                            false)
                    .map(OutputterChecked::getWriters);
        } else {
            return Optional.of(parentWriters);
        }
    }
}

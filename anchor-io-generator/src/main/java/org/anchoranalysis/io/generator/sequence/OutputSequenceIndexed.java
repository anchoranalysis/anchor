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
import java.util.function.BiFunction;
import lombok.Getter;
import org.anchoranalysis.core.exception.InitException;
import org.anchoranalysis.core.exception.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.manifest.file.FileType;
import org.anchoranalysis.io.manifest.sequencetype.SequenceType;
import org.anchoranalysis.io.manifest.sequencetype.SequenceTypeException;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.recorded.RecordingWriters;

/**
 * A sequence of outputs that use the same generator with non-incrementing indexes for each output.
 *
 * <p>An {@code index} is associated with each output that must be unique, and must follow only the
 * order expected by {@code sequenceType}.
 *
 * @author Owen Feehan
 * @param <T> element-type in generator
 * @param <S> index-type in sequence
 */
public class OutputSequenceIndexed<T, S> implements OutputSequence {

    private final Generator<T> generator;
    private final SequenceWriters sequenceWriter;
    private final BiFunction<S, String, S> combineIndexWithExtension;

    @Getter private SequenceType<S> sequenceType;

    /**
     * Creates a non-incremental sequence of outputs, passing the index to the {@code sequenceType}
     * without combination with the file extension.
     *
     * @param outputter parameters for the output-sequence
     * @param sequenceType sequenceType the indexes are expected to follow
     * @throws OutputWriteFailedException
     */
    OutputSequenceIndexed(BoundOutputter<T> outputter, SequenceType<S> sequenceType)
            throws OutputWriteFailedException {
        this(outputter, sequenceType, (index, extension) -> index);
    }

    /**
     * Creates a non-incremental sequence of outputs, combining the index with the {@code
     * sequenceType} through a parameterized function.
     *
     * @param outputter parameters for the output-sequence
     * @param sequenceType sequenceType the indexes are expected to follow
     * @param combineIndexWithExtension combines both an index of type {@code S} with the
     *     file-extension to produce the index passed to the {@link SequenceType}.
     * @throws OutputWriteFailedException
     */
    OutputSequenceIndexed(
            BoundOutputter<T> outputter,
            SequenceType<S> sequenceType,
            BiFunction<S, String, S> combineIndexWithExtension)
            throws OutputWriteFailedException {

        if (!outputter.getOutputter().getSettings().hasBeenInitialized()) {
            throw new AnchorFriendlyRuntimeException("outputter has not yet been initialized");
        }

        this.sequenceWriter =
                new SequenceWriters(
                        outputter.getOutputter().getWriters(), outputter.getOutputPattern());
        this.generator = outputter.getGenerator();
        this.sequenceType = sequenceType;
        this.combineIndexWithExtension = combineIndexWithExtension;

        try {
            this.sequenceWriter.init(this.sequenceType);
        } catch (InitException e) {
            throw new OutputWriteFailedException(e);
        }
    }

    @Override
    public boolean isOn() {
        return sequenceWriter.isOn();
    }

    /**
     * Outputs an additional element in the sequence.
     *
     * <p>This method is <i>thread-safe</i>.
     *
     * @param element the element
     * @param index index of the element to output
     * @throws OutputWriteFailedException if the output cannot be successfully written.
     */
    public void add(T element, S index) throws OutputWriteFailedException {
        add(element, Optional.of(index));
    }

    /**
     * Outputs an additional element in the sequence.
     *
     * <p>This method is <i>thread-safe</i>.
     *
     * @param element the element
     * @param index index of the element to output, if it exists. if it doesn't exist, the element
     *     will be written without any name.
     * @throws OutputWriteFailedException if the output cannot be successfully written.
     */
    public void add(T element, Optional<S> index) throws OutputWriteFailedException {

        try {
            // Then output isn't allowed and we should just exit
            if (!sequenceWriter.isOn()) {
                return;
            }

            if (index.isPresent()) {
                Optional<FileType[]> fileTypes =
                        this.sequenceWriter.write(
                                () -> generator, () -> element, String.valueOf(index.get()));
                if (fileTypes.isPresent()) {
                    updateSequence(fileTypes.get(), index.get());
                }
            } else {
                this.sequenceWriter.writeWithoutName(() -> generator, () -> element);
            }

        } catch (SequenceTypeException e) {
            throw new OutputWriteFailedException(e);
        }
    }

    private void updateSequence(FileType[] fileTypes, S index) throws SequenceTypeException {
        synchronized (sequenceType) {
            for (FileType type : fileTypes) {
                sequenceType.update(
                        combineIndexWithExtension.apply(index, type.getFileExtension()));
            }
        }
        sequenceWriter.addFileTypes(fileTypes);
    }

    @Override
    public Optional<RecordingWriters> writers() {
        return sequenceWriter.writers();
    }
}

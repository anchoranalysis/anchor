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
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.friendly.AnchorFriendlyRuntimeException;
import org.anchoranalysis.core.index.SetOperationFailedException;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.manifest.file.FileType;
import org.anchoranalysis.io.manifest.sequencetype.SequenceType;
import org.anchoranalysis.io.manifest.sequencetype.SequenceTypeException;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.recorded.RecordingWriters;
import lombok.Getter;

/**
 * A sequence of outputs that use the same generator with non-incrementing indexes for each output.
 * 
 * <p>An {@code index} is associated with each output that must be unique, and must
 * follow only the order expected by {@code sequenceType}.
 * 
 * @author Owen Feehan
 *
 * @param <T> element-type in generator
 * @param <S> index-type in sequence
 */
public class OutputSequenceIndexed<T,S> implements OutputSequence {

    private final Generator<T> generator;
    private final SequenceWriters sequenceWriter;
    private final OutputWriteSettings settings;

    @Getter private SequenceType<S> sequenceType;
    private boolean firstAdd = true;
    
    /**
     * Creates a non-incremental sequence of outputs.
     * 
     * @param parameters parameters for the output-sequence
     * @param sequenceType sequenceType the indexes are expected to follow
     * @throws OutputWriteFailedException 
     */
    OutputSequenceIndexed(BoundOutputter<T> parameters, SequenceType<S> sequenceType) throws OutputWriteFailedException {

        if (!parameters.getOutputter().getSettings().hasBeenInit()) {
            throw new AnchorFriendlyRuntimeException("outputter has not yet been initialized");
        }

        this.sequenceWriter =
                new SequenceWriters(
                        parameters.getOutputter().getWriters(),
                        parameters.getOutputPattern()
                );
        this.settings = parameters.getOutputter().getSettings();
        this.generator = parameters.getGenerator();
        this.sequenceType = sequenceType;
        generator.start();
        
    }

    @Override
    public boolean isOn() {
        return sequenceWriter.isOn();
    }

    public void add(T element, S index) throws OutputWriteFailedException {

        try {
            generator.assignElement(element);

            // We delay the initialisation of subFolder until the first iteration and we have a
            // valid generator
            if (firstAdd) {

                initOnFirstAdd();
                firstAdd = false;
            }

            // Then output isn't allowed and we should just exit
            if (!sequenceWriter.isOn()) {
                return;
            }

            sequenceType.update(index);
            this.sequenceWriter.write(() -> generator, String.valueOf(index));
        } catch (InitException | SequenceTypeException | SetOperationFailedException e) {
            throw new OutputWriteFailedException(e);
        }
    }

    @Override
    public void close() throws OutputWriteFailedException {
        generator.end();
    }

    @Override
    public Optional<RecordingWriters> writers() {
        return sequenceWriter.writers();
    }

    private void initOnFirstAdd() throws InitException {
        try {
            // For now we only take the first FileType from the generator, we will have to modify
            // this
            // in future
            this.sequenceWriter.init(fileTypes(), this.sequenceType);
        } catch (OperationFailedException e) {
            throw new InitException(e);
        }
    }
    
    private FileType[] fileTypes() throws OperationFailedException {
        return generator
            .getFileTypes(settings)
            .orElseThrow(OutputSequenceIndexed::fileTypesException);
    }
    
    private static OperationFailedException fileTypesException() {
        return new OperationFailedException(
                "This operation requires file-types to be defined by the generator");
    }
}

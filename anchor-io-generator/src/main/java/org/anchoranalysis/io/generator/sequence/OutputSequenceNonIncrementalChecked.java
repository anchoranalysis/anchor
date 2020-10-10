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

public class OutputSequenceNonIncrementalChecked<T> {

    private final Generator<T> generator;
    private final SequenceWriters sequenceWriter;
    private final OutputWriteSettings settings;

    private SequenceType sequenceType;
    private boolean firstAdd = true;
    
    /**
     * Creates a non-incremental sequence of outputs.
     * 
     * @param parameters parameters for the output-sequence
     */
    OutputSequenceNonIncrementalChecked(OutputSequenceParameters<T> parameters) {

        if (!parameters.getOutputter().getSettings().hasBeenInit()) {
            throw new AnchorFriendlyRuntimeException("outputter has not yet been initialized");
        }

        this.sequenceWriter =
                new SequenceWriters(
                        parameters.getOutputter().getWriters(),
                        parameters.getDirectory()
                );
        this.settings = parameters.getOutputter().getSettings();
        this.generator = parameters.getGenerator();
    }

    public boolean isOn() {
        return sequenceWriter.isOn();
    }

    public void add(T element, String index) throws OutputWriteFailedException {

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

    public void start(SequenceType sequenceType) throws OutputWriteFailedException {
        generator.start();
        this.sequenceType = sequenceType;
    }

    public void end() throws OutputWriteFailedException {
        generator.end();
    }

    public Optional<RecordingWriters> writers() {
        return sequenceWriter.writers();
    }

    private void initOnFirstAdd() throws InitException {
        try {
            // For now we only take the first FileType from the generator, we will have to modify
            // this
            // in future
            FileType[] fileTypes =
                    generator
                            .getFileTypes(settings)
                            .orElseThrow(
                                    () ->
                                            new InitException(
                                                    "This operation requires file-types to be defined by the generator"));

            this.sequenceWriter.init(fileTypes, this.sequenceType);
        } catch (OperationFailedException e) {
            throw new InitException(e);
        }
    }
}

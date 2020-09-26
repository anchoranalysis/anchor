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
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.file.FileType;
import org.anchoranalysis.io.manifest.sequencetype.SequenceType;
import org.anchoranalysis.io.manifest.sequencetype.SequenceTypeException;
import org.anchoranalysis.io.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.output.bound.BoundOutputManager;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.writer.RecordingWriters;

public class GeneratorSequenceNonIncremental<T> {

    // totalNumAdd indicates in advance, how many times add will be called
    // If this is unknown, it should be set to -1
    // Not all writers support additions when this is unknown
    private BoundOutputManager parentOutputManager = null;

    private Generator<T> generator;

    private SequenceType sequenceType;

    private SequenceWriter sequenceWriter;

    private boolean firstAdd = true;

    private boolean suppressSubfolder;

    // Automatically create a ManifestDescription for the folder from the Generator
    public GeneratorSequenceNonIncremental(
            BoundOutputManager outputManager,
            String subfolderName,
            IndexableOutputNameStyle outputNameStyle,
            Generator<T> generator,
            boolean checkIfAllowed, boolean suppressSubfolder) {
        this(
                outputManager,
                subfolderName,
                outputNameStyle,
                generator,
                checkIfAllowed,
                suppressSubfolder,
                null);
    }

    // User-specified ManifestDescription for the folder
    public GeneratorSequenceNonIncremental(
            BoundOutputManager outputManager,
            String subfolderName,
            IndexableOutputNameStyle outputNameStyle,
            Generator<T> generator,
            boolean checkIfAllowed,
            boolean suppressSubfolder,
            ManifestDescription folderManifestDescription) {

        if (!outputManager.getOutputWriteSettings().hasBeenInit()) {
            throw new AnchorFriendlyRuntimeException("outputManager has not yet been initialized");
        }

        this.sequenceWriter =
                new SequenceWriters(
                        outputManager.getWriters(),
                        subfolderName,
                        outputNameStyle,
                        folderManifestDescription,
                        checkIfAllowed);
        this.parentOutputManager = outputManager;
        this.generator = generator;
        this.suppressSubfolder = suppressSubfolder;
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
            this.sequenceWriter.write(
                    () -> generator, String.valueOf(index));
        } catch (InitException | SequenceTypeException | SetOperationFailedException e) {
            throw new OutputWriteFailedException(e);
        }
    }

    public void start(SequenceType sequenceType, int totalNumAdd)
            throws OutputWriteFailedException {
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
            // For now we only take the first FileType from the generator, we will have to modify this
            // in future
            FileType[] fileTypes =
                    generator
                            .getFileTypes(this.parentOutputManager.getOutputWriteSettings())
                            .orElseThrow(
                                    () ->
                                            new InitException(
                                                    "This operation requires file-types to be defined by the generator"));
    
            this.sequenceWriter.init(fileTypes, this.sequenceType, this.suppressSubfolder);
        } catch (OperationFailedException e) {
            throw new InitException(e);
        }
    }
}

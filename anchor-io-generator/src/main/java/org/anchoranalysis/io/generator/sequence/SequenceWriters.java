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

import java.util.Arrays;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.anchoranalysis.core.exception.InitException;
import org.anchoranalysis.io.generator.sequence.pattern.OutputPattern;
import org.anchoranalysis.io.manifest.ManifestDirectoryDescription;
import org.anchoranalysis.io.manifest.file.FileType;
import org.anchoranalysis.io.manifest.sequencetype.SequenceType;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.outputter.OutputterChecked;
import org.anchoranalysis.io.output.recorded.RecordingWriters;
import org.anchoranalysis.io.output.writer.ElementSupplier;
import org.anchoranalysis.io.output.writer.ElementWriterSupplier;

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

    @Getter private Optional<RecordingWriters> writers = Optional.empty();

    private IndexableSubdirectory directoryManifest;

    public void init(SequenceType<?> sequenceType) throws InitException {

        this.directoryManifest = new IndexableSubdirectory(pattern.getOutputNameStyle());

        try {
            this.writers =
                    selectWritersMaybeCreateSubdirectory(
                            createDirectoryDescription(sequenceType), directoryManifest);
        } catch (OutputWriteFailedException e) {
            throw new InitException(e);
        }
    }

    public void addFileTypes(FileType[] fileTypes) {
        Arrays.stream(fileTypes).forEach(directoryManifest::addFileType);
    }

    public <T> Optional<FileType[]> write(
            ElementWriterSupplier<T> generator, ElementSupplier<T> element, String index)
            throws OutputWriteFailedException {

        if (isOn()) {
            return this.writers // NOSONAR
                    .get()
                    .multiplex(pattern.isSelective())
                    .writeWithIndex(pattern.getOutputNameStyle(), generator, element, index);
        } else {
            return Optional.empty();
        }
    }

    public boolean isOn() {
        return writers.isPresent();
    }

    private Optional<RecordingWriters> selectWritersMaybeCreateSubdirectory(
            ManifestDirectoryDescription directoryDescription, IndexableSubdirectory subdirectory)
            throws OutputWriteFailedException {
        if (pattern.getSubdirectoryName().isPresent()) {
            return parentWriters
                    .multiplex(pattern.isSelective())
                    .createSubdirectory(
                            pattern.getSubdirectoryName().get(), // NOSONAR
                            directoryDescription,
                            Optional.of(subdirectory),
                            false)
                    .map(OutputterChecked::getWriters);
        } else {
            return Optional.of(parentWriters);
        }
    }

    private ManifestDirectoryDescription createDirectoryDescription(SequenceType<?> sequenceType) {
        return new ManifestDirectoryDescription(pattern.getManifestDescription(), sequenceType);
    }
}

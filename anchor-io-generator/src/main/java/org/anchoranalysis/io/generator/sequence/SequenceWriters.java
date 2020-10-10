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
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.generator.sequence.pattern.OutputPattern;
import org.anchoranalysis.io.manifest.ManifestFolderDescription;
import org.anchoranalysis.io.manifest.file.FileType;
import org.anchoranalysis.io.manifest.folder.FolderWriteIndexableOutputName;
import org.anchoranalysis.io.manifest.sequencetype.SequenceType;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.outputter.OutputterChecked;
import org.anchoranalysis.io.output.recorded.RecordingWriters;
import org.anchoranalysis.io.output.writer.GenerateWritableItem;

/**
 * Like {@link RecordingWriters} but for a sequence of items, maybe in a subfolder.
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor @Accessors(fluent=true)
class SequenceWriters {

    // START: REQUIRED ARGUMENTS
    private final RecordingWriters parentWriters;
    
    private final OutputPattern pattern;
    // END: REQUIRED ARGUMENTS

    @Getter private Optional<RecordingWriters> writers = Optional.empty();

    public void init(FileType[] fileTypes, SequenceType<?> sequenceType) throws InitException {

        if (fileTypes.length == 0) {
            throw new InitException("The generator has no associated FileTypes");
        }

        ManifestFolderDescription folderDescription =
                new ManifestFolderDescription(pattern.createFolderDescription(fileTypes), sequenceType);

        // FolderWriteIndexableOutputName
        FolderWriteIndexableOutputName subFolderWrite =
                new FolderWriteIndexableOutputName(pattern.getOutputNameStyle());
        for (FileType fileType : fileTypes) {
            subFolderWrite.addFileType(fileType);
        }

        try {
            this.writers = selectWritersMaybeCreateSubdirectory(folderDescription, subFolderWrite);
        } catch (OutputWriteFailedException e) {
            throw new InitException(e);
        }
    }

    public void write(GenerateWritableItem<Generator<?>> generator, String index)
            throws OutputWriteFailedException {

        if (!isOn()) {
            return;
        }

        this.writers // NOSONAR
                .get()
                .multiplex(pattern.isSelective())
                .write(pattern.getOutputNameStyle(), generator, index);
    }

    public boolean isOn() {
        return writers.isPresent();
    }

    private Optional<RecordingWriters> selectWritersMaybeCreateSubdirectory(
            ManifestFolderDescription folderDescription,
            FolderWriteIndexableOutputName subFolderWrite)
            throws OutputWriteFailedException {
        if (pattern.getSubdirectoryName().isPresent()) {
            return parentWriters
                    .multiplex(pattern.isSelective())
                    .createSubdirectory(
                            pattern.getSubdirectoryName().get(),  // NOSONAR
                            folderDescription,
                            Optional.of(subFolderWrite),
                            false)
                    .map(OutputterChecked::getWriters);
        } else {
            return Optional.of(parentWriters);
        }
    }
}

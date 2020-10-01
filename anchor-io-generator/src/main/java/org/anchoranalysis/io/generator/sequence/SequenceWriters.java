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
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.ManifestFolderDescription;
import org.anchoranalysis.io.manifest.file.FileType;
import org.anchoranalysis.io.manifest.folder.FolderWriteIndexableOutputName;
import org.anchoranalysis.io.manifest.sequencetype.SequenceType;
import org.anchoranalysis.io.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.outputter.OutputterChecked;
import org.anchoranalysis.io.output.recorded.RecordingWriters;
import org.anchoranalysis.io.output.writer.GenerateWritableItem;

/**
 * Like {@link RecordingWriters} but for a sequence of items, maybe in a subfolder.
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
class SequenceWriters {

    // START: REQUIRED ARGUMENTS
    private final RecordingWriters parentWriters;
    private final Optional<String> subfolderName;
    private final IndexableOutputNameStyle outputNameStyle;
    private final ManifestDescription folderManifestDescription;
    private final boolean selectSelective;
    // END: REQUIRED ARGUMENTS

    private Optional<RecordingWriters> writers = Optional.empty();

    public void init(FileType[] fileTypes, SequenceType sequenceType) throws InitException {

        if (fileTypes.length == 0) {
            throw new InitException("The generator has no associated FileTypes");
        }

        ManifestFolderDescription folderDescription =
                new ManifestFolderDescription(createFolderDescription(fileTypes), sequenceType);

        // FolderWriteIndexableOutputName
        FolderWriteIndexableOutputName subFolderWrite =
                new FolderWriteIndexableOutputName(outputNameStyle);
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

        this.writers
                .get() // NOSONAR
                .multiplex(selectSelective)
                .write(outputNameStyle, generator, index);
    }

    public Optional<RecordingWriters> writers() {
        return writers;
    }

    public boolean isOn() {
        return writers.isPresent();
    }

    // Requires the generator to be in a valid state
    private ManifestDescription createFolderDescription(FileType[] fileTypes) {
        if (folderManifestDescription != null) {
            return folderManifestDescription;
        } else {
            return guessFolderDescriptionFromFiles(fileTypes);
        }
    }

    // Attempts to come up with a description of the folder from the underlying file template
    //  If there is only one filetype, we use it
    //  If there is more than one, we look for bits which are the same
    private static ManifestDescription guessFolderDescriptionFromFiles(FileType[] fileTypes) {

        assert (fileTypes.length > 0);

        if (fileTypes.length == 1) {
            return fileTypes[0].getManifestDescription();
        }

        String function = "";
        String type = "";
        boolean first = true;
        for (FileType fileType : fileTypes) {
            String functionItr = fileType.getManifestDescription().getFunction();
            String typeItr = fileType.getManifestDescription().getType();

            if (first) {
                // FIRST ITERATION
                function = functionItr;
                type = typeItr;
                first = false;
            } else {
                // SUBSEQUENT ITERATIONS
                if (!functionItr.equals(function)) {
                    function = "combined";
                }

                if (!typeItr.equals(type)) {
                    type = "combined";
                }
            }
        }

        return new ManifestDescription(type, function);
    }

    private Optional<RecordingWriters> selectWritersMaybeCreateSubdirectory(
            ManifestFolderDescription folderDescription,
            FolderWriteIndexableOutputName subFolderWrite)
            throws OutputWriteFailedException {
        if (subfolderName.isPresent()) {
            return parentWriters
                    .multiplex(selectSelective)
                    .createSubdirectory(
                            subfolderName.get(), folderDescription, Optional.of(subFolderWrite))
                    .map(OutputterChecked::getWriters);
        } else {
            return Optional.of(parentWriters);
        }
    }
}

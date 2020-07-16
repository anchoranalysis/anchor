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
/* (C)2020 */
package org.anchoranalysis.io.generator.sequence;

import java.util.Optional;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.functional.Operation;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.ManifestFolderDescription;
import org.anchoranalysis.io.manifest.file.FileType;
import org.anchoranalysis.io.manifest.folder.FolderWriteIndexableOutputName;
import org.anchoranalysis.io.manifest.sequencetype.SequenceType;
import org.anchoranalysis.io.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.output.bound.BoundOutputManager;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.writer.Writer;

public class SubfolderWriter implements SequenceWriter {

    private BoundOutputManager parentOutputManager;
    private IndexableOutputNameStyle outputNameStyle;
    private ManifestDescription folderManifestDescription;

    private Optional<BoundOutputManager> subFolderOutputManager = Optional.empty();
    private boolean checkIfAllowed;
    private String subfolderName;

    public SubfolderWriter(
            BoundOutputManager outputManager,
            String subfolderName,
            IndexableOutputNameStyle outputNameStyle,
            ManifestDescription folderManifestDescription,
            boolean checkIfAllowed) {
        super();
        this.parentOutputManager = outputManager;
        this.outputNameStyle = outputNameStyle;
        this.folderManifestDescription = folderManifestDescription;
        this.checkIfAllowed = checkIfAllowed;
        this.subfolderName = subfolderName;
    }

    private Optional<BoundOutputManager> createSubfolder(
            boolean suppressSubfolder,
            ManifestFolderDescription folderDescription,
            FolderWriteIndexableOutputName subFolderWrite)
            throws OutputWriteFailedException {
        if (suppressSubfolder) {
            return Optional.of(parentOutputManager);
        } else {
            Writer writer =
                    checkIfAllowed
                            ? parentOutputManager.getWriterCheckIfAllowed()
                            : parentOutputManager.getWriterAlwaysAllowed();
            return writer.bindAsSubdirectory(
                    subfolderName, folderDescription, Optional.of(subFolderWrite));
        }
    }

    @Override
    public void init(FileType[] fileTypes, SequenceType sequenceType, boolean suppressSubfolder)
            throws InitException {

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
            this.subFolderOutputManager =
                    createSubfolder(suppressSubfolder, folderDescription, subFolderWrite);
        } catch (OutputWriteFailedException e) {
            throw new InitException(e);
        }
    }

    @Override
    public boolean isOn() {
        return subFolderOutputManager.isPresent();
    }

    @Override
    public void write(Operation<Generator, OutputWriteFailedException> generator, String index)
            throws OutputWriteFailedException {

        if (!isOn()) {
            return;
        }

        if (checkIfAllowed) {
            this.subFolderOutputManager		// NOSONAR
                    .get()
                    .getWriterCheckIfAllowed()
                    .write(outputNameStyle, generator, index);
        } else {
            this.subFolderOutputManager		// NOSONAR
                    .get()
                    .getWriterAlwaysAllowed()
                    .write(outputNameStyle, generator, index);
        }
    }

    // Requires the iterableGenerator to be in a valid state
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

    @Override
    public Optional<BoundOutputManager> getOutputManagerForFiles() {
        return subFolderOutputManager;
    }
}

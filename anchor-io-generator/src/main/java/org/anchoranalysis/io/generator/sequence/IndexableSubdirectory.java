/*-
 * #%L
 * anchor-io-manifest
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

import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import lombok.Getter;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.directory.Subdirectory;
import org.anchoranalysis.io.manifest.directory.SubdirectoryBase;
import org.anchoranalysis.io.manifest.file.FileType;
import org.anchoranalysis.io.manifest.file.OutputtedFile;
import org.anchoranalysis.io.manifest.finder.FindFailedException;
import org.anchoranalysis.io.manifest.sequencetype.IncompleteElementRange;
import org.anchoranalysis.io.output.namestyle.IndexableOutputNameStyle;

/**
 * A {@link SubdirectoryBase} entry in the manifest that is indexable.
 *
 * <p>This means it follows a predictable pattern, and entries can be predicated from the manifest.
 *
 * <p>See {@link Subdirectory} for subdirectories that have no indexable pattern.
 *
 * @author Owen Feehan
 */
class IndexableSubdirectory extends SubdirectoryBase {

    /** */
    private static final long serialVersionUID = -8404795823155555672L;

    @Getter private IndexableOutputNameStyle outputName;

    @Getter private Set<FileType> fileTypes;

    // Constructor
    public IndexableSubdirectory(IndexableOutputNameStyle outputName) {
        super();
        this.outputName = outputName;
        this.fileTypes = new HashSet<>();
    }

    public void addFileType(FileType fileType) {
        this.fileTypes.add(fileType);
    }

    // Every time a file is written, we do a check to ensure the outputName
    //   and manifestDescription and path matches one of our templates, otherwise
    //   we throw an Exception as something is wrong
    @Override
    public void recordWrittenFile(
            String outputName,
            ManifestDescription manifestDescription,
            Path outFilePath,
            String index) {
        // CURRENTLY - we do no check
    }

    /**
     * Finds a file.
     *
     * <p>We apply the match to each element in our sequence type, if the folder has no SequenceType
     * then something is wrong and we throw an exception.
     */
    @Override
    public void findFile(
            List<OutputtedFile> foundList, Predicate<OutputtedFile> predicate, boolean recursive)
            throws FindFailedException {

        IncompleteElementRange elements = description().getSequenceType().elementRange(); // NOSONAR

        int i = elements.getMinimumIndex();
        do {
            // We loop through each file type
            for (FileType fileType : fileTypes) {
                OutputtedFile virtualFile =
                        createOutputtedFile(elements.stringRepresentationForElement(i), fileType);
                if (predicate.test(virtualFile)) {
                    foundList.add(virtualFile);
                }
            }

            i = elements.nextIndex(i);

        } while (i != -1);
    }

    private OutputtedFile createOutputtedFile(String index, FileType fileType) {
        return new OutputtedFile(
                this,
                outputName.filenameWithoutExtension(index) + "." + fileType.getFileExtension(),
                outputName.getOutputName(),
                index,
                Optional.of(fileType.getManifestDescription()));
    }
}

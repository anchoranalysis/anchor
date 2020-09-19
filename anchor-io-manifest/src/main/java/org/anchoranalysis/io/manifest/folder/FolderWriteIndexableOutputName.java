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

package org.anchoranalysis.io.manifest.folder;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.file.FileType;
import org.anchoranalysis.io.manifest.file.FileWrite;
import org.anchoranalysis.io.manifest.match.Match;
import org.anchoranalysis.io.manifest.sequencetype.SequenceType;
import org.anchoranalysis.io.namestyle.IndexableOutputNameStyle;

public class FolderWriteIndexableOutputName extends FolderWriteWithPath {

    /** */
    private static final long serialVersionUID = -8404795823155555672L;

    private IndexableOutputNameStyle outputName;

    private ArrayList<FileType> template;

    // Constructor
    public FolderWriteIndexableOutputName(IndexableOutputNameStyle outputName) {
        super();
        this.outputName = outputName;
        this.template = new ArrayList<>();
    }

    public void addFileType(FileType fileType) {
        this.template.add(fileType);
    }

    // Every time a file is written, we do a check to ensure the outputName
    //   and manifestDescription and path matches one of our templates, otherwise
    //   we throw an Exception as something is wrong
    @Override
    public void write(
            String outputName,
            ManifestDescription manifestDescription,
            Path outFilePath,
            String index) {
        // CURRENTLY - we do no check
    }
    
    /**
     * Finds a file.
     * 
     * <p>We apply the match to each element in our sequence type, if the folder
     * has no SequenceType then something is wrong and we throw an exception.
     */
    @Override
    public void findFile(List<FileWrite> foundList, Match<FileWrite> match, boolean recursive) {
        SequenceType sequenceType = getManifestFolderDescription().getSequenceType();

        int i = sequenceType.getMinimumIndex();
        do {
            // We loop through each file type
            for (FileType fileType : template) {
                FileWrite virtualFile = createFileWrite(sequenceType.indexStr(i), fileType);
                if (match.matches(virtualFile)) {
                    foundList.add(virtualFile);
                }
            }

            i = sequenceType.nextIndex(i);

        } while (i != -1);
    }

    private FileWrite createFileWrite(String index, FileType fileType) {

        FileWrite write = new FileWrite();
        write.setFileName(outputName.getPhysicalName(index) + "." + fileType.getFileExtension());
        write.setOutputName(outputName.getOutputName());
        write.setManifestDescription(fileType.getManifestDescription());
        write.setIndex(index);
        write.setParentFolder(this);
        return write;
    }

    @Override
    public List<FileWrite> fileList() {
        throw new UnsupportedOperationException();
    }
}

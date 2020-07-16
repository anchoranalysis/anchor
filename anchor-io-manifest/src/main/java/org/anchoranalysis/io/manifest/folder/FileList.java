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
/* (C)2020 */
package org.anchoranalysis.io.manifest.folder;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.file.FileWrite;
import org.anchoranalysis.io.manifest.match.Match;

class FileList implements Serializable {

    /** */
    private static final long serialVersionUID = 5858857164978822313L;

    private FolderWrite folder;
    private ArrayList<FileWrite> files;

    public FileList(FolderWrite folder) {
        super();
        this.folder = folder;
        this.files = new ArrayList<>();
    }

    // Finds a folder a comparator matches
    public void findFile(List<FileWrite> foundList, Match<FileWrite> match, boolean recursive) {

        for (FileWrite file : files) {

            if (match.matches(file)) {
                foundList.add(file);
            }
        }

        if (!recursive) {
            return;
        }

        for (FolderWrite f : this.folder.getFolderList()) {
            f.findFile(foundList, match, recursive);
        }
    }

    public void add(FileWrite fw) {
        this.files.add(fw);
    }

    public void write(
            String outputName,
            ManifestDescription manifestDescription,
            Path outfilePath,
            String index) {

        FileWrite fw = new FileWrite(folder);
        fw.setOutputName(outputName);
        fw.setFileName(outfilePath.toString());
        fw.setManifestDescription(manifestDescription);
        fw.setIndex(index);
        add(fw);
    }

    public List<FileWrite> getFileList() {
        return files;
    }
}

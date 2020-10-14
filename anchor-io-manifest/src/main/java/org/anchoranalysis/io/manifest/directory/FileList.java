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

package org.anchoranalysis.io.manifest.directory;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.file.FileWrite;
import org.anchoranalysis.io.manifest.finder.FindFailedException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class FileList implements Serializable {

    /** */
    private static final long serialVersionUID = 5858857164978822313L;

    // START REQUIRED ARGUMENTS
    private final DirectoryWrite directory;
    // END REQUIRED ARGUMENTS
    
    private List<FileWrite> files = new ArrayList<>();

    // Finds a folder a comparator matches
    public void findFile(List<FileWrite> foundList, Predicate<FileWrite> predicate, boolean recursive) throws FindFailedException {

        files.stream().filter(predicate).forEach(foundList::add);

        if (!recursive) {
            return;
        }

        for (DirectoryWrite subdirectory : this.directory.subdirectories()) {
            subdirectory.findFile(foundList, predicate, recursive);
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

        FileWrite fileWrite = new FileWrite(directory);
        fileWrite.setOutputName(outputName);
        fileWrite.setFileName(outfilePath.toString());
        fileWrite.setManifestDescription(manifestDescription);
        fileWrite.setIndex(index);
        add(fileWrite);
    }

    public List<FileWrite> getFileList() {
        return files;
    }
}

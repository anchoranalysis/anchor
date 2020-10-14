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

import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.file.FileWrite;
import org.anchoranalysis.io.manifest.finder.FindFailedException;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DirectoryWritePhysical extends Subdirectory {

    /** */
    private static final long serialVersionUID = 8992970758732036941L;

    public DirectoryWritePhysical(Path directory) {
        super(directory);
    }
    
    private FileList delegate = new FileList(this);

    // Finds a folder a comparator matches
    @Override
    public void findFile(List<FileWrite> foundList, Predicate<FileWrite> predicate, boolean recursive) throws FindFailedException {
        delegate.findFile(foundList, predicate, recursive);
    }

    public void add(FileWrite fw) {
        delegate.add(fw);
    }

    @Override
    public void write(
            String outputName,
            ManifestDescription manifestDescription,
            Path outFilePath,
            String index) {
        delegate.write(outputName, manifestDescription, outFilePath, index);
    }

    @Override
    public List<FileWrite> fileList() {
        return delegate.getFileList();
    }
}

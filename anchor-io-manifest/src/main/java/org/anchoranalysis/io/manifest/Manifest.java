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

package org.anchoranalysis.io.manifest;

import java.io.Serializable;
import java.nio.file.Path;
import org.anchoranalysis.io.manifest.directory.DirectoryWrite;
import org.anchoranalysis.io.manifest.directory.RootDirectory;

/**
 * A manifest for a particular job (experiment recording where the outputs of particular jobs are stored on the filesystem.
 * 
 * @author Owen Feehan
 *
 */
public class Manifest implements Serializable {

    /** */
    private static final long serialVersionUID = -7253272905284863941L;

    /**
     * An entry for the root-directory in the manifest.
     * 
     * <p>All paths that occur in other elements are relative to this directory.
     */
    private RootDirectory rootFolder;

    public void init(Path rootFolderPath) {
        rootFolder = new RootDirectory(rootFolderPath);
    }

    public DirectoryWrite getRootFolder() {
        return rootFolder;
    }
}

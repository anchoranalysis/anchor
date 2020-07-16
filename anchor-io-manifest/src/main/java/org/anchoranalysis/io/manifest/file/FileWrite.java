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
package org.anchoranalysis.io.manifest.file;

import java.io.Serializable;
import java.nio.file.Path;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.folder.FolderWrite;

public class FileWrite implements Serializable {

    /** */
    private static final long serialVersionUID = 5796355859093885433L;

    private FolderWrite parentFolder;

    private String fileName;
    private String outputName;
    private ManifestDescription manifestDescription;
    private String index;

    public FileWrite() {}

    public FileWrite(FolderWrite parentFolder) {
        super();
        this.parentFolder = parentFolder;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getOutputName() {
        return outputName;
    }

    public void setOutputName(String outputName) {
        this.outputName = outputName;
    }

    public ManifestDescription getManifestDescription() {
        return manifestDescription;
    }

    public void setManifestDescription(ManifestDescription manifestDescription) {
        this.manifestDescription = manifestDescription;
    }

    public Path calcPath() {
        return parentFolder.calcPath().resolve(fileName);
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public void setIndex(int index) {
        this.index = Integer.toString(index);
    }

    public FolderWrite getParentFolder() {
        return parentFolder;
    }

    public void setParentFolder(FolderWrite parentFolder) {
        this.parentFolder = parentFolder;
    }
}

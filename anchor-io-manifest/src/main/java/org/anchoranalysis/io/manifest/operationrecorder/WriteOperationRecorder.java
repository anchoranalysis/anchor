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

package org.anchoranalysis.io.manifest.operationrecorder;

import java.nio.file.Path;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.manifest.ManifestDirectoryDescription;
import org.anchoranalysis.io.manifest.directory.SubdirectoryBase;

/**
 * Makes a record of an output written to the filesystem.
 *
 * @author Owen Feehan
 */
public interface WriteOperationRecorder {

    /**
     * Records a file written.
     *
     * @param outputName the "output name" used to generate the file
     * @param manifestDescription a description of the directory
     * @param outFilePath the path it's wrriten to relative to the folder
     * @param index an index, if it's part of a set of files
     */
    void recordWrittenFile(
            String outputName,
            ManifestDescription manifestDescription,
            Path outFilePath,
            String index);

    /**
     * Records a subdirectory that has been recated.
     *
     * @param relativeDirectoryPath the path of the subdirectory relative to the parent
     * @param manifestDescription a description of the directory
     * @param folderWrite the folder object to write
     * @return
     */
    WriteOperationRecorder recordSubdirectoryCreated(
            Path relativeDirectoryPath,
            ManifestDirectoryDescription manifestDescription,
            SubdirectoryBase folderWrite);
}

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
import org.anchoranalysis.io.manifest.file.OutputtedFile;

/**
 * The root directory for where outputs for a particular experiment occur.
 *
 * <p>Subdirectories are recorded in this, but individual files are ignored.
 *
 * @author Owen Feehan
 */
public class JobRootDirectory extends SubdirectoryBase {

    /** */
    private static final long serialVersionUID = 4923049916867897251L;

    @Override
    public void recordWrittenFile(
            String outputName,
            ManifestDescription manifestDescription,
            Path outFilePath,
            String index) {
        // NOTHING TO DO
    }

    @Override
    public void findFile(
            List<OutputtedFile> foundList, Predicate<OutputtedFile> predicate, boolean recursive) {
        // NOTHING TO DO
    }
}

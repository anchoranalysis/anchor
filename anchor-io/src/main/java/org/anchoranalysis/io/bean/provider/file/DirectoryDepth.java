/*-
 * #%L
 * anchor-io
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

package org.anchoranalysis.io.bean.provider.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.progress.ProgressReporterMultiple;
import org.anchoranalysis.io.bean.input.InputManagerParams;
import org.anchoranalysis.io.error.FileProviderException;

/** Lists all directories to a certain depth */
public class DirectoryDepth extends FileProviderWithDirectoryString {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private int exactDepth = 0;
    // END BEAN PROPERTIES

    @Override
    public Collection<File> matchingFilesForDirectory(Path directory, InputManagerParams params)
            throws FileProviderException {

        String[] filesDir = directory.toFile().list();

        if (filesDir == null) {
            throw new FileProviderException(
                    String.format("Path %s is not valid. Cannot enumerate directory.", directory));
        }

        int numFiles = filesDir.length;

        try (ProgressReporterMultiple prm =
                new ProgressReporterMultiple(params.getProgressReporter(), numFiles)) {
            WalkToDepth walkTo = new WalkToDepth(exactDepth, prm);
            return walkTo.findDirs(directory.toFile());
        } catch (IOException e) {
            throw new FileProviderException(e);
        }
    }
}

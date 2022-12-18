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

package org.anchoranalysis.io.input.bean.files;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.io.input.InputContextParameters;
import org.anchoranalysis.io.input.bean.InputManagerParameters;

/**
 * Provides a single file only.
 * 
 * @author Owen Feehan
 *
 */
@NoArgsConstructor
public class SingleFile extends FilesProviderWithDirectory {

    // START BEAN PROPERTIES
	/** The path of the single-file. */
    @BeanField @Getter private String path;
    // END BEAN PROPERTIES

    // Optionally changes the directory of the path
    private Path directory;

    /**
     * Creates with a path for the single-file.
     * 
     * @param path the path.
     */
    public SingleFile(String path) {
        this.path = path;
    }

    @Override
    public List<File> matchingFilesForDirectory(Path directory, InputManagerParameters parameters) {

        File file = new File(path);

        if (hasDirectory()) {
            file = directory.resolve(file.getName()).toFile();
        }

        return Collections.singletonList(file);
    }

    @Override
    public Path getDirectoryAsPath(InputContextParameters inputContext) {

        if (hasDirectory()) {
            return directory;
        } else {
            // We infer the directory if it isn't set
            return Paths.get(path).getParent();
        }
    }
    
    private boolean hasDirectory() {
        return directory != null;
    }
}

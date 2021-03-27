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

package org.anchoranalysis.io.input.file;

import com.google.common.base.Preconditions;
import java.nio.file.Path;
import lombok.Getter;

/**
 * An input pertaining to a single file on the file-system - and a directory in which it resides.
 *
 * @author Owen Feehan
 */
public class FileWithDirectoryInput extends SingleFileInputBase {

    /**
     * A directory that must be a containing directory (at some hierarchical level) of file
     * associated with this input.
     */
    @Getter private Path directory;

    /**
     * Create for a particular file and directory.
     *
     * @param file the file this input prefers to.
     * @param directory a directory that must be a containing directory (at some hierarchical level)
     *     of {@code file}
     */
    public FileWithDirectoryInput(NamedFile file, Path directory) {
        super(file);
        Preconditions.checkArgument(directory.toFile().isDirectory());
        Preconditions.checkArgument(
                file.getPath().toAbsolutePath().normalize().startsWith(directory));
        this.directory = directory;
    }
}

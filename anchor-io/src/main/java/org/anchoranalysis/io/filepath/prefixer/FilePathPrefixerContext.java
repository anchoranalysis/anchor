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

package org.anchoranalysis.io.filepath.prefixer;

import java.nio.file.Path;
import java.util.Optional;
import lombok.Getter;
import org.anchoranalysis.io.error.FilePathPrefixerException;

public class FilePathPrefixerContext {

    @Getter private boolean debugMode;

    /** A directory indicating where inputs can be located */
    @Getter private final Optional<Path> outputDirectory;

    public FilePathPrefixerContext(boolean debugMode, Optional<Path> outputDirectory)
            throws FilePathPrefixerException {
        super();
        this.debugMode = debugMode;
        this.outputDirectory = outputDirectory;
        checkAbsolutePath();
    }

    private void checkAbsolutePath() throws FilePathPrefixerException {
        if (outputDirectory.isPresent() && !outputDirectory.get().isAbsolute()) {
            throw new FilePathPrefixerException(
                    String.format(
                            "An non-absolute path was passed to FilePathPrefixerParams of %s",
                            outputDirectory.get()));
        }
    }
}
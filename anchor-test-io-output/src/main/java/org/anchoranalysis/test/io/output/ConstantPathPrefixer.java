/*-
 * #%L
 * anchor-test-image
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
package org.anchoranalysis.test.io.output;

import java.nio.file.Path;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.anchoranalysis.io.output.bean.path.prefixer.PathPrefixer;
import org.anchoranalysis.io.output.path.prefixer.DirectoryWithPrefix;
import org.anchoranalysis.io.output.path.prefixer.NamedPath;
import org.anchoranalysis.io.output.path.prefixer.PathPrefixerContext;

/**
 * Outputs every file to a particular directory, adding an additional prefix for each input (to make
 * them unique).
 *
 * <p>Specifically a prefix is assigned from the name of an the input, followed by an underscore
 * delimiter.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
class ConstantPathPrefixer extends PathPrefixer {

    /** The directory in which all files are placed (or in subdirectories within). */
    private final Path directory;

    @Override
    public DirectoryWithPrefix outFilePrefix(
            NamedPath path, Optional<String> experimentIdentifier, PathPrefixerContext context) {
        DirectoryWithPrefix out = new DirectoryWithPrefix(directory);
        String identifier = path.getName();
        out.setPrefix(identifier);
        out.setDelimiter("_");
        return out;
    }

    @Override
    public DirectoryWithPrefix rootDirectoryPrefix(
            Optional<String> experimentIdentifier, PathPrefixerContext context) {
        return new DirectoryWithPrefix(directory);
    }
}

/*-
 * #%L
 * anchor-io-output
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

package org.anchoranalysis.io.output.bound;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.io.output.writer.WriterExecuteBeforeEveryOperation;

/**
 * Memoizes LazyDirectoryInit creation for particular outputDirectory (normalized)
 *
 * <p>The {@code parent} parameter is always assumed to be uniform for a given outputDirectory
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
public class LazyDirectoryFactory {

    // START REQUIRED ARGUMENTS
    private final boolean delExistingFolder;
    // END REQUIRED ARGUMENTS

    // Cache all directories created by Path
    private Map<Path, WriterExecuteBeforeEveryOperation> map = new HashMap<>();

    public synchronized WriterExecuteBeforeEveryOperation createOrReuse(
            Path outputDirectory, Optional<WriterExecuteBeforeEveryOperation> parent) {
        // So that we are always referring to a canonical output-directory path
        Path outputDirectoryNormalized = outputDirectory.normalize();
        return map.computeIfAbsent(
                outputDirectoryNormalized,
                path -> new LazyDirectoryInit(path, delExistingFolder, parent));
    }
}

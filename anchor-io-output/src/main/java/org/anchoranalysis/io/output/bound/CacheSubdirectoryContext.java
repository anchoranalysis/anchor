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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.io.manifest.ManifestFolderDescription;

/**
 * Caches sub-directories as they are created, so as to reuse the BoundIOContext, without creating
 * duplicate manifest entries.
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
public class CacheSubdirectoryContext {

    /** the context of the directory in which subdirectories may be created */
    private final BoundIOContext parentContext;

    /** A description to use for every created folder */
    private final ManifestFolderDescription manifestFolderDescription;

    private Map<Optional<String>, BoundIOContext> mapOutputManagers = new HashMap<>();

    /**
     * Gets (from the cache if it's already there) subdirectory for a given-name
     *
     * @param subdirectoryName the sub-directory name. if not set, then the parentContext is
     *     returned instead.
     * @return
     */
    public BoundIOContext get(Optional<String> subdirectoryName) {
        return mapOutputManagers.computeIfAbsent(
                subdirectoryName,
                key -> parentContext.maybeSubdirectory(key, manifestFolderDescription));
    }
}

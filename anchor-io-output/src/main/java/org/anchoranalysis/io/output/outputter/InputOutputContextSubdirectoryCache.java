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

package org.anchoranalysis.io.output.outputter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

/**
 * Caches a {@link InputOutputContext} for each subdirectory as they are created.
 *
 * <p>This allows reusage of an existing {@link InputOutputContext} in other outputters without
 * creating any potential clashes.
 *
 * <p>TODO is this still needed without manifests?
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
public class InputOutputContextSubdirectoryCache {

    /** the context of the directory in which subdirectories may be created */
    private final InputOutputContext parentContext;

    /**
     * If true, the output rules and recording are inherited from the parent directory. if false,
     * they are not, and all outputs are allowed and are unrecorded.
     */
    private final boolean inheritOutputRulesAndRecording;

    private Map<Optional<String>, InputOutputContext> mapContexts = new HashMap<>();

    /**
     * Gets (from the cache if it's already there) a context for a subdirectory of given-name
     *
     * @param subdirectoryName the subdirectory name. if not set, then the parentContext is returned
     *     instead.
     * @return either an existing context for the subdirectory or a newly created one
     */
    public InputOutputContext get(Optional<String> subdirectoryName) {
        return mapContexts.computeIfAbsent(
                subdirectoryName,
                key -> parentContext.maybeSubdirectory(key, inheritOutputRulesAndRecording));
    }
}

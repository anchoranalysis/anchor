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

package org.anchoranalysis.io.input.path.matcher;

import java.nio.file.Files;	//NOSONAR
import java.util.Optional;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * Some constraints against which paths must match.
 *
 * <p>This is an <i>immutable</i> class.
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PathMatchConstraints {

    /** Predicates to match against both a file and a directory. */
    private DualPathPredicates predicates;

    /**
     * Limits on the depth of how many sub-directories are to be recursed.
     *
     * <p>See {@link Files#walkFileTree} for an explanation of what each value means.
     *
     * <p>Assign {@link Integer#MAX_VALUE} to place no limit.
     *
     * <p>A value of {@code 0} visits only the directory root, but not any files or subdirectories
     * within.
     */
    private int maxDirectoryDepth;

    /**
     * Creates with {@link DualPathPredicates} only.
     *
     * @param predicates predicates to match against both a file and a directory.
     */
    public PathMatchConstraints(DualPathPredicates predicates) {
        this(predicates, Optional.empty());
    }

    /**
     * Creates with {@link DualPathPredicates} and an optional maximum directory-depth.
     *
     * @param predicates predicates to match against both a file and a directory.
     * @param maxDirectoryDepth limits on the depth of how many sub-directories are to be recursed. If undefined, it
     *     is unlimited.
     */
    public PathMatchConstraints(
            DualPathPredicates predicates, Optional<Integer> maxDirectoryDepth) {
        this.predicates = predicates;
        this.maxDirectoryDepth = maxDirectoryDepth.orElse(Integer.MAX_VALUE);
    }

    /**
     * Creates a duplicate of the current state, but with a different {@code maxDirectoryDepth}.
     *
     * @param depthToAssign the new depth to assign in the duplicate.
     * @return a newly created duplicate object, as per above.
     */
    public PathMatchConstraints replaceMaxDirectoryDepth(int depthToAssign) {
        return new PathMatchConstraints(predicates, depthToAssign);
    }
}

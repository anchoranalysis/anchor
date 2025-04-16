/*-
 * #%L
 * anchor-image-feature
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

package org.anchoranalysis.image.feature.calculator.merged;

/**
 * Specifies which parts of a merged pair to include in calculations.
 *
 * <p>This class allows for selective inclusion of the first object, second object, and/or the
 * merged object in feature calculations.
 */
public final class MergedPairsInclude {

    private boolean includeFirst;
    private boolean includeSecond;
    private boolean includeMerged;

    /** Constructs a MergedPairsInclude with all options set to true. */
    public MergedPairsInclude() {
        this(true, true, true);
    }

    /**
     * Constructs a MergedPairsInclude with specified inclusion options.
     *
     * @param includeFirst whether to include the first object
     * @param includeSecond whether to include the second object
     * @param includeMerged whether to include the merged object
     */
    public MergedPairsInclude(boolean includeFirst, boolean includeSecond, boolean includeMerged) {
        super();
        this.includeFirst = includeFirst;
        this.includeSecond = includeSecond;
        this.includeMerged = includeMerged;
    }

    /**
     * Checks if either the first or second object should be included.
     *
     * @return true if either the first or second object should be included, false otherwise
     */
    public boolean includeFirstOrSecond() {
        return includeFirst || includeSecond;
    }

    /**
     * Checks if the first object should be included.
     *
     * @return true if the first object should be included, false otherwise
     */
    public boolean includeFirst() {
        return includeFirst;
    }

    /**
     * Checks if the second object should be included.
     *
     * @return true if the second object should be included, false otherwise
     */
    public boolean includeSecond() {
        return includeSecond;
    }

    /**
     * Checks if the merged object should be included.
     *
     * @return true if the merged object should be included, false otherwise
     */
    public boolean includeMerged() {
        return includeMerged;
    }
}

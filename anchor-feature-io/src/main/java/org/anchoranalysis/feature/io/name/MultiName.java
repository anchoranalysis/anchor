/*-
 * #%L
 * anchor-feature-io
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

package org.anchoranalysis.feature.io.name;

import java.util.Optional;

/**
 * A name that that uniquely identifies an entity, and is composed of two parts.
 *
 * <p>The first part is also a grouping key for aggregation.
 *
 * <p>Together both the first and second parts uniquely identify an entity.
 *
 * @author Owen Feehan
 */
public interface MultiName extends Iterable<String>, Comparable<MultiName> {

    /**
     * The first part, and grouping key.
     *
     * @return the name of part, if it exists.
     */
    Optional<String> firstPart();

    /**
     * The second part of the name.
     *
     * @return the name of the part.
     */
    String secondPart();

    /**
     * The full name, including both directory and file part, as a string.
     *
     * @return the name.
     */
    String toString();

    /**
     * If another name is equal to the current name.
     *
     * @param other the other name.
     * @return true iff the two objects are equal.
     */
    boolean equals(Object other);
}

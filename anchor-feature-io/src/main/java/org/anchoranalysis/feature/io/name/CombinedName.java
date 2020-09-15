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

import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;

/**
 * A name which is unique when a primaryName is combined with a secondaryName
 *
 * <p>In some cases (e.g. when grouping) the primaryName has relevance.
 *
 * @author Owen
 */
public class CombinedName implements MultiName {

    private static final String SEPARATOR = "/";

    /** The first-part (and key used for aggregating). */
    private String key;

    /** The second-part (all the names not used in aggregating joined together). */
    private String remainder;

    /**
     * All names combined together in a single string with a forward slash as separator.
     *
     * <p>The primaryName is used for aggregating. The rest are not. primaryName/secondaryName
     */
    private String allTogether;

    /**
     * Creates with explicit strings for both parts.
     *
     * @param key first-part (and key used for aggregating).
     * @param remainder the second-part (all the names not used in aggregating joined together).
     */
    public CombinedName(String key, String remainder) {
        this.key = key;
        this.remainder = remainder;
        this.allTogether = String.join(SEPARATOR, key, remainder);
    }

    @Override
    public int hashCode() {
        return allTogether.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CombinedName) {
            CombinedName objCast = (CombinedName) obj;
            return allTogether.equals(objCast.allTogether);
        } else {
            return false;
        }
    }

    @Override
    public Optional<String> firstPart() {
        // The primary name is used for aggregation
        return Optional.of(key);
    }

    @Override
    public Iterator<String> iterator() {
        return Arrays.asList(key, remainder).iterator();
    }

    @Override
    public String secondPart() {
        return remainder;
    }

    @Override
    public String toString() {
        return allTogether;
    }

    @Override
    public int compareTo(MultiName other) {

        if (other instanceof CombinedName) {

            CombinedName otherCast = (CombinedName) other;

            int compareResult = key.compareTo(otherCast.key);

            if (compareResult != 0) {
                return compareResult;
            }

            return remainder.compareTo(otherCast.remainder);
        } else {
            return 1;
        }
    }
}

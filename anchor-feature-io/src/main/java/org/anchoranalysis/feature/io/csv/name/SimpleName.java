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

package org.anchoranalysis.feature.io.csv.name;

import java.util.Iterator;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import org.apache.commons.collections.iterators.SingletonIterator;

/**
 * A name with only one part, and is always unique
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
@EqualsAndHashCode
public class SimpleName implements MultiName {

    private String name;

    @SuppressWarnings("unchecked")
    @Override
    public Iterator<String> iterator() {
        return new SingletonIterator(name);
    }

    @Override
    public Optional<String> directoryPart() {
        return Optional.empty();
    }

    @Override
    public String filePart() {
        return name;
    }

    @Override
    public int compareTo(MultiName other) {

        if (other instanceof SimpleName) {
            return name.compareTo(((SimpleName) other).name);
        } else {
            return -1;
        }
    }

    @Override
    public String toString() {
        return name;
    }
}

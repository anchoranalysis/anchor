/*-
 * #%L
 * anchor-io-manifest
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
package org.anchoranalysis.io.manifest.sequencetype;

import java.util.Collection;
import java.util.Iterator;
import lombok.AllArgsConstructor;

@AllArgsConstructor
class CollectionAsRange implements IncompleteElementRange {

    private Collection<String> collection;

    @Override
    public int nextIndex(int index) {
        if (index < 0) {
            return -1;
        }
        if (index >= (collection.size() - 1)) {
            return -1;
        }
        return index + 1;
    }

    @Override
    public int previousIndex(int index) {
        if (index <= 0) {
            return -1;
        }
        if (index >= collection.size()) {
            return -1;
        }
        return index - 1;
    }

    @Override
    public int previousEqualIndex(int index) {
        return index;
    }

    @Override
    public int getMinimumIndex() {
        return 0;
    }

    @Override
    public int getMaximumIndex() {
        return collection.size() - 1;
    }

    @Override
    public String stringRepresentationForElement(int elementIndex) {

        assert (!collection.isEmpty());

        Iterator<String> itr = collection.iterator();

        String ret = "";
        for (int i = 0; i <= elementIndex; i++) {
            assert (itr.hasNext());
            ret = itr.next();
        }
        return ret;
    }
}
